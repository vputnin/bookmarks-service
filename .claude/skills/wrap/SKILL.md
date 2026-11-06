---
name: wrap
description: End-of-session wrap-up — update TASKS.md, append to the session log, propose CLAUDE.md updates and keep agent memory within limits. Use when the user runs /wrap or says they are done for today.
---

# /wrap

Goal: the next session (which starts with an empty context) can continue without
re-explaining anything.

1. **TASKS.md**
   - Tick finished checkboxes. Move fully merged tasks from *In progress* to *Done*
     with the merge date.
   - For a task still in progress, add a `Next:` line with the very next concrete step.
   - Append to *Session log*: `- YYYY-MM-DD: <what changed>. Next: <next step>.`
     Max 2 lines per entry. If the log has more than 15 entries, collapse the oldest
     into one summary line.

2. **CLAUDE.md** — only propose, never silently edit.
   If this session revealed something every future session needs (a new command, a new
   convention, a recurring mistake), show a diff of the proposed change and ask.
   Keep CLAUDE.md under ~80 lines: if it grows, move details into `docs/` and link
   them with `@docs/<file>.md`.

3. **Agent memory hygiene**
   Check `.claude/agent-memory/code-reviewer/MEMORY.md`. Only the first 200 lines are
   loaded into the agent, so it must stay an *index*:
   - over 150 lines → move detailed entries into topic files in the same folder and
     leave one-line links in MEMORY.md;
   - remove entries that contradict current CLAUDE.md.

4. Show `git status`. If there are uncommitted changes, remind the user about `/commit`.
5. Finish with a 3-line summary: done, in progress, next.
