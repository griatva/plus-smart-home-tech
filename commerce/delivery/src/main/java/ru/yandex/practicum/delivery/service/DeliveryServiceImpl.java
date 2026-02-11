package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.mapper.AddressMapper;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.AddressRepository;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.client.OrderClient;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.interaction.enums.DeliveryState;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NoDeliveryFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(5.0);
    private static final BigDecimal ADDRESS_1_MULTIPLIER = BigDecimal.valueOf(1.0);
    private static final BigDecimal ADDRESS_2_MULTIPLIER = BigDecimal.valueOf(2.0);
    private static final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final BigDecimal REMOTENESS_MULTIPLIER = BigDecimal.valueOf(0.2);
    private static final int SCALE = 2;

    private final AddressRepository addressRepository;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;
    private final DeliveryMapper deliveryMapper;
    private final AddressMapper addressMapper;

    @Transactional
    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {

        if (deliveryRepository.existsByOrderId(deliveryDto.getOrderId())) {
            throw new ConflictException("Delivery for order with orderId " + deliveryDto.getOrderId() + " already exists");
        }

        Address fromCandidate = addressMapper.toEntity(deliveryDto.getFromAddress());
        Optional<Address> existingFrom = addressRepository
                .findByCountryAndCityAndStreetAndHouseAndFlat(
                        fromCandidate.getCountry(),
                        fromCandidate.getCity(),
                        fromCandidate.getStreet(),
                        fromCandidate.getHouse(),
                        fromCandidate.getFlat()
                );
        Address fromAddress = existingFrom.orElseGet(() -> addressRepository.save(fromCandidate));

        Address toCandidate = addressMapper.toEntity(deliveryDto.getToAddress());
        Optional<Address> existingTo = addressRepository
                .findByCountryAndCityAndStreetAndHouseAndFlat(
                        toCandidate.getCountry(),
                        toCandidate.getCity(),
                        toCandidate.getStreet(),
                        toCandidate.getHouse(),
                        toCandidate.getFlat()
                );
        Address toAddress = existingTo.orElseGet(() -> addressRepository.save(toCandidate));


        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setFromAddress(fromAddress);
        delivery.setToAddress(toAddress);
        delivery.setDeliveryState(DeliveryState.CREATED);

        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Transactional
    @Override
    public void markDeliverySuccessful(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.delivery(orderId);
    }


    @Transactional
    @Override
    public void markDeliveryPicked(UUID orderId) {

        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        orderClient.assembly(orderId);

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(orderId)
                .build();
        warehouseClient.shippedToDelivery(request);

        deliveryRepository.save(delivery);
    }

    @Transactional
    @Override
    public void markDeliveryFailed(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.deliveryFailed(orderId);
    }

    @Transactional
    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        String warehouseStreet = warehouseAddress.getStreet();

        BigDecimal deliveryCost;

        BigDecimal baseRate = BASE_RATE;

        BigDecimal multiplier = warehouseStreet.equals("ADDRESS_1")
                ? ADDRESS_1_MULTIPLIER
                : ADDRESS_2_MULTIPLIER;
        deliveryCost = baseRate.add(baseRate.multiply(multiplier));

        Boolean isFragile = orderDto.getFragile();
        if (Boolean.TRUE.equals(isFragile)) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(FRAGILE_MULTIPLIER));
        }

        BigDecimal weight = orderDto.getDeliveryWeight() != null
                ? BigDecimal.valueOf(orderDto.getDeliveryWeight())
                : BigDecimal.ZERO;
        deliveryCost = deliveryCost.add(weight.multiply(WEIGHT_MULTIPLIER));


        BigDecimal volume = orderDto.getDeliveryVolume() != null
                ? BigDecimal.valueOf(orderDto.getDeliveryVolume())
                : BigDecimal.ZERO;
        deliveryCost = deliveryCost.add(volume.multiply(VOLUME_MULTIPLIER));


        Delivery delivery = getByDeliveryId(orderDto.getDeliveryId());
        Address toAddress = delivery.getToAddress();
        if (!toAddress.getStreet().equals(warehouseStreet)) {
            deliveryCost = deliveryCost.add(deliveryCost.multiply(REMOTENESS_MULTIPLIER));
        }

        return deliveryCost.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery for order with id " + orderId + " was not found"));
    }

    private Delivery getByDeliveryId(UUID deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery with id " + deliveryId + " was not found"));
    }
}
