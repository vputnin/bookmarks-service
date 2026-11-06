package com.example.bookmarks.bookmark;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/** A saved link. Immutable value object. */
public record Bookmark(UUID id, String url, String title, Set<String> tags, Instant createdAt) {

    public Bookmark {
        tags = Set.copyOf(tags);
    }
}
