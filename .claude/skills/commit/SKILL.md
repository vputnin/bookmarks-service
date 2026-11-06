---
name: commit
description: Stage and commit the current changes with a Conventional Commits message. Use only when the user runs /commit.
disable-model-invocation: true
argument-hint: "[optional extra context for the message]"
---

# /commit

1. Run `git branch --show-current`. If it is `main`, STOP and tell the user to create a
   feature branch first (`feat/<task-id>-<name>`). Never commit to main.
2. Run `git status` and `git diff`. If there is nothing to commit, say so and stop.
3. If any `.java` file changed, run `mvn -q test`. If tests fail, STOP, show the failing
   tests and do not commit.
4. Stage only files related to the current task. Never stage `target/`, IDE files or
   `.claude/settings.local.json`. If unrelated changes exist, list them and ask.
5. Write the message:
   - Format: `<type>(<scope>): <summary>` — types: feat, fix, test, refactor, docs, chore.
   - Scope = area (`api`, `ui`, `tasks`, `claude`).
   - Summary: imperative, lower case, max 72 chars, no trailing period.
   - Body (optional): *why*, not *what*. Add `Refs: T-<n>` if the branch name has a task id.
   - Extra context from the user: $ARGUMENTS
6. Commit and show `git log --oneline -1`.
