package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.PermissionManager;

public class StabSamtale implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && (getValue((Player) sender) <= 1)) {
            sender.sendMessage("You don't have permission to do this!");
            return true;
        }
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (getValue(p) >= PermissionManager.Rank.JARL.value) {
                p.sendMessage(argstoString(args));
            }
        }
        return true;
    }

    private String argstoString(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.BOLD);
        for (String s : args) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }

    private int getValue(Player sender) {
        return PermissionManager.getRank(sender).value;
    }
}
