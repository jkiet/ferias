package com.example.ferias.model;

import java.util.List;

public class MetaDTO {
    private final String reason;
    private final List<String> errors;

    public MetaDTO() {
        this.reason = null;
        this.errors = null;
    }

    public MetaDTO(String reason) {
        this.reason = reason;
        this.errors = List.of();
    }

    public MetaDTO(String reason, String error) {
        this.reason = reason;
        this.errors = List.of(error);
    }

    public MetaDTO(String reason, List<String> errors) {
        this.reason = reason;
        this.errors = errors;
    }

    public String getReason() { return reason; }

    public List<String> getErrors() { return errors; }
}
