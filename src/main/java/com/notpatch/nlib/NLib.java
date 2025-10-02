package com.notpatch.nlib;

import com.notpatch.nlib.manager.CooldownManager;
import com.notpatch.nlib.util.NLogger;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import fr.mrmicky.fastinv.FastInvManager;
import lombok.Getter;
import org.bukkit.command.CommandSender;
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

    @Getter
    private static BukkitCommandManager<CommandSender> commandManager;

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

        cooldownManager = CooldownManager.getInstance();

        FastInvManager.register(plugin);

        commandManager = BukkitCommandManager.create(plugin);

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
