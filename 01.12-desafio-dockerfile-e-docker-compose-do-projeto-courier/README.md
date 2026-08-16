# 1.12 — Desafio: Dockerfile e Docker Compose do projeto Courier

Containerização do `courier-management`, replicando o que foi feito no `Delivery-Tracking` nas
aulas 1.10 e 1.11.

Parte do estado entregue em `01.06-desafio-conversao-para-gradle-do-projeto-courier` (projeto já
em Gradle).

---

## 1. Pré-requisitos aplicados (aulas 1.7, 1.8 e 1.9)

Não dá para containerizar um serviço com host de banco escrito no `application.yml` nem colocar um
`HEALTHCHECK` sem endpoint de saúde. Então, antes do Dockerfile, o conteúdo das aulas 1.7 a 1.9
entrou no projeto:

| Aula | O que foi feito |
|---|---|
| **1.7 — envs** | `application.yml` passou a ler `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_CONSUMER_GROUP_ID`, `SERVER_PORT`, `EUREKA_SERVER_URL` e `ACTUATOR_*`, cada um com default para o desenvolvimento local |
| **1.8 — Flyway** | `ddl-auto` trocado de `update` para `validate`; schema versionado em `src/main/resources/db/migration/V1__Create_courier_tables.sql` |
| **1.9 — Actuator** | `spring-boot-starter-actuator` + probes de liveness/readiness em `/actuator/health` |

A migration cria exatamente o que as entidades mapeiam:

- `courier` — `id`, `name`, `phone`, `fulfilled_deliveries_quantity`, `pending_deliveries_quantity`,
  `last_fulfilled_delivery_at`
- `assigned_delivery` — `id`, `assigned_at`, `courier_id` (FK)
- índice em `last_fulfilled_delivery_at`, que é a coluna usada pelo
  `findTop1ByOrderByLastFulfilledDeliveryAtAsc`
- check constraints garantindo que os contadores não fiquem negativos

## 2. Dockerfile

`courier-management/Dockerfile` — mesmo padrão do `Delivery-Tracking`:

| Decisão | Motivo |
|---|---|
| `FROM eclipse-temurin:21-jre-alpine-3.23` | Só o JRE (não o JDK) e base Alpine — imagem final pequena |
| `addgroup -S spring && adduser -S spring` + `USER spring` | O processo **não roda como root** |
| `apk add --no-cache wget tzdata` + `TZ=America/Sao_Paulo` | `wget` para o healthcheck; timezone correta nos logs e nos campos `OffsetDateTime` |
| `COPY build/libs/*.jar $JAR_NAME` | Não acopla o Dockerfile ao nome versionado do artefato |
| `COPY --chmod=777 docker/docker-entrypoint.sh .` | Entrypoint executável sem `RUN chmod` extra |
| `HEALTHCHECK ... /actuator/health \| grep -i UP` | Usa a probe da aula 1.9 — o Docker só considera o container saudável quando a aplicação responde |
| `ENTRYPOINT ["./docker-entrypoint.sh"]` | Shell script permite calcular `JAVA_OPTS` antes de subir a JVM |

O `docker/docker-entrypoint.sh` define, quando `JAVA_OPTS` não vem de fora:

```sh
JAVA_OPTS="-XX:MinRAMPercentage=10.0 -XX:MaxRAMPercentage=75.0"
```

Heap por **percentual da RAM do container**, e não valor fixo — assim a JVM respeita o limite de
memória do container em qualquer ambiente.

## 3. Docker Compose

São dois arquivos, no mesmo esquema do `Delivery-Tracking`:

- **`docker-compose.yml` (raiz)** — a infraestrutura compartilhada entre os microsserviços, igual à
  do repositório do curso: Postgres 17.5, pgAdmin, Kafka 4.1.1 (KRaft) e Kafka UI, todos na rede
  `local-network`
- **`courier-management/docker-compose.yml`** — o stack do serviço, que herda a infra com `extends`:

```yaml
postgres:
  extends:
    file: ../docker-compose.yml
    service: postgres
  environment:
    POSTGRES_DB: courierdb
```

O `extends` evita duplicar a definição do Postgres e do Kafka; aqui só se sobrescreve o que é
específico do courier — o `POSTGRES_DB` e o serviço `app`.

O `app` recebe por env exatamente as variáveis criadas na aula 1.7 e depende de `postgres` e
`kafka`. Ele fala com o broker pelo listener **interno** (`kafka:9090`); o `9092` é o listener
externo, para quem acessa do host. O `healthcheck` do compose espelha o do Dockerfile.

> A aplicação sobe em `8081` para **não colidir com o `Delivery-Tracking`**, que usa `8080`.

## 4. Validação executada localmente

```bash
cd courier-management
./gradlew clean bootJar
docker compose up -d --build
```

| Verificação | Resultado |
|---|---|
| `bootJar` → `build/libs/courier-management.jar` | OK |
| `docker compose config` | Projeto válido |
| `docker compose up` (postgres + kafka + app) | Container `app` chega a `healthy` |
| Migration do Flyway | `courier`, `assigned_delivery` e `flyway_schema_history` criadas no `courierdb` |
| `GET /actuator/health` | `{"status":"UP","groups":["liveness","readiness"]}` |
| `POST /api/v1/couriers` | `201` com o entregador persistido |

Como o `ddl-auto` está em `validate`, a subida saudável do container também prova que a migration
do Flyway bate com o mapeamento das entidades.
