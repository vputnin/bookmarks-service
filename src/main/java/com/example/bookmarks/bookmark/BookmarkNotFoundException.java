package com.example.bookmarks.bookmark;

import java.util.UUID;

public class BookmarkNotFoundException extends RuntimeException {

    public BookmarkNotFoundException(UUID id) {
        super("Bookmark " + id + " not found");
    }
}
