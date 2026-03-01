package org.airtribe.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    OPEN("open"),
    COMPLETED("completed"),
    DUE("due");

    private String status;

    private Status(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return this.status;
    }
}
