package ru.yandex.practicum.delivery.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;

@Component
@RequiredArgsConstructor
public class DeliveryMapper {

    private final AddressMapper addressMapper;


    public DeliveryDto toDto(Delivery delivery) {
        if (delivery == null) {
            return null;
        }
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .fromAddress(addressMapper.toDto(delivery.getFromAddress()))
                .toAddress(addressMapper.toDto(delivery.getToAddress()))
                .orderId(delivery.getOrderId())
                .deliveryState(delivery.getDeliveryState())
                .build();
    }

    public Delivery toEntity(DeliveryDto dto) {
        if (dto == null) {
            return null;
        }
        return Delivery.builder()
                .deliveryId(dto.getDeliveryId())
                .fromAddress(addressMapper.toEntity(dto.getFromAddress()))
                .toAddress(addressMapper.toEntity(dto.getToAddress()))
                .orderId(dto.getOrderId())
                .deliveryState(dto.getDeliveryState())
                .build();
    }
}