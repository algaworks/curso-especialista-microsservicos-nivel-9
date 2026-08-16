# 2.9 — Desafio: Pipeline no Courier-Management

Criação da pipeline do `courier-management` no GitLab CI, replicando o que foi construído no
`delivery-tracking` nas aulas 2.2 a 2.8.

Parte do estado entregue em `01.12-desafio-dockerfile-e-docker-compose-do-projeto-courier`
(Gradle + envs + Flyway + Actuator + Dockerfile + Docker Compose).

---

## 1. A pipeline (`courier-management/.gitlab-ci.yml`)

```
test → build → image_build_and_push
```

| Stage | Job | Imagem | O que faz |
|---|---|---|---|
| `test` | `test_project` | `gradle:8.8-jdk21-alpine` | `gradle test` e publica o relatório JUnit |
| `test` | `test_migration` | `alpine:latest` + service `postgres:17.5` | Sobe um Postgres como *service* e aplica a migration do Flyway nele |
| `build` | `build_project` | `gradle:8.8-jdk21-alpine` | `gradle clean bootJar`, publicando o `.jar` como artifact |
| `image_build_and_push` | `docker_build_and_push` | `docker:28-cli` + service `docker:28-dind` | Build da imagem e push para o Docker Hub |

### Decisões

**`image: gradle:8.8-jdk21-alpine` no topo** — a imagem padrão do pipeline já traz o Gradle 8.8 e o
JDK 21, então os jobs de teste e build não precisam instalar nada. Os jobs que fogem disso
(`test_migration`, `docker_build_and_push`) sobrescrevem a imagem localmente.

**`rules` em vez de `only`/`except`** — a pipeline só roda em push para `main` ou `develop`:

```yaml
rules:
  - if: $CI_COMMIT_BRANCH =~ /^(main|develop)$/ && $CI_PIPELINE_SOURCE == "push"
    when: always
```

Sem isso, cada push em branch de feature dispararia build e push de imagem à toa.

**Os dois jobs de `test` rodam em paralelo** — estão no mesmo stage, então o tempo do stage é o do
job mais lento, não a soma dos dois.

**`services` no `test_migration` (aula 2.6)** — o Postgres sobe como container auxiliar do job,
acessível pelo alias `database`. O `before_script` espera a porta 5432 abrir com `nc -z` antes de
seguir; sem essa espera o job é uma corrida perdida contra o boot do banco. Depois, a migration do
Flyway é aplicada com `psql -v ON_ERROR_STOP=1` — se o SQL tiver erro, o job falha.

**`docker:28-dind` no job de imagem (aula 2.7)** — o job roda em um container, então não existe
daemon Docker nele. O `dind` fornece esse daemon como service, e a comunicação é configurada pelas
variáveis:

```yaml
DOCKER_HOST: tcp://docker:2376
DOCKER_TLS_VERIFY: 1
DOCKER_TLS_CERTDIR: "/certs"
DOCKER_CERT_PATH: "/certs/client"
```

As variáveis de Docker ficaram no bloco `variables` global (e não dentro do job) porque a partir
do desafio 3.8 mais de um job precisa delas.

**Login por `--password-stdin`** — `echo "$DOCKER_SECRET" | docker login --password-stdin` evita que
o segredo apareça na linha de comando e vaze no log do job.

**Artifacts** — o relatório JUnit é publicado com `when: always` (interessa justamente quando o job
falha) e com `expire_in` curto, para não acumular storage no GitLab.

### Variáveis de CI necessárias

| Variável | Uso |
|---|---|
| `DOCKER_USERNAME` | Usuário do Docker Hub |
| `DOCKER_SECRET` | Token de acesso do Docker Hub (marcar como *masked*) |

## 2. Suíte de testes

Para o estágio de `test` significar alguma coisa, o projeto ganhou testes unitários de verdade —
antes só existiam os dois `@SpringBootTest` que vinham do projeto original:

| Teste | Cobre |
|---|---|
| `CourierTest` | `brandNew`, `assign`, `fulfill`, baixa de entrega inexistente e imutabilidade da coleção de entregas |
| `CourierPayoutServiceTest` | Cálculo do payout, escala de duas casas com `HALF_EVEN` e distância nula (parametrizado) |
| `CourierRegistrationServiceTest` | Cadastro, atualização e falha ao atualizar entregador inexistente |
| `CourierDeliveryServiceTest` | Atribuição ao entregador mais ocioso, ausência de entregador, baixa de entrega e entrega órfã |
| `KafkaDeliveriesMessageHandlerTest` | Roteamento dos eventos de integração e handler default |

Todos são unitários (JUnit 5 + Mockito + AssertJ) e **não dependem de infraestrutura**, então o job
`test_project` roda sem `services`.

Os dois testes herdados do projeto original (`CourierManagementApplicationTests` e
`CourierControllerTest`) foram removidos daqui: os dois são `@SpringBootTest` e exigiriam Postgres,
Kafka e Eureka dentro do job. Eles voltam no desafio 4.11, quando o Testcontainers entra no
projeto e passa a provisionar essa infraestrutura.

## 3. Validação executada localmente

```bash
cd courier-management
./gradlew clean test bootJar
```

| Verificação | Resultado |
|---|---|
| `gradle test` | **21 testes**, 0 falhas, 0 erros |
| `bootJar` → `build/libs/courier-management.jar` | OK |
| `docker build` + `docker compose up` | OK (herdado do desafio 1.12) |

O job `docker_build_and_push` depende de credenciais do Docker Hub, então foi validado apenas por
configuração — os mesmos comandos (`docker build`, `docker login`) já foram executados localmente
no desafio 1.12.
