#!/usr/bin/env python3
"""PostToolUse hook: after Claude edits a production Java file, check team conventions.

The edit already happened, so exit code 2 doesn't undo it; it feeds stderr back to
Claude, which then fixes the problem in its next step. This turns CLAUDE.md rules
into an automatic feedback loop instead of hoping the model remembers them.
"""
import json
import re
import sys
from pathlib import Path

RULES = [
    (re.compile(r"System\.(out|err)\.print"), "Use SLF4J instead of System.out/err."),
    # @Autowired followed (possibly on the next line) by a field declaration, not a constructor
    (
        re.compile(r"@Autowired\s+(?:(?:private|protected|public|final)\s+)*[\w.<>?,\s]+?\s+\w+\s*;"),
        "Field injection found. Use constructor injection.",
    ),
    (re.compile(r"Instant\.now\(\s*\)"), "Use Instant.now(clock) with the injected Clock."),
]


def check(source: str, file_name: str) -> list[str]:
    problems = []
    for pattern, message in RULES:
        for match in pattern.finditer(source):
            line = source.count("\n", 0, match.start()) + 1
            problems.append(f"{file_name}:{line}: {message}")
    return problems


def main() -> int:
    event = json.load(sys.stdin)
    path = event.get("tool_input", {}).get("file_path", "")
    normalized = path.replace("\\", "/")
    if not normalized.endswith(".java") or "/src/main/" not in normalized:
        return 0

    file = Path(path)
    if not file.exists():
        return 0

    problems = check(file.read_text(encoding="utf-8"), file.name)
    if problems:
        print("Convention check failed (CLAUDE.md > Code conventions):", file=sys.stderr)
        print("\n".join(problems), file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
