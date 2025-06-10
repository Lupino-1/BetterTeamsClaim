package com.lupino.betterteamsclaim;

import com.lupino.betterteamsclaim.commands.ClaimCommand;
import com.lupino.betterteamsclaim.listeners.regionprotecters.RegionProtectorListener;
import com.lupino.betterteamsclaim.listeners.MenuListener;
import com.lupino.betterteamsclaim.listeners.PlayerInteractListener;
import com.lupino.betterteamsclaim.listeners.PlayerLeaveListener;
import com.lupino.betterteamsclaim.managers.MessageManager;

import com.lupino.betterteamsclaim.managers.RegionManager;
import com.lupino.betterteamsclaim.tabcompleters.ClaimCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterTeamsClaim extends JavaPlugin {
     private MessageManager messageManager;
     private RegionManager regionManager;
    @Override
    public void onEnable() {
        Keys.initialize(this);
        saveDefaultConfig();
        messageManager = new MessageManager(this);
        regionManager= new RegionManager(this,messageManager);
        ClaimCommand claimCommand = new ClaimCommand(messageManager,regionManager,this);
        getCommand("claim").setExecutor(claimCommand);
        getCommand("claim").setTabCompleter(new ClaimCompleter());

        MenuListener menuListener = new MenuListener(messageManager,regionManager,claimCommand,this);
        getServer().getPluginManager().registerEvents(menuListener,this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(messageManager,menuListener,claimCommand),this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(menuListener),this);
        getServer().getPluginManager().registerEvents(new RegionProtectorListener(regionManager,messageManager),this);

        regionManager.loadRegions();

        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
