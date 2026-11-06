# Team decisions

## javadoc
Javadoc is required only on public types and on non-obvious public methods.
Package-private classes (e.g. `InMemoryBookmarkRepository`, `ApiExceptionHandler`)
don't need it. Don't raise missing Javadoc there as a finding.
