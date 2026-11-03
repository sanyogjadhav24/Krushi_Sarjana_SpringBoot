\# Kafka Setup – Krushi Sarjana



This directory contains the Docker Compose configuration for running \*\*Apache Kafka\*\* and \*\*Kafka UI\*\* for the Krushi Sarjana microservices architecture.



Kafka is used as an event streaming platform to enable asynchronous communication between different microservices, reducing direct service dependencies and improving scalability.



\## 🚀 Tech Stack



\- \*\*Apache Kafka:\*\* 4.1.0

\- \*\*Kafka UI:\*\* Provectus Kafka UI

\- \*\*Docker:\*\* Docker Compose

\- \*\*Kafka Architecture:\*\* KRaft Mode (Combined Broker + Controller)

\- \*\*Storage:\*\* Docker Named Volume



\## 📁 Project Structure



```text

kafka/

│

├── docker-compose.yml

└── README.md

```



\## 🏗️ Architecture



The setup consists of two Docker containers:



| Service | Container Name | Port | Purpose |

|---|---|---|---|

| Kafka | krushi-kafka | 9092 | Message broker |

| Kafka UI | krushi-kafka-ui | 8090 | Kafka monitoring dashboard |



\### Architecture Overview



```text

&#x20;                Krushi Sarjana Microservices

&#x20;                             |

&#x20;            +----------------+----------------+

&#x20;            |                |                |

&#x20;       UserService      OrderService     PaymentService

&#x20;            |                |                |

&#x20;            +----------------+----------------+

&#x20;                             |

&#x20;                        Apache Kafka

&#x20;                        localhost:9092

&#x20;                             |

&#x20;                   +---------+---------+

&#x20;                   |                   |

&#x20;              Kafka Topics        Kafka Storage

&#x20;                   |              (kafka\_data)

&#x20;                   |

&#x20;                Kafka UI

&#x20;             localhost:8090

```



\## ⚙️ Configuration Details



\### 1. Apache Kafka



\- Uses the official `apache/kafka:4.1.0` Docker image.

\- Runs in \*\*KRaft mode\*\*, eliminating the need for a separate ZooKeeper instance.

\- Configured as both a broker and controller.

\- Uses PLAINTEXT communication for local development.

\- Exposes port `9092` for Kafka clients.

\- Uses three default partitions for newly created topics.

\- Stores Kafka data in a persistent Docker volume.



\### 2. Kafka Environment Variables



| Variable | Description |

|---|---|

| KAFKA\_NODE\_ID | Unique Kafka node identifier |

| KAFKA\_PROCESS\_ROLES | Runs as broker and controller |

| KAFKA\_LISTENERS | Defines broker and controller listeners |

| KAFKA\_ADVERTISED\_LISTENERS | Address advertised to Kafka clients |

| KAFKA\_CONTROLLER\_LISTENER\_NAMES | Controller communication listener |

| KAFKA\_CONTROLLER\_QUORUM\_VOTERS | Defines the KRaft controller quorum |

| KAFKA\_NUM\_PARTITIONS | Default number of partitions |

| KAFKA\_OFFSETS\_TOPIC\_REPLICATION\_FACTOR | Replication factor for consumer offsets |

| KAFKA\_TRANSACTION\_STATE\_LOG\_REPLICATION\_FACTOR | Transaction state replication factor |



\### 3. Kafka UI



Kafka UI provides a web-based dashboard to monitor and manage Kafka.



Features:

\- View Kafka clusters.

\- Create and manage topics.

\- View messages inside topics.

\- Monitor consumer groups.

\- Inspect partitions and offsets.

\- Monitor broker information.



\*\*Dashboard URL:\*\*



http://localhost:8090



\## 🛠️ Getting Started



\### Prerequisites



Make sure the following are installed:



\- Docker Desktop

\- Docker Compose V2



Verify the installation:



```bash

docker --version

docker compose version

```



\### Step 1: Navigate to Kafka Directory



```bash

cd kafka

```



\### Step 2: Start Kafka and Kafka UI



```bash

docker compose up -d

```



This command starts both containers in detached mode.



\### Step 3: Verify Running Containers



```bash

docker compose ps

```



Check container logs:



```bash

docker compose logs -f kafka

```



For Kafka UI:



```bash

docker compose logs -f kafka-ui

```



\### Step 4: Access Kafka UI



Open the following URL in your browser:



http://localhost:8090



The Kafka UI container will start after Kafka passes its health check.



\## 📨 Working with Kafka Topics



Kafka topics are used to organize messages between microservices.



\### Create a Topic



```bash

docker exec -it krushi-kafka \\

/opt/kafka/bin/kafka-topics.sh \\

\--create \\

\--topic order-events \\

\--bootstrap-server localhost:9092 \\

\--partitions 3 \\

\--replication-factor 1

```



\### List All Topics



```bash

docker exec -it krushi-kafka \\

/opt/kafka/bin/kafka-topics.sh \\

\--list \\

\--bootstrap-server localhost:9092

```



\### Describe a Topic



```bash

docker exec -it krushi-kafka \\

/opt/kafka/bin/kafka-topics.sh \\

\--describe \\

\--topic order-events \\

\--bootstrap-server localhost:9092

```



\### Produce Messages



```bash

docker exec -it krushi-kafka \\

/opt/kafka/bin/kafka-console-producer.sh \\

\--topic order-events \\

\--bootstrap-server localhost:9092

```



Enter messages directly into the terminal.



\### Consume Messages



Open another terminal:



```bash

docker exec -it krushi-kafka \\

/opt/kafka/bin/kafka-console-consumer.sh \\

\--topic order-events \\

\--from-beginning \\

\--bootstrap-server localhost:9092

```



\## 🔌 Connecting Spring Boot Microservices



For Spring Boot applications running directly on the host machine, configure Kafka as follows:



\*\*application.properties\*\*



```properties

spring.kafka.bootstrap-servers=localhost:9092



spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer

spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer



spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer

spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer

```



For Spring Boot services running inside the same Docker network, use the Kafka service hostname instead:



```properties

spring.kafka.bootstrap-servers=kafka:9092

```



\*\*Important:\*\* The current Docker Compose configuration advertises `localhost:9092`, which is suitable for host-based clients. For container-based clients, configure an additional internal listener and advertise `kafka:9092`.



\## 💾 Data Persistence



Kafka data is stored using a Docker named volume:



```yaml

volumes:

&#x20; - kafka\_data:/var/lib/kafka/data

```



This ensures Kafka data survives container restarts and container recreation.



To inspect Docker volumes:



```bash

docker volume ls

```



\## 🛑 Managing Kafka Containers



\*\*Stop containers:\*\*



```bash

docker compose stop

```



\*\*Restart containers:\*\*



```bash

docker compose start

```



\*\*Stop and remove containers:\*\*



```bash

docker compose down

```



\*\*Remove containers and Kafka data:\*\*



```bash

docker compose down -v

```



⚠️ Warning: The last command permanently deletes the Kafka named volume and its stored data.



\## 🩺 Health Check



Kafka uses a Docker health check to verify broker availability.



```bash

/opt/kafka/bin/kafka-broker-api-versions.sh \\

\--bootstrap-server localhost:9092

```



Configuration:



\- Interval: 10 seconds

\- Timeout: 10 seconds

\- Retries: 10



Kafka UI depends on Kafka being healthy before starting.



\## 🔍 Troubleshooting



| Issue | Solution |

|---|---|

| Kafka container exits | Check Kafka logs |

| Kafka UI unavailable | Verify port 8090 and container status |

| Connection refused | Ensure Kafka is running and healthy |

| Topic not found | Verify topic name and Kafka cluster |

| Data lost | Check whether the Docker volume was deleted |

| Microservice cannot connect | Verify advertised listeners and bootstrap server |



\## 🔐 Production Considerations



This configuration is intended for local development and testing.



Before production deployment, consider:



\- Enable SASL/SSL authentication.

\- Configure multiple Kafka brokers.

\- Increase replication factors.

\- Configure appropriate retention policies.

\- Monitor broker health and consumer lag.

\- Use secure networking and access controls.

\- Configure external and internal listeners correctly.



\---



\*\*Maintained as part of the Krushi Sarjana Microservices Project.\*\*

