package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
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
            if (Main.playerrank.get(sender.getName()).value <= PermissionManager.Rank.ARCHDUKE.value) {
                sender.sendMessage("You dont have permission to do this!");
                return true;
            } else {
                if (Main.maintnance) {
                    Main.maintnance = false;
                    for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if (Main.playerrank.get(p.getName()).value <= PermissionManager.Rank.ARCHDUKE.value) {
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
                    if (Main.playerrank.get(p.getName()).value <= PermissionManager.Rank.ARCHDUKE.value) {
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
