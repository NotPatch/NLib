package com.notpatch.nlib.effect;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.util.NLogger;
import org.bukkit.*;
import org.bukkit.entity.Player;
import java.util.concurrent.ThreadLocalRandom;

public class NSound {

    private final Location location;
    private Sound sound = Sound.BLOCK_NOTE_BLOCK_PLING;
    private SoundCategory category = SoundCategory.MASTER;
    private float volume = 1.0f;
    private float pitch = 1.0f;

    private NSound(Location location) {
        this.location = location;
    }

    public static NSound at(Location location) {
        return new NSound(location);
    }

    public static NSound at(World world, double x, double y, double z) {
        return new NSound(new Location(world, x, y, z));
    }

    public static NSound at(Player player) {
        return new NSound(player.getLocation());
    }

    public NSound sound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public NSound sound(String soundName) {
        try {
            this.sound = Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            NLogger.warn("Invalid sound name: " + soundName);
        }
        return this;
    }

    public NSound category(SoundCategory category) {
        this.category = category;
        return this;
    }

    public NSound volume(float volume) {
        this.volume = Math.max(0.0f, volume);
        return this;
    }

    public NSound volume(double volume) {
        return volume((float) Math.max(0.0, Math.min(1.0, volume)));
    }

    public NSound pitch(float pitch) {
        this.pitch = Math.max(0.5f, Math.min(2.0f, pitch));
        return this;
    }

    public NSound pitch(double pitch) {
        return pitch((float) Math.max(0.5, Math.min(2.0, pitch)));
    }

    public NSound randomPitch(float min, float max) {
        float randomPitch = ThreadLocalRandom.current().nextFloat() * (max - min) + min;
        return pitch(randomPitch);
    }

    public NSound randomPitch() {
        return randomPitch(0.8f, 1.2f);
    }

    public void play() {
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, sound, category, volume, pitch);
        }
    }

    public void playFor(Player player) {
        player.playSound(location, sound, category, volume, pitch);
    }

    public void playTo(Player player) {
        player.playSound(player.getLocation(), sound, category, volume, pitch);
    }

    public void playFor(Player... players) {
        for (Player player : players) {
            playFor(player);
        }
    }

    public void playForNearby(double radius) {
        if (location.getWorld() != null) {
            location.getWorld().getNearbyEntities(location, radius, radius, radius)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .forEach(this::playFor);
        }
    }

    public void playForAll() {
        Bukkit.getOnlinePlayers().forEach(this::playTo);
    }

    public void playForWorld(World world) {
        world.getPlayers().forEach(this::playTo);
    }

    public void playWithDelay(long delayTicks) {
        Bukkit.getScheduler().runTaskLater(NLib.getInstance().getPlugin(), this::play, delayTicks);
    }

    public void playRepeating(int times, long intervalTicks) {
        playRepeating(times, intervalTicks, 0);
    }

    public void playRepeating(int times, long intervalTicks, long initialDelay) {
        Bukkit.getScheduler().runTaskTimer(NLib.getInstance().getPlugin(), new Runnable() {
            private int count = 0;

            @Override
            public void run() {
                play();
                count++;
                if (count >= times) {
                    return;

                }
            }
        }, initialDelay, intervalTicks);
    }

    public static void quick(Location location, Sound sound) {
        NSound.at(location).sound(sound).play();
    }

    public static void quick(Location location, Sound sound, float volume, float pitch) {
        NSound.at(location).sound(sound).volume(volume).pitch(pitch).play();
    }

    public static void quickFor(Player player, Sound sound) {
        NSound.at(player).sound(sound).playTo(player);
    }

    public static void quickFor(Player player, Sound sound, float volume, float pitch) {
        NSound.at(player).sound(sound).volume(volume).pitch(pitch).playTo(player);
    }

    public static void success(Player player) {
        NSound.at(player)
                .sound(Sound.ENTITY_PLAYER_LEVELUP)
                .volume(0.7f)
                .pitch(1.2f)
                .playTo(player);
    }

    public static void error(Player player) {
        NSound.at(player)
                .sound(Sound.BLOCK_ANVIL_LAND)
                .volume(0.5f)
                .pitch(0.8f)
                .playTo(player);
    }

    public static void notification(Player player) {
        NSound.at(player)
                .sound(Sound.BLOCK_NOTE_BLOCK_PLING)
                .volume(0.8f)
                .pitch(1.5f)
                .playTo(player);
    }

    public static void click(Player player) {
        NSound.at(player)
                .sound(Sound.UI_BUTTON_CLICK)
                .volume(0.5f)
                .pitch(1.0f)
                .playTo(player);
    }


    public static void pop(Location location) {
        NSound.at(location)
                .sound(Sound.ENTITY_ITEM_PICKUP)
                .volume(0.6f)
                .randomPitch(0.9f, 1.1f)
                .play();
    }

    public static void teleport(Location location) {
        NSound.at(location)
                .sound(Sound.ENTITY_ENDERMAN_TELEPORT)
                .volume(1.0f)
                .pitch(1.0f)
                .play();
    }

    public static void explosion(Location location) {
        NSound.at(location)
                .sound(Sound.ENTITY_GENERIC_EXPLODE)
                .volume(1.0f)
                .pitch(0.8f)
                .play();
    }

    public static void magic(Location location) {
        NSound.at(location)
                .sound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
                .volume(0.8f)
                .randomPitch(0.8f, 1.3f)
                .play();
    }

    public static void woosh(Player player) {
        NSound.at(player)
                .sound(Sound.ENTITY_BAT_TAKEOFF)
                .volume(0.7f)
                .pitch(1.5f)
                .playTo(player);
    }

    public static void coin(Player player) {
        NSound.at(player)
                .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                .volume(0.6f)
                .randomPitch(1.0f, 1.4f)
                .playTo(player);
    }


    public static void anvil(Location location) {
        NSound.at(location)
                .sound(Sound.BLOCK_ANVIL_USE)
                .volume(0.8f)
                .pitch(1.0f)
                .play();
    }

    public static void breaking(Location location) {
        NSound.at(location)
                .sound(Sound.ENTITY_ITEM_BREAK)
                .volume(0.7f)
                .randomPitch(0.8f, 1.2f)
                .play();
    }

    public static void note(Location location, float note) {
        NSound.at(location)
                .sound(Sound.BLOCK_NOTE_BLOCK_HARP)
                .volume(1.0f)
                .pitch(0.5f + note)
                .play();
    }

    public static void piano(Location location, float note) {
        NSound.at(location)
                .sound(Sound.BLOCK_NOTE_BLOCK_HARP)
                .volume(0.8f)
                .pitch(0.5f + note)
                .play();
    }


    public static void bass(Location location, float note) {
        NSound.at(location)
                .sound(Sound.BLOCK_NOTE_BLOCK_BASS)
                .volume(1.0f)
                .pitch(0.5f + note)
                .play();
    }

    public NSound randomize() {
        return randomPitch();
    }

    public static void randomAmbient(Location location) {
        Sound[] ambientSounds = {
                Sound.AMBIENT_CAVE, Sound.AMBIENT_CRIMSON_FOREST_LOOP,
                Sound.AMBIENT_NETHER_WASTES_LOOP, Sound.AMBIENT_SOUL_SAND_VALLEY_LOOP
        };

        Sound randomSound = ambientSounds[ThreadLocalRandom.current().nextInt(ambientSounds.length)];
        NSound.at(location)
                .sound(randomSound)
                .volume(0.3f)
                .randomPitch(0.8f, 1.2f)
                .play();
    }
}