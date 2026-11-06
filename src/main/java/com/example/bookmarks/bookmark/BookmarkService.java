package com.example.bookmarks.bookmark;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    private final BookmarkRepository repository;
    private final Clock clock;

    public BookmarkService(BookmarkRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Bookmark create(CreateBookmarkRequest request) {
        Bookmark bookmark = new Bookmark(
                UUID.randomUUID(),
                request.url().trim(),
                request.title().trim(),
                normalizeTags(request.tagsOrEmpty()),
                Instant.now(clock));
        return repository.save(bookmark);
    }

    public List<Bookmark> findAll() {
        return repository.findAll();
    }

    public Bookmark get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new BookmarkNotFoundException(id));
    }

    public void delete(UUID id) {
        if (!repository.deleteById(id)) {
            throw new BookmarkNotFoundException(id);
        }
    }

    /** Tags are stored trimmed and lower-cased so "Java" and "java " are the same tag. */
    static Set<String> normalizeTags(Set<String> tags) {
        return tags.stream()
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
