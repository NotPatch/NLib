package com.notpatch.nlib.compatibility;

import lombok.Getter;

@Getter
public class CompatibilityResult {
    private final CompatibilityStatus status;
    private final String message;

    public CompatibilityResult(CompatibilityStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}