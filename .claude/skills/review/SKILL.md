---
name: review
description: Review the current branch against main using the code-reviewer subagent. Use when the user runs /review or asks for a code review before merging.
argument-hint: "[optional focus, e.g. security or tests]"
---

# /review

1. Get the diff: `git diff main...HEAD` (plus `git diff` for uncommitted work).
   If the diff is empty, say there is nothing to review.
2. Delegate to the **code-reviewer** subagent. Pass it: the branch name, the task id from
   the branch name and its text from `TASKS.md`, and the focus "$ARGUMENTS" if given.
   The subagent reads the diff itself; don't paste the whole diff into the prompt.
3. Present the subagent's findings to the user grouped as
   **Must fix / Should fix / Nit**, each with `file:line` and a one-line reason.
4. Ask which findings to fix. Do not change code before the user answers.
