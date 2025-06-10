package com.lupino.betterteamsclaim.commands;

import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.lupino.betterteamsclaim.BetterTeamsClaim;
import com.lupino.betterteamsclaim.Keys;
import com.lupino.betterteamsclaim.managers.MessageManager;
import com.lupino.betterteamsclaim.managers.RegionManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class ClaimCommand implements CommandExecutor {

    private final Map<UUID,Location> tempCorners1 = new HashMap<>();
    private final Map<UUID,Location> tempCorners2 = new HashMap<>();
    private final MessageManager messageManager;

    private final RegionManager regionManager;
    public void removeTempCorner2(Player player){
        tempCorners2.remove(player.getUniqueId());
    }
    public void removeTempCorner1(Player player){
        tempCorners1.remove(player.getUniqueId());
    }
    public void addTempCorner2(Player player,Location location){
        tempCorners2.put(player.getUniqueId(),location);
    }
    public void addTempCorner1(Player player,Location location){
        tempCorners1.put(player.getUniqueId(),location);
    }

    public Map<UUID, Location> getTempCorners1() {
        return tempCorners1;
    }

    public Map<UUID, Location> getTempCorners2() {
        return tempCorners2;
    }

    private final BetterTeamsClaim plugin;

    public ClaimCommand(MessageManager messageManager, RegionManager regionManager, BetterTeamsClaim plugin) {
        this.messageManager = messageManager;
        this.regionManager = regionManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player){
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if(!player.hasPermission("betterteamsclaim.admin")) {
                    messageManager.sendMessageFromConfig(player,"no-permission-message");
                    return true;

                }
                try {
                    messageManager.reloadMessages();
                    regionManager.reload();
                    regionManager.loadRegions();
                    plugin.reloadConfig();
                    messageManager.sendMessageFromConfig(player,"commands.reload-command-message");


                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while reloading config.yml.");
                    e.printStackTrace();
                }

                return true;
            }



            Team team = Team.getTeam(player);
            if(team == null){
                messageManager.sendMessageFromConfig(player,"commands.not-in-team-message");

                return true;
            }

            List<TeamPlayer> owners = team.getRank(PlayerRank.OWNER);
            List<UUID> uuids = new ArrayList<>();

            for(TeamPlayer teamPlayerp : owners){
                UUID uuid= teamPlayerp.getPlayerUUID();
                if(uuid==null)continue;
                uuids.add(uuid);

            }
            if(!uuids.contains(player.getUniqueId())){
                messageManager.sendMessageFromConfig(player,"commands.not-owner-message");

                return true;
            }


            if (args.length > 0 && args[0].equalsIgnoreCase("unclaim")) {
                if(!regionManager.hasRegion(team))  {
                    messageManager.sendMessageFromConfig(player,"commands.dont-have-region-message");

                    return true;
                }

                regionManager.deleteRegion(team.getName());
                messageManager.sendMessageFromConfig(player,"claim-unclaim-message");
                return true;
            }




            if(regionManager.hasRegion(team))  {
                messageManager.sendMessageFromConfig(player,"commands.already-have-region-message");

                return true;
            }
            openMenu(player);
        }
        return true;
    }


    public void openMenu(Player player){

        Inventory inventory = Bukkit.createInventory(player,45,messageManager.translateColors(plugin.getConfig().getString("menu-title","&0Claim menu")));
        for (int i = 0; i < 45; i++) {

            if (isFiller(i)) {
                inventory.setItem(i,
                        createItem(plugin.getConfig().getString("filler.material","GRAY_STAINED_GLASS_PANE"),
                        plugin.getConfig().getString("filler.name",""),
                        null,
                        0));

            }
        }

        inventory.setItem(plugin.getConfig().getInt("buttons.corner1.slot",20),
                createItemWithPersi(plugin.getConfig().getString("buttons.corner1.material","CAULDRON"),
                        plugin.getConfig().getString("buttons.corner1.name","&aCorner 1"),
                getLoreForCorner("buttons.corner1", tempCorners1.get(player.getUniqueId())),
                        plugin.getConfig().getInt("buttons.corner1.model-data",0),
                Keys.COR1_BUTTON));

        inventory.setItem(plugin.getConfig().getInt("buttons.corner2.slot",24),
                createItemWithPersi(plugin.getConfig().getString("buttons.corner2.material","CAULDRON"),
                        plugin.getConfig().getString("buttons.corner2.name","&aCorner 2"),
                        getLoreForCorner("buttons.corner2", tempCorners2.get(player.getUniqueId())),
                        plugin.getConfig().getInt("buttons.corner2.model-data",0),
                        Keys.COR2_BUTTON));

        inventory.setItem(plugin.getConfig().getInt("buttons.save.slot",22),
                createItemWithPersi(plugin.getConfig().getString("buttons.save.material"),
                        plugin.getConfig().getString("buttons.save.name","&bSave"),
                        messageManager.translateLore(plugin.getConfig().getStringList("buttons.save.lore")),
                        plugin.getConfig().getInt("buttons.save.model-data",0),
                        Keys.SAVE_BUTTON));
        player.openInventory(inventory);


    }
    private List<String> getLoreForCorner(String path,Location loc) {
    List <String> locNotSetLore = plugin.getConfig().getStringList(path+".loc-not-set-lore");
    List <String> lore = plugin.getConfig().getStringList(path+".lore");
        if (loc == null) {

            return messageManager.translateLore(locNotSetLore);
        }
        String x = String.valueOf(loc.getX());
        String y = String.valueOf(loc.getY());
        String z = String.valueOf(loc.getZ());
        return messageManager.replacePlaceholdersInLore(lore,Map.of("x",x,"y",y,"z",z));

    }



    private boolean isFiller(int index) {

        List<Short> slots= plugin.getConfig().getShortList("filler.slots");

        for(short i : slots){
           if(index==i)return true;
        }
        return false;
    }


    public ItemStack createItemWithPersi(String material, String name, List<String> lore, int modelData, NamespacedKey key) {
        ItemStack item = createItem(material, name, lore, modelData);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createItem(String material, String name, List<String> lore, int modelData) {
        ItemStack item;

        if (material.startsWith("texture-")) {
            item = new ItemStack(Material.PLAYER_HEAD);
            try {
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                if (skullMeta != null) {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

                    PlayerTextures textures = profile.getTextures();

                    try {
                        URL urlObject = new URL("https://textures.minecraft.net/texture/" + material.substring(8));
                        textures.setSkin(urlObject);
                    } catch (MalformedURLException ignored) {}

                    profile.setTextures(textures);
                    skullMeta.setPlayerProfile(profile);

                    if (name != null && !name.isEmpty()) {
                        skullMeta.setDisplayName(messageManager.translateColors(name));
                    }
                    if (lore != null && !lore.isEmpty()) {
                        skullMeta.setLore(lore);
                    }
                    if (modelData > 0) {
                        skullMeta.setCustomModelData(modelData);
                    }

                    item.setItemMeta(skullMeta);
                }
            } catch (ClassCastException ignored) {}
        } else {
            Material mat = Material.getMaterial(material.toUpperCase());
            if (mat == null) throw new IllegalArgumentException("Invalid material: " + material);

            item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (name != null && !name.isEmpty()) {
                    meta.setDisplayName(messageManager.translateColors(name));
                }
                if (lore != null && !lore.isEmpty()) {
                    meta.setLore(lore);
                }
                if (modelData > 0) {
                    meta.setCustomModelData(modelData);
                }

                item.setItemMeta(meta);
            }
        }

        return item;
    }

}
