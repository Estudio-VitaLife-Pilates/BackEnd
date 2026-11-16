# SonarCloud Security Gate + OWASP Traceability Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a PR-triggered SonarCloud security gate to the `BackEnd` and `FrontEnd` repositories, and connect every automated finding to the OWASP Top 10:2021 category it belongs to, both in a reference doc and in an automatic PR comment.

**Architecture:** Each repo gets its own `pull_request`-triggered GitHub Actions workflow that builds, runs the SonarCloud scan with `sonar.qualitygate.wait=true` (so the job fails the PR check if the gate fails), then runs a small Python script that queries the SonarCloud REST API for the PR's new findings and posts a markdown summary grouped by OWASP category as a PR comment. The two repos are independent git repositories, so every file below is duplicated (not shared) between them.

**Tech Stack:** GitHub Actions, SonarCloud (Maven plugin for `BackEnd`, `sonarqube-scan-action` for `FrontEnd`), Python 3 stdlib only (`json`, `urllib.request`, `subprocess`, `unittest`) for the OWASP summary script — no new runtime dependency in either repo.

**Spec:** [BackEnd/docs/security-owasp-gate-design.md](security-owasp-gate-design.md)

## Global Constraints

- Quality Gate conditions (New Code only): `Security Rating` worse than `A` fails; `Security Hotspots Reviewed` < 100% fails. No coverage/duplication/reliability/maintainability conditions in this phase.
- Workflows trigger on `pull_request` (`opened`, `synchronize`, `reopened`) targeting `develop` only.
- The OWASP PR-summary script must never fail the job on its own — a SonarCloud API error degrades to a warning, not a workflow failure. Only the Quality Gate step is allowed to fail the job.
- `sonar.organization` is read from the `SONAR_ORGANIZATION` repository variable (`vars.SONAR_ORGANIZATION`) — never hardcoded in a file, since it's set during the manual SonarCloud setup described in the spec.
- No new Python/Node dependency is added to either repo — `python3` is preinstalled on `ubuntu-latest` GitHub-hosted runners and used only via stdlib.

---

### Task 1: OWASP traceability doc (`BackEnd`)

**Files:**
- Create: `BackEnd/docs/security-owasp.md`

**Interfaces:**
- Produces: a reference doc other tasks' workflow comments can link to (no code interface).

- [ ] **Step 1: Write the doc**

```markdown
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
```

- [ ] **Step 2: Verify the required sections exist**

Run: `grep -c "^##" "C:\Users\isafa\Projeto\BackEnd\docs\security-owasp.md"`
Expected: `3` (one `# ` title doesn't count towards `^##`, the three `##` section headers do)

- [ ] **Step 3: Commit**

```bash
cd "C:\Users\isafa\Projeto\BackEnd"
git add docs/security-owasp.md
git commit -m "docs: mapear controles de seguranca existentes ao OWASP Top 10:2021"
```

---

### Task 2: OWASP render module (`BackEnd`)

**Files:**
- Create: `BackEnd/scripts/owasp_render.py`
- Test: `BackEnd/scripts/test_owasp_render.py`

**Interfaces:**
- Produces: `render_owasp_summary(issues_json: dict, hotspots_json: dict) -> str`, imported by Task 3's `owasp_pr_summary.py` as `from owasp_render import render_owasp_summary`.
- Consumes: nothing (pure function, no I/O).

- [ ] **Step 1: Write the failing test**

Create `BackEnd/scripts/test_owasp_render.py`:

```python
import unittest

from owasp_render import render_owasp_summary


class RenderOwaspSummaryTest(unittest.TestCase):
    def test_no_findings_renders_fallback_message(self):
        issues = {"total": 0, "facets": []}
        hotspots = {"paging": {"total": 0}, "hotspots": []}

        result = render_owasp_summary(issues, hotspots)

        self.assertIn("Nenhum achado novo de segurança (OWASP) neste PR.", result)

    def test_vulnerabilities_grouped_by_owasp_category(self):
        issues = {
            "total": 2,
            "facets": [
                {
                    "property": "owaspTop10-2021",
                    "values": [{"val": "a1", "count": 1}, {"val": "a5", "count": 1}],
                }
            ],
            "issues": [
                {
                    "severity": "CRITICAL",
                    "message": "Endpoint sem controle de acesso",
                    "component": "src/main/java/Foo.java",
                    "line": 42,
                },
                {
                    "severity": "MAJOR",
                    "message": "CORS permissivo",
                    "component": "src/main/java/Bar.java",
                    "line": 10,
                },
            ],
        }
        hotspots = {"paging": {"total": 0}, "hotspots": []}

        result = render_owasp_summary(issues, hotspots)

        self.assertIn("A01 - Broken Access Control", result)
        self.assertIn("A05 - Security Misconfiguration", result)
        self.assertIn("Endpoint sem controle de acesso", result)

    def test_hotspots_listed_in_their_own_section(self):
        issues = {"total": 0, "facets": []}
        hotspots = {
            "paging": {"total": 1},
            "hotspots": [
                {
                    "vulnerabilityProbability": "HIGH",
                    "securityCategory": "csrf",
                    "message": "Revisar necessidade de CSRF",
                    "component": "src/main/java/SecurityConfiguracao.java",
                    "line": 119,
                }
            ],
        }

        result = render_owasp_summary(issues, hotspots)

        self.assertIn("Security Hotspots a revisar", result)
        self.assertIn("Revisar necessidade de CSRF", result)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd "C:\Users\isafa\Projeto\BackEnd\scripts" && python3 test_owasp_render.py -v`
Expected: FAIL with `ModuleNotFoundError: No module named 'owasp_render'`

- [ ] **Step 3: Write the minimal implementation**

Create `BackEnd/scripts/owasp_render.py`:

```python
OWASP_NAMES = {
    "a1": "A01 - Broken Access Control",
    "a2": "A02 - Cryptographic Failures",
    "a3": "A03 - Injection",
    "a4": "A04 - Insecure Design",
    "a5": "A05 - Security Misconfiguration",
    "a6": "A06 - Vulnerable and Outdated Components",
    "a7": "A07 - Identification and Authentication Failures",
    "a8": "A08 - Software and Data Integrity Failures",
    "a9": "A09 - Security Logging and Monitoring Failures",
    "a10": "A10 - Server-Side Request Forgery",
}


def _location(entry):
    component = entry.get("component", "")
    line = entry.get("line")
    return f"`{component}`:{line}" if line else f"`{component}`"


def render_owasp_summary(issues_json, hotspots_json):
    issues_total = issues_json.get("total", 0)
    hotspots_total = hotspots_json.get("paging", {}).get("total", 0)

    lines = ["## Resumo de seguranca (OWASP) deste PR", ""]

    if issues_total == 0 and hotspots_total == 0:
        lines.append("Nenhum achado novo de segurança (OWASP) neste PR.")
        return "\n".join(lines)

    facet_values = []
    for facet in issues_json.get("facets", []):
        if facet.get("property") == "owaspTop10-2021":
            facet_values = facet.get("values", [])
            break

    if facet_values:
        lines.append("### Vulnerabilidades novas por categoria")
        lines.append("")
        lines.append("| Categoria OWASP | Qtd |")
        lines.append("|---|---|")
        for value in facet_values:
            code = value.get("val", "")
            count = value.get("count", 0)
            lines.append(f"| {OWASP_NAMES.get(code, code)} | {count} |")
        lines.append("")

    issues = issues_json.get("issues", [])
    if issues:
        lines.append("### Detalhe das vulnerabilidades novas")
        lines.append("")
        for issue in issues:
            severity = issue.get("severity", "?")
            message = issue.get("message", "")
            lines.append(f"- **{severity}** {message} ({_location(issue)})")
        lines.append("")

    hotspots = hotspots_json.get("hotspots", [])
    if hotspots:
        lines.append("### Security Hotspots a revisar")
        lines.append("")
        for hotspot in hotspots:
            probability = hotspot.get("vulnerabilityProbability", "?")
            category = hotspot.get("securityCategory", "others")
            message = hotspot.get("message", "")
            lines.append(f"- **{probability}** [{category}] {message} ({_location(hotspot)})")
        lines.append("")

    return "\n".join(lines)
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd "C:\Users\isafa\Projeto\BackEnd\scripts" && python3 test_owasp_render.py -v`
Expected: `OK` with 3 tests passing

- [ ] **Step 5: Commit**

```bash
cd "C:\Users\isafa\Projeto\BackEnd"
git add scripts/owasp_render.py scripts/test_owasp_render.py
git commit -m "feat: adicionar render do resumo de seguranca por categoria OWASP"
```

---

### Task 3: OWASP PR-summary orchestrator (`BackEnd`)

**Files:**
- Create: `BackEnd/scripts/owasp_pr_summary.py`
- Test: `BackEnd/scripts/test_owasp_pr_summary.py`

**Interfaces:**
- Consumes: `render_owasp_summary(issues_json, hotspots_json) -> str` from Task 2.
- Produces: a `main()` entry point invoked by the workflow as `python3 scripts/owasp_pr_summary.py`, reading `SONAR_TOKEN`, `SONAR_PROJECT_KEY`, `PR_NUMBER`, optional `SONAR_HOST_URL` from the environment, and calling `gh pr comment` via `subprocess`.

- [ ] **Step 1: Write the failing test**

Create `BackEnd/scripts/test_owasp_pr_summary.py`:

```python
import os
import unittest
from unittest import mock

import owasp_pr_summary


class RequiredEnvTest(unittest.TestCase):
    def test_missing_env_var_exits_with_error(self):
        with mock.patch.dict(os.environ, {}, clear=True):
            with self.assertRaises(SystemExit) as context:
                owasp_pr_summary._required_env("SONAR_TOKEN")
            self.assertEqual(context.exception.code, 1)

    def test_present_env_var_is_returned(self):
        with mock.patch.dict(os.environ, {"SONAR_TOKEN": "abc123"}, clear=True):
            self.assertEqual(owasp_pr_summary._required_env("SONAR_TOKEN"), "abc123")


class FetchJsonTest(unittest.TestCase):
    def test_returns_parsed_json_on_success(self):
        fake_response = mock.MagicMock()
        fake_response.read.return_value = b'{"total": 0}'
        fake_response.__enter__.return_value = fake_response

        with mock.patch("owasp_pr_summary.urllib.request.urlopen", return_value=fake_response):
            result = owasp_pr_summary._fetch_json("https://example.org", "token")

        self.assertEqual(result, {"total": 0})

    def test_returns_none_on_request_failure(self):
        with mock.patch(
            "owasp_pr_summary.urllib.request.urlopen",
            side_effect=owasp_pr_summary.urllib.error.URLError("boom"),
        ):
            result = owasp_pr_summary._fetch_json("https://example.org", "token")

        self.assertIsNone(result)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd "C:\Users\isafa\Projeto\BackEnd\scripts" && python3 test_owasp_pr_summary.py -v`
Expected: FAIL with `ModuleNotFoundError: No module named 'owasp_pr_summary'`

- [ ] **Step 3: Write the minimal implementation**

Create `BackEnd/scripts/owasp_pr_summary.py`:

```python
import json
import os
import subprocess
import sys
import urllib.error
import urllib.request

from owasp_render import render_owasp_summary


def _required_env(name):
    value = os.environ.get(name)
    if not value:
        print(f"::error::{name} e obrigatorio", file=sys.stderr)
        sys.exit(1)
    return value


def _fetch_json(url, token):
    request = urllib.request.Request(url)
    request.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(request, timeout=30) as response:
            return json.loads(response.read())
    except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError) as error:
        print(f"::warning::Falha ao consultar {url}: {error}", file=sys.stderr)
        return None


def main():
    sonar_token = _required_env("SONAR_TOKEN")
    project_key = _required_env("SONAR_PROJECT_KEY")
    pr_number = _required_env("PR_NUMBER")
    host_url = os.environ.get("SONAR_HOST_URL", "https://sonarcloud.io")

    issues_url = (
        f"{host_url}/api/issues/search?componentKeys={project_key}"
        f"&pullRequest={pr_number}&resolved=false&types=VULNERABILITY"
        f"&facets=owaspTop10-2021&ps=100"
    )
    hotspots_url = (
        f"{host_url}/api/hotspots/search?projectKey={project_key}"
        f"&pullRequest={pr_number}&status=TO_REVIEW&ps=100"
    )

    issues_json = _fetch_json(issues_url, sonar_token)
    hotspots_json = _fetch_json(hotspots_url, sonar_token)

    if issues_json is None or hotspots_json is None:
        print("::warning::Pulando resumo OWASP no PR por falha na API do SonarCloud.")
        return

    summary = render_owasp_summary(issues_json, hotspots_json)

    result = subprocess.run(
        ["gh", "pr", "comment", pr_number, "--body", summary],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print(f"::warning::Nao foi possivel comentar no PR: {result.stderr}")


if __name__ == "__main__":
    main()
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd "C:\Users\isafa\Projeto\BackEnd\scripts" && python3 test_owasp_pr_summary.py -v`
Expected: `OK` with 4 tests passing

- [ ] **Step 5: Commit**

```bash
cd "C:\Users\isafa\Projeto\BackEnd"
git add scripts/owasp_pr_summary.py scripts/test_owasp_pr_summary.py
git commit -m "feat: adicionar script orquestrador do resumo OWASP no PR"
```

---

### Task 4: SonarCloud PR workflow (`BackEnd`)

**Files:**
- Create: `BackEnd/.github/workflows/sonarqube.yml`

**Interfaces:**
- Consumes: `BackEnd/scripts/owasp_pr_summary.py` (Task 3), the `SONAR_TOKEN` repo secret and `SONAR_ORGANIZATION` repo variable (set manually per the spec), the `jacoco-maven-plugin` report already configured in `pom.xml`.

- [ ] **Step 1: Write the workflow file**

Create `BackEnd/.github/workflows/sonarqube.yml`:

```yaml
name: SonarCloud Security Gate

on:
  pull_request:
    branches: ["develop"]
    types: [opened, synchronize, reopened]

permissions:
  contents: read
  pull-requests: write

jobs:
  sonarqube:
    name: "[quality] - SonarCloud scan + security gate"
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "21"
          cache: maven

      - name: Build and test
        run: ./mvnw --batch-mode verify

      - name: SonarCloud scan (waits for Quality Gate)
        run: >
          ./mvnw --batch-mode org.sonarsource.scanner.maven:sonar-maven-plugin:sonar
          -Dsonar.projectKey=vitalife-backend
          -Dsonar.organization=${{ vars.SONAR_ORGANIZATION }}
          -Dsonar.host.url=https://sonarcloud.io
          -Dsonar.token=${{ secrets.SONAR_TOKEN }}
          -Dsonar.qualitygate.wait=true

      - name: OWASP PR summary
        if: always()
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_PROJECT_KEY: vitalife-backend
          PR_NUMBER: ${{ github.event.pull_request.number }}
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: python3 scripts/owasp_pr_summary.py
```

- [ ] **Step 2: Validate the YAML syntax**

Run: `python3 -c "import yaml; yaml.safe_load(open(r'C:\Users\isafa\Projeto\BackEnd\.github\workflows\sonarqube.yml'))" && echo VALID`
Expected: `VALID`

- [ ] **Step 3: Commit**

```bash
cd "C:\Users\isafa\Projeto\BackEnd"
git add .github/workflows/sonarqube.yml
git commit -m "ci: adicionar workflow de PR com SonarCloud security gate"
```

---

### Task 5: OWASP render module (`FrontEnd`)

**Files:**
- Create: `FrontEnd/sonar-project.properties`
- Create: `FrontEnd/scripts/owasp_render.py`
- Test: `FrontEnd/scripts/test_owasp_render.py`

**Interfaces:**
- Produces: `render_owasp_summary(issues_json: dict, hotspots_json: dict) -> str`, imported by Task 6's `owasp_pr_summary.py` as `from owasp_render import render_owasp_summary`. Identical contract to Task 2 — this is a separate git repository, so the file is duplicated, not shared.

- [ ] **Step 1: Create the Sonar project properties**

Create `FrontEnd/sonar-project.properties`:

```properties
sonar.projectKey=vitalife-frontend
sonar.sources=src
```

- [ ] **Step 2: Write the failing test**

Create `FrontEnd/scripts/test_owasp_render.py`:

```python
import unittest

from owasp_render import render_owasp_summary


class RenderOwaspSummaryTest(unittest.TestCase):
    def test_no_findings_renders_fallback_message(self):
        issues = {"total": 0, "facets": []}
        hotspots = {"paging": {"total": 0}, "hotspots": []}

        result = render_owasp_summary(issues, hotspots)

        self.assertIn("Nenhum achado novo de segurança (OWASP) neste PR.", result)

    def test_vulnerabilities_grouped_by_owasp_category(self):
        issues = {
            "total": 2,
            "facets": [
                {
                    "property": "owaspTop10-2021",
                    "values": [{"val": "a3", "count": 1}, {"val": "a7", "count": 1}],
                }
            ],
            "issues": [
                {
                    "severity": "CRITICAL",
                    "message": "Possivel XSS em innerHTML",
                    "component": "src/components/Foo.jsx",
                    "line": 21,
                },
                {
                    "severity": "MAJOR",
                    "message": "Token JWT armazenado em localStorage",
                    "component": "src/services/auth.js",
                    "line": 8,
                },
            ],
        }
        hotspots = {"paging": {"total": 0}, "hotspots": []}

        result = render_owasp_summary(issues, hotspots)

        self.assertIn("A03 - Injection", result)
        self.assertIn("A07 - Identification and Authentication Failures", result)
        self.assertIn("Possivel XSS em innerHTML", result)

    def test_hotspots_listed_in_their_own_section(self):
        issues = {"total": 0, "facets": []}
        hotspots = {
            "paging": {"total": 1},
            "hotspots": [
                {
                    "vulnerabilityProbability": "MEDIUM",
                    "securityCategory": "others",
                    "message": "Revisar URL externa hardcoded",
                    "component": "src/services/api.js",
                    "line": 5,
                }
            ],
        }

        result = render_owasp_summary(issues, hotspots)

        self.assertIn("Security Hotspots a revisar", result)
        self.assertIn("Revisar URL externa hardcoded", result)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 3: Run test to verify it fails**

Run: `cd "C:\Users\isafa\Projeto\FrontEnd\scripts" && python3 test_owasp_render.py -v`
Expected: FAIL with `ModuleNotFoundError: No module named 'owasp_render'`

- [ ] **Step 4: Write the minimal implementation**

Create `FrontEnd/scripts/owasp_render.py`:

```python
OWASP_NAMES = {
    "a1": "A01 - Broken Access Control",
    "a2": "A02 - Cryptographic Failures",
    "a3": "A03 - Injection",
    "a4": "A04 - Insecure Design",
    "a5": "A05 - Security Misconfiguration",
    "a6": "A06 - Vulnerable and Outdated Components",
    "a7": "A07 - Identification and Authentication Failures",
    "a8": "A08 - Software and Data Integrity Failures",
    "a9": "A09 - Security Logging and Monitoring Failures",
    "a10": "A10 - Server-Side Request Forgery",
}


def _location(entry):
    component = entry.get("component", "")
    line = entry.get("line")
    return f"`{component}`:{line}" if line else f"`{component}`"


def render_owasp_summary(issues_json, hotspots_json):
    issues_total = issues_json.get("total", 0)
    hotspots_total = hotspots_json.get("paging", {}).get("total", 0)

    lines = ["## Resumo de seguranca (OWASP) deste PR", ""]

    if issues_total == 0 and hotspots_total == 0:
        lines.append("Nenhum achado novo de segurança (OWASP) neste PR.")
        return "\n".join(lines)

    facet_values = []
    for facet in issues_json.get("facets", []):
        if facet.get("property") == "owaspTop10-2021":
            facet_values = facet.get("values", [])
            break

    if facet_values:
        lines.append("### Vulnerabilidades novas por categoria")
        lines.append("")
        lines.append("| Categoria OWASP | Qtd |")
        lines.append("|---|---|")
        for value in facet_values:
            code = value.get("val", "")
            count = value.get("count", 0)
            lines.append(f"| {OWASP_NAMES.get(code, code)} | {count} |")
        lines.append("")

    issues = issues_json.get("issues", [])
    if issues:
        lines.append("### Detalhe das vulnerabilidades novas")
        lines.append("")
        for issue in issues:
            severity = issue.get("severity", "?")
            message = issue.get("message", "")
            lines.append(f"- **{severity}** {message} ({_location(issue)})")
        lines.append("")

    hotspots = hotspots_json.get("hotspots", [])
    if hotspots:
        lines.append("### Security Hotspots a revisar")
        lines.append("")
        for hotspot in hotspots:
            probability = hotspot.get("vulnerabilityProbability", "?")
            category = hotspot.get("securityCategory", "others")
            message = hotspot.get("message", "")
            lines.append(f"- **{probability}** [{category}] {message} ({_location(hotspot)})")
        lines.append("")

    return "\n".join(lines)
```

- [ ] **Step 5: Run test to verify it passes**

Run: `cd "C:\Users\isafa\Projeto\FrontEnd\scripts" && python3 test_owasp_render.py -v`
Expected: `OK` with 3 tests passing

- [ ] **Step 6: Commit**

```bash
cd "C:\Users\isafa\Projeto\FrontEnd"
git add sonar-project.properties scripts/owasp_render.py scripts/test_owasp_render.py
git commit -m "feat: adicionar config do SonarCloud e render do resumo OWASP"
```

---

### Task 6: OWASP PR-summary orchestrator (`FrontEnd`)

**Files:**
- Create: `FrontEnd/scripts/owasp_pr_summary.py`
- Test: `FrontEnd/scripts/test_owasp_pr_summary.py`

**Interfaces:**
- Consumes: `render_owasp_summary(issues_json, hotspots_json) -> str` from Task 5.
- Produces: same `main()` contract as Task 3 — `python3 scripts/owasp_pr_summary.py` reading `SONAR_TOKEN`, `SONAR_PROJECT_KEY`, `PR_NUMBER`, optional `SONAR_HOST_URL`.

- [ ] **Step 1: Write the failing test**

Create `FrontEnd/scripts/test_owasp_pr_summary.py`:

```python
import os
import unittest
from unittest import mock

import owasp_pr_summary


class RequiredEnvTest(unittest.TestCase):
    def test_missing_env_var_exits_with_error(self):
        with mock.patch.dict(os.environ, {}, clear=True):
            with self.assertRaises(SystemExit) as context:
                owasp_pr_summary._required_env("SONAR_TOKEN")
            self.assertEqual(context.exception.code, 1)

    def test_present_env_var_is_returned(self):
        with mock.patch.dict(os.environ, {"SONAR_TOKEN": "abc123"}, clear=True):
            self.assertEqual(owasp_pr_summary._required_env("SONAR_TOKEN"), "abc123")


class FetchJsonTest(unittest.TestCase):
    def test_returns_parsed_json_on_success(self):
        fake_response = mock.MagicMock()
        fake_response.read.return_value = b'{"total": 0}'
        fake_response.__enter__.return_value = fake_response

        with mock.patch("owasp_pr_summary.urllib.request.urlopen", return_value=fake_response):
            result = owasp_pr_summary._fetch_json("https://example.org", "token")

        self.assertEqual(result, {"total": 0})

    def test_returns_none_on_request_failure(self):
        with mock.patch(
            "owasp_pr_summary.urllib.request.urlopen",
            side_effect=owasp_pr_summary.urllib.error.URLError("boom"),
        ):
            result = owasp_pr_summary._fetch_json("https://example.org", "token")

        self.assertIsNone(result)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd "C:\Users\isafa\Projeto\FrontEnd\scripts" && python3 test_owasp_pr_summary.py -v`
Expected: FAIL with `ModuleNotFoundError: No module named 'owasp_pr_summary'`

- [ ] **Step 3: Write the minimal implementation**

Create `FrontEnd/scripts/owasp_pr_summary.py` (identical to `BackEnd/scripts/owasp_pr_summary.py` from Task 3):

```python
import json
import os
import subprocess
import sys
import urllib.error
import urllib.request

from owasp_render import render_owasp_summary


def _required_env(name):
    value = os.environ.get(name)
    if not value:
        print(f"::error::{name} e obrigatorio", file=sys.stderr)
        sys.exit(1)
    return value


def _fetch_json(url, token):
    request = urllib.request.Request(url)
    request.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(request, timeout=30) as response:
            return json.loads(response.read())
    except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError) as error:
        print(f"::warning::Falha ao consultar {url}: {error}", file=sys.stderr)
        return None


def main():
    sonar_token = _required_env("SONAR_TOKEN")
    project_key = _required_env("SONAR_PROJECT_KEY")
    pr_number = _required_env("PR_NUMBER")
    host_url = os.environ.get("SONAR_HOST_URL", "https://sonarcloud.io")

    issues_url = (
        f"{host_url}/api/issues/search?componentKeys={project_key}"
        f"&pullRequest={pr_number}&resolved=false&types=VULNERABILITY"
        f"&facets=owaspTop10-2021&ps=100"
    )
    hotspots_url = (
        f"{host_url}/api/hotspots/search?projectKey={project_key}"
        f"&pullRequest={pr_number}&status=TO_REVIEW&ps=100"
    )

    issues_json = _fetch_json(issues_url, sonar_token)
    hotspots_json = _fetch_json(hotspots_url, sonar_token)

    if issues_json is None or hotspots_json is None:
        print("::warning::Pulando resumo OWASP no PR por falha na API do SonarCloud.")
        return

    summary = render_owasp_summary(issues_json, hotspots_json)

    result = subprocess.run(
        ["gh", "pr", "comment", pr_number, "--body", summary],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print(f"::warning::Nao foi possivel comentar no PR: {result.stderr}")


if __name__ == "__main__":
    main()
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd "C:\Users\isafa\Projeto\FrontEnd\scripts" && python3 test_owasp_pr_summary.py -v`
Expected: `OK` with 4 tests passing

- [ ] **Step 5: Commit**

```bash
cd "C:\Users\isafa\Projeto\FrontEnd"
git add scripts/owasp_pr_summary.py scripts/test_owasp_pr_summary.py
git commit -m "feat: adicionar script orquestrador do resumo OWASP no PR"
```

---

### Task 7: SonarCloud PR workflow (`FrontEnd`)

**Files:**
- Create: `FrontEnd/.github/workflows/sonarqube.yml`

**Interfaces:**
- Consumes: `FrontEnd/scripts/owasp_pr_summary.py` (Task 6), `FrontEnd/sonar-project.properties` (Task 5), the `SONAR_TOKEN` repo secret and `SONAR_ORGANIZATION` repo variable (set manually per the spec).

- [ ] **Step 1: Write the workflow file**

Create `FrontEnd/.github/workflows/sonarqube.yml`:

```yaml
name: SonarCloud Security Gate

on:
  pull_request:
    branches: ["develop"]
    types: [opened, synchronize, reopened]

permissions:
  contents: read
  pull-requests: write

jobs:
  sonarqube:
    name: "[quality] - SonarCloud scan + security gate"
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: npm

      - name: Install and build
        run: |
          npm ci
          npm run build

      - name: SonarCloud scan (waits for Quality Gate)
        uses: SonarSource/sonarqube-scan-action@v4
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        with:
          args: >
            -Dsonar.organization=${{ vars.SONAR_ORGANIZATION }}
            -Dsonar.qualitygate.wait=true

      - name: OWASP PR summary
        if: always()
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_PROJECT_KEY: vitalife-frontend
          PR_NUMBER: ${{ github.event.pull_request.number }}
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: python3 scripts/owasp_pr_summary.py
```

- [ ] **Step 2: Validate the YAML syntax**

Run: `python3 -c "import yaml; yaml.safe_load(open(r'C:\Users\isafa\Projeto\FrontEnd\.github\workflows\sonarqube.yml'))" && echo VALID`
Expected: `VALID`

- [ ] **Step 3: Commit**

```bash
cd "C:\Users\isafa\Projeto\FrontEnd"
git add .github/workflows/sonarqube.yml
git commit -m "ci: adicionar workflow de PR com SonarCloud security gate"
```

---

## Appendix: Manual Setup Checklist (performed by you, not by the plan executor)

These steps happen outside the two repos — in the SonarCloud and GitHub web UIs — and cannot be scripted by the engineer executing this plan. Do them once, in order, after Tasks 1–7 are merged:

1. Create a SonarCloud account/organization bound to the `Estudio-VitaLife-Pilates` GitHub org, and import both the `BackEnd` and `FrontEnd` repositories as projects (`vitalife-backend`, `vitalife-frontend`).
2. In SonarCloud, create a Quality Gate named "Security Gate" with exactly two conditions on New Code: `Security Rating` worse than `A`, and `Security Hotspots Reviewed` less than `100%`. Set it as the default gate for both projects.
3. Generate a SonarCloud token and add it as the `SONAR_TOKEN` secret in both repos (GitHub repo → Settings → Secrets and variables → Actions). Add your SonarCloud organization slug as the `SONAR_ORGANIZATION` repository variable in both repos (same screen, "Variables" tab).
4. Open a throwaway PR in each repo (or push to an existing branch) to trigger the first scan. Then, in SonarCloud, go to each project's Security Hotspots tab and mark the pre-existing ones (CSRF disabled, CORS, H2 console) as "Reviewed → Safe", pasting the justification already written as comments in `SecurityConfiguracao.java`. This must happen before enabling the required check below, otherwise pre-existing debt blocks the first real PR.
5. In each repo's branch protection rule for `develop` (Settings → Branches), add the SonarCloud check (named `SonarCloud Code Analysis` or similar, visible after step 4's scan ran once) as a required status check before merging.
6. Run the end-to-end validation from the spec's "Teste de validação" section: open a test PR introducing a known issue (e.g. CORS with `*` and `allowCredentials=true`, or a new endpoint without `authorizeHttpRequests`), confirm the gate fails and blocks merge, confirm the PR comment names the correct OWASP category, then fix it and confirm the gate passes and merge unblocks.
