package com.falamoore.plugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.PermissionManager.Rank;

public class PromoteDemote implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("promote")) {
            if (args.length == 1) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                Rank r = PermissionManager.getRank(p);
                if (sender instanceof Player && Main.playerrank.get(((Player)sender).getName()).value<=r.value) {
                    sender.sendMessage("You cant promote to a rank equal or above your own!");
                    return true;
                }
                PermissionManager.setNewRank(p, r.getHigherRank());
                sender.sendMessage("You promoted "+p.getName()+" to "+ PermissionManager.getRank(p).toString());
            }
        } else if (cmd.getName().equalsIgnoreCase("demote")) {
            if (args.length == 1) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                Rank r = PermissionManager.getRank(p);
                if (sender instanceof Player && Main.playerrank.get(((Player)sender).getName()).value<=r.value) {
                    sender.sendMessage("You cant demote to a rank equal or above your own!");
                    return true;
                }
                PermissionManager.setNewRank(p, r.getLowerRank());
                sender.sendMessage("You demoted "+p.getName()+" to "+r.toString());
            }
        }
        return false;
    }
}
