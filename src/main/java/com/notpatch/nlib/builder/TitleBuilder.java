package com.notpatch.nlib.builder;

import com.notpatch.nlib.NLib;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TitleBuilder {
    private String title;
    private String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private List<Player> players;

    private boolean animated;
    private List<String> titleFrames;
    private List<String> subtitleFrames;
    private int animationSpeed;
    private BukkitTask animationTask;

    public TitleBuilder send() {
        if (players == null || players.isEmpty()) {
            throw new IllegalStateException("At least one player is required to send a title!");
        }

        int fadeInTicks = fadeIn <= 0 ? 10 : fadeIn;
        int stayTicks = stay <= 0 ? 70 : stay;
        int fadeOutTicks = fadeOut <= 0 ? 20 : fadeOut;

        String processedTitle = title != null ? ChatColor.translateAlternateColorCodes('&', title) : "";
        String processedSubtitle = subtitle != null ? ChatColor.translateAlternateColorCodes('&', subtitle) : "";

        for (Player player : players) {
            sendTitle(player, processedTitle, processedSubtitle, fadeInTicks, stayTicks, fadeOutTicks);
        }

        return this;
    }

    public TitleBuilder startAnimation() {
        if (NLib.getInstance().getPlugin() == null || (!hasAnimationFrames())) {
            throw new IllegalStateException("Plugin instance and at least one animation frame are required to start animation!");
        }

        if (animationTask != null) {
            animationTask.cancel();
        }

        int speed = animationSpeed <= 0 ? 20 : animationSpeed;
        final int[] frameIndex = {0};

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (players == null || players.isEmpty()) {
                    cancel();
                    return;
                }

                String currentTitle = "";
                String currentSubtitle = "";

                if (titleFrames != null && !titleFrames.isEmpty()) {
                    currentTitle = titleFrames.get(frameIndex[0] % titleFrames.size());
                }

                if (subtitleFrames != null && !subtitleFrames.isEmpty()) {
                    currentSubtitle = subtitleFrames.get(frameIndex[0] % subtitleFrames.size());
                }

                for (Player player : players) {
                    sendTitle(player,
                            ChatColor.translateAlternateColorCodes('&', currentTitle),
                            ChatColor.translateAlternateColorCodes('&', currentSubtitle),
                            0, speed + 5, 0);
                }

                frameIndex[0]++;
            }
        }.runTaskTimer(NLib.getInstance().getPlugin(), 0, speed);

        return this;
    }

    private boolean hasAnimationFrames() {
        return (titleFrames != null && !titleFrames.isEmpty()) ||
                (subtitleFrames != null && !subtitleFrames.isEmpty());
    }

    public TitleBuilder stopAnimation() {
        if (animationTask != null) {
            animationTask.cancel();
            animationTask = null;
        }
        return this;
    }

    public TitleBuilder clear() {
        for (Player player : players) {
            sendTitle(player, "", "", 0, 1, 0);
        }
        return this;
    }

    private void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            if (hasNewTitleAPI()) {
                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
                return;
            }

            sendTitleReflection(player, title, subtitle, fadeIn, stay, fadeOut);

        } catch (Exception e) {
            if (!title.isEmpty()) player.sendMessage(title);
            if (!subtitle.isEmpty()) player.sendMessage(subtitle);
        }
    }

    private boolean hasNewTitleAPI() {
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void sendTitleReflection(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) throws Exception {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        Class<?> titlePacketClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutTitle");
        Class<?> titleTypeClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutTitle$EnumTitleAction");
        Class<?> chatComponentClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
        Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");

        Method chatSerializer = chatSerializerClass.getMethod("a", String.class);
        Object titleComponent = chatSerializer.invoke(null, "{\"text\":\"" + title + "\"}");
        Object subtitleComponent = chatSerializer.invoke(null, "{\"text\":\"" + subtitle + "\"}");

        if (!title.isEmpty()) {
            Object titlePacket = titlePacketClass.getConstructor(titleTypeClass, chatComponentClass, int.class, int.class, int.class)
                    .newInstance(titleTypeClass.getField("TITLE").get(null), titleComponent, fadeIn, stay, fadeOut);
            sendPacket(player, titlePacket);
        }

        if (!subtitle.isEmpty()) {
            Object subtitlePacket = titlePacketClass.getConstructor(titleTypeClass, chatComponentClass, int.class, int.class, int.class)
                    .newInstance(titleTypeClass.getField("SUBTITLE").get(null), subtitleComponent, fadeIn, stay, fadeOut);
            sendPacket(player, subtitlePacket);
        }
    }

    private void sendPacket(Player player, Object packet) throws Exception {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
                .invoke(playerConnection, packet);
    }

    public static TitleBuilder send(Player player, String title) {
        return TitleBuilder.builder()
                .title(title)
                .players(Arrays.asList(player))
                .build()
                .send();
    }

    public static TitleBuilder send(Player player, String title, String subtitle) {
        return TitleBuilder.builder()
                .title(title)
                .subtitle(subtitle)
                .players(Arrays.asList(player))
                .build()
                .send();
    }

    public static TitleBuilder broadcast(String title, String subtitle) {
        return TitleBuilder.builder()
                .title(title)
                .subtitle(subtitle)
                .players(new ArrayList<>(Bukkit.getOnlinePlayers()))
                .build()
                .send();
    }

    public static TitleBuilder typewriterAnimation(Plugin plugin, Player player, String text) {
        List<String> frames = new ArrayList<>();
        for (int i = 1; i <= text.length(); i++) {
            frames.add(text.substring(0, i));
        }

        return TitleBuilder.builder()
                .players(Arrays.asList(player))
                .titleFrames(frames)
                .animationSpeed(5)
                .build();
    }

    public static TitleBuilder rainbowAnimation(Plugin plugin, Player player, String text) {
        List<String> frames = new ArrayList<>();
        String[] colors = {"&c", "&6", "&e", "&a", "&b", "&9", "&d"};

        for (String color : colors) {
            frames.add(color + text);
        }

        return TitleBuilder.builder()
                .players(Arrays.asList(player))
                .titleFrames(frames)
                .animationSpeed(10)
                .build();
    }
}