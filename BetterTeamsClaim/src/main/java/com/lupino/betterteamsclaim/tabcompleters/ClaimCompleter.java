package com.lupino.betterteamsclaim.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClaimCompleter implements TabCompleter {


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> list = new ArrayList<>();

        if(sender instanceof Player player) {
            if (args.length == 1) {
                list.add("unclaim");

                if(player.hasPermission("betterteamsclaim.admin")){
                    list.add("reload");
                }
                return list;

            }



        }
        return null;
    }
}
