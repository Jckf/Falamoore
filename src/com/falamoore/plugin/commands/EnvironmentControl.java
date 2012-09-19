package com.falamoore.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.PermissionManager;

public class EnvironmentControl implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can use this command!");
            return true;
        }
        final Player p = (Player) sender;
        if (getValue(p) <= 1) {
            sender.sendMessage("You don't have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("time")) {
                if (args[1].equalsIgnoreCase("day")) {
                    p.getWorld().setTime(1600);
                    p.sendMessage("Set time to day");
                    return true;
                } else if (args[1].equalsIgnoreCase("night")) {
                    p.getWorld().setTime(16000);
                    p.sendMessage("Set time to night");
                    return true;
                } else if (args[1].equalsIgnoreCase("set")) {
                    if (args.length == 3) {
                        try {
                            final int i = Integer.parseInt(args[2]);
                            p.getWorld().setTime(i);
                            sender.sendMessage("Set time to " + i);
                            return true;
                        } catch (final Exception e) {
                            sender.sendMessage("Invalid time!");
                            return true;
                        }
                    } else {
                        sender.sendMessage("Invalid usage!");
                        return true;
                    }
                }
            } else if (args[0].equalsIgnoreCase("weather")) {
                switch (args[1]) {
                    case "rain":
                        p.getWorld().setWeatherDuration(Integer.MAX_VALUE);
                        p.sendMessage("Set weather to " + args[1]);
                        break;
                    case "sun":
                    case "clear":
                        p.getWorld().setWeatherDuration(0);
                        p.sendMessage("Set weather to " + args[1]);
                        break;
                    default:
                        sender.sendMessage("Not a valid option!");
                        break;
                }
                return true;
            }
        }
        return false;
    }

    private int getValue(Player sender) {
        return PermissionManager.getRank(sender).value;
    }
}
