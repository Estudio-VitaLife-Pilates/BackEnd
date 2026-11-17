# OWASP Top 10:2021 — Rastreabilidade de Controles

Este documento mapeia os controles de segurança já implementados no backend às
categorias do OWASP Top 10:2021, e à proteção automática que o Security Gate do
SonarCloud oferece contra regressão em cada um deles. Veja o desenho completo em
`security-owasp-gate-design.md`.

## Controles mapeados

| Categoria OWASP | Controle existente | Onde | Proteção automática |
|---|---|---|---|
| A01 – Broken Access Control | `authorizeHttpRequests` com whitelist (`URLS_PERMITIDAS`), `@EnableMethodSecurity`/`@PreAuthorize` | `SecurityConfiguracao.java` | Regras Sonar tag `owasp-a1`; qualquer PR com regressão de controle de acesso aparece como vulnerabilidade nova |
| A02 – Cryptographic Failures | `BCryptPasswordEncoder`, JWT assinado (`jjwt`) | `SecurityConfiguracao.java`, `GerenciadorTokenJwt.java` | Regras Sonar tag `owasp-a2` (algoritmo fraco, chave hardcoded) |
| A03 – Injection | Spring Data JPA (queries parametrizadas) + Bean Validation (`@NotBlank`, `@Size` etc.) nos DTOs | `dto/**/*Dto.java` | Regras Sonar tag `owasp-a3` (SQL/command injection) |
| A05 – Security Misconfiguration | CSP (`default-src 'self'`), CORS com origem explícita | `SecurityConfiguracao.java` | Security Hotspots de configuração (CSP, CORS, headers) |
| A07 – Identification/Auth Failures | JWT stateless, `AutenticacaoProvider` customizado | `SecurityConfiguracao.java`, `AutenticacaoFilter.java` | Regras Sonar tag `owasp-a7` |

## Riscos conhecidos, não mitigados (fora do escopo do gate)

- **A05 — Security Misconfiguration**: `/actuator/*` está liberado como público
  (`permitAll` em `URLS_PERMITIDAS`). Risco aceito/pendente — decisão de produto.
- **A04 — Insecure Design**: não há rate limiting em `/usuarios/login`, o que
  facilita brute-force de credenciais. Risco aceito/pendente.

Esses dois itens não são pegos pelo Security Gate porque não são regressões de
código novo — são lacunas de design já existentes. Ficam documentados aqui para
não serem confundidos com falha do gate.
