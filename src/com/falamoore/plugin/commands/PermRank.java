package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.Main;

public class PermRank implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && sender.hasPermission("rank.use")) {
            sender.sendMessage("You don't have permission to do this!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("rank")) {
            if (args.length == 2) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                Main.rank.put(p.getName(), args[1]);
                sender.sendMessage("You changed " + p.getName() + "s' rank to " + args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid usage!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("perm")) {
            if (args.length == 3) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                final String permission = args[2];
                if (args[1].equalsIgnoreCase("add")) {
                    Main.permissions.get(p.getName()).setPermission(permission, true);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    Main.permissions.get(p.getName()).unsetPermission(permission);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid usage!");
                }
                return true;
            }
        }
        return false;
    }
}
