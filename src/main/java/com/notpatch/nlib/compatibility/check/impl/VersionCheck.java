package com.notpatch.nlib.compatibility.check.impl;

import com.notpatch.nlib.compatibility.CompatibilityResult;
import com.notpatch.nlib.compatibility.CompatibilityStatus;
import com.notpatch.nlib.compatibility.check.CompatibilityCheck;
import org.bukkit.Bukkit;
import java.util.List;

public class VersionCheck implements CompatibilityCheck {
    private final String minVersion;
    private final String maxVersion;
    private final List<String> supportedVersions;
    private Runnable onSuccessAction;

    public VersionCheck(String minVersion, String maxVersion) {
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.supportedVersions = null;
    }

    public VersionCheck(List<String> supportedVersions) {
        this.minVersion = null;
        this.maxVersion = null;
        this.supportedVersions = supportedVersions;
    }

    @Override
    public CompatibilityResult check() {
        String currentVersion = Bukkit.getBukkitVersion().split("-")[0];

        if (supportedVersions != null) {
            boolean supported = supportedVersions.stream()
                    .anyMatch(version -> currentVersion.startsWith(version));

            if (supported) {
                return new CompatibilityResult(CompatibilityStatus.PASS,
                        "Server version " + currentVersion + " is supported");
            } else {
                return new CompatibilityResult(CompatibilityStatus.FAIL,
                        "Server version " + currentVersion + " is not supported. Supported: " +
                                String.join(", ", supportedVersions));
            }
        } else {
            if (isVersionInRange(currentVersion, minVersion, maxVersion)) {
                return new CompatibilityResult(CompatibilityStatus.PASS,
                        "Server version " + currentVersion + " is compatible (Range: " +
                                minVersion + " - " + maxVersion + ")");
            } else {
                boolean tooOld = compareVersions(currentVersion, minVersion) < 0;
                boolean tooNew = compareVersions(currentVersion, maxVersion) > 0;

                CompatibilityStatus status = (tooNew) ? CompatibilityStatus.WARNING : CompatibilityStatus.FAIL;
                String reason = tooOld ? "too old" : "untested/newer";

                return new CompatibilityResult(status,
                        "Server version " + currentVersion + " is " + reason +
                                " (Supported: " + minVersion + " - " + maxVersion + ")");
            }
        }
    }

    @Override
    public void setOnSuccessAction(Runnable action) {
        this.onSuccessAction = action;
    }

    @Override
    public Runnable getOnSuccessAction() {
        return this.onSuccessAction;
    }

    private boolean isVersionInRange(String version, String min, String max) {
        return compareVersions(version, min) >= 0 && compareVersions(version, max) <= 0;
    }

    private int compareVersions(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");

        int maxLength = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < maxLength; i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part != v2Part) {
                return Integer.compare(v1Part, v2Part);
            }
        }

        return 0;
    }


}