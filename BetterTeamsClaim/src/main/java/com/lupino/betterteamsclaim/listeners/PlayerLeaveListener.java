package com.lupino.betterteamsclaim.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {


    private final MenuListener menuListener;

    public PlayerLeaveListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player= event.getPlayer();
        UUID uuid = player.getUniqueId();

        menuListener.waitingForInput.remove(uuid);

    }
}
