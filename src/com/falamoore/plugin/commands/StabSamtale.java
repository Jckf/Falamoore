package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StabSamtale implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("stabsamtale.use")) {
            sender.sendMessage("You don't have permission to do this!");
            return true;
        }
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.hasPermission("stabsamtale.use")) {
                p.sendMessage(argstoString(args));
            }
        }
        return true;
    }

    private String argstoString(String[] args) {
        final StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.BOLD);
        for (final String s : args) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }
}
