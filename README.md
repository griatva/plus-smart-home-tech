# Smart Home Tech — Telemetry & Commerce (Microservices, Kafka, gRPC, Spring Cloud)

*Developed as part of a learning program to practice microservice architecture,
distributed communication, and the Spring Cloud ecosystem.*

**Smart Home Tech** represents the backend infrastructure of a smart-home company.
It connects real-time IoT telemetry from devices with a commerce platform,
allowing business processes (orders, payments, delivery, stock management)
to react dynamically to device data and user behavior.

The system is designed as a **multi-module**, **event-driven**, **cloud-native** platform.
It ingests IoT telemetry via **gRPC**, processes data through **Kafka**,
analyzes device behavior, and powers multiple **commerce microservices**
such as orders, delivery, payments, and warehouse management.
Service discovery and centralized configuration are managed via
**Eureka** and **Spring Cloud Config**.

> **Java 21 · Spring Boot 3.3 · Spring Cloud (Eureka, Config, Feign, Circuit Breaker, LoadBalancer) · Kafka · gRPC · Protobuf · PostgreSQL · Maven · Docker Compose**
---

## Highlights

- **Event-driven telemetry pipeline:** gRPC ingestion → Kafka topics → aggregation → analysis
- **Microservices architecture:** telemetry (collector, aggregator, analyzer), commerce (order, warehouse, delivery), infra (gateway, config, discovery)
- **Spring Cloud ecosystem:** Eureka discovery, Config Server, Feign clients, load balancing, and circuit breakers
- **Asynchronous communication:** Kafka producers/consumers, Protobuf serialization
- **Resilient communication:** Circuit breakers and retries for inter-service calls
- **Service discovery:** Eureka clients for automatic registration and routing
- **Centralized configuration:** shared properties via Spring Cloud Config Server
- **PostgreSQL schemas:** per-service databases, initialized via `schema.sql`

---

## Tech Stack

- **Language:** Java 21
- **Frameworks:** Spring Boot, Spring Cloud (Eureka, Config, Feign, Circuit Breaker, LoadBalancer)
- **Messaging/Streaming:** Apache Kafka (event bus)
- **RPC:** gRPC (Protobuf) for real-time telemetry ingestion
- **Database:** PostgreSQL (per-service schema, `schema.sql` initialization)
- **Build Tools:** Maven (multi-module)
- **Containerization:** Docker Compose (Kafka + PostgreSQL + Config + Eureka)

---

## Modules Overview

### Infra
- **infra/config-server** — Centralized configuration hub (Spring Cloud Config).  
  Hosts application YAMLs for all services.  
  Services import configs via:  
  `spring.config.import=configserver:http://localhost:8888`

- **infra/discovery-server** — Eureka service registry.  
  All microservices register here for discovery and routing.  

- **infra/gateway** — Spring Cloud Gateway (Eureka client).  
  Routes external requests to backend microservices via service names.  
  Supports load balancing and circuit breaker patterns.

---

### Telemetry
- **telemetry/collector** — gRPC ingestion service for **hub** and **sensor** events.  
  Produces messages to Kafka topics:  
  `topic-sensor: "telemetry.sensors.v1"`, `topic-hub: "telemetry.hubs.v1"`.


- **telemetry/aggregator** — aggregates raw sensor telemetry from Kafka (`telemetry.sensors.v1`)
  into compact **sensor snapshots** per hub when new sensor events arrive,
  and republishes them to `telemetry.snapshots.v1`.
  Uses the native Kafka Producer API (`ProducerRecord`, Avro serialization) with
  manual consumer configuration for real-time stream processing.


- **telemetry/analyzer** — Consumes **hub events** and **snapshots** from Kafka,  
  persists data (scenarios, sensors) in PostgreSQL, and runs automation logic.  
  Uses Feign for internal calls and is resilient with circuit breakers.


- **telemetry/serialization** — Common protobuf definitions and serializers.

---

### Commerce
- **commerce/** — business microservices layer supporting commerce operations:
  - `shopping-store` — product catalog
  - `shopping-cart` — user cart management
  - `warehouse` — stock and inventory
  - `order` — order lifecycle orchestration (uses Feign clients)
  - `payment` — payment handling
  - `delivery` — delivery management
  - `interaction-api` — unified API gateway for commerce operations

Each service:
- Registers with **Eureka**
- Loads config from **Config Server**
- Communicates via **Feign clients** with built-in **LoadBalancer**
- Handles failures via **Circuit Breaker**

---

## Project Structure
```
plus-smart-home-tech/
├── pom.xml                                  # Parent Maven POM (Spring Boot 3.3, Java 21)
├── compose.yaml                             # Local dev stack (Kafka in KRaft mode + PostgreSQL)
│
├── infra/ (module group)
│   ├── config-server/ (module)              # Centralized configuration (Spring Cloud Config)
│   ├── discovery-server/ (module)           # Service discovery (Eureka Server)
│   └── gateway/ (module)                    # API Gateway (routing, LB, resilience; Eureka client)
│     
│
├── telemetry/ (module group)                # Telemetry pipeline
│   ├── collector/ (module)                  # gRPC ingestion → Kafka producer
│   ├── aggregator/ (module)                 # Kafka consumer → snapshot producer
│   ├── analyzer/ (module)                   # Kafka consumer → DB persistence → scenario evaluation → gRPC control commands
│   ├── serialization/ (module)              # Protobuf schemas & serialization tooling
│   └── hub-router/                          # Local hub event simulator (utility JAR, provided as part of the course)
│
└── commerce/ (module group)                 # Commerce microservices
    ├── shopping-store/ (module)             # Product catalog
    ├── shopping-cart/ (module)              # User carts
    ├── order/ (module)                      # Order orchestration
    ├── payment/ (module)                    # Payment workflow
    ├── delivery/ (module)                   # Delivery tracking
    ├── warehouse/ (module)                  # Stock & booking management
    └── interaction-api/ (module)            # Unified commerce API facade
```

## Database Schema (Example)

### Telemetry Analyzer (PostgreSQL)
`telemetry/analyzer/src/main/resources/schema.sql`

- `scenarios(id, hub_id, name)` — unique per `hub_id`
- `sensors(id, hub_id, type)`
- `conditions(id, type, value)`
- `scenario_conditions(scenario_id, sensor_id, condition_id)`
- `actions(id, type, value)`
- `scenario_actions(scenario_id, sensor_id, action_id)`

### Commerce Modules
Each service has its own database schema, for example:
- `delivery/src/main/resources/schema.sql`
- `shopping-store/src/main/resources/schema.sql`
- `warehouse/src/main/resources/schema.sql`

---

## APIs & Communication

### gRPC
- Implemented in `telemetry/collector` (`CollectorControllerGrpc`)
- Handles telemetry ingestion from IoT hubs and sensors

### Kafka
- Event bus for telemetry and snapshots (`topicHub`, `topicSensor`)
- Consumed by **aggregator** and **analyzer** modules

### Feign Clients
- Used for inter-service HTTP communication (e.g., commerce microservices)
- Integrated with **Spring Cloud LoadBalancer** for dynamic routing via Eureka

### Circuit Breakers
- Implemented with **Spring Cloud Resilience4j**
- Provides graceful fallbacks for Feign calls under failure conditions

### Config & Discovery
- **Spring Cloud Config Server:** centralized configuration source
- **Eureka Discovery Server:** automatic registration and service lookup

---

## Skills Practiced

- Designing **event-driven microservices** (Kafka + gRPC + REST)
- Applying **Spring Cloud stack:**
  - **Eureka** — service discovery and dynamic routing
  - **Config Server** — centralized configuration
  - **Feign** — declarative REST clients with built-in load balancing
  - **Circuit Breaker (Resilience4j)** — fault tolerance and fallback handling
- Structuring **multi-module Maven builds**
- Implementing **Kafka consumers/producers** and **Protobuf serialization**
- Modeling **PostgreSQL** schemas and initializing via `schema.sql`
- Running distributed systems via **Docker Compose**

---

## How to Run Locally

The project consists of multiple Spring Boot services and requires
Kafka and PostgreSQL for local execution.

1. Start infrastructure (Kafka and databases):
```bash
docker compose -f compose.yaml up
```
2. Start Spring Cloud infrastructure:

- discovery-server (Eureka)
- config-server
- gateway

3. Start application services as needed (telemetry and commerce modules)
using spring-boot:run or an IDE.

## About

**Smart Home Tech** demonstrates a scalable **microservice architecture**  
combining telemetry processing, event-driven analytics, and commerce operations.  
It showcases best practices in **Spring Cloud**, **Kafka streaming**,  
and **distributed service orchestration**.

---