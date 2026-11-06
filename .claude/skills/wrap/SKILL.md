---
name: wrap
description: End-of-session wrap-up — update TASKS.md, append to the session log, propose CLAUDE.md updates, keep agent memory within limits, auto-commit TASKS.md on main and delete merged feat/* branches. Use only when the user runs /wrap.
disable-model-invocation: true
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

4. **Commit the session log** — automatic, do not ask.
   This is the one allowed direct commit to `main` outside `/merge-to-main`
   (see CLAUDE.md → Workflow).
   - Run `git branch --show-current`.
     - On `main`: if `git status --porcelain -- TASKS.md` is non-empty, run
       `git commit -m "chore(tasks): session log" -- TASKS.md`.
       The pathspec commits **only** `TASKS.md`; anything else that is staged or
       modified stays uncommitted. Never `git add -A` / `git commit -a` here.
     - On any other branch: do not switch branches and do not commit. `TASKS.md`
       changes belong to that branch — remind the user to `/commit` them.
   - Nothing changed in `TASKS.md` → skip silently.

5. **Delete merged feature branches** — automatic, do not ask.
   - List them: `git branch --merged main --list 'feat/*' --format='%(refname:short)'`.
   - Skip the current branch.
   - Skip any branch with no commits of its own: if its tip is on main's first-parent
     line (`git rev-list --first-parent main | grep -qx "$(git rev-parse <branch>)"`),
     it was just created from `main` and is about to be used — `--merged` lists it
     anyway. Branches merged by `/merge-to-main` (`--no-ff`) are never on that line.
   - Delete each remaining branch with `git branch -d <branch>`.
   - Only lower-case `-d` (git refuses unmerged branches). Never `-D`, never touch
     `fix/*` or other prefixes, never delete remote branches.
   - If `-d` refuses a branch, leave it and mention it in the summary.

6. Show `git status`. If uncommitted changes remain:
   - on a feature branch → remind the user about `/commit`;
   - on `main` (e.g. agent-memory edits from step 3 or an accepted CLAUDE.md change) →
     they are outside the auto-commit; tell the user they need a branch first
     (`git switch -c feat/<short-name>`, then `/commit`), since `/commit` refuses `main`.
7. Finish with a 3-line summary: done, in progress, next. Mention the session-log
   commit hash (if any) and the deleted branches.
