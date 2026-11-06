# bookmarks-service

Small Spring Boot REST API + static HTML page for saving links with tags.
The real point of this repo is the AI-first workflow in `.claude/`.

## Commands
- Run tests: `mvn -q test`
- Run app: `mvn spring-boot:run` → http://localhost:8080
- Single test class: `mvn -q test -Dtest=BookmarkServiceTest`

## Stack
Java 21, Spring Boot 3.4, Maven, JUnit 5 + AssertJ + MockMvc. No database: in-memory repository.
Frontend: one file, `src/main/resources/static/index.html`, plain JS, no build step.

## Architecture
@docs/architecture.md

## Code conventions
- Constructor injection only. No `@Autowired` on fields in `src/main` (a hook enforces this).
- No `System.out.println` — use SLF4J if logging is needed (a hook enforces this).
- DTOs and domain objects are Java records. Validation lives on request records (`jakarta.validation`).
- Errors are RFC 9457 `ProblemDetail`. Domain exceptions are mapped in `web/ApiExceptionHandler`.
- Time comes from the injected `Clock`, never `Instant.now()` without a clock.
- Every new behaviour gets a test: service logic → plain unit test, HTTP contract → `BookmarkControllerTest`.
- Frontend: build DOM with `textContent`, never `innerHTML` with user data.

## Workflow (follow this order)
1. Pick the top task from **Backlog** in `TASKS.md`, move it to **In progress**.
2. Create a branch: `git switch -c feat/<task-id>-<short-name>` (e.g. `feat/T-1-tag-filter`). Never commit to `main`.
3. Implement as a vertical slice: API + service + tests + UI if the task touches UI.
4. `mvn -q test` must be green.
5. `/commit` → `/review` → fix findings → `/merge-to-main`.
6. End every session with `/wrap`.

## Do not
- Edit `.claude/settings.json` (a hook blocks it; ask the human).
- Add dependencies to `pom.xml` without saying why in the task notes.
- Push to a remote or force-push. The human pushes.
