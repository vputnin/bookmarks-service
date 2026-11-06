# Tasks

Single source of truth for work in progress. Claude reads it at the start of work
and updates it in `/wrap`. Keep each task small enough for one branch.

## In progress

### T-2 Delete from the UI
The API already supports `DELETE /api/bookmarks/{id}`. Add a "Delete" button to each list item.
- [x] button per item, list refreshes after delete
- [x] error message if the request fails

## Backlog

### T-3 Reject duplicate URLs
Creating a bookmark with a URL that already exists returns `409 Conflict` as a ProblemDetail.
URL comparison ignores a trailing slash.
- [ ] domain exception + handler mapping
- [ ] tests for 409 and the trailing-slash case
- [ ] UI shows the problem `detail`

### T-4 Mark as read
`PATCH /api/bookmarks/{id}/read` toggles a `read` flag. The UI shows read items dimmed with a toggle.
- [ ] `read` field on `Bookmark`, default false
- [ ] endpoint + tests
- [ ] UI toggle

## Done

### T-1 Filter bookmarks by tag — done 2026-11-06
`GET /api/bookmarks?tag=java` returns only bookmarks with that tag. Tag match is case-insensitive.
Without the parameter the endpoint behaves as today.
- [x] service + repository support
- [x] controller param
- [x] unit test + MockMvc test
- [x] UI: clicking a tag chip filters the list; a "Show all" link resets it

## Session log
<!-- /wrap appends one short entry per session: date, what changed, what's next. -->
- 2026-11-06: T-1 tag filter merged to main (API `?tag=`, UI chip filter + Show all, review fixes; 19 tests). Hooks verified. Next: T-2 Delete from the UI.
