package com.rogers.api.interceptors;

import com.rogers.api.constants.ApiConstants;
import com.rogers.api.exception.CommerceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for {@link HeaderInterceptor}.
 */
class HeaderInterceptorTest {

    private HeaderInterceptor interceptor;
    private MockHttpServletResponse response;
    private Object handler;

    @BeforeEach
    void setUp() {
        interceptor = new HeaderInterceptor();
        response    = new MockHttpServletResponse();
        handler     = new Object();
    }

    // ── Actuator / non-business paths pass through without header validation ──

    @Test
    @DisplayName("preHandle: actuator path skips header validation and returns true")
    void preHandle_actuatorPath_skipsValidation() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        boolean result = assertDoesNotThrow(() -> interceptor.preHandle(request, response, handler));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("preHandle: swagger api-docs path skips header validation")
    void preHandle_apiDocsPath_skipsValidation() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api-docs");
        boolean result = assertDoesNotThrow(() -> interceptor.preHandle(request, response, handler));
        assertThat(result).isTrue();
    }

    // ── Business API paths require all headers ────────────────────────────────

    @Test
    @DisplayName("preHandle: business path with no headers throws CommerceException")
    void preHandle_businessPath_noHeaders_throws() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/some-resource");
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
                .isInstanceOf(CommerceException.class)
                .hasMessageContaining("No required HTTP headers were found");
    }

    @Test
    @DisplayName("preHandle: business path with partial headers throws CommerceException with missing list")
    void preHandle_businessPath_partialHeaders_throws() {
        MockHttpServletRequest request = buildRequestWithAllHeaders();
        // Remove one required header to simulate partial set
        request.removeHeader(ApiConstants.HEADER_TRANSACTION_ID);

        assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
                .isInstanceOf(CommerceException.class)
                .hasMessageContaining("Missing required HTTP headers");
    }

    @Test
    @DisplayName("preHandle: business path with all required headers returns true")
    void preHandle_businessPath_allHeaders_returnsTrue() {
        MockHttpServletRequest request = buildRequestWithAllHeaders();
        boolean result = assertDoesNotThrow(() -> interceptor.preHandle(request, response, handler));
        assertThat(result).isTrue();
    }

    // ── afterCompletion cleans up ThreadLocal ─────────────────────────────────

    @Test
    @DisplayName("afterCompletion: removes context without error")
    void afterCompletion_removesContext() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/resource");
        assertDoesNotThrow(() -> interceptor.afterCompletion(request, response, handler, null));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private MockHttpServletRequest buildRequestWithAllHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/some-resource");
        request.addHeader(ApiConstants.HEADER_DEALER_CODE,     "D001");
        request.addHeader(ApiConstants.HEADER_CHAIN,           "ROGERS");
        request.addHeader(ApiConstants.HEADER_REGION,          "ON");
        request.addHeader(ApiConstants.HEADER_STORE,           "S100");
        request.addHeader(ApiConstants.HEADER_APPLICATION_ID,  "APP01");
        request.addHeader(ApiConstants.HEADER_USER_ID,         "user123");
        request.addHeader(ApiConstants.HEADER_SESSION_ID,      "sess-abc");
        request.addHeader(ApiConstants.HEADER_TRANSACTION_ID,  "txn-xyz");
        request.addHeader(ApiConstants.HEADER_REQUEST_ID,      "req-001");
        request.addHeader(ApiConstants.HEADER_WEB_USER_ID,     "webuser1");
        request.addHeader(ApiConstants.HEADER_LANGUAGE,        "en");
        request.addHeader(ApiConstants.HEADER_IP_ADDRESS,      "10.0.0.1");
        request.addHeader(ApiConstants.HEADER_ENV,             "dev");
        return request;
    }
}


