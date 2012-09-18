package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.Main;
import com.falamoore.plugin.PermissionManager;

public class Maintenance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (PermissionManager.getRank((OfflinePlayer) sender).value <= PermissionManager.Rank.JARL.value) {
                sender.sendMessage("You dont have permission to do this!");
                return true;
            } else {
                if (Main.maintnance) {
                    Main.maintnance = false;
                    for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if (PermissionManager.getRank(p).value <= PermissionManager.Rank.JARL.value) {
                            p.kickPlayer("Maintnance started!");
                        }
                    }
                    return true;
                } else {
                    Main.maintnance = false;
                }
            }
        } else {
            if (Main.maintnance) {
                Main.maintnance = false;
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (PermissionManager.getRank(p).value <= PermissionManager.Rank.GUIDE.value) {
                        p.kickPlayer("Maintnance started!");
                    }
                }
                return true;
            } else {
                Main.maintnance = false;
            }
        }
        return false;
    }

}
