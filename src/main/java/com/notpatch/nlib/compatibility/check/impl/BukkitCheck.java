package com.notpatch.nlib.compatibility.check.impl;


import com.notpatch.nlib.compatibility.CompatibilityResult;
import com.notpatch.nlib.compatibility.CompatibilityStatus;
import com.notpatch.nlib.compatibility.check.CompatibilityCheck;
import org.bukkit.Bukkit;
import java.util.List;

public class BukkitCheck implements CompatibilityCheck {
    private final List<String> supportedImplementations;

    public BukkitCheck(List<String> supportedImplementations) {
        this.supportedImplementations = supportedImplementations;
    }

    @Override
    public CompatibilityResult check() {
        String serverName = Bukkit.getName().toLowerCase();
        String serverVersion = Bukkit.getVersion();

        boolean isSupported = supportedImplementations.stream()
                .anyMatch(impl -> serverName.contains(impl.toLowerCase()));

        if (isSupported) {
            return new CompatibilityResult(CompatibilityStatus.PASS,
                    "Server implementation '" + Bukkit.getName() + "' is supported");
        } else {
            return new CompatibilityResult(CompatibilityStatus.WARNING,
                    "Server implementation '" + Bukkit.getName() + "' is untested. " +
                            "Supported: " + String.join(", ", supportedImplementations));
        }
    }
}