package com.rogers.api.interceptors;

import jakarta.servlet.http.HttpServletRequest;

public interface BusinessApiValidation {
    default boolean isBusinessApiCall(HttpServletRequest request) {
        return isBusinessApiCall(request.getRequestURI());
    }

    default boolean isBusinessApiCall(String uri) {
        return !uri.contains("/actuator/") && !uri.contains("/redis/clearCache") && !uri.contains("/api-docs") && !uri.contains("/error");
    }
}
