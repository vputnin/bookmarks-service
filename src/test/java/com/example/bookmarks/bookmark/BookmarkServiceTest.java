package com.example.bookmarks.bookmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookmarkServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    private BookmarkService service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new BookmarkService(new InMemoryBookmarkRepository(), fixedClock);
    }

    @Test
    void createsBookmarkWithNormalizedTagsAndTimestamp() {
        Bookmark created = service.create(new CreateBookmarkRequest(
                " https://spring.io ", " Spring ", Set.of("Java", " SPRING ")));

        assertThat(created.url()).isEqualTo("https://spring.io");
        assertThat(created.title()).isEqualTo("Spring");
        assertThat(created.tags()).containsExactlyInAnyOrder("java", "spring");
        assertThat(created.createdAt()).isEqualTo(NOW);
    }

    @Test
    void createsBookmarkWithoutTags() {
        Bookmark created = service.create(new CreateBookmarkRequest("https://a.dev", "A", null));

        assertThat(created.tags()).isEmpty();
    }

    @Test
    void findByTagMatchesNormalizedTag() {
        service.create(new CreateBookmarkRequest("https://spring.io", "Spring", Set.of("Java")));
        service.create(new CreateBookmarkRequest("https://go.dev", "Go", Set.of("go")));

        assertThat(service.findByTag(" JAVA "))
                .extracting(Bookmark::title)
                .containsExactly("Spring");
    }

    @Test
    void findByTagReturnsEmptyListWhenNothingMatches() {
        service.create(new CreateBookmarkRequest("https://go.dev", "Go", Set.of("go")));

        assertThat(service.findByTag("rust")).isEmpty();
    }

    @Test
    void findByBlankTagReturnsAllBookmarks() {
        service.create(new CreateBookmarkRequest("https://spring.io", "Spring", Set.of("java")));
        service.create(new CreateBookmarkRequest("https://go.dev", "Go", Set.of("go")));

        assertThat(service.findByTag("  ")).hasSize(2);
    }

    @Test
    void getThrowsWhenBookmarkIsMissing() {
        UUID missing = UUID.randomUUID();

        assertThatThrownBy(() -> service.get(missing))
                .isInstanceOf(BookmarkNotFoundException.class)
                .hasMessageContaining(missing.toString());
    }

    @Test
    void deleteRemovesBookmark() {
        Bookmark created = service.create(new CreateBookmarkRequest("https://a.dev", "A", Set.of()));

        service.delete(created.id());

        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void deleteThrowsWhenBookmarkIsMissing() {
        assertThatThrownBy(() -> service.delete(UUID.randomUUID()))
                .isInstanceOf(BookmarkNotFoundException.class);
    }
}
