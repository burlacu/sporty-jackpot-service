# sporty-jackpot-service

Backend service for managing jackpot contributions and rewards. Built as part of the Sporty Group Backend Engineer assignment.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Design Decisions](#design-decisions)
- [Running the Service](#running-the-service)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Postman Collection](#postman-collection)
- [Future Improvements](#future-improvements)

---

## Architecture Overview

The service is structured around two flows: **bet ingestion** (synchronous HTTP → async Kafka) and **reward evaluation** (synchronous HTTP).

```
┌─────────────────────────────────────────────────────────────────┐
│                        REST API Layer                           │
│  BetController  JackpotController  ContributionController       │
└────────────┬────────────────┬──────────────────────────────────┘
             │                │
     ┌───────▼──────┐  ┌──────▼────────────────────────────────┐
     │  BetService  │  │  JackpotService  ContributionService   │
     │  (ingest)    │  │  RewardEvaluationService               │
     └───────┬──────┘  └──────┬────────────────────────────────┘
             │                │
     ┌───────▼──────┐  ┌──────▼──────────────┐
     │ KafkaProducer│  │  Strategy Layer      │
     │  jackpot-bets│  │  Contribution/Reward │
     └───────┬──────┘  └──────────────────────┘
             │
     ┌───────▼──────┐
     │  BetConsumer │  ← subscribes to jackpot-bets
     └───────┬──────┘
             │
     ┌───────▼──────────────────┐
     │  BetProcessingService    │
     │  → ContributionService   │
     └──────────────────────────┘
```

### Package Structure

```
com.sporty.jackpot
├── config/          Configuration properties and Kafka topic definitions
├── consumer/        Kafka listener (entry point only, no business logic)
├── controller/      REST controllers
├── dto/             Request/response data transfer objects
├── exception/       Domain exceptions, error codes, global handler
├── filter/          Correlation ID propagation filter
├── model/           JPA entities
├── publisher/       Kafka producer abstraction
├── repository/      Spring Data JPA repositories
├── reward/          Reward strategy interface and implementations
├── service/         Service interfaces and implementations
└── strategy/        Contribution strategy interface and implementations
```

### Data Model

```
Jackpot
  │── JackpotContribution  (one per processed bet, audit record)
  └── JackpotReward        (created on successful reward evaluation)

ProcessedBet               (idempotency guard, unique betId constraint)
```

---

## Technology Stack

| Concern           | Technology                       |
|-------------------|----------------------------------|
| Language          | Java 17                          |
| Framework         | Spring Boot 3.2.5                |
| Messaging         | Apache Kafka (Spring Kafka)      |
| Persistence       | Spring Data JPA / Hibernate      |
| Database (dev)    | H2 (in-memory)                   |
| Database (prod)   | PostgreSQL                       |
| Validation        | Jakarta Bean Validation          |
| Boilerplate       | Lombok                           |
| Build             | Maven                            |
| Testing           | JUnit 5, Mockito, AssertJ        |

---

## Design Decisions

### Strategy Pattern — Contribution Calculation

Jackpot contribution amount is calculated using one of three pluggable strategies, selected per jackpot via `ContributionType`:

| Type         | Behaviour                                          |
|--------------|----------------------------------------------------|
| `PERCENTAGE` | Fixed percentage of the bet stake                  |
| `FIXED`      | Fixed monetary amount per bet                      |
| `TIERED`     | Percentage varies based on current pool size       |

`ContributionStrategyFactory` resolves the correct implementation using an explicit switch expression. Adding a new type requires updating the factory and the enum.

### Strategy Pattern — Reward Evaluation

Reward evaluation is also fully pluggable, selected per jackpot via `RewardType`:

| Type                | Behaviour                                                   |
|---------------------|-------------------------------------------------------------|
| `FULL_POOL`         | Always wins; reward equals the entire pool                  |
| `FIXED`             | Always wins; reward is a configured fixed amount            |
| `PERCENTAGE`        | Always wins; reward is a percentage of the pool             |
| `FIXED_PROBABILITY` | Configurable flat win probability per evaluation            |
| `TIERED_PROBABILITY`| Win probability increases as the pool grows                 |

`RewardStrategyFactory` uses Spring's `Map<String, RewardStrategy>` injection keyed by bean name (`@Component("TYPE_NAME")`). Adding a new reward type requires no factory changes — only a new annotated class.

### Asynchronous Bet Processing

Bets are ingested via HTTP (`POST /api/v1/bets`), written to a `processed_bets` table for idempotency, then published to the `jackpot-bets` Kafka topic. `BetConsumer` picks up the event and delegates to `BetProcessingService`, which triggers `ContributionService`. The consumer contains no business logic.

### Idempotent Bet Ingestion

`ProcessedBet` has a unique constraint on `betId`. A `DataIntegrityViolationException` on duplicate insert is caught and returned as `ALREADY_PROCESSED` without re-publishing.

### Transactional Atomicity

Reward creation and jackpot pool reset execute within a single `@Transactional` method. If reward persistence fails, the pool is not reset. If either write fails, both roll back.

### RandomProvider Abstraction

Random number generation in `FixedProbabilityRewardStrategy` and `TieredProbabilityRewardStrategy` is isolated behind a `RandomProvider` interface. This makes probability logic fully testable without randomness.

### Correlation IDs

`CorrelationIdFilter` reads or generates an `X-Correlation-ID` on every request, places it in MDC, and echoes it in the response header. All log lines and error responses include the correlation ID for end-to-end traceability.

### Error Response Format

All errors return a consistent JSON structure:

```json
{
  "errorCode": "JACKPOT_NOT_FOUND",
  "message": "Jackpot not found with id: 10",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-19T10:00:00"
}
```

Validation errors additionally include a `details` map of field-level messages.

---

## Running the Service

### Prerequisites

- Java 17
- Maven 3.8+
- Apache Kafka (for full bet flow)
- PostgreSQL (for production profile)

### Development (H2, Kafka optional)

```bash
# Clone and build
git clone <repo-url>
cd sporty-jackpot-service

# Run with embedded H2 (no external dependencies needed for REST endpoints)
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

### With Kafka

Start Kafka locally using Docker (KRaft mode, no Zookeeper needed):

**Linux / Git Bash / macOS:**

```bash
docker run -d \
  --name kafka \
  -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_LOG_DIRS=/tmp/kraft-combined-logs \
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk \
  apache/kafka:3.7.0
```

**Windows PowerShell:**

```powershell
docker run -d `
  --name kafka `
  -p 9092:9092 `
  -e KAFKA_NODE_ID=1 `
  -e KAFKA_PROCESS_ROLES=broker,controller `
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 `
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 `
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER `
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT `
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 `
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 `
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 `
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 `
  -e KAFKA_LOG_DIRS=/tmp/kraft-combined-logs `
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk `
  apache/kafka:3.7.0
```

Then run the service:

```bash
mvn spring-boot:run
```

### Production (PostgreSQL)

```bash
export KAFKA_BOOTSTRAP_SERVERS=kafka:9092
export SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/jackpot
export SPRING_DATASOURCE_USERNAME=jackpot
export SPRING_DATASOURCE_PASSWORD=secret

mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Running Tests

```bash
# All tests (no Kafka or PostgreSQL required)
mvn test

# Specific layer
mvn test -Dtest="*StrategyTest"
mvn test -Dtest="*RepositoryTest"
mvn test -Dtest="*ControllerTest"
```

---

## Configuration

All values have sensible defaults. Override in `application.yml` or via environment variables.

```yaml
jackpot:
  contribution:
    percentage-rate: 5          # % of stake contributed (PERCENTAGE strategy)
    fixed-amount: 10            # fixed amount contributed (FIXED strategy)
    tiered-rates:               # pool-based tiers (TIERED strategy)
      - threshold: 10000
        rate: 10
      - threshold: 50000
        rate: 5
      - rate: 2                 # catch-all (pool >= 50,000)

  reward:
    fixed-amount: 500           # fixed payout (FIXED strategy)
    percentage-rate: 50         # % of pool paid out (PERCENTAGE strategy)
    probability-rate: 5         # win chance in % (FIXED_PROBABILITY strategy)
    tiered-probabilities:       # pool-based win probability (TIERED_PROBABILITY strategy)
      - threshold: 10000
        probability-rate: 1
      - threshold: 50000
        probability-rate: 5
      - threshold: 100000
        probability-rate: 10
      - probability-rate: 100   # guaranteed win above 100,000
```

---

## API Reference

All endpoints are prefixed with `/api/v1`. Pass `X-Correlation-ID` header for request tracing.

### Jackpots

#### Create a jackpot

```http
POST /api/v1/jackpots
Content-Type: application/json

{
  "initialPoolAmount": 1000.00,
  "contributionType": "PERCENTAGE",
  "rewardType": "TIERED_PROBABILITY"
}
```

```http
HTTP/1.1 201 Created

{
  "id": 1,
  "initialPoolAmount": 1000.00,
  "currentPoolAmount": 1000.00,
  "contributionType": "PERCENTAGE",
  "rewardType": "TIERED_PROBABILITY"
}
```

#### Get a jackpot

```http
GET /api/v1/jackpots/1
```

```http
HTTP/1.1 200 OK

{
  "id": 1,
  "initialPoolAmount": 1000.00,
  "currentPoolAmount": 1250.00,
  "contributionType": "PERCENTAGE",
  "rewardType": "TIERED_PROBABILITY"
}
```

#### List all jackpots

```http
GET /api/v1/jackpots
```

#### Update a jackpot

```http
PUT /api/v1/jackpots/1
Content-Type: application/json

{
  "contributionType": "TIERED",
  "rewardType": "FIXED_PROBABILITY",
  "currentPoolAmount": 1250.00
}
```

#### Delete a jackpot

```http
DELETE /api/v1/jackpots/1
```

```http
HTTP/1.1 204 No Content
```

---

### Bet Ingestion

#### Submit a bet

```http
POST /api/v1/bets
Content-Type: application/json

{
  "betId": 42,
  "userId": 7,
  "jackpotId": 1,
  "amount": 100.00
}
```

```http
HTTP/1.1 200 OK

{
  "betId": 42,
  "status": "ACCEPTED",
  "timestamp": "2026-06-19T10:00:00"
}
```

Submitting the same `betId` twice:

```http
HTTP/1.1 200 OK

{
  "betId": 42,
  "status": "ALREADY_PROCESSED",
  "timestamp": "2026-06-19T10:00:01"
}
```

---

### Contributions

#### Add a contribution manually

```http
POST /api/v1/jackpots/1/contributions
Content-Type: application/json

{
  "betId": 42,
  "userId": 7,
  "stakeAmount": 100.00
}
```

```http
HTTP/1.1 201 Created

{
  "id": 1,
  "betId": 42,
  "userId": 7,
  "jackpotId": 1,
  "stakeAmount": 100.00,
  "contributionAmount": 5.0000,
  "currentJackpotAmount": 1005.0000,
  "createdAt": "2026-06-19T10:00:00"
}
```

#### List contributions for a jackpot

```http
GET /api/v1/jackpots/1/contributions
```

#### Get a contribution by ID

```http
GET /api/v1/contributions/1
```

#### List contributions for a user

```http
GET /api/v1/users/7/contributions
```

---

### Reward Evaluation

#### Evaluate reward for a contribution

```http
POST /api/v1/jackpots/evaluate
Content-Type: application/json

{
  "contributionId": 1
}
```

Winner:

```http
HTTP/1.1 200 OK

{
  "winner": true,
  "rewardAmount": 1005.00
}
```

No win:

```http
HTTP/1.1 200 OK

{
  "winner": false,
  "rewardAmount": 0
}
```

When a jackpot is won, the pool resets to `initialPoolAmount` and a `JackpotReward` record is persisted. Both operations are atomic.

---

### Error Responses

```http
HTTP/1.1 404 Not Found

{
  "errorCode": "JACKPOT_NOT_FOUND",
  "message": "Jackpot not found with id: 99",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-19T10:00:00"
}
```

```http
HTTP/1.1 400 Bad Request

{
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-19T10:00:00",
  "details": {
    "stakeAmount": "Stake amount is required",
    "betId": "Bet ID is required"
  }
}
```

| Error Code              | HTTP Status | Cause                                  |
|-------------------------|-------------|----------------------------------------|
| `JACKPOT_NOT_FOUND`     | 404         | No jackpot with the given ID           |
| `CONTRIBUTION_NOT_FOUND`| 404         | No contribution with the given ID      |
| `VALIDATION_ERROR`      | 400         | Bean validation failure                |
| `INVALID_ARGUMENT`      | 400         | Unregistered strategy type or bad input|
| `INTERNAL_ERROR`        | 500         | Unexpected server error                |

---

## Postman Collection

A ready-to-use Postman collection is included at the root of the repository: **`sporty-jackpot.postman_collection.json`**.

### Import

In Postman: **Import** → select `sporty-jackpot.postman_collection.json`.

The collection uses a `baseUrl` variable (default `http://localhost:8080/api/v1`). `jackpotId` and `contributionId` are captured automatically by test scripts as you run the flow.

### Happy path (no Kafka required)

Run requests in this order:

1. **1.1 Create Jackpot** — creates a jackpot and captures `jackpotId`
2. **3.1 Add Contribution** — directly adds a contribution and captures `contributionId`
3. **4.1 Evaluate Reward** — evaluates win/loss; resets pool if won
4. **4.2 Verify Pool After Evaluation** — confirms the pool state

### Full Kafka flow

Requires Kafka running at `localhost:9092` (see [With Kafka](#with-kafka)).

1. **1.1 Create Jackpot**
2. **2.1 Submit Bet** — publishes to `jackpot-bets` topic; consumer processes asynchronously
3. **2.2 Submit Same Bet Again** — verifies idempotency (`ALREADY_PROCESSED`)
4. **1.2 Get Jackpot** — confirm pool has grown after consumer processes the event
5. **3.1 Add Contribution** — capture a `contributionId` for evaluation
6. **4.1 Evaluate Reward**

### Error cases

Section **5** covers expected error responses: 404 for unknown jackpot/contribution, 400 for validation failures and missing fields.

> **Note:** Always run **1.1 Create Jackpot** before any request that references `{{jackpotId}}`, otherwise the variable will be empty and the request will fail with a JSON parse error.

---

## Future Improvements

### Jackpot Lifecycle
- **Jackpot status** (`ACTIVE`, `SUSPENDED`, `COMPLETED`) to prevent contributions to inactive jackpots and cleanly close out won jackpots.
- **Scheduled jackpot draws** so evaluation runs automatically at configured intervals rather than being triggered per contribution.

### Resilience
- **Dead Letter Queue** for Kafka messages that fail processing, with retry and alerting.
- **Outbox pattern** for the bet publish step to guarantee at-least-once delivery even if the broker is temporarily unavailable.
- **Optimistic locking** on `Jackpot.currentPoolAmount` to prevent race conditions under concurrent contribution writes.

### Observability
- **Micrometer + Prometheus** metrics: pool size gauges, contribution counters, reward hit rate, processing latency histograms.
- **Distributed tracing** (OpenTelemetry) to propagate `X-Correlation-ID` across service boundaries.
- **Structured JSON logging** via Logstash encoder for log aggregation pipelines.

### Business Features
- **Per-jackpot strategy configuration** stored in the `Jackpot.configuration` JSON column, allowing different rates per jackpot rather than a single global config.
- **Multiple winners** per evaluation cycle with configurable winner count.
- **Reward history API** (`GET /api/v1/jackpots/{id}/rewards`) for transparency.
- **User contribution limits** to enforce responsible gambling rules.

### Testing
- **Integration tests** using Testcontainers (PostgreSQL + Kafka) for full end-to-end flow validation.
- **Contract tests** (Spring Cloud Contract) between this service and upstream bet producers.
