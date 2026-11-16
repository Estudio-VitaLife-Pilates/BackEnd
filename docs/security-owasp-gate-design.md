# Design: Gate de Segurança (SonarCloud) + Rastreabilidade OWASP

**Data:** 2026-11-16
**Repos afetados:** `BackEnd` (este repositório) e `FrontEnd` (`Estudio-VitaLife-Pilates/FrontEnd`)
**Status:** Aprovado para implementação

## Contexto

Hoje os dois repositórios têm GitHub Actions apenas para deploy (push em `develop`) e
auto-criação de PR a partir de branches `feature/*`/`feat/*`. Não existe nenhum workflow
disparado em `pull_request`, nem análise estática de segurança. O `BackEnd` já tem um
conjunto de controles de segurança (`SecurityConfiguracao.java`) com comentários pontuais
referenciando OWASP, mas sem rastreabilidade formal nem proteção automática contra
regressão.

Este design adiciona um gate de qualidade/segurança via SonarCloud em PRs para `develop`,
e conecta esse gate aos conceitos OWASP Top 10:2021 já aplicados no projeto.

## Decisões tomadas

| Decisão | Escolha |
|---|---|
| Hospedagem do Sonar | SonarCloud (SaaS) |
| Repos no escopo | `BackEnd` e `FrontEnd` (não inclui `Infraestrutura`) |
| Critério do Quality Gate | Somente segurança, permissivo no resto (sem exigir cobertura mínima agora) |
| Enforcement | Bloqueia merge (check obrigatório na branch protection de `develop`) |
| Integração OWASP | Documentação de rastreabilidade + revisão formal dos Security Hotspots existentes + resumo automático por categoria OWASP no comentário do PR |

## Quality Gate — "Security Gate"

Gate customizado no SonarCloud, aplicado aos dois projetos (`vitalife-backend`,
`vitalife-frontend`), com condições avaliadas em **New Code**:

- `Security Rating` pior que `A` → falha (equivalente a "zero vulnerabilidades novas")
- `Security Hotspots Reviewed` < 100% → falha

Sem condições de cobertura, duplicação, confiabilidade ou manutenibilidade nesta fase.
Migrar para o gate padrão "Sonar way" (que adiciona essas condições) é um passo futuro,
não faz parte deste design.

## Mapeamento OWASP Top 10:2021 ↔ controle no código ↔ proteção automática

| Categoria OWASP | Controle existente | Onde | Proteção automática |
|---|---|---|---|
| A01 – Broken Access Control | `authorizeHttpRequests` com whitelist (`URLS_PERMITIDAS`), `@EnableMethodSecurity`/`@PreAuthorize` | `SecurityConfiguracao.java` | Regras Sonar taggeadas `owasp-a1`; gate falha se novo bug/vulnerabilidade de controle de acesso aparecer |
| A02 – Cryptographic Failures | `BCryptPasswordEncoder`, JWT assinado (`jjwt`) | `SecurityConfiguracao.java`, `GerenciadorTokenJwt.java` | Regras Sonar taggeadas `owasp-a2` (ex: algoritmo fraco, chave hardcoded) |
| A03 – Injection | Spring Data JPA (queries parametrizadas) + Bean Validation (`@NotBlank`, `@Size` etc.) nos DTOs | `dto/**/*Dto.java` | Regras Sonar taggeadas `owasp-a3` (SQL/command injection) |
| A05 – Security Misconfiguration | CSP (`default-src 'self'`), CORS com origem explícita | `SecurityConfiguracao.java` | Security Hotspots de configuração (CSP, CORS, headers) |
| A07 – Identification/Auth Failures | JWT stateless, `AutenticacaoProvider` customizado | `SecurityConfiguracao.java`, `AutenticacaoFilter.java` | Regras Sonar taggeadas `owasp-a7` |

### Gaps conhecidos (fora do escopo deste design, só documentados)

- **A05**: `/actuator/*` está liberado como público (`permitAll`). Risco aceito/pendente —
  decisão de produto, não corrigido aqui.
- **A04 (Insecure Design)**: não há rate limiting no endpoint de login
  (`/usuarios/login`), o que facilita brute-force. Risco aceito/pendente.

Esses dois pontos entram no `docs/security-owasp.md` como "conhecido, não mitigado" para
não serem confundidos com lacunas do gate.

## Componentes a criar

### BackEnd (este repo)

- `docs/security-owasp.md` — tabela de rastreabilidade (conteúdo acima) + os dois gaps.
- `.github/workflows/sonarqube.yml` — dispara em `pull_request` (`opened`, `synchronize`,
  `reopened`) contra `develop`. Steps: checkout (`fetch-depth: 0`), setup-java 21,
  `./mvnw -B verify` (gera relatório JaCoCo já configurado no `pom.xml`), depois
  `./mvnw -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar` com
  `-Dsonar.projectKey=vitalife-backend -Dsonar.organization=<org> -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=${{ secrets.SONAR_TOKEN }}`.
  Usa `sonar.qualitygate.wait=true` para o job falhar se o gate falhar.
- `scripts/owasp-pr-summary.sh` — chamado como último step do workflow. Recebe
  `projectKey`, `pullRequestNumber`, `SONAR_TOKEN` via env. Chama:
  - `GET /api/issues/search?componentKeys=<key>&pullRequest=<pr>&resolved=false&facets=owaspTop10-2021`
  - `GET /api/hotspots/search?projectKey=<key>&pullRequest=<pr>&status=TO_REVIEW`

  Monta uma tabela markdown "achados novos por categoria OWASP" e publica com
  `gh pr comment <pr> --body-file <tmp>` (reusa o padrão de autenticação via
  `GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}` já usado em `auto-pr-to-develop.yml`). Se a
  busca não retornar nenhum achado novo, posta "Nenhum achado novo de segurança
  (OWASP) neste PR." em vez de uma tabela vazia.

### FrontEnd (repo separado)

- `sonar-project.properties` — `sonar.projectKey=vitalife-frontend`,
  `sonar.organization=<org>`, `sonar.sources=src`.
- `.github/workflows/sonarqube.yml` — dispara em `pull_request` contra `develop`. Steps:
  checkout (`fetch-depth: 0`), setup-node 22, `npm ci`, `npm run build`, depois
  `SonarSource/sonarqube-scan-action@v4` com `SONAR_TOKEN` do secret do repo.
- `scripts/owasp-pr-summary.sh` — mesma lógica do BackEnd, path ajustado ao padrão de
  scripts já usado neste repo (`scripts/deploy-frontend.sh`).

## Fluxo

1. PR aberto/atualizado contra `develop` em qualquer um dos dois repos.
2. Workflow builda, testa (quando há testes) e roda o scanner Sonar.
3. SonarCloud avalia o "Security Gate" sobre o código novo do PR.
4. Script `owasp-pr-summary.sh` busca os achados novos do PR, agrupa por categoria OWASP
   e comenta no PR.
5. Branch protection de `develop` exige o check `SonarCloud Code Analysis` (ou equivalente)
   como obrigatório — PR só mescla se o gate passar.

## Tratamento de erros

- `SONAR_TOKEN` ausente/vazio → o step de scan falha imediatamente com mensagem clara
  (comportamento padrão do scanner), antes de tentar o resumo OWASP.
- API do Sonar retorna vazio (projeto novo, PR sem achados) → script posta mensagem de
  "nenhum achado novo", não uma tabela vazia confusa.
- Chamada à API do Sonar falha (timeout, 5xx) → o script loga um aviso e sai com sucesso
  sem comentar; isso **não** derruba o job, porque quem decide o merge é o gate (já
  avaliado no step anterior), não o comentário informativo.

## Ações manuais (fora do que será implementado em código)

1. Criar organização no SonarCloud vinculada ao GitHub, importar os dois repositórios.
2. Criar o Quality Gate customizado "Security Gate" com as duas condições acima e
   aplicá-lo aos dois projetos.
3. Gerar `SONAR_TOKEN` e cadastrar como secret em cada repositório.
4. Rodar o primeiro scan e revisar manualmente os Security Hotspots pré-existentes
   (CSRF desabilitado, CORS, H2 console) como "Revisado → Seguro", com a justificativa
   já documentada nos comentários do código.
5. Marcar o check do Sonar como obrigatório na branch protection de `develop` em cada
   repositório.

## Teste de validação

Abrir um PR de teste introduzindo um problema de categoria conhecida (ex: CORS com `*` e
`allowCredentials=true`, ou um endpoint novo sem `authorizeHttpRequests`) e confirmar que:

1. O Security Gate falha e o check aparece como obrigatório bloqueando o merge.
2. O comentário do PR lista o achado na categoria OWASP correta (A05 ou A01).

Corrigir o problema e confirmar que o gate passa e o merge é liberado.
