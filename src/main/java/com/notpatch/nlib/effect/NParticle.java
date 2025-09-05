package com.notpatch.nlib.effect;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NParticle {

    private final Location location;
    private Particle particle = Particle.FLAME;
    private int count = 1;
    private double offsetX = 0.0;
    private double offsetY = 0.0;
    private double offsetZ = 0.0;
    private double extra = 0.0;
    private Object data = null;
    private boolean force = false;

    private NParticle(Location location) {
        this.location = location;
    }

    public static NParticle at(Location location) {
        return new NParticle(location);
    }

    public static NParticle at(World world, double x, double y, double z) {
        return new NParticle(new Location(world, x, y, z));
    }

    public static NParticle at(Player player) {
        return new NParticle(player.getLocation());
    }

    public NParticle type(Particle particle) {
        this.particle = particle;
        return this;
    }

    public NParticle count(int count) {
        this.count = count;
        return this;
    }

    public NParticle offset(double x, double y, double z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        return this;
    }

    public NParticle offset(double offset) {
        return offset(offset, offset, offset);
    }

    public NParticle extra(double extra) {
        this.extra = extra;
        return this;
    }

    public NParticle data(Object data) {
        this.data = data;
        return this;
    }

    public NParticle force(boolean force) {
        this.force = force;
        return this;
    }

    public NParticle color(Color color) {
        this.data = new Particle.DustOptions(color, 1.0f);
        return this;
    }

    public NParticle color(int red, int green, int blue) {
        return color(Color.fromRGB(red, green, blue));
    }

    public NParticle color(String hex) {
        Color color = Color.fromRGB(Integer.valueOf(hex.substring(1), 16));
        return color(color);
    }

    public void spawn() {
        if (location.getWorld() != null) {
            location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data, force);
        }
    }

    public void spawnFor(Player player) {
        player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
    }

    public void spawnFor(Player... players) {
        for (Player player : players) {
            spawnFor(player);
        }
    }

    public void spawnForNearby(double radius) {
        if (location.getWorld() != null) {
            location.getWorld().getNearbyEntities(location, radius, radius, radius)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .forEach(this::spawnFor);
        }
    }

    public void spawnCircle(double radius, int points) {
        World world = location.getWorld();
        if (world == null) return;

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = location.getX() + radius * Math.cos(angle);
            double z = location.getZ() + radius * Math.sin(angle);

            Location particleLoc = new Location(world, x, location.getY(), z);
            NParticle.at(particleLoc)
                    .type(particle)
                    .count(count)
                    .offset(offsetX, offsetY, offsetZ)
                    .extra(extra)
                    .data(data)
                    .spawn();
        }
    }


    public void spawnSphere(double radius, int density) {
        World world = location.getWorld();
        if (world == null) return;

        for (int i = 0; i < density; i++) {
            Vector vector = new Vector(
                    Math.random() - 0.5,
                    Math.random() - 0.5,
                    Math.random() - 0.5
            ).normalize().multiply(radius);

            Location particleLoc = location.clone().add(vector);
            NParticle.at(particleLoc)
                    .type(particle)
                    .count(count)
                    .offset(offsetX, offsetY, offsetZ)
                    .extra(extra)
                    .data(data)
                    .spawn();
        }
    }


    public void spawnLine(Location target, double density) {
        World world = location.getWorld();
        if (world == null || target.getWorld() == null) return;

        Vector direction = target.toVector().subtract(location.toVector());
        double distance = direction.length();
        direction.normalize();

        double step = 1.0 / density;
        for (double i = 0; i <= distance; i += step) {
            Location particleLoc = location.clone().add(direction.clone().multiply(i));
            NParticle.at(particleLoc)
                    .type(particle)
                    .count(count)
                    .offset(offsetX, offsetY, offsetZ)
                    .extra(extra)
                    .data(data)
                    .spawn();
        }
    }

    public void spawnHelix(double radius, double height, int points) {
        World world = location.getWorld();
        if (world == null) return;

        for (int i = 0; i < points; i++) {
            double angle = 4 * Math.PI * i / points; // 2 tur
            double y = height * i / points;
            double x = location.getX() + radius * Math.cos(angle);
            double z = location.getZ() + radius * Math.sin(angle);

            Location particleLoc = new Location(world, x, location.getY() + y, z);
            NParticle.at(particleLoc)
                    .type(particle)
                    .count(count)
                    .offset(offsetX, offsetY, offsetZ)
                    .extra(extra)
                    .data(data)
                    .spawn();
        }
    }

    public static void quick(Location location, Particle particle) {
        NParticle.at(location).type(particle).spawn();
    }


    public static void quick(Location location, Color color) {
        NParticle.at(location).type(Particle.DUST).color(color).spawn();
    }

    public static void playerEffect(Player player, Particle particle, int count) {
        NParticle.at(player)
                .type(particle)
                .count(count)
                .offset(0.5, 1.0, 0.5)
                .spawn();
    }


    public static void heal(Player player) {
        NParticle.at(player.getLocation().add(0, 1, 0))
                .type(Particle.HEART)
                .count(5)
                .offset(0.5, 0.5, 0.5)
                .spawn();
    }

}