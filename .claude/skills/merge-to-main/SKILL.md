---
name: merge-to-main
description: Merge the current feature branch into main locally after checks pass. Use only when the user runs /merge-to-main.
disable-model-invocation: true
---

# /merge-to-main

Pre-flight — stop at the first failure and report it:
1. Current branch is not `main` and matches `feat/*` or `fix/*`.
2. Working tree is clean (`git status --porcelain` is empty). If not, suggest `/commit`.
3. `mvn -q test` is green on the branch.
4. A `/review` was done in this session, or ask the user to confirm skipping it.

Merge:
5. `git switch main`
6. `git merge --no-ff <branch> -m "Merge <branch>: <task title from TASKS.md>"`
   (permissions will ask the user to confirm the merge).
7. Run `mvn -q test` on main. If it fails, tell the user immediately and suggest
   `git revert -m 1 HEAD`; do not try clever fixes on main.
8. Offer to delete the branch with `git branch -d <branch>`. If the user declines, that's
   fine: `/wrap` deletes merged `feat/*` branches automatically at the end of the session.
9. Update `TASKS.md`: move the task to *Done* with today's date, then commit on main
   with `chore(tasks): mark T-<n> done`.

Never push. Tell the user the merge is local and they push when ready.
