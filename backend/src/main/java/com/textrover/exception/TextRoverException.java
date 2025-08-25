package com.textrover.exception;

public class TextRoverException extends RuntimeException {
    private final String errorCode;
    
    public TextRoverException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public TextRoverException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
