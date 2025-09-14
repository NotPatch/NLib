package com.notpatch.nlib.compatibility.check.impl;


import com.notpatch.nlib.compatibility.CompatibilityResult;
import com.notpatch.nlib.compatibility.CompatibilityStatus;
import com.notpatch.nlib.compatibility.check.CompatibilityCheck;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginCheck implements CompatibilityCheck {
    private final String pluginName;
    private final boolean required;

    public PluginCheck(String pluginName, boolean required) {
        this.pluginName = pluginName;
        this.required = required;
    }

    @Override
    public CompatibilityResult check() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        boolean isInstalled = plugin != null;
        boolean isEnabled = isInstalled && plugin.isEnabled();

        if (isEnabled) {
            String version = plugin.getDescription().getVersion();
            return new CompatibilityResult(CompatibilityStatus.PASS,
                    "Plugin " + pluginName + " v" + version + " found and enabled");
        } else if (isInstalled) {
            CompatibilityStatus status = required ? CompatibilityStatus.FAIL : CompatibilityStatus.WARNING;
            return new CompatibilityResult(status,
                    "Plugin " + pluginName + " found but disabled" +
                            (required ? " (REQUIRED)" : " (Optional)"));
        } else {
            CompatibilityStatus status = required ? CompatibilityStatus.FAIL : CompatibilityStatus.WARNING;
            return new CompatibilityResult(status,
                    "Plugin " + pluginName + " not found" +
                            (required ? " (REQUIRED)" : " (Optional)"));
        }
    }
}