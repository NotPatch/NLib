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
    private CompatibilityCheck lastCheck = null;
    private boolean hasErrors = false;
    private boolean hasWarnings = false;

    public NCompatibility checkVersion(@NonNull String minVersion, @NonNull String maxVersion) {
        VersionCheck check = new VersionCheck(minVersion, maxVersion);
        checks.add(check);
        lastCheck = check;
        return this;
    }

    public NCompatibility checkVersion(@NonNull String... supportedVersions) {
        VersionCheck check = new VersionCheck(Arrays.asList(supportedVersions));
        checks.add(check);
        lastCheck = check;
        return this;
    }

    public NCompatibility checkPlugin(@NonNull String pluginName, boolean required) {
        PluginCheck check = new PluginCheck(pluginName, required);
        checks.add(check);
        lastCheck = check;
        return this;
    }

    public NCompatibility checkBukkit(@NonNull String... supportedImplementations) {
        BukkitCheck check = new BukkitCheck(Arrays.asList(supportedImplementations));
        checks.add(check);
        lastCheck = check;
        return this;
    }

    public NCompatibility onSuccess(Runnable action){
        if(lastCheck != null){
            lastCheck.setOnSuccessAction(action);
        }else{
            NLogger.warn("No last check to set onSuccess action for!");
        }
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
                    if (check.getOnSuccessAction() != null) {
                        try {
                            check.getOnSuccessAction().run();
                        } catch (Exception e) {
                            NLogger.error("Error executing onSuccess action for check: " + e.getMessage());
                        }
                    }
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