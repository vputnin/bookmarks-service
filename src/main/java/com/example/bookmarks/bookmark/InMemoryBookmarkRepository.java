package com.example.bookmarks.bookmark;

import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory storage. Data is lost on restart by design: this project
 * is about the workflow, not persistence (see docs/architecture.md).
 */
@Repository
class InMemoryBookmarkRepository implements BookmarkRepository {

    private final Map<UUID, Bookmark> storage = new ConcurrentHashMap<>();

    @Override
    public Bookmark save(Bookmark bookmark) {
        storage.put(bookmark.id(), bookmark);
        return bookmark;
    }

    @Override
    public Optional<Bookmark> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Bookmark> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Bookmark::createdAt).reversed())
                .toList();
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }
}
