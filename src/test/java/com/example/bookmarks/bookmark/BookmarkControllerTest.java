package com.example.bookmarks.bookmark;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-context API test against the real in-memory repository (no mocks needed).
 * Each test gets a fresh context so stored bookmarks don't leak between tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void createReturns201WithLocation() throws Exception {
        mvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"url": "https://spring.io", "title": "Spring", "tags": ["Java"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Spring"))
                .andExpect(jsonPath("$.tags[0]").value("java"));
    }

    @Test
    void createRejectsInvalidUrlWithProblemDetail() throws Exception {
        mvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"url": "not-a-url", "title": "Bad"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void listReturnsCreatedBookmarks() throws Exception {
        mvc.perform(post("/api/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"url": "https://a.dev", "title": "A"}
                        """));

        mvc.perform(get("/api/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].url").value("https://a.dev"));
    }

    @Test
    void listFiltersByTagCaseInsensitively() throws Exception {
        create("""
                {"url": "https://spring.io", "title": "Spring", "tags": ["java", "spring"]}
                """);
        create("""
                {"url": "https://go.dev", "title": "Go", "tags": ["go"]}
                """);

        mvc.perform(get("/api/bookmarks").param("tag", "JAVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring"));
    }

    @Test
    void listWithUnknownTagReturnsEmptyArray() throws Exception {
        create("""
                {"url": "https://go.dev", "title": "Go", "tags": ["go"]}
                """);

        mvc.perform(get("/api/bookmarks").param("tag", "rust"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listWithoutTagReturnsAllBookmarks() throws Exception {
        create("""
                {"url": "https://spring.io", "title": "Spring", "tags": ["java"]}
                """);
        create("""
                {"url": "https://go.dev", "title": "Go", "tags": ["go"]}
                """);

        mvc.perform(get("/api/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private void create(String json) throws Exception {
        mvc.perform(post("/api/bookmarks").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void getUnknownReturns404ProblemDetail() throws Exception {
        mvc.perform(get("/api/bookmarks/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Bookmark not found"));
    }

    @Test
    void deleteUnknownReturns404() throws Exception {
        mvc.perform(delete("/api/bookmarks/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
