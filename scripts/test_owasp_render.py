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
