#!/usr/bin/env python3
"""PreToolUse hook: block edits to files the agent must not touch.

Claude Code sends the tool call as JSON on stdin. Exit code 2 blocks the call,
and whatever we print to stderr is shown to Claude as the reason.
"""
import json
import sys
from pathlib import PurePath

PROTECTED_EXACT = {".claude/settings.json"}
PROTECTED_NAMES = {".env"}
PROTECTED_SUFFIXES = (".pem", ".key")


def is_protected(path: str) -> bool:
    p = PurePath(path.replace("\\", "/"))
    normalized = p.as_posix()
    if any(normalized.endswith(exact) for exact in PROTECTED_EXACT):
        return True
    if p.name in PROTECTED_NAMES or p.name.startswith(".env."):
        return True
    return p.suffix in PROTECTED_SUFFIXES


def main() -> int:
    event = json.load(sys.stdin)
    path = event.get("tool_input", {}).get("file_path", "")
    if path and is_protected(path):
        print(
            f"Blocked: '{path}' is protected (see CLAUDE.md 'Do not'). "
            "Ask the human to change it instead.",
            file=sys.stderr,
        )
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
