---
name: add-endpoint
description: Checklist and patterns for adding or changing a REST endpoint in this Spring Boot service (controller, service, validation, ProblemDetail errors, tests, UI call). Use whenever a task adds or modifies an /api endpoint.
---

# Adding a REST endpoint

This skill loads automatically when a task touches the API, so these rules don't have
to live in CLAUDE.md and cost context in every session.

## Order of work (test-first)
1. Write the MockMvc test in `BookmarkControllerTest` describing the HTTP contract:
   path, status codes, response JSON, error ProblemDetail.
2. Write the service unit test in `BookmarkServiceTest` for the business rule.
3. Implement: repository (if needed) → service → controller.
4. `mvn -q test` green.
5. If the task mentions UI, update `index.html` (see below).

## Patterns
- Request body → a record with `jakarta.validation` annotations, `@Valid` in the controller.
- Query params → `@RequestParam(required = false)`; keep the old behaviour when absent.
- Not found → throw `BookmarkNotFoundException`; new error types → new exception in
  `bookmark/` + one `@ExceptionHandler` in `web/ApiExceptionHandler` returning `ProblemDetail`.
- Status codes: 200 read, 201 + `Location` create, 204 delete/no body, 400 validation,
  404 missing, 409 conflict.
- Controller holds no logic beyond mapping.

## UI
- Use `fetch`, check `response.ok`, show `problem.detail` in `#error` on failure.
- Build elements with `textContent`. No `innerHTML` with server data.
- After a mutation, call `load()` to refresh the list.
