package com.rogers.api.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rogers.api.constants.ApiConstants;
import com.rogers.api.exception.*;
import com.rogers.api.logger.CommonLogger;
import com.rogers.response.Error;
import com.rogers.response.Meta;
import com.rogers.response.Response;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionAdvisor {
    private final CommonLogger logger = CommonLogger.getLogger(this.getClass());

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        List<Error> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Error.of(null, ApiConstants.ERROR_CODE_50000, fe.getField() + ": " + fe.getDefaultMessage()))
                .toList();
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + "=" + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.error("Validation failed: " + details, ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input", errors, getRequestPath(request));
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<Object> handleBindException(BindException ex, WebRequest request) {
        List<Error> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Error.of(null, ApiConstants.ERROR_CODE_50000, fe.getField() + ": " + fe.getDefaultMessage()))
                .toList();
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + "=" + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.error("Bind validation failed: " + details, ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input", errors, getRequestPath(request));
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<Error> errors = ex.getConstraintViolations().stream()
                .map(cv -> Error.of(null, ApiConstants.ERROR_CODE_50000, cv.getPropertyPath() + ": " + cv.getMessage()))
                .toList();
        String details = errors.stream().map(Error::getDetail).collect(Collectors.joining("; "));
        logger.error("Constraint violations: " + details, ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input", errors, getRequestPath(request));
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity<Object> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        logger.error("Invalid Input Exception: ", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Invalid Input Exception: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        logger.error("Illegal State Exception: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Illegal State, no data found for the appContextId : " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {FeignException.class})
    public ResponseEntity<Object> handleFeignException(FeignException ex, WebRequest request) {
        logger.error("Common service Exceptions: ", ex);
        String remoteUrl = extractFeignUrl(ex, request);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Common service Exception: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                remoteUrl);
    }

    @ExceptionHandler(value = {CustomizedFeignException.class})
    public ResponseEntity<Object> handleCustomerFeignException(CustomizedFeignException ex, WebRequest request) {
        logger.error("Common service Exceptions: ", ex);
        if (ex.getApiResponse() instanceof Response<?> remoteResponse) {
            return new ResponseEntity<>(remoteResponse, ex.getHttpStatus());
        }
        String remoteUrl = ex.getRemoteUrl() != null ? ex.getRemoteUrl() : getRequestPath(request);
        return buildErrorResponse(ex.getHttpStatus(),
                "Common service Exception: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                remoteUrl);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex, WebRequest request) {
        logger.error("Null point Exception: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Null point Exception in the application",
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {CommerceException.class})
    public ResponseEntity<Object> handleCommerceException(CommerceException ex, WebRequest request) {
        logger.error("Commerce Exception: ", ex);
        return buildErrorResponse(ex.getHttpStatus(),
                "Commerce Exception: " + ex.getMessage(),
                List.of(Error.of(null, ex.getErrorCode(), ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {CommBusinessException.class})
    public ResponseEntity<Object> handleBusinessException(CommBusinessException ex, WebRequest request) {
        logger.error("Business Exceptions: ", ex);
        return buildErrorResponse(ex.getHttpStatus(),
                "Business Exception: " + ex.getMessage(),
                List.of(Error.of(null, ex.getErrorCode(), ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        logger.error("Unauthorized Exceptions: ", ex);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                "Unauthorized Exception: " + ex.getMessage(),
                List.of(Error.of(null, String.valueOf(HttpStatus.UNAUTHORIZED.value()), ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {JsonProcessingException.class})
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        logger.error("JSON Processing Exception: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "JSON Processing Error: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {java.security.NoSuchAlgorithmException.class})
    public ResponseEntity<Object> handleNoSuchAlgorithmException(java.security.NoSuchAlgorithmException ex, WebRequest request) {
        logger.error("No Such Algorithm Exception: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Algorithm Error: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {java.text.ParseException.class})
    public ResponseEntity<Object> handleParseException(java.text.ParseException ex, WebRequest request) {
        logger.error("Parse Exception: ", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Parse Error: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {java.net.URISyntaxException.class})
    public ResponseEntity<Object> handleURISyntaxException(java.net.URISyntaxException ex, WebRequest request) {
        logger.error("URI Syntax Exception: ", ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Invalid URI: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {RogersSoapFaultException.class})
    public ResponseEntity<Object> handleRogersSoapFaultException(RogersSoapFaultException ex, WebRequest request) {
        String code   = ex.getErrorCode() != null ? ex.getErrorCode() : "SOAP_ERROR";
        String type   = ex.getErrorType() != null ? ex.getErrorType().name() : "OtherType";
        String detail = ex.getMessage();
        logger.error("Rogers SOAP fault — code='{}' type='{}' detail='{}'", code, type, detail);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY,
                "Upstream SOAP service fault [" + type + "]: " + detail,
                List.of(Error.of(null, code, detail)),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        logger.warn("No handler found: " + ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                "Resource not found: " + ex.getResourcePath(),
                List.of(Error.of(null, "NOT_FOUND", ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected Exception: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error: " + ex.getMessage(),
                List.of(Error.of(null, ApiConstants.ERROR_CODE_50000, ex.getMessage())),
                getRequestPath(request));
    }

    @ExceptionHandler(feign.RetryableException.class)
    public ResponseEntity<Response<?>> handleRetryableException(feign.RetryableException ex, WebRequest request) {
        Throwable cause = ex.getCause();
        String message = ex.getMessage();
        String detail = message != null && !message.isBlank() ? message : (cause != null && cause.getMessage() != null ? cause.getMessage() : "upstream service unavailable");

        String causeClass = cause == null ? "" : cause.getClass().getName();
        String code = switch (causeClass) {
            case "java.net.ConnectException"      -> "UPSTREAM_UNREACHABLE";
            case "java.net.SocketTimeoutException" -> "UPSTREAM_TIMEOUT";
            case "java.net.UnknownHostException"  -> "UPSTREAM_DNS";
            default                               -> "UPSTREAM_UNAVAILABLE";
        };

        String targetUrl = null;
        try {
            if (ex.request() != null && ex.request().url() != null) {
                targetUrl = ex.request().url();
            } else if (request instanceof ServletWebRequest swr) {
                targetUrl = swr.getRequest().getRequestURI();
            } else if (request != null) {
                targetUrl = getRequestPath(request);
            }
        } catch (Exception e) {
            logger.debug("Unable to derive target URL for RetryableException", e);
        }

        List<String> metaDetails = targetUrl == null ? List.of(detail) : List.of(detail, "url:" + targetUrl);

        Response<?> body = Response.<Void>builder()
                .meta(Meta.of(HttpStatus.SERVICE_UNAVAILABLE, metaDetails))
                .errors(List.of(Error.of(null, code, detail)))
                .build();

        logger.debug("RetryableException intercepted; mapped to code='" + code + "' detail='" + detail + "' targetUrl='" + targetUrl + "'", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private ResponseEntity<Object> buildErrorResponse(HttpStatusCode status, String detail, List<Error> errors, String url) {
        List<String> details = new ArrayList<>();
        details.add(detail);
        if (url != null && !url.isBlank()) {
            details.add("url:" + url);
        }
        Response<?> response = Response.<Void>builder()
                .meta(Meta.of(status, details))
                .errors(errors)
                .build();
        return new ResponseEntity<>(response, status);
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }

    private String extractFeignUrl(FeignException ex, WebRequest request) {
        try {
            if (ex.request() != null && ex.request().url() != null) {
                return ex.request().url();
            }
        } catch (Exception e) {
            logger.debug("Unable to extract remote URL from FeignException", e);
        }
        return getRequestPath(request);
    }
}
