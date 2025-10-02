package com.notpatch.nlib.builder;

import com.notpatch.nlib.NLib;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class BossBarBuilder {

    private String title;
    @Builder.Default private BarColor color = BarColor.PURPLE;
    @Builder.Default private BarStyle style = BarStyle.SOLID;
    @Builder.Default private double progress = 1.0;
    @Singular("flag") private Set<BarFlag> flags;
    private List<Player> players;

    private transient BossBar bossBar;
    private transient BukkitTask task;

    public BossBarBuilder show() {
        if (players == null || players.isEmpty()) {
            throw new IllegalStateException("BossBar'ı göstermek için en az bir oyuncu gerekir!");
        }

        if (bossBar == null) {
            String formattedTitle = title != null ? title.replace("&", "§") : "";

            bossBar = Bukkit.createBossBar(formattedTitle, color, style);
            if (flags != null) {
                flags.forEach(bossBar::addFlag);
            }
        }

        bossBar.setProgress(progress);
        bossBar.setVisible(true);

        players.forEach(bossBar::addPlayer);

        return this;
    }

    public BossBarBuilder hide() {
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
        return this;
    }

    public BossBarBuilder destroy() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (bossBar != null) {
            hide();
            bossBar = null;
        }
        return this;
    }

    public BossBarBuilder setTitle(String title) {
        this.title = title;
        if (bossBar != null) {
            bossBar.setTitle(title.replace("&", "§"));
        }
        return this;
    }

    public BossBarBuilder setProgress(double progress) {
        this.progress = Math.max(0.0, Math.min(1.0, progress));
        if (bossBar != null) {
            bossBar.setProgress(this.progress);
        }
        return this;
    }

    public BossBarBuilder setColor(BarColor color) {
        this.color = color;
        if (bossBar != null) {
            bossBar.setColor(this.color);
        }
        return this;
    }

    public BossBarBuilder startCountdown(int seconds, Runnable onFinish) {
        if (task != null) task.cancel();

        show();

        final int[] remaining = {seconds};

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (remaining[0] <= 0) {
                    if (onFinish != null) {
                        onFinish.run();
                    }
                    destroy();
                    return;
                }

                setProgress((double) remaining[0] / seconds);

                if (title != null && title.contains("%time%")) {
                    bossBar.setTitle(title.replace("%time%", String.valueOf(remaining[0])).replace("&", "§"));
                }

                remaining[0]--;
            }
        }.runTaskTimer(NLib.getInstance().getPlugin(), 0L, 20L);

        return this;
    }

    public static BossBarBuilder showTimedBar(Player player, String title, BarColor color, int seconds) {
        BossBarBuilder builder = BossBarBuilder.builder()
                .title(title)
                .color(color)
                .players(Collections.singletonList(player))
                .build()
                .show();

        new BukkitRunnable() {
            @Override
            public void run() {
                builder.destroy();
            }
        }.runTaskLater(NLib.getInstance().getPlugin(), seconds * 20L);

        return builder;
    }
}