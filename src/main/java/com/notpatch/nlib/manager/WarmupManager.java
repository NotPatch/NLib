package com.notpatch.nlib.manager;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.builder.WarmupBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarmupManager {

    private static final WarmupManager INSTANCE = new WarmupManager();
    private final Map<UUID, WarmupBuilder> activeWarmups = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    private WarmupManager() {}

    public static WarmupManager getInstance() {
        return INSTANCE;
    }

    public void startWarmup(WarmupBuilder builder) {
        Player player = builder.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (isWarmingUp(player)) {
            cancelWarmup(player, true);
        }

        if (builder.getOnStart() != null) {
            builder.getOnStart().accept(player);
        }

        long ticks = builder.getTimeUnit().toSeconds(builder.getDuration()) * 20L;

        BukkitTask task = Bukkit.getScheduler().runTaskLater(NLib.getInstance().getPlugin(), () -> {
            builder.getOnSuccess().run();
            clearWarmup(playerUUID);
        }, ticks);

        activeWarmups.put(playerUUID, builder);
        activeTasks.put(playerUUID, task);
    }

    public void cancelWarmup(Player player, boolean silent) {
        UUID playerUUID = player.getUniqueId();
        if (!isWarmingUp(player)) {
            return;
        }

        activeTasks.get(playerUUID).cancel();

        if (!silent && activeWarmups.get(playerUUID).getOnCancel() != null) {
            activeWarmups.get(playerUUID).getOnCancel().accept(player);
        }

        clearWarmup(playerUUID);
    }

    public boolean isWarmingUp(Player player) {
        return activeWarmups.containsKey(player.getUniqueId());
    }

    private void clearWarmup(UUID playerUUID) {
        activeWarmups.remove(playerUUID);
        activeTasks.remove(playerUUID);
    }
}