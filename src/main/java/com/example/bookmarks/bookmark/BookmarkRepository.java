package com.example.bookmarks.bookmark;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository {

    Bookmark save(Bookmark bookmark);

    Optional<Bookmark> findById(UUID id);

    /** All bookmarks, newest first. */
    List<Bookmark> findAll();

    /** Bookmarks carrying exactly this (already normalised) tag, newest first. */
    List<Bookmark> findByTag(String tag);

    boolean deleteById(UUID id);
}
