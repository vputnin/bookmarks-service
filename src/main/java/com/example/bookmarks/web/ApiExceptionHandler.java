package com.example.bookmarks.web;

import com.example.bookmarks.bookmark.BookmarkNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps domain exceptions to RFC 9457 problem details.
 * Validation errors are already turned into problem details by Spring
 * (spring.mvc.problemdetails.enabled=true).
 */
@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(BookmarkNotFoundException.class)
    ProblemDetail handleNotFound(BookmarkNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Bookmark not found");
        return problem;
    }
}
