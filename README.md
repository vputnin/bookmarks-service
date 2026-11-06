# bookmarks-service

A small Spring Boot 3 / Java 21 bookmarks API with a plain HTML page, built to showcase
an **AI-first development workflow with Claude Code**: shared `.claude/` configuration,
`CLAUDE.md` as the project reference, `TASKS.md`-driven iterations, project skills
(`/commit`, `/review`, `/wrap`, `/merge-to-main`), a code-review subagent with persistent
memory, and hooks that enforce conventions automatically.

## Run
```bash
mvn -q test
mvn spring-boot:run   # http://localhost:8080
```

## API
| Method | Path | Result |
|---|---|---|
| GET | `/api/bookmarks` | list, newest first |
| GET | `/api/bookmarks/{id}` | one bookmark or 404 ProblemDetail |
| POST | `/api/bookmarks` | 201 + Location, 400 ProblemDetail on validation errors |
| DELETE | `/api/bookmarks/{id}` | 204 or 404 |

## AI workflow layout
```
CLAUDE.md                     project reference, loaded every session
TASKS.md                      backlog + session log, updated by /wrap
docs/architecture.md          imported into CLAUDE.md via @docs/architecture.md
.claude/settings.json         shared permissions + hooks
.claude/hooks/                protect_files.py (PreToolUse), check_java_conventions.py (PostToolUse)
.claude/skills/               commit, review, wrap, merge-to-main, add-endpoint
.claude/agents/code-reviewer.md   review subagent, memory: project
.claude/agent-memory/code-reviewer/   the subagent's persistent memory (index + topic files)
```
