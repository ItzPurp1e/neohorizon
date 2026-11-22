import json
from pathlib import Path
import re
import unittest

from tools import lab


TABLES = [
    "crates/horizon-protocol/src/ids.rs",
    "crates/horizon-client/src/lan/tracking.rs",
    "crates/horizon-client/src/lan/command_order.rs",
    "crates/horizon-client/src/lan/command_suggestions.rs",
    "crates/horizon-command/src/vanilla_order.rs",
]


class BackportVersionTableTests(unittest.TestCase):
    def test_generated_tables_match_the_active_pin(self):
        """A pin switch may not leave a generated 26.3 table active."""
        version = json.loads(lab.LOCK.read_text())["minecraft"]["version"]
        for relative in TABLES:
            text = (lab.ROOT / relative).read_text()
            match = re.search(r'GENERATED_FOR_VERSION: &str = "([^"]+)"', text)
            self.assertIsNotNone(match, f"missing version marker in {relative}")
            self.assertEqual(match[1], version, relative)
            self.assertNotIn('GENERATED_FOR_VERSION: &str = "26.3"', text, relative)

    def test_active_rust_resource_paths_do_not_name_the_old_layout(self):
        for path in (lab.ROOT / "crates").rglob("*.rs"):
            self.assertNotIn("horizon-client/26.3", path.read_text(errors="replace"), str(path))


if __name__ == "__main__":
    unittest.main()
