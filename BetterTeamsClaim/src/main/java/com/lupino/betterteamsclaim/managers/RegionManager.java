package com.lupino.betterteamsclaim.managers;

import com.booksaw.betterTeams.Team;
import com.lupino.betterteamsclaim.BetterTeamsClaim;
import com.lupino.betterteamsclaim.models.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionManager {
    private final List<Region> regions = new ArrayList<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    private final JavaPlugin plugin;

    private final MessageManager messageManager;

    public RegionManager(JavaPlugin plugin,MessageManager messageManager) {
        dataFile = new File(plugin.getDataFolder(), "regions.yml");


        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.messageManager= messageManager;
        this.plugin= plugin;
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    public void loadRegions() {
        regions.clear();
        ConfigurationSection regionSection = dataConfig.getConfigurationSection("regions");
        if (regionSection == null) return;



        for (String name : regionSection.getKeys(false)) {
            ConfigurationSection section = regionSection.getConfigurationSection(name);
            if (section == null) continue;


            Location corner1 = section.getLocation("corner1");
            Location corner2 = section.getLocation("corner2");

            if (corner1 == null || corner2 == null) {
                Bukkit.getLogger().warning("Region " + name + " is missing required data.");
                continue;
            }
            Team team = Team.getTeam(name);
            if(team == null) continue;
            Region region = new Region(team,corner1,corner2);

            regions.add(region);





        }



    }

    public void saveRegionToConfig(Region region){
        String path = "regions."+region.getTeam().getName();
        dataConfig.set(path+".corner1",region.getCorner1());
        dataConfig.set(path+".corner2",region.getCorner2());
        saveData();
        regions.add(region);



    }

    public List<Region> getRegions() {
        return regions;
    }
    public boolean hasRegion(Team team) {
        return regions.stream().anyMatch(r -> r.getTeam().equals(team));
    }

    public void deleteRegion(String name){
        Region region = regions.stream()
                .filter(a -> a.getTeam().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (region == null) return;

        // Odstranit z paměti
        regions.remove(region);

        // Odstranit z configu
        dataConfig.set("regions." + name, null);
        saveData();


    }

    public List<String> getAllArenasNames() {
        List<String> list = new ArrayList<>();
        for(Region region:getRegions()){
            String name= region.getTeam().getName();
            list.add(name);



        }
        return list;
    }
    public Region getRegionAt(Location loc) {
        for (Region region : regions) {
            if (region.isInside(loc)) {
                return region;
            }
        }
        return null;
    }
    public boolean isOverlapping(Region newRegion) {
        for (Region existing : regions) {

            if (existing.intersects(newRegion)) {
                return true;
            }

            Location loc1 = plugin.getConfig().getLocation("spawn-zone.loc1");
            Location loc2 = plugin.getConfig().getLocation("spawn-zone.loc2");

            if(loc1==null||loc2==null)return false;

            if(existing.intersects(loc1,loc2)){
                return true;
            }
        }
        return false;
    }
    private int getCountOfBlocks(Region region) {
        Location c1 = region.getCorner1();
        Location c2 = region.getCorner2();

        if (c1 == null || c2 == null) return 0;

        // Ujisti se, že rohy jsou ve stejném světě
        if (!c1.getWorld().equals(c2.getWorld())) return 0;

        int minX = Math.min(c1.getBlockX(), c2.getBlockX());
        int maxX = Math.max(c1.getBlockX(), c2.getBlockX());

        int minY = Math.min(c1.getBlockY(), c2.getBlockY());
        int maxY = Math.max(c1.getBlockY(), c2.getBlockY());

        int minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
        int maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;

        return sizeX * sizeY * sizeZ;
    }


    public boolean isRegionTooBig(Region region,Player player){
        if(region.getTeam()==null) return true;
        Team team = region.getTeam();
        int level = team.getLevel();
        int count = getCountOfBlocks(region);
        String stringLevel = String.valueOf(level);
        int configCount = plugin.getConfig().getInt("levels." +stringLevel+".block-count",0);
        if(count<configCount) return false;
        messageManager.sendMessageFromConfig(player,"selection-too-big-message", Map.of("count",String.valueOf(count),"limit",String.valueOf(configCount)));


        return true;
    }


    public boolean canInteract(Player player, Location loc) {
        //permise bypass
        Region region = getRegionAt(loc);

        if (region == null) return true;
        Team playerTeam = Team.getTeam(player);
        if (playerTeam == null) return false;

        return region.getTeam().getName().equalsIgnoreCase(playerTeam.getName());
    }
    public boolean canInteract( Location loc) {
        //permise bypass
        Region region = getRegionAt(loc);

        if (region == null) return true;


        return false;
    }




    public void loadConfig() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }


    public void reload() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                loadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadConfig();
        }
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}