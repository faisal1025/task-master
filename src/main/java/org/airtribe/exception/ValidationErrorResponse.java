package org.airtribe.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationErrorResponse {
    private String message;
    private LocalDateTime timeStamp;
    private Map<String, String> fieldErrors = new LinkedHashMap<>();

    public ValidationErrorResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void addFieldErrors(String field, String message) {
        this.fieldErrors.put(field, message);
    }
}

