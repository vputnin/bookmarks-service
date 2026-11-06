# Team decisions

## javadoc
Javadoc is required only on public types and on non-obvious public methods.
Package-private classes (e.g. `InMemoryBookmarkRepository`, `ApiExceptionHandler`)
don't need it. Don't raise missing Javadoc there as a finding.

## frontend-tests
`src/main/resources/static/index.html` has no automated tests, and that is intentional:
UI behaviour is verified manually in the running app (see CLAUDE.md, "For UI checks").
Don't raise missing frontend/UI tests, or "relies on a manual check", as a finding.
Backend behaviour behind the UI still needs tests (service unit test / `BookmarkControllerTest`).
