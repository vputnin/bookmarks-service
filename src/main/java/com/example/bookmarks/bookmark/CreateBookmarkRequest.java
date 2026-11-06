package com.example.bookmarks.bookmark;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateBookmarkRequest(
        @NotBlank
        @Size(max = 2048)
        @Pattern(regexp = "https?://.+", message = "must start with http:// or https://")
        String url,

        @NotBlank
        @Size(max = 200)
        String title,

        Set<@NotBlank @Size(max = 30) String> tags
) {

    public Set<String> tagsOrEmpty() {
        return tags == null ? Set.of() : tags;
    }
}
