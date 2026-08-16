# 4.11 — Desafio: Replique o pipeline de qualidade no Courier-Management

Aplicação do **Shift Left Quality Model** no microsserviço `courier-management`, replicando o
que foi construído no `delivery-tracking` nas aulas 4.02 a 4.10.

O código-fonte do serviço vem de
[algaworks/algadelivery — microservices/courier-management](https://github.com/algaworks/algadelivery/tree/main/microservices/courier-management),
com as resoluções dos desafios anteriores já aplicadas (Gradle, envs, Flyway, Actuator, Dockerfile,
Docker Compose, pipeline no GitLab CI e Semantic Release).

---

## 1. Base do projeto (desafios anteriores)

| Item | O que foi feito |
|---|---|
| **Gradle** | Projeto convertido de Maven para Gradle 8.8 (`build.gradle`, `settings.gradle`, wrapper). `bootJar` publica `courier-management-<versão>.jar` |
| **Envs (12 fatores)** | `application.yml` lê `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_CONSUMER_GROUP_ID`, `SERVER_PORT`, `EUREKA_SERVER_URL` |
| **Flyway** | `ddl-auto` trocado de `update` para `validate`; schema versionado em `src/main/resources/db/migration/V1__Create_courier_tables.sql` (tabelas `courier` e `assigned_delivery`) |
| **Actuator** | `spring-boot-starter-actuator` + probes de liveness/readiness expostas em `/actuator/health` |
| **Dockerfile** | `eclipse-temurin:21-jre-alpine-3.23`, usuário não-root `spring`, `HEALTHCHECK` no Actuator, entrypoint com `JAVA_OPTS` calculado por percentual de RAM |
| **Docker Compose** | `docker-compose.yml` estendendo os serviços compartilhados (Postgres com `courierdb` e Kafka) |
| **Semantic Release** | `.releaserc` com conventional commits, `tagFormat v${version}`, prerelease `dev` em `develop` |

## 2. Shift Left Quality — o que foi implementado

### 2.1 Testcontainers (aulas 4.02 / 4.03)

Infra de teste em `src/test/java/.../utils/`:

- `TestcontainersPostgreSQL` — Postgres 16 com `@ServiceConnection`
- `TestcontainersKafka` — `ConfluentKafkaContainer` com `@ServiceConnection`
- `TestcontainersConfig` — agrega as duas para importar em qualquer teste

Classes-base por camada, no mesmo padrão do `delivery-tracking`:

- `AbstractPresentationIT` — `@SpringBootTest(RANDOM_PORT)` + RestAssured + Eureka desligado
- `AbstractPersistenceIT` — `@DataJpaTest` contra o Postgres real

Os ITs rodam contra Postgres real, então o **Flyway e o `ddl-auto: validate` são validados em
cada execução** — se a migration divergir das entidades, o build quebra.

### 2.2 Suíte de testes

| Teste | Tipo | Cobre |
|---|---|---|
| `CourierTest` | Unitário | `brandNew`, `assign` (+ evento `CourierAssigned`), `fulfill`, coleção imutável |
| `CourierCalculationServiceTest` | Unitário (parametrizado) | Cálculo do payout e arredondamento `HALF_EVEN` |
| `CourierRegistrationServiceTest` | Unitário (Mockito) | Cadastro e atualização, incluindo `DomainEntityNotFoundException` |
| `CourierDeliveryServiceTest` | Unitário (Mockito) | Atribuição, entrega já atribuída, ausência de entregador, baixa de entrega |
| `KafkaDeliveriesMessageHandlerTest` | Unitário (Mockito) | Roteamento dos eventos de integração e handler default |
| `ApiExceptionHandlerTest` | Unitário | Tradução de `DomainException` para `ProblemDetail` 422 |
| `CourierRepositoryIT` | Integração (Postgres) | Persistência do agregado, queries derivadas, ordenação do entregador mais ocioso |
| `CourierControllerIT` | Integração (Postgres + Kafka) | CRUD da API, validação de campos (400), 404, cálculo de payout |
| `CourierManagementApplicationTests` | Integração | Subida do contexto completo |

### 2.3 JaCoCo (aulas 4.05 / 4.06)

- `jacocoTestReport` gera XML (consumido pelo Sonar) e HTML
- `jacocoTestCoverageVerification` com **mínimo de 80%**, encadeado via `finalizedBy` — o build
  falha localmente e na pipeline se a cobertura cair
- Exclusões alinhadas com o `delivery-tracking`: `CourierManagementApplication`, `**/event/**`,
  `**/model/**`, `**/api/model/**`

### 2.4 SonarQube (aulas 4.04 / 4.08 / 4.09 / 4.10)

```groovy
sonar {
  properties {
    property "sonar.projectKey", "algadelivery_courier-management"
    property "sonar.organization", "algadelivery"
    property "sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml"
    property "sonar.coverage.exclusions", "**/event/**,**/model/**,**/api/model/**,**/CourierManagementApplication.java"
  }
}
```

`tasks.named('sonar') { dependsOn test }` garante que a análise sempre enxergue o relatório
de cobertura mais recente.

### 2.5 Pipeline (`courier-management/.gitlab-ci.yml`)

```
sonar_scan → prepare_version → test → build → build_and_push → semantic_release → sonar_sync
```

| Stage | Quando roda | Papel no shift left |
|---|---|---|
| `sonar_scan` | Merge Request para `develop` | **Quality Gate bloqueante** (`-Psonar.qualitygate.wait=true`) — barra o MR antes do merge |
| `prepare_version` | push em `main`/`develop` | Calcula a próxima versão (dry-run) e exporta `NEXT_VERSION` via dotenv |
| `test_project` | push em `main`/`develop` | `gradle test` com Testcontainers (DinD); publica JUnit report e JaCoCo como artifacts |
| `build_project` | push em `main`/`develop` | `bootJar` versionado |
| `build_and_push` | push em `main`/`develop` | Build e push da imagem para o Docker Hub |
| `semantic_release` | push em `main`/`develop` | Tag, CHANGELOG e release |
| `sonar_sync` | push em `develop` | Sincroniza o código analisado sem bloquear (`wait=false`) |

Variáveis de CI necessárias: `SONAR_TOKEN`, `SONAR_HOST_URL`, `DOCKER_USERNAME`, `DOCKER_SECRET`,
`GITLAB_TOKEN`.

---

## 3. Validação executada localmente

```bash
cd courier-management
./gradlew clean test jacocoTestReport bootJar -Pversion=canary
```

- **34 testes**, todos passando (Postgres e Kafka via Testcontainers)
- **Cobertura de instruções: 98,5%** — acima do gate de 80%, `jacocoTestCoverageVerification` OK
- `bootJar` e `docker build` concluídos com sucesso
