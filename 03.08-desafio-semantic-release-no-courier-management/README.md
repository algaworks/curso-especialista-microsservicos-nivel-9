# 3.8 — Desafio: Semantic Release no Courier-Management

Aplicação do **Semantic Release** na pipeline do `courier-management`, replicando o que foi
construído no `delivery-tracking` nas aulas 3.2 a 3.7.

Parte do estado entregue em `02.09-desafio-pipeline-no-courier-management` (pipeline de
teste, build e push de imagem já funcionando).

---

## 1. Configuração do Semantic Release (`courier-management/.releaserc`)

```json
"tagFormat": "v${version}",
"branches": [ "main", { "name": "develop", "prerelease": "dev" } ]
```

- `main` gera release estável (`v1.4.0`)
- `develop` gera pré-release (`v1.5.0-dev.1`), então dá para publicar imagem de homologação sem
  queimar número de versão de produção

### Plugins e por que cada um está lá

| Plugin | Papel |
|---|---|
| `@semantic-release/commit-analyzer` | Lê os conventional commits e decide se a próxima versão é major, minor, patch — ou se não há release |
| `@semantic-release/release-notes-generator` | Monta as notas da release agrupadas por seção |
| `@semantic-release/exec` | `verifyReleaseCmd: echo ${nextRelease.version} > .VERSION` — **é essa linha que conecta o Semantic Release ao resto da pipeline** |
| `@semantic-release/changelog` | Mantém o `CHANGELOG.md` |
| `@semantic-release/git` | Commita o `CHANGELOG.md` de volta, com `[skip ci]` na mensagem para não disparar pipeline em loop |
| `@semantic-release/gitlab` | Cria a release no GitLab |

### `releaseRules` — o que gera versão

`feat` → minor, `fix`/`perf`/`refactor` → patch, breaking change → major. `docs`, `style`, `chore`,
`build`, `ci` e o escopo `no-release` **não** geram versão: commit de ajuste de pipeline não deveria
publicar uma imagem nova.

## 2. Versionamento no build (`build.gradle`)

```groovy
def resolvedVersion = findProperty('version')
version = (resolvedVersion && resolvedVersion != 'unspecified') ? resolvedVersion : 'canary'

bootJar {
    archiveBaseName = 'courier-management'
    archiveVersion = "${version}"
}
```

A versão sai do `build.gradle` e passa a vir de fora, por `-Pversion=`. O default `canary` faz o
build local e o de branch sem release continuarem funcionando.

> O idioma óbvio (`findProperty('version') ?: 'canary'`) **não funciona**: o Gradle já define
> `version` como a string `'unspecified'`, que é um valor verdadeiro, então o Elvis nunca cai no
> default e o build sem `-Pversion` gera `courier-management-unspecified.jar`. Daí a comparação
> explícita.

O artefato passa a ser `courier-management-<versão>.jar`. O Dockerfile já usa
`COPY build/libs/*.jar $JAR_NAME`, então não precisou de ajuste.

## 3. Pipeline (`courier-management/.gitlab-ci.yml`)

```
prepare_version → test → build → build_and_push → semantic_release
```

### 3.1 `prepare_version` — o stage que resolve o problema do "ovo e a galinha"

O `semantic_release` só pode rodar **no fim** (ele cria a tag e o CHANGELOG do que foi entregue),
mas o `build` precisa saber a versão **antes**. A solução é rodar o Semantic Release duas vezes:

```yaml
prepare_version:
  script:
    - semantic-release --dry-run --no-ci
    - |
      if [ ! -e .VERSION ]; then
        echo "canary" > .VERSION
      else
        echo "Arquivo .VERSION já existe."
      fi
    - echo "NEXT_VERSION=$(cat .VERSION)" >> build.env
  artifacts:
    reports:
      dotenv: build.env
```

- `--dry-run` calcula a próxima versão **sem** criar tag nem release
- o `@semantic-release/exec` escreve essa versão no arquivo `.VERSION`
- o fallback `canary` cobre o caso de não haver commit que gere release — sem ele, o `cat .VERSION`
  quebraria o job
- `artifacts:reports:dotenv` publica `NEXT_VERSION` como **variável de ambiente para todos os jobs
  seguintes** — é isso que faz `${NEXT_VERSION}` existir no `build_project` e no `build_and_push`

E o consumo, com o mesmo default por segurança:

```yaml
- gradle clean bootJar -Pversion=${NEXT_VERSION:-canary}
- docker build -t $DOCKER_ORGANIZATION/$DOCKER_REPOSITORY_NAME:${NEXT_VERSION:-canary} .
```

Assim a **tag do Git, a versão do jar e a tag da imagem Docker são sempre o mesmo número**.

### 3.2 Âncora YAML (aula 3.6)

A regra de execução estava repetida em todos os jobs. Virou âncora:

```yaml
.rules-standard: &rules-standard
  - if: $CI_COMMIT_BRANCH =~ /^(main|develop)$/ && $CI_PIPELINE_SOURCE == "push"
    when: always
```

E cada job usa `rules: *rules-standard`. A chave começa com `.`, então o GitLab a trata como job
oculto e não tenta executá-la. Mudar a regra agora é mudar em um lugar só.

### 3.3 Imagem própria do Semantic Release (aula 3.7)

Instalar o `semantic-release` e os sete plugins via `npm install -g` a cada execução custava tempo
em **dois** jobs. O `semantic.dockerfile` na raiz constrói a imagem com tudo pré-instalado:

```dockerfile
FROM node:22-alpine
RUN apk update && apk add --no-cache git && \
  npm install -g semantic-release@24.0.0 ...
```

Os jobs `prepare_version` e `semantic_release` passam a usar
`image: algaworks/devops-semantic-release:v1.0.1`, com as versões dos plugins fixadas — ninguém
acorda com a pipeline quebrada por um major novo de plugin.

### Variáveis de CI necessárias

| Variável | Uso |
|---|---|
| `GITLAB_TOKEN` | Semantic Release — criar tag, release e commitar o CHANGELOG (precisa de escopo `api` e `write_repository`) |
| `DOCKER_USERNAME`, `DOCKER_SECRET` | Login no Docker Hub |

## 4. Validação executada localmente

```bash
cd courier-management
./gradlew clean test bootJar -Pversion=1.2.3
```

| Verificação | Resultado |
|---|---|
| `gradle test` | 21 testes, 0 falhas, 0 erros |
| `bootJar -Pversion=1.2.3` | gera `build/libs/courier-management-1.2.3.jar` |
| `bootJar` sem `-Pversion` | gera `build/libs/courier-management-canary.jar` (fallback funcionando) |
| `.releaserc` | JSON válido |
| `.gitlab-ci.yml` | YAML válido, âncora resolvida |

Os jobs `prepare_version` e `semantic_release` dependem do `GITLAB_TOKEN` e do histórico de commits
do repositório remoto, então foram validados apenas por configuração.
