package com.lupino.betterteamsclaim.listeners;

import com.lupino.betterteamsclaim.commands.ClaimCommand;
import com.lupino.betterteamsclaim.enums.InputState;
import com.lupino.betterteamsclaim.managers.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final MessageManager messageManager;

    private final MenuListener menuListener;

    private final ClaimCommand claimCommand;

    public PlayerInteractListener(MessageManager messageManager, MenuListener menuListener, ClaimCommand claimCommand) {
        this.messageManager = messageManager;
        this.menuListener = menuListener;
        this.claimCommand = claimCommand;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        if (event.getHand()!= EquipmentSlot.HAND||event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_AIR)return;
        UUID uuid = player.getUniqueId();
        if (!menuListener.waitingForInput.containsKey(uuid)) return;
        InputState state = menuListener.waitingForInput.remove(uuid);

        Location location = event.getClickedBlock().getLocation();
        if (location==null)return;
        event.setCancelled(true);
        String x = String.valueOf(location.getX());
        String y = String.valueOf(location.getY());
        String z = String.valueOf(location.getZ());

        if (state == InputState.COR1) {
            claimCommand.getTempCorners1().put(player.getUniqueId(),location);
            messageManager.sendMessageFromConfig(player,"corner1-set-message", Map.of("x",x,"y",y,"z",z));

        } else if (state == InputState.COR2) {
            claimCommand.getTempCorners2().put(player.getUniqueId(),location);
            messageManager.sendMessageFromConfig(player,"corner2-set-message", Map.of("x",x,"y",y,"z",z));

        }
        claimCommand.openMenu(player);






    }
}
