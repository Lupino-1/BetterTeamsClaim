package com.lupino.betterteamsclaim.listeners.regionprotecters;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.customEvents.DisbandTeamEvent;
import com.lupino.betterteamsclaim.managers.MessageManager;
import com.lupino.betterteamsclaim.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;


public class RegionProtectorListener implements Listener {

    private final RegionManager regionManager;

    private final MessageManager messageManager;

    public RegionProtectorListener(RegionManager regionManager, MessageManager messageManager) {
        this.regionManager = regionManager;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(!regionManager.canInteract(player,event.getBlock().getLocation())){
            event.setCancelled(true);
            messageManager.sendMessageFromConfig(player,"cant-do-action-message");

        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(!regionManager.canInteract(player,event.getBlock().getLocation())){
            event.setCancelled(true);
            messageManager.sendMessageFromConfig(player,"cant-do-action-message");

        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getHand()!= EquipmentSlot.HAND||event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_AIR)return;


        Location location = event.getClickedBlock().getLocation();
        if(location==null)return;


        if(!regionManager.canInteract(player,location)){
            event.setCancelled(true);
            messageManager.sendMessageFromConfig(player,"cant-do-action-message");

        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !regionManager.canInteract(block.getLocation()));
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> !regionManager.canInteract(block.getLocation()));
    }



    @EventHandler
    public void onWitherChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.WITHER) return;

        if (!regionManager.canInteract(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPistonPush(BlockPistonExtendEvent event) {
        Location pistonLoc = event.getBlock().getLocation();
        List<Block> blocks = event.getBlocks();

        for (Block block : blocks) {
            Location newLoc = block.getLocation().clone().add(event.getDirection().getDirection());

            if (!regionManager.canInteract(block.getLocation()) || !regionManager.canInteract(newLoc)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void onPistonPull(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;

        for (Block block : event.getBlocks()) {
            Location oldLoc = block.getLocation();
            Location newLoc = oldLoc.clone().add(event.getDirection().getOppositeFace().getDirection());

            if (!regionManager.canInteract(oldLoc) || !regionManager.canInteract(newLoc)) {
                event.setCancelled(true);
                return;
            }
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();



        Location location = target.getLocation();

        if (damager instanceof Player player) {
            if (!regionManager.canInteract(player, location)) {
                event.setCancelled(true);
                messageManager.sendMessageFromConfig(player,"cant-do-action-message");
            }
        } else {
            if (!regionManager.canInteract(location)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Location location = entity.getLocation();

            if (!regionManager.canInteract(player, location)) {
                event.setCancelled(true);
                messageManager.sendMessageFromConfig(player,"cant-do-action-message");

            }

    }
    @EventHandler
    public void onTeamDisband(DisbandTeamEvent event) {
        Team team = event.getTeam();
        regionManager.deleteRegion(team.getName());
    }


}
