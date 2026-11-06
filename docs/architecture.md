## Layers (package `com.example.bookmarks`)

```
bookmark/
  BookmarkController   HTTP only: mapping, @Valid, status codes, Location header
  BookmarkService      business rules (normalisation, not-found), uses Clock
  BookmarkRepository   port (interface)
  InMemoryBookmarkRepository   adapter, ConcurrentHashMap, newest-first ordering
  Bookmark             immutable record
  CreateBookmarkRequest  request record + validation
web/
  ApiExceptionHandler  domain exceptions → ProblemDetail
  ClockConfig          Clock bean
```

Rules:
- Controller never talks to the repository directly.
- Repository is an interface so a JPA adapter can replace the in-memory one later without touching the service.
- Tags are normalised (trim + lower-case) in the service, not in the controller or the UI.
