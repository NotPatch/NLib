package com.notpatch.nlib.compatibility;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.compatibility.check.CompatibilityCheck;
import com.notpatch.nlib.compatibility.check.impl.BukkitCheck;
import com.notpatch.nlib.compatibility.check.impl.PluginCheck;
import com.notpatch.nlib.compatibility.check.impl.VersionCheck;
import com.notpatch.nlib.util.NLogger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class NCompatibility {

    private final List<CompatibilityCheck> checks = new ArrayList<>();
    private boolean hasErrors = false;
    private boolean hasWarnings = false;

    public NCompatibility checkVersion(@NonNull String minVersion, @NonNull String maxVersion) {
        checks.add(new VersionCheck(minVersion, maxVersion));
        return this;
    }

    public NCompatibility checkVersion(@NonNull String... supportedVersions) {
        checks.add(new VersionCheck(Arrays.asList(supportedVersions)));
        return this;
    }

    public NCompatibility checkPlugin(@NonNull String pluginName, boolean required) {
        checks.add(new PluginCheck(pluginName, required));
        return this;
    }

    public NCompatibility checkBukkit(@NonNull String... supportedImplementations) {
        checks.add(new BukkitCheck(Arrays.asList(supportedImplementations)));
        return this;
    }

    public NCompatibility validate() {
        boolean allPassed = true;

        NLogger.info("§7--- Running compatibility checks for §b" + NLib.getInstance().getPluginName() + " §7---");

        for (CompatibilityCheck check : checks) {
            CompatibilityResult result = check.check();

            switch (result.getStatus()) {
                case PASS:
                    NLogger.info("§a✓ " + result.getMessage());
                    break;
                case WARNING:
                    hasWarnings = true;
                    NLogger.info("§6⚠ " + result.getMessage());
                    break;
                case FAIL:
                    hasErrors = true;
                    allPassed = false;
                    NLogger.info("§c✗ " + result.getMessage());
                    break;
            }
        }

        if (hasErrors) {
            NLogger.error("Plugin cannot continue due to compatibility issues!");
            Bukkit.getPluginManager().disablePlugin(NLib.getInstance().getPlugin());
        } else if (hasWarnings) {
            NLogger.warn("Plugin loaded with warnings!");
        } else {
            NLogger.info("All compatibility checks passed!");
        }

        return this;
    }


}