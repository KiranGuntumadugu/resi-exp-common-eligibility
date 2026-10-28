package com.rogers.api.exception;

public class RogersSoapFaultException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;
    private final ErrorTypeEnum errorType;

    public RogersSoapFaultException(String message) {
        super(message);
        this.errorCode = null;
        this.errorMessage = message;
        this.errorType = null;
    }

    public RogersSoapFaultException(String message, String errorCode, String errorMessage, ErrorTypeEnum errorType) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public RogersSoapFaultException(String message, String errorCode, String errorMessage, ErrorTypeEnum errorType, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    public enum ErrorTypeEnum {
        EIFExceptionType, SSGExceptionType, OtherType
    }
}
