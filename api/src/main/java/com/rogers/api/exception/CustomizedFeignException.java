package com.rogers.api.exception;

import com.rogers.response.Response;
import org.springframework.http.HttpStatus;

public class CustomizedFeignException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final Response<?> apiResponse;
    private final String remoteUrl;

    // preserve existing constructor for backward compatibility
    public CustomizedFeignException(HttpStatus httpStatus, String message, Throwable exception) {
        super(message, exception);
        this.httpStatus = httpStatus;
        this.apiResponse = null;
        this.remoteUrl = null;
    }

    // new constructor that carries parsed ApiResponse (when remote service already responded with our wrapper)
    public CustomizedFeignException(HttpStatus httpStatus, String message, Throwable exception, Response<?> apiResponse) {
        super(message, exception);
        this.httpStatus = httpStatus;
        this.apiResponse = apiResponse;
        this.remoteUrl = null;
    }

    // constructor with remote URL
    public CustomizedFeignException(HttpStatus httpStatus, String message, Throwable exception, Response<?> apiResponse, String remoteUrl) {
        super(message, exception);
        this.httpStatus = httpStatus;
        this.apiResponse = apiResponse;
        this.remoteUrl = remoteUrl;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Response<?> getApiResponse() {
        return apiResponse;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

}
