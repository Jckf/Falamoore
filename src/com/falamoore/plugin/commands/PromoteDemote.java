package com.falamoore.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.PermissionManager;
import com.falamoore.plugin.PermissionManager.Rank;

public class PromoteDemote implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && getValue((Player)sender) <= 1) {
            sender.sendMessage("You don't have permission to do this!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("promote")) {
            if (args.length == 1) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                final Rank r = PermissionManager.getRank(p);
                if ((sender instanceof Player) && (PermissionManager.getRank((Player) sender).value <= r.value)) {
                    sender.sendMessage("You can't promote to a rank equal to or above your own!");
                    return true;
                }
                PermissionManager.setNewRank(p, r.getHigherRank());
                sender.sendMessage("You promoted " + p.getName() + " to " + r.getHigherRank().toString());
            }
        } else if (cmd.getName().equalsIgnoreCase("demote")) {
            if (args.length == 1) {
                final OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                final Rank r = PermissionManager.getRank(p);
                if ((sender instanceof Player) && (PermissionManager.getRank((Player) sender).value <= r.value)) {
                    sender.sendMessage("You can't demote to a rank equal to or above your own!");
                    return true;
                }
                PermissionManager.setNewRank(p, r.getLowerRank());
                sender.sendMessage("You demoted " + p.getName() + " to " + r.toString());
            }
        }
        return false;
    }
    private int getValue(Player sender) {
        return PermissionManager.getRank(sender).value;
    }
}
