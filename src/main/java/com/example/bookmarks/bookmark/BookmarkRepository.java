package com.example.bookmarks.bookmark;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository {

    Bookmark save(Bookmark bookmark);

    Optional<Bookmark> findById(UUID id);

    /** All bookmarks, newest first. */
    List<Bookmark> findAll();

    boolean deleteById(UUID id);
}
