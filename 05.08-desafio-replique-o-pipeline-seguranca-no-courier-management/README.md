# 5.8 — Desafio: Replique o pipeline de segurança no Courier-Management

Aplicação do **Shift Left Security Model** no microsserviço `courier-management`, replicando o que
foi construído no `Delivery-Tracking` nas aulas 5.03 a 5.07.

Parte do estado entregue em
`04.11-desafio-replique-o-pipeline-de-qualidade-no-courier-management` (Shift Left Quality já
aplicado) e adiciona a camada de segurança.

---

## 1. Scan de CVEs com Trivy (aulas 5.03 / 5.04)

Novo stage `security_scan`, posicionado **entre o build e o push da imagem** — a imagem só chega
ao registry se passar na varredura:

```
build_image → security_scan → push_image
```

- `build_image` constrói e salva a imagem em `image.tar` (artifact), **sem publicar**
- `security_scan` carrega o tar, gera `trivy-report.json` (artifact, 3 dias) e roda o gate
  bloqueante `trivy image --exit-code 1 --severity CRITICAL,HIGH`
- `push_image` só executa se o gate passar

O relatório JSON é gerado com `--exit-code 0` e o gate roda em seguida com `--exit-code 1`: assim
o artifact fica disponível mesmo quando o job falha, que é justamente quando ele interessa.

### 1.1 Correções de CVEs aplicadas

O primeiro scan da imagem acusou **39 vulnerabilidades CRITICAL/HIGH**:

| Onde | Correção | O que resolveu |
|---|---|---|
| `build.gradle` | Spring Boot `3.5.3` → `3.5.16` | `spring-boot`, `spring-core`, `spring-expression`, `spring-webmvc`, `spring-data-commons`, `spring-kafka`, `tomcat-embed-core` (14 CVEs), `jackson-core`, `jackson-databind`, `micrometer-core`, `kafka-clients` |
| `build.gradle` | `force 'org.bouncycastle:bcprov-jdk18on:1.84'` | CVE no BouncyCastle (transitivo do eureka-client) |
| `build.gradle` | `eachDependency` trocando `org.lz4:lz4-java` por `at.yawk.lz4:lz4-java:1.10.1` | 2 CVEs no lz4-java, que vem do `spring-kafka` |
| `build.gradle` | `exclude` do `xstream` no eureka-client + `com.thoughtworks.xstream:xstream:1.4.21` | CVE de desserialização no XStream |
| `build.gradle` | `ext['postgresql.version'] = '42.7.12'` | 2 CVEs no driver JDBC do Postgres |
| `build.gradle` | `ext['httpcore5.version'] = '5.4.3'` | CVEs no HttpCore5 e HttpCore5-H2 |
| `Dockerfile` | `apk update && apk upgrade --no-cache` | CVEs de SO na imagem base: `libexpat`, `p11-kit`, `p11-kit-trust` |

Duas armadilhas que valem registrar:

**`resolutionStrategy.force` não funciona para tudo.** Para `postgresql` e `httpcore5` o `force` é
silenciosamente ignorado: o plugin `io.spring.dependency-management` roda a própria regra
`eachDependency` **depois** e reaplica a versão do BOM. Nesses casos a sobrescrita correta é a
property do BOM, `ext['<artefato>.version']`. O sintoma é cruel — o build passa, o Gradle não
reclama, e o Trivy continua acusando a mesma CVE.

**`org.lz4:lz4-java` está abandonado** e não tem versão corrigida publicada. A correção é migrar
para o fork mantido `at.yawk.lz4:lz4-java`, o que exige `useTarget` (troca de coordenada), e não
um simples `force` (troca de versão).

Resultado após as correções: **0 CVEs CRITICAL/HIGH** — o gate do Trivy passa.

> Vale a nota: as versões que o curso fixa envelheceram. O mesmo projeto que passava no gate na
> gravação hoje acusa dezenas de CVEs — não porque alguém errou, mas porque scanner de CVE é um
> alvo móvel. Isso é o próprio argumento do shift left: o gate roda a cada pipeline, não uma vez.

## 2. Assinatura de imagens com Cosign (aulas 5.05 / 5.06 / 5.07)

Dois stages novos, após o push:

```
push_image → sign_image → verify_sign_image
```

- **`sign_image`** — decodifica `$COSIGN_PRIVATE_KEY` (base64) em `cosign.key` e executa
  `cosign sign --key cosign.key index.docker.io/$DOCKER_ORGANIZATION/$DOCKER_REPOSITORY_NAME:$NEXT_VERSION`
- **`verify_sign_image`** — decodifica `$COSIGN_PUBLIC_KEY` em `cosign.pub` e executa
  `cosign verify --key cosign.pub ...`, garantindo que **nada não-assinado siga para o deploy**

`COSIGN_YES: "true"` evita o prompt interativo, que travaria o job esperando uma confirmação que
nunca vem.

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
| `COSIGN_PRIVATE_KEY` | Chave privada Cosign em base64 (**masked**) |
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
./gradlew clean test bootJar -Pversion=canary
docker build -t algaworks/courier-management:canary .
trivy image --exit-code 1 --no-progress --severity CRITICAL,HIGH algaworks/courier-management:canary
```

| Verificação | Resultado |
|---|---|
| Testes (Testcontainers: Postgres + Kafka) | 33 testes, 0 falhas |
| `jacocoTestCoverageVerification` (mínimo 80%) | OK |
| `bootJar` + `docker build` | OK |
| Trivy **antes** das correções | 39 vulnerabilidades CRITICAL/HIGH |
| Trivy **depois** das correções | **0 vulnerabilidades** — gate aprovado |

Resolução das versões conferida com `gradle dependencies`:

```
org.postgresql:postgresql                  -> 42.7.12
org.apache.httpcomponents.core5:httpcore5  5.3.6 -> 5.4.3
org.bouncycastle:bcprov-jdk18on            1.80  -> 1.84
org.lz4:lz4-java                           -> at.yawk.lz4:lz4-java:1.10.1
com.thoughtworks.xstream:xstream           1.4.21
org.apache.tomcat.embed:tomcat-embed-core  10.1.55
```

Os stages `sign_image` e `verify_sign_image` dependem das chaves Cosign e do Docker Hub, portanto
foram validados apenas por configuração.
