# 1.6 — Desafio: Conversão para Gradle do projeto Courier

Conversão do microsserviço `Courier-Management` de **Maven** para **Gradle**, replicando o que foi
feito no `Delivery-Tracking` na aula 1.5.

Código-fonte de origem:
[algaworks/ems-algadelivery-inicial — `Microservices/Courier-Management`](https://github.com/algaworks/ems-algadelivery-inicial)

---

## 1. O que foi convertido

| Antes (Maven) | Depois (Gradle) |
|---|---|
| `pom.xml` | `build.gradle` + `settings.gradle` |
| `spring-boot-starter-parent` | plugins `org.springframework.boot` + `io.spring.dependency-management` |
| `<properties><java.version>21` | `java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }` |
| `<properties><spring-cloud.version>` | `ext { set('springCloudVersion', '2025.0.0') }` |
| `<dependencyManagement>` importando o BOM do Spring Cloud | bloco `dependencyManagement { imports { mavenBom ... } }` |
| `maven-compiler-plugin` com `annotationProcessorPaths` do Lombok | `annotationProcessor 'org.projectlombok:lombok'` |
| `<optional>true</optional>` no Lombok | `configurations { compileOnly { extendsFrom annotationProcessor } }` |
| `spring-boot-maven-plugin` (repackage) | task `bootJar` |
| `mvnw` / `mvnw.cmd` / `.mvn/` | `gradlew` / `gradlew.bat` / `gradle/wrapper/` (Gradle 8.8) |

### Mapeamento das dependências

| `pom.xml` | `build.gradle` |
|---|---|
| `spring-boot-starter-data-jpa` | `implementation` |
| `spring-boot-starter-validation` | `implementation` |
| `spring-boot-starter-web` | `implementation` |
| `spring-cloud-starter-netflix-eureka-client` | `implementation` |
| `spring-kafka` | `implementation` |
| `lombok` (`optional`) | `implementation` + `annotationProcessor` |
| `postgresql` (`runtime`) | `runtimeOnly` |
| `spring-kafka-test` (`test`) | `testImplementation` |
| `spring-boot-starter-test` (`test`) | `testImplementation` |
| `rest-assured` `5.5.5` (`test`) | `testImplementation` com a versão explícita |

### Decisões

- **Spring Boot mantido em `3.5.3`**: a conversão é de build, não de versão. O upgrade só acontece
  no desafio 5.8, e por um motivo concreto (correção de CVEs).
- **`archiveFileName = 'courier-management.jar'`**: nome de artefato fixo, no mesmo padrão do
  `Delivery-Tracking`, para o Dockerfile do desafio 1.12 não depender da versão.
- **`-parameters` no `compileJava` e no `compileTestJava`**: o Maven já ligava isso via
  `spring-boot-starter-parent`; no Gradle é explícito. Sem a flag, o binding de `@PathVariable` e
  `@RequestParam` sem nome explícito quebra em runtime.
- **`rest-assured` com versão explícita**: é a única dependência do `pom.xml` que declara versão,
  porque não é gerenciada pelo BOM do Spring Boot. No Gradle continua igual.
- **`mavenLocal()` antes de `mavenCentral()`**: mesmo `repositories` do `Delivery-Tracking`.
- Os arquivos do Maven (`pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/`) foram **removidos** — a conversão
  está concluída e manter os dois build systems convivendo só gera ambiguidade.

## 2. Código da aplicação

O `src/` **não foi alterado** — a conversão é só de build. Os arquivos são idênticos aos do
repositório de origem, incluindo os dois testes que já vinham com o projeto
(`CourierManagementApplicationTests` e `CourierControllerTest`).

## 3. Validação executada localmente

```bash
cd courier-management
./gradlew clean bootJar compileTestJava
```

| Verificação | Resultado |
|---|---|
| `compileJava` (main) | OK |
| `compileTestJava` | OK |
| `bootJar` → `build/libs/courier-management.jar` | OK (95 MB) |

> `./gradlew test` ainda depende de Postgres e Kafka locais: os dois testes herdados do projeto
> original são `@SpringBootTest` e sobem o contexto completo. Isso é resolvido no desafio 4.11,
> quando o Testcontainers entra no projeto.
