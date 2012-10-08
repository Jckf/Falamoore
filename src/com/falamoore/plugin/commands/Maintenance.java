package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.Main;

public class Maintenance implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("maintenance.use")) {
            sender.sendMessage("You don't have permission to do this!");
            return true;
        } else {
            if (Main.maintenance_enabled) {
                Main.maintenance_enabled = false;
                for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!sender.hasPermission("maintenance.join")) {
                        p.kickPlayer("Maintenance started!");
                    }
                }
                return true;
            } else {
                Main.maintenance_enabled = false;
            }
        }
        return false;
    }

}
