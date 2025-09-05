package com.notpatch.nlib;

import com.notpatch.nlib.util.NLogger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class NLib {

    private static NLib instance;

    @Getter
    private JavaPlugin plugin;
    @Getter
    private String pluginName;

    private boolean initialized = false;

    private NLib() {}

    public static NLib initialize(JavaPlugin plugin) {
        if (instance == null) {
            instance = new NLib();
        }
        instance.pluginName = plugin.getName();
        instance.initialized = true;
        instance.plugin = plugin;

        NLogger.info("NLib initialized.");


        return instance;
    }

    public static NLib getInstance() {
        if (instance == null || !instance.initialized) {
            throw new IllegalStateException("NLib is not initialized! Call NLib.initialize() first.");
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null && instance.initialized;
    }

}
