# 4.11 — Desafio: Replique o pipeline de qualidade no Courier-Management

Aplicação do **Shift Left Quality Model** no microsserviço `courier-management`, replicando o que
foi construído no `Delivery-Tracking` nas aulas 4.02 a 4.10.

Parte do estado entregue em `03.08-desafio-semantic-release-no-courier-management` (Gradle, envs,
Flyway, Actuator, Dockerfile, Docker Compose, pipeline no GitLab CI e Semantic Release).

---

## 1. Testcontainers (aulas 4.02 / 4.03)

Infra de teste em `src/test/java/.../utils/`:

- `TestcontainersPostgreSQL` — Postgres 16 com `@ServiceConnection`
- `TestcontainersKafka` — `ConfluentKafkaContainer` com `@ServiceConnection`
- `TestcontainersConfig` — agrega as duas para importar em qualquer teste

Classes-base por camada, no mesmo padrão do `Delivery-Tracking`:

- `AbstractPresentationIT` — `@SpringBootTest(RANDOM_PORT)` + RestAssured, com as autoconfigurações
  de Eureka e LoadBalancer desligadas
- `AbstractPersistenceIT` — `@DataJpaTest` contra o Postgres real
  (`@AutoConfigureTestDatabase(replace = NONE)`, senão o Spring troca o container por um H2)

Perfil `test` em `src/test/resources/application.yml`, com o grupo `test-env` desligando discovery
e load balancer.

Os ITs rodam contra Postgres real, então o **Flyway e o `ddl-auto: validate` são validados a cada
execução** — se a migration divergir das entidades, o build quebra.

> **Versão do Testcontainers:** o `Delivery-Tracking` fixa `1.20.4`. Aqui as dependências entram
> **sem versão**, deixando o BOM do Spring Boot resolver (hoje `1.21.2`). Motivo concreto: a `1.20.4`
> negocia a API `1.32` do Docker, e o Docker Engine 29 recusa qualquer coisa abaixo de `1.40`
> (`client version 1.32 is too old`). Fixar a versão do Testcontainers é fixar um problema com data
> para aparecer.

## 2. Suíte de testes

| Teste | Tipo | Cobre |
|---|---|---|
| `CourierTest` | Unitário | `brandNew`, `assign`, `fulfill`, baixa de entrega inexistente e imutabilidade da coleção |
| `CourierPayoutServiceTest` | Unitário (parametrizado) | Cálculo do payout, escala `HALF_EVEN` e distância nula |
| `CourierRegistrationServiceTest` | Unitário (Mockito) | Cadastro, atualização e entregador inexistente |
| `CourierDeliveryServiceTest` | Unitário (Mockito) | Atribuição, ausência de entregador, baixa e entrega órfã |
| `KafkaDeliveriesMessageHandlerTest` | Unitário (Mockito) | Roteamento dos eventos de integração e handler default |
| `CourierRepositoryIT` | Integração (Postgres) | Persistência do agregado, `findByPendingDeliveries_id` e ordenação do entregador mais ocioso |
| `CourierControllerIT` | Integração (Postgres + Kafka) | POST/PUT/GET da API, validação (400), 404 e cálculo de payout |
| `CourierManagementApplicationTests` | Integração | Subida do contexto completo |

Dois pontos que valem o comentário:

- **`shouldCalculatePayoutFee`** — o endpoint `/payout-calculation` falha de propósito em ~50% das
  chamadas (`if (Math.random() < 0.5) throw new RuntimeException()`). É o cenário que o
  `Delivery-Tracking` usa para exercitar retry e circuit breaker. O teste insiste até obter uma
  resposta bem-sucedida e só então valida o valor calculado.
- **`shouldReturnTheCourierWhoFulfilledADeliveryLongestAgo`** — a query ordena por
  `last_fulfilled_delivery_at` ascendente, e no Postgres `NULL` vai para o **fim** nessa ordenação.
  Por isso o teste compara dois entregadores que já concluíram entregas, e não um que nunca
  concluiu nenhuma.

## 3. JaCoCo (aulas 4.05 / 4.06)

- `jacocoTestReport` gera XML (consumido pelo Sonar) e HTML
- `jacocoTestCoverageVerification` com **mínimo de 80%**, encadeado via `finalizedBy` — o build
  falha localmente e na pipeline se a cobertura cair
- Exclusões idênticas às do `Delivery-Tracking`: `CourierManagementApplication`, `**/event/**`,
  `**/model/**`, `**/api/model/**`

> A exclusão `**/model/**` tira o agregado `Courier` da métrica, mesmo ele sendo o que mais tem
> regra de negócio no serviço — e ele **está** coberto pelo `CourierTest` e pelo `CourierRepositoryIT`.
> Mantive a lista igual à do `Delivery-Tracking` porque o desafio é replicar o pipeline; num projeto
> próprio eu excluiria só `api/model` e os eventos.

## 4. SonarQube (aulas 4.04 / 4.08 / 4.09 / 4.10)

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

`tasks.named('sonar') { dependsOn test }` garante que a análise sempre enxergue o relatório de
cobertura mais recente.

## 5. Pipeline (`courier-management/.gitlab-ci.yml`)

```
sonar_scan → prepare_version → test → build → build_and_push → semantic_release → sonar_sync
```

| Stage | Quando roda | Papel no shift left |
|---|---|---|
| `sonar_scan` | Merge Request para `develop` | **Quality Gate bloqueante** (`-Psonar.qualitygate.wait=true`) — barra o MR antes do merge |
| `prepare_version` | push em `main`/`develop` | Calcula a próxima versão (dry-run) e exporta `NEXT_VERSION` via dotenv |
| `test_project` | push em `main`/`develop` | `gradle test` com Testcontainers (DinD); publica JUnit e JaCoCo como artifacts |
| `build_project` | push em `main`/`develop` | `bootJar` versionado |
| `build_and_push` | push em `main`/`develop` | Build e push da imagem para o Docker Hub |
| `semantic_release` | push em `main`/`develop` | Tag, CHANGELOG e release |
| `sonar_sync` | push em `develop` | Sincroniza a análise sem bloquear (`wait=false`) |

O `test_project` ganhou o service `docker:28-dind` — é ele que o Testcontainers usa para subir
Postgres e Kafka dentro do job.

Variáveis de CI necessárias: `SONAR_TOKEN`, `SONAR_HOST_URL`, `DOCKER_USERNAME`, `DOCKER_SECRET`,
`GITLAB_TOKEN`.

---

## 6. Validação executada localmente

```bash
cd courier-management
./gradlew clean test jacocoTestReport
```

| Verificação | Resultado |
|---|---|
| `gradle test` (Testcontainers: Postgres + Kafka) | **33 testes**, 0 falhas, 0 erros |
| `jacocoTestCoverageVerification` (mínimo 80%) | **100%** de instruções nas classes medidas — OK |
| `bootJar` | OK |

Classes medidas pelo JaCoCo (as demais estão na lista de exclusão):
`CourierController`, `CourierDeliveryService`, `CourierPayoutService`, `CourierRegistrationService`
e `KafkaDeliveriesMessageHandler` — todas em 100%.

Os stages de Sonar dependem de `SONAR_TOKEN`/`SONAR_HOST_URL`, então foram validados apenas por
configuração.

### Nota sobre Docker Desktop no macOS

Se o `gradle test` falhar com `Could not find a valid Docker environment`, o motivo é o
`/var/run/docker.sock` do Docker Desktop apontar para o socket da **CLI**, que responde `400` ao
`/info`, e o docker-java negociar uma versão de API que o Engine recusa. Nesse caso:

```bash
export DOCKER_HOST="unix://$HOME/Library/Containers/com.docker.docker/Data/docker.raw.sock"
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
export DOCKER_API_VERSION=1.44
```

O `build.gradle` repassa `DOCKER_API_VERSION` para o Testcontainers (`systemProperty 'api.version'`)
**somente quando a variável existe** — na pipeline, onde o `docker:dind` funciona sem isso, nada
muda.
