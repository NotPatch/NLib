package com.notpatch.nlib.listener;

import com.notpatch.nlib.manager.WarmupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMoveListener implements Listener {

    private final WarmupManager warmupManager = WarmupManager.getInstance();

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ() && e.getFrom().getBlockY() == e.getTo().getBlockY()) return;
        Player player = e.getPlayer();

        if(warmupManager.isWarmingUp(player)){
            warmupManager.cancelWarmup(player, false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(warmupManager.isWarmingUp(player)){
            warmupManager.cancelWarmup(player, false);
        }
    }

}
