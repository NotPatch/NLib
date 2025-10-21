package com.notpatch.nlib;

import com.notpatch.nlib.fastinv.FastInvManager;
import com.notpatch.nlib.listener.PlayerMoveListener;
import com.notpatch.nlib.manager.CooldownManager;
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

    @Getter
    private static CooldownManager cooldownManager;

    private NLib() {}

    public static NLib initialize(JavaPlugin plugin) {
        if (instance != null && instance.initialized) {
            return instance;
        }

        if (instance == null) {
            instance = new NLib();
        }

        instance.pluginName = plugin.getName();
        instance.initialized = true;
        instance.plugin = plugin;

        FastInvManager.register(plugin);

        cooldownManager = CooldownManager.getInstance();

        NLogger.info("NLib initialized.");

        plugin.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), plugin);

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
