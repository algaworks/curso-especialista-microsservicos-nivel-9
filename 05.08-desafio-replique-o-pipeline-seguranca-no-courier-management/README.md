# 5.8 — Desafio: Replique o pipeline de segurança no Courier-Management

Aplicação do **Shift Left Security Model** no microsserviço `courier-management`, replicando o
que foi construído no `delivery-tracking` nas aulas 5.03 a 5.07.

Este diretório parte do estado entregue em
`04.11-desafio-replique-o-pipeline-de-qualidade-no-courier-management` (Shift Left Quality já
aplicado) e adiciona a camada de segurança.

---

## 1. Scan de CVEs com Trivy (aulas 5.03 / 5.04)

Novo stage `security_scan`, posicionado **entre o build e o push da imagem** — a imagem só chega
ao registry se passar na varredura:

```
build_image → security_scan → push_image
```

- `build_image` constrói e salva a imagem em `image.tar` (artifact), sem publicar
- `security_scan` carrega o tar, gera `trivy-report.json` (artifact, 3 dias) e roda o gate
  bloqueante `trivy image --exit-code 1 --severity CRITICAL,HIGH`
- `push_image` só executa se o gate passar

### 1.1 Correções de CVEs aplicadas no projeto

O primeiro scan acusou **24 vulnerabilidades CRITICAL/HIGH**. Correções aplicadas:

| Onde | Correção | CVEs endereçadas |
|---|---|---|
| `build.gradle` | Spring Boot `3.5.8` → `3.5.16` | `spring-boot`, `spring-data-commons`, `spring-expression`, `spring-webmvc`, `spring-kafka`, `tomcat-embed-core`, `jackson-databind`, `jackson-core`, `micrometer-core`, `kafka-clients` |
| `build.gradle` | `force 'at.yawk.lz4:lz4-java:1.10.1'` + regra `eachDependency` | CVE no `org.lz4:lz4-java` trazido pelo `spring-kafka` |
| `build.gradle` | `exclude` do `xstream` no eureka-client + `com.thoughtworks.xstream:xstream:1.4.21` | CVEs de desserialização no XStream |
| `build.gradle` | `force 'org.bouncycastle:bcprov-jdk18on:1.84'` | CVE CRITICAL no BouncyCastle |
| `build.gradle` | `ext['postgresql.version'] = '42.7.12'` | CVEs no driver JDBC do Postgres |
| `build.gradle` | `ext['httpcore5.version'] = '5.4.3'` | CVEs no HttpCore5 (transitivo do eureka-client) |
| `Dockerfile` | `apk update && apk upgrade --no-cache` | CVEs de SO no base image (`libexpat`, `p11-kit`, `p11-kit-trust`) |

> Nota: `postgresql` e `httpcore5` têm a versão fixada pelo BOM do Spring Boot através do plugin
> `io.spring.dependency-management`. Nesses dois casos o `resolutionStrategy.force` **não** tem
> efeito — a sobrescrita correta é pela property do BOM (`ext['<artefato>.version']`).

Resultado após as correções: **0 CVEs CRITICAL/HIGH** — o gate do Trivy passa.

## 2. Assinatura de imagens com Cosign (aulas 5.05 / 5.06 / 5.07)

Dois stages novos, após o push:

```
push_image → sign_image → verify_sign_image
```

- **`sign_image`** — decodifica `$COSIGN_PRIVATE_KEY` (base64) em `cosign.key` e executa
  `cosign sign --key cosign.key index.docker.io/$DOCKER_ORGANIZATION/$DOCKER_REPOSITORY_NAME:$NEXT_VERSION`
- **`verify_sign_image`** — decodifica `$COSIGN_PUBLIC_KEY` em `cosign.pub` e executa
  `cosign verify --key cosign.pub ...`, garantindo que **nada não-assinado siga para o deploy**

`COSIGN_YES: "true"` evita o prompt interativo no runner.

## 3. Pipeline completa (`courier-management/.gitlab-ci.yml`)

```
sonar_scan → prepare_version → test → build → build_image → security_scan
  → push_image → sign_image → verify_sign_image → semantic_release → sonar_sync
```

| Stage | Camada | Papel |
|---|---|---|
| `sonar_scan` | Qualidade | Quality Gate bloqueante no MR para `develop` |
| `test_project` | Qualidade | Testes com Testcontainers + gate de cobertura JaCoCo (80%) |
| `build_image` | Segurança | Constrói a imagem e a mantém local (`image.tar`) |
| `security_scan` | **Segurança** | Trivy — falha em CRITICAL/HIGH; publica `trivy-report.json` |
| `push_image` | Segurança | Publica somente imagem aprovada no scan |
| `sign_image` | **Segurança** | Assina a imagem com Cosign |
| `verify_sign_image` | **Segurança** | Verifica a assinatura antes do deploy |
| `semantic_release` | Entrega | Tag, CHANGELOG e release |
| `sonar_sync` | Qualidade | Sincroniza a análise em `develop` sem bloquear |

### Variáveis de CI necessárias

| Variável | Uso |
|---|---|
| `SONAR_TOKEN`, `SONAR_HOST_URL` | Análise SonarQube |
| `DOCKER_USERNAME`, `DOCKER_SECRET` | Login no Docker Hub (build/push/cosign) |
| `COSIGN_PRIVATE_KEY` | Chave privada Cosign em base64 (masked) |
| `COSIGN_PUBLIC_KEY` | Chave pública Cosign em base64 |
| `GITLAB_TOKEN` | Semantic Release |

Geração do par de chaves:

```bash
cosign generate-key-pair
base64 -i cosign.key | tr -d '\n'   # -> COSIGN_PRIVATE_KEY
base64 -i cosign.pub | tr -d '\n'   # -> COSIGN_PUBLIC_KEY
```

---

## 4. Validação executada localmente

```bash
cd courier-management
./gradlew clean test jacocoTestReport bootJar -Pversion=canary
docker build -t algaworks/courier-management:canary .
trivy image --exit-code 1 --no-progress --severity CRITICAL,HIGH algaworks/courier-management:canary
```

| Verificação | Resultado |
|---|---|
| Testes (Testcontainers: Postgres + Kafka) | 34 testes, todos passando |
| `jacocoTestCoverageVerification` (mínimo 80%) | 98,5% de cobertura de instruções — OK |
| `bootJar` + `docker build` | OK |
| `trivy image --severity CRITICAL,HIGH` | **0 vulnerabilidades** — gate aprovado |

Os stages `sign_image` e `verify_sign_image` dependem das chaves Cosign e do Docker Hub, portanto
foram validados apenas por configuração (não executados localmente).
