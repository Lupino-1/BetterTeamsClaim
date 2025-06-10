package com.lupino.betterteamsclaim.listeners;

import com.booksaw.betterTeams.Team;
import com.lupino.betterteamsclaim.BetterTeamsClaim;
import com.lupino.betterteamsclaim.Keys;
import com.lupino.betterteamsclaim.commands.ClaimCommand;
import com.lupino.betterteamsclaim.enums.InputState;
import com.lupino.betterteamsclaim.managers.MessageManager;
import com.lupino.betterteamsclaim.managers.RegionManager;
import com.lupino.betterteamsclaim.models.Region;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MenuListener implements Listener {
    private final MessageManager messageManager;

    private final RegionManager regionManager;

    private final ClaimCommand claimCommand;

    private final BetterTeamsClaim plugin;


    public MenuListener(MessageManager messageManager, RegionManager regionManager, ClaimCommand claimCommand, BetterTeamsClaim plugin) {
        this.messageManager = messageManager;
        this.regionManager = regionManager;
        this.claimCommand = claimCommand;
        this.plugin = plugin;
    }

    public final Map<UUID, InputState> waitingForInput = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        ItemStack hand = event.getCurrentItem();
        Player player  = (Player) event.getWhoClicked();

        if(event.getView().getTitle().equalsIgnoreCase(messageManager.translateColors(plugin.getConfig().getString("menu-title","&0Claim menu")))){
            event.setCancelled(true);
            if(hasPersi(hand, Keys.COR1_BUTTON)){
                player.closeInventory();
                messageManager.sendMessageFromConfig(player,"click-on-corner1-message");

                waitingForInput.put(player.getUniqueId(), InputState.COR1);
                return;
            }
            if(hasPersi(hand, Keys.COR2_BUTTON)){
                player.closeInventory();
                messageManager.sendMessageFromConfig(player,"click-on-corner2-message");

                waitingForInput.put(player.getUniqueId(), InputState.COR2);
                return;
            }
            if(hasPersi(hand, Keys.SAVE_BUTTON)){
                if (!isAllTempArenaDataSet(player)) {
                    messageManager.sendMessageFromConfig(player,"first-set-values-message");

                    return;
                }

                if(!areAllLocationsInSameWorld(player)){
                    messageManager.sendMessageFromConfig(player,"all-loc-not-in-same-world-message");

                    return;


                }

                Team team =Team.getTeam(player);
                Region region = new Region(team,claimCommand.getTempCorners1().get(player.getUniqueId()),claimCommand.getTempCorners2().get(player.getUniqueId()));
                if(regionManager.isOverlapping(region)){
                    messageManager.sendMessageFromConfig(player,"already-region-message");

                    return;
                }
                if(regionManager.hasRegion(team))  {
                    messageManager.sendMessageFromConfig(player,"commands.already-have-region-message");

                    return;
                }
                if(regionManager.isRegionTooBig(region,player))  {
                    return;
                }



                regionManager.saveRegionToConfig(region);
                messageManager.sendMessageFromConfig(player,"claim-loaded-message");

                resetTempArenaDataSet(player);
                player.closeInventory();


            }


        }

    }


    public boolean areAllLocationsInSameWorld(Player player) {
        World world = claimCommand.getTempCorners1().get(player.getUniqueId()).getWorld();

        return world != null && world.equals(claimCommand.getTempCorners2().get(player.getUniqueId()).getWorld()) ;
    }

    public void resetTempArenaDataSet(Player player) {

        claimCommand.removeTempCorner1(player);
        claimCommand.removeTempCorner2(player);



    }
    public boolean isAllTempArenaDataSet(Player player) {
        Team team =Team.getTeam(player);
        if(team == null){

            return false;
        }


        return claimCommand.getTempCorners1().get(player.getUniqueId()) != null &&
                claimCommand.getTempCorners2().get(player.getUniqueId())!= null ;
    }







    public boolean hasPersi(ItemStack hand, NamespacedKey key){
        if (hand != null && hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(key)) {
            return true;
        }
        return false;
    }
}
