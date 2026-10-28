package com.rogers.api.exception;

import com.rogers.api.constants.ApiConstants;
import org.springframework.http.HttpStatus;

public class CommBusinessException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public CommBusinessException(String message){
        this(HttpStatus.OK, ApiConstants.ERROR_CODE_50000, message);
    }

    public CommBusinessException(String message, Throwable exception){
        this(HttpStatus.OK, ApiConstants.ERROR_CODE_50000, message, exception);
    }
    public CommBusinessException( String errorCode, String message){
        this(HttpStatus.OK, errorCode, message);
    }
    public CommBusinessException(HttpStatus httpStatus, String errorCode, String message) {

        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public CommBusinessException(HttpStatus httpStatus, String errorCode, String message, Throwable exception) {
        super(message, exception);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
