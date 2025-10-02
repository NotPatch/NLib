package com.notpatch.nlib.compatibility.check;

import com.notpatch.nlib.compatibility.CompatibilityResult;

public interface CompatibilityCheck {
    CompatibilityResult check();
    void setOnSuccessAction(Runnable action);
    Runnable getOnSuccessAction();
}