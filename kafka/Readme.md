# 🚀 Kafka Setup – Krushi Sarjana

This directory contains the Docker Compose configuration for running **Apache Kafka** and **Kafka UI** for the Krushi Sarjana microservices architecture.

Kafka acts as an event streaming platform, enabling asynchronous communication between microservices. It reduces direct service dependencies, improves scalability, and supports event-driven communication.

---

## 🛠️ Tech Stack

| Technology | Version / Details |
|---|---|
| Apache Kafka | 4.1.0 |
| Kafka UI | Provectus Kafka UI |
| Containerization | Docker |
| Orchestration | Docker Compose V2 |
| Kafka Architecture | KRaft Mode |
| Storage | Docker Named Volume |

---

## 📁 Project Structure

```text
kafka/
│
├── docker-compose.yml
└── README.md
```

---

## 🏗️ Architecture

The Kafka setup consists of two Docker containers:

| Service | Container Name | Port | Purpose |
|---|---|---|---|
| Apache Kafka | `krushi-kafka` | `9092` | Event streaming and message broker |
| Kafka UI | `krushi-kafka-ui` | `8090` | Kafka monitoring dashboard |

### Architecture Overview

```text
                 KRUSHI SARJANA
              MICROSERVICES ARCHITECTURE

       ┌──────────────┬──────────────┐
       │              │              │
  ┌────▼─────┐  ┌─────▼────┐  ┌──────▼─────┐
  │  User    │  │  Order   │  │  Payment   │
  │ Service  │  │ Service  │  │  Service   │
  └────┬─────┘  └─────┬────┘  └──────┬─────┘
       │              │              │
       └──────────────┼──────────────┘
                      │
               ┌──────▼──────┐
               │    KAFKA    │
               │  localhost  │
               │    :9092    │
               └──────┬──────┘
                      │
             ┌────────┴────────┐
             │                 │
       ┌─────▼──────┐    ┌─────▼──────┐
       │   Kafka    │    │   Kafka    │
       │   Topics   │    │  Storage   │
       └────────────┘    └────────────┘
                             
                      ┌──────────────┐
                      │   Kafka UI   │
                      │    :8090     │
                      └──────────────┘
```

---

## ⚙️ Configuration Details

### 1. Apache Kafka

Kafka runs using the official `apache/kafka:4.1.0` Docker image.

Key configurations:

- Uses **KRaft mode**, eliminating the requirement for ZooKeeper.
- Configured as both a Broker and Controller.
- Uses PLAINTEXT communication for local development.
- Exposes port `9092` for Kafka clients.
- Creates three partitions by default for new topics.
- Persists messages using a Docker named volume.

### 2. Kafka Environment Variables

| Environment Variable | Description |
|---|---|
| `KAFKA_NODE_ID` | Unique Kafka node identifier |
| `KAFKA_PROCESS_ROLES` | Defines broker and controller roles |
| `KAFKA_LISTENERS` | Configures broker and controller listeners |
| `KAFKA_ADVERTISED_LISTENERS` | Addresses advertised to Kafka clients |
| `KAFKA_CONTROLLER_LISTENER_NAMES` | Defines the controller listener |
| `KAFKA_CONTROLLER_QUORUM_VOTERS` | Configures the KRaft controller quorum |
| `KAFKA_NUM_PARTITIONS` | Default partition count for new topics |
| `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR` | Replication factor for consumer offsets |
| `KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR` | Replication factor for transaction state |

### 3. Kafka UI

Kafka UI provides a web-based dashboard to monitor and manage Kafka.

**Features:**

- View Kafka clusters and brokers.
- Create, view, and manage topics.
- Browse messages inside topics.
- Monitor consumer groups.
- Inspect partitions and offsets.
- Monitor broker information.

**Dashboard:** http://localhost:8090

---

## 🚀 Getting Started

### Prerequisites

Make sure the following tools are installed:

- Docker Desktop
- Docker Compose V2

Verify the installation:

```bash
docker --version
docker compose version
```

### Step 1: Navigate to the Kafka Directory

```bash
cd kafka
```

### Step 2: Start Kafka and Kafka UI

```bash
docker compose up -d
```

This command starts both containers in detached mode.

### Step 3: Verify Running Containers

```bash
docker compose ps
```

Check Kafka logs:

```bash
docker compose logs -f kafka
```

Check Kafka UI logs:

```bash
docker compose logs -f kafka-ui
```

### Step 4: Access Kafka UI

Open your browser and navigate to:

**http://localhost:8090**

Kafka UI starts after Kafka passes its configured health check.

---

## 📨 Working with Kafka Topics

Kafka topics organize messages exchanged between microservices.

### 1. Create a Topic

```bash
docker exec -it krushi-kafka /opt/kafka/bin/kafka-topics.sh \
--create \
--topic order-events \
--bootstrap-server localhost:9092 \
--partitions 3 \
--replication-factor 1
```

### 2. List All Topics

```bash
docker exec -it krushi-kafka /opt/kafka/bin/kafka-topics.sh \
--list \
--bootstrap-server localhost:9092
```

### 3. Describe a Topic

```bash
docker exec -it krushi-kafka /opt/kafka/bin/kafka-topics.sh \
--describe \
--topic order-events \
--bootstrap-server localhost:9092
```

### 4. Produce Messages

```bash
docker exec -it krushi-kafka /opt/kafka/bin/kafka-console-producer.sh \
--topic order-events \
--bootstrap-server localhost:9092
```

Enter messages directly into the terminal.

### 5. Consume Messages

Open another terminal:

```bash
docker exec -it krushi-kafka /opt/kafka/bin/kafka-console-consumer.sh \
--topic order-events \
--from-beginning \
--bootstrap-server localhost:9092
```

Messages published to the topic will appear in the consumer terminal.

---

## 🔌 Connecting Spring Boot Microservices

Spring Boot microservices can communicate with Kafka using Spring Kafka.

### 1. Add Maven Dependency

Add the following dependency to the `pom.xml` of the microservice:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### 2. Configure application.properties

For Spring Boot applications running directly on the host machine:

```properties
# Kafka Bootstrap Server
spring.kafka.bootstrap-servers=localhost:9092

# Producer Configuration
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Consumer Configuration
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
```

### 3. Docker Network Configuration

For Spring Boot services running inside the same Docker network, use the Kafka service hostname:

```properties
spring.kafka.bootstrap-servers=kafka:9092
```

**Important:** The current configuration advertises `localhost:9092`, which is suitable for applications running on the host machine.

For container-based microservices, configure an additional internal Kafka listener and advertise `kafka:9092`.

---

## 💾 Data Persistence

Kafka stores its data using a Docker named volume.

```yaml
volumes:
  - kafka_data:/var/lib/kafka/data
```

This ensures that Kafka messages and metadata persist across container restarts and container recreation.

Inspect Docker volumes:

```bash
docker volume ls
```

**Note:** Removing the named volume will permanently delete the stored Kafka data.

---

## 🛑 Managing Kafka Containers

| Operation | Command |
|---|---|
| Start containers | `docker compose start` |
| Stop containers | `docker compose stop` |
| Start in background | `docker compose up -d` |
| View containers | `docker compose ps` |
| View logs | `docker compose logs -f` |
| Remove containers | `docker compose down` |
| Remove containers and volumes | `docker compose down -v` |

⚠️ **Warning:** `docker compose down -v` removes the Kafka named volume and permanently deletes its stored data.

---

## 🩺 Health Check

Kafka uses a Docker health check to verify broker availability.

Health check command:

```bash
/opt/kafka/bin/kafka-broker-api-versions.sh \
--bootstrap-server localhost:9092
```

Health check configuration:

| Parameter | Value |
|---|---|
| Interval | 10 seconds |
| Timeout | 10 seconds |
| Retries | 10 |

Kafka UI depends on Kafka becoming healthy before starting.

---

## 🔍 Troubleshooting

| Issue | Possible Solution |
|---|---|
| Kafka container exits | Check Kafka container logs |
| Kafka UI unavailable | Verify port `8090` and container status |
| Connection refused | Ensure Kafka is running and healthy |
| Topic not found | Verify topic name and Kafka cluster |
| Data lost after restart | Check whether the Docker volume was deleted |
| Microservice cannot connect | Verify bootstrap server and advertised listeners |
| Kafka UI cannot connect | Verify Kafka UI cluster configuration |

---

## 🔐 Production Considerations

The current configuration is intended for local development and testing.

Before deploying Kafka in production, consider:

- Enable SASL/SSL authentication.
- Configure multiple Kafka brokers.
- Increase replication factors.
- Configure appropriate message retention policies.
- Monitor broker health and consumer lag.
- Use secure networking and access controls.
- Configure internal and external listeners correctly.
- Implement error handling and dead-letter topics.
- Configure reliable message processing and retry mechanisms.

---

## 📌 Summary

The Kafka infrastructure provides an event-driven communication layer for the **Krushi Sarjana Microservices Project**.

It enables services such as UserService, OrderService, and PaymentService to exchange events asynchronously, reducing tight coupling and improving the overall scalability of the application.

