package com.rogers.api.advice;

import com.rogers.api.exception.CommBusinessException;
import com.rogers.api.exception.CommerceException;
import com.rogers.api.exception.InvalidInputException;
import com.rogers.api.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ExceptionAdvisor}.
 * Covers all @ExceptionHandler methods for correct HTTP status and response body.
 */
class ExceptionAdvisorTest {

    private ExceptionAdvisor advisor;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        advisor    = new ExceptionAdvisor();
        webRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/v1/test"));
    }

    // ── CommerceException ─────────────────────────────────────────────────────

    @Test
    @DisplayName("handleCommerceException: maps to the exception's own HttpStatus")
    void handleCommerceException_returnsExceptionStatus() {
        CommerceException ex = new CommerceException(HttpStatus.NOT_FOUND, "ERR001", "resource not found");
        ResponseEntity<Object> response = advisor.handleCommerceException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("handleCommerceException: response body is not null")
    void handleCommerceException_responseBodyNotNull() {
        CommerceException ex = new CommerceException(HttpStatus.BAD_REQUEST, "50000", "bad input");
        ResponseEntity<Object> response = advisor.handleCommerceException(ex, webRequest);
        assertThat(response.getBody()).isNotNull();
    }

    // ── CommBusinessException ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleBusinessException: maps to the exception's own HttpStatus")
    void handleBusinessException_returnsExceptionStatus() {
        CommBusinessException ex = new CommBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "BIZ001", "business rule violated");
        ResponseEntity<Object> response = advisor.handleBusinessException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // ── InvalidInputException ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleInvalidInputException: returns 400 BAD_REQUEST")
    void handleInvalidInputException_returns400() {
        InvalidInputException ex = new InvalidInputException("field X is required");
        ResponseEntity<Object> response = advisor.handleInvalidInputException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── UnauthorizedException ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleUnauthorizedException: returns 401 UNAUTHORIZED")
    void handleUnauthorizedException_returns401() {
        UnauthorizedException ex = new UnauthorizedException("invalid token");
        ResponseEntity<Object> response = advisor.handleUnauthorizedException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── NullPointerException ──────────────────────────────────────────────────

    @Test
    @DisplayName("handleNullPointerException: returns 500 INTERNAL_SERVER_ERROR")
    void handleNullPointerException_returns500() {
        NullPointerException ex = new NullPointerException("null ref");
        ResponseEntity<Object> response = advisor.handleNullPointerException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ── IllegalStateException ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleIllegalStateException: returns 500 INTERNAL_SERVER_ERROR")
    void handleIllegalStateException_returns500() {
        IllegalStateException ex = new IllegalStateException("bad state");
        ResponseEntity<Object> response = advisor.handleIllegalStateException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ── Generic Exception ─────────────────────────────────────────────────────

    @Test
    @DisplayName("handleGenericException: returns 500 for unexpected exception")
    void handleGenericException_returns500() {
        Exception ex = new Exception("unexpected");
        ResponseEntity<Object> response = advisor.handleGenericException(ex, webRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

