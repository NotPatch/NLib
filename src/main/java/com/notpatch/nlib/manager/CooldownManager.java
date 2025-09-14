package com.notpatch.nlib.manager;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.builder.CooldownBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class CooldownManager {
    private static CooldownManager instance;

    private final Map<String, CooldownData> cooldownTypes = new HashMap<>();
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();
    private final Map<String, Map<UUID, BukkitTask>> notificationTasks = new HashMap<>();

    private CooldownManager() {}

    public static CooldownManager getInstance() {
        if (instance == null) {
            instance = new CooldownManager();
        }
        return instance;
    }

    public void registerCooldown(CooldownBuilder builder, JavaPlugin plugin) {
        cooldownTypes.put(builder.getName(), new CooldownData(builder, plugin));
        cooldowns.putIfAbsent(builder.getName(), new HashMap<>());
        notificationTasks.putIfAbsent(builder.getName(), new HashMap<>());
    }

    public CooldownManager getOrCreate(String cooldownName, Supplier<CooldownBuilder> builderSupplier) {
        if (!cooldownTypes.containsKey(cooldownName)) {
            CooldownBuilder builder = builderSupplier.get();
            registerCooldown(builder, NLib.getInstance().getPlugin());
        }
        return this;
    }

    public boolean isOnCooldown(@NonNull String cooldownName, @NonNull Player player) {
        return isOnCooldown(cooldownName, player.getUniqueId());
    }

    public boolean isOnCooldown(@NonNull String cooldownName, @NonNull Player player,
                                Supplier<CooldownBuilder> builderSupplier) {
        getOrCreate(cooldownName, builderSupplier);
        return isOnCooldown(cooldownName, player);
    }

    public boolean isOnCooldown(@NonNull String cooldownName, @NonNull UUID uuid) {
        Map<UUID, Long> playerCooldowns = cooldowns.get(cooldownName);
        if (playerCooldowns == null) return false;

        Long cooldownEnd = playerCooldowns.get(uuid);
        if (cooldownEnd == null) return false;

        if (System.currentTimeMillis() >= cooldownEnd) {
            playerCooldowns.remove(uuid);
            cancelNotificationTask(cooldownName, uuid);
            return false;
        }
        return true;
    }

    public void startCooldown(@NonNull String cooldownName, @NonNull Player player) {
        startCooldown(cooldownName, player.getUniqueId());

        CooldownData data = cooldownTypes.get(cooldownName);
        if (data != null && data.builder.getOnCooldownStarted() != null) {
            data.builder.getOnCooldownStarted().accept(player);
        }
    }

    public void startCooldown(@NonNull String cooldownName, @NonNull Player player,
                              Supplier<CooldownBuilder> builderSupplier) {
        getOrCreate(cooldownName, builderSupplier);
        startCooldown(cooldownName, player);
    }

    public void startCooldown(@NonNull String cooldownName, @NonNull UUID uuid) {
        CooldownData data = cooldownTypes.get(cooldownName);
        if (data == null) {
            throw new IllegalArgumentException("Cooldown tipi bulunamadı: " + cooldownName);
        }

        long cooldownEnd = System.currentTimeMillis() +
                data.builder.getTimeUnit().toMillis(data.builder.getDuration());

        Map<UUID, Long> playerCooldowns = cooldowns.get(cooldownName);
        playerCooldowns.put(uuid, cooldownEnd);

        if (data.builder.isNotifyOnFinish()) {
            setupNotificationTask(cooldownName, uuid, cooldownEnd);
        }
    }

    public long getRemainingTime(@NonNull String cooldownName, @NonNull Player player) {
        return getRemainingTime(cooldownName, player.getUniqueId());
    }

    public long getRemainingTime(@NonNull String cooldownName, @NonNull UUID uuid) {
        Map<UUID, Long> playerCooldowns = cooldowns.get(cooldownName);
        if (playerCooldowns == null) return 0L;

        Long cooldownEnd = playerCooldowns.get(uuid);
        if (cooldownEnd == null) return 0L;

        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0L, remaining);
    }

    public String getFormattedRemainingTime(@NonNull String cooldownName, @NonNull Player player) {
        return getFormattedRemainingTime(cooldownName, player.getUniqueId());
    }

    public String getFormattedRemainingTime(@NonNull String cooldownName, @NonNull UUID uuid) {
        long remainingMs = getRemainingTime(cooldownName, uuid);
        if (remainingMs <= 0) return "0s";

        long seconds = remainingMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public void clearCooldown(@NonNull String cooldownName, @NonNull Player player) {
        clearCooldown(cooldownName, player.getUniqueId());
    }

    public void clearCooldown(@NonNull String cooldownName, @NonNull UUID uuid) {
        Map<UUID, Long> playerCooldowns = cooldowns.get(cooldownName);
        if (playerCooldowns != null) {
            playerCooldowns.remove(uuid);
        }
        cancelNotificationTask(cooldownName, uuid);
    }

    public void sendCooldownMessage(@NonNull String cooldownName, @NonNull Player player) {
        CooldownData data = cooldownTypes.get(cooldownName);
        if (data == null) return;

        String message = data.builder.getOnCooldownMessage()
                .replace("{time}", getFormattedRemainingTime(cooldownName, player))
                .replace("{name}", cooldownName)
                .replace("&", "§");

        player.sendMessage(message);
    }

    public void clearAllCooldowns(@NonNull String cooldownName) {
        Map<UUID, Long> playerCooldowns = cooldowns.get(cooldownName);
        if (playerCooldowns != null) {
            playerCooldowns.clear();
        }

        Map<UUID, BukkitTask> tasks = notificationTasks.get(cooldownName);
        if (tasks != null) {
            tasks.values().forEach(BukkitTask::cancel);
            tasks.clear();
        }
    }

    public void clearAllCooldowns() {
        cooldowns.values().forEach(Map::clear);
        notificationTasks.values().forEach(taskMap -> {
            taskMap.values().forEach(BukkitTask::cancel);
            taskMap.clear();
        });
    }

    public java.util.Set<String> getRegisteredCooldowns() {
        return cooldownTypes.keySet();
    }

    public CooldownBuilder getCooldownBuilder(@NonNull String cooldownName) {
        CooldownData data = cooldownTypes.get(cooldownName);
        return data != null ? data.builder : null;
    }

    private void setupNotificationTask(String cooldownName, UUID uuid, long cooldownEnd) {
        cancelNotificationTask(cooldownName, uuid);

        CooldownData data = cooldownTypes.get(cooldownName);
        if (data == null) return;

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = data.plugin.getServer().getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    String message = data.builder.getCooldownFinishedMessage()
                            .replace("{name}", cooldownName)
                            .replace("&", "§");
                    player.sendMessage(message);

                    if (data.builder.getOnCooldownExpired() != null) {
                        data.builder.getOnCooldownExpired().accept(player);
                    }
                }

                Map<UUID, BukkitTask> tasks = notificationTasks.get(cooldownName);
                if (tasks != null) {
                    tasks.remove(uuid);
                }
            }
        }.runTaskLater(data.plugin, Math.max(1L, (cooldownEnd - System.currentTimeMillis()) / 50L));

        Map<UUID, BukkitTask> tasks = notificationTasks.get(cooldownName);
        if (tasks != null) {
            tasks.put(uuid, task);
        }
    }

    private void cancelNotificationTask(String cooldownName, UUID uuid) {
        Map<UUID, BukkitTask> tasks = notificationTasks.get(cooldownName);
        if (tasks != null) {
            BukkitTask task = tasks.remove(uuid);
            if (task != null) {
                task.cancel();
            }
        }
    }

    @Getter
    private static class CooldownData {
        private final CooldownBuilder builder;
        private final JavaPlugin plugin;

        public CooldownData(CooldownBuilder builder, JavaPlugin plugin) {
            this.builder = builder;
            this.plugin = plugin;
        }
    }
}