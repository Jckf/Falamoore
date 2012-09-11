package com.falamoore.plugin.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.Main;

public class BanKick implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ban")) {
            if ((sender instanceof Player) && (getValue((Player) sender) <= 1)) {
                sender.sendMessage("You dont have permission to do this");
                return true;
            }
            if (args.length >= 2) {
                final OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                if (pl == null) {
                    sender.sendMessage("No such player has played on the server before!");
                    return true;
                }
                if (isBanned(pl.getName())) {
                    sender.sendMessage("This player is already banned!");
                    return true;
                }
                if (pl.isOnline()) {
                    final Player pl2 = (Player) pl;
                    pl2.kickPlayer("You are banned!");
                }
                if (!setBanned(pl.getName(), Long.MAX_VALUE, giveReason(args))) {
                    sender.sendMessage("Something went wrong when banning " + pl.getName());
                    return true;
                }
                return true;
            } else {
                sender.sendMessage("Wrong usage!");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("tempban")) {
            if ((sender instanceof Player) && (getValue((Player) sender) <= 1)) {
                sender.sendMessage("You dont have permission to do this");
                return true;
            }
            if (args.length >= 2) {
                final OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                final Calendar d = Calendar.getInstance();
                try {
                    final String[] ds = args[1].split("/");
                    final int[] dateint = new int[5];
                    int i = 0;
                    for (final String s : ds) {
                        dateint[i] = Integer.parseInt(s);
                        i++;
                    }
                    d.set(dateint[2] + 1900, dateint[1], dateint[0], dateint[3], dateint[4]);
                } catch (final Exception e) {
                    sender.sendMessage("Invalid date format!");
                    sender.sendMessage("dd/mm/yy/hh/mm");
                    return true;
                }
                if (pl == null) {
                    sender.sendMessage("No such player has played on the server before!");
                    return true;
                }
                if (isBanned(pl.getName())) {
                    sender.sendMessage("This player is already banned!");
                    return true;
                }
                if (d.getTimeInMillis() <= System.currentTimeMillis()) {
                    sender.sendMessage("Something went wrong!");
                    sender.sendMessage("Contact the plugin author!");
                    return true;
                }
                if (pl.isOnline()) {
                    final Player pl2 = (Player) pl;
                    pl2.kickPlayer("You are banned!");
                }
                if (!setBanned(pl.getName(), d.getTimeInMillis(), giveReason(args))) {
                    sender.sendMessage("Something went wrong when banning " + pl.getName());
                    return true;
                }
                return true;
            } else {
                sender.sendMessage("Wrong usage!");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("kick")) {
            if ((sender instanceof Player) && (getValue((Player) sender) <= 1)) {
                sender.sendMessage("You dont have permission to do this");
                return true;
            }
            if (args.length >= 1) {
                final Player pl = Bukkit.getPlayer(args[0]);
                pl.kickPlayer("You are kicked!\nReason:" + giveReason(args));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("IPBan")) {
            if ((sender instanceof Player) && (getValue((Player) sender) <= 1)) {
                sender.sendMessage("You dont have permission to do this");
                return true;
            }
            if (args.length >= 2) {
                final OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                if (pl == null) {
                    sender.sendMessage("No such player has played on the server before!");
                    return true;
                }
                if (pl.isOnline()) {
                    final Player pl2 = (Player) pl;
                    if (isBanned(pl2.getAddress().getAddress().getHostAddress())) {
                        sender.sendMessage("This IP is already banned!");
                        return true;
                    }
                    if (!setBanned(pl2.getAddress().getAddress().getHostAddress(), Long.MAX_VALUE, giveReason(args))) {
                        sender.sendMessage("Something went wrong when banning " + pl.getName());
                        return true;
                    }
                    pl2.kickPlayer("You are banned!");
                } else {
                    sender.sendMessage("Can't find the IP of " + pl.getName());
                    return true;
                }
                return true;
            } else {
                sender.sendMessage("Wrong usage!");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("iptempban")) {
            if ((sender instanceof Player) && (getValue((Player) sender) <= 1)) {
                sender.sendMessage("You dont have permission to do this");
                return true;
            }
            if (args.length >= 2) {
                final OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                final Calendar d = Calendar.getInstance();
                try {
                    final String[] ds = args[1].split("/");
                    final int[] dateint = new int[5];
                    int i = 0;
                    for (final String s : ds) {
                        dateint[i] = Integer.parseInt(s);
                        i++;
                    }
                    d.set(dateint[2] + 1900, dateint[1], dateint[0], dateint[3], dateint[4]);
                } catch (final Exception e) {
                    sender.sendMessage("Invalid date format!");
                    sender.sendMessage("dd/mm/yy/hh/mm");
                    return true;
                }
                if (pl == null) {
                    sender.sendMessage("No such player has played on the server before!");
                    return true;
                }

                if (d.getTimeInMillis() <= System.currentTimeMillis()) {
                    sender.sendMessage("Something went wrong!");
                    sender.sendMessage("Contact the plugin author!");
                    return true;
                }
                if (!pl.isOnline()) {
                    sender.sendMessage("Player not online, Can't find IP!");
                    return true;
                }
                final Player pl2 = (Player) pl;
                if (isBanned(pl2.getAddress().getAddress().getHostAddress())) {
                    sender.sendMessage("This player is already banned!");
                    return true;
                }
                if (!setBanned(pl2.getAddress().getAddress().getHostAddress(), d.getTimeInMillis(), giveReason(args))) {
                    sender.sendMessage("Something went wrong when banning " + pl.getName());
                    return true;
                }
                pl2.kickPlayer("You are banned!");
                return true;
            } else {
                sender.sendMessage("Wrong usage!");
                return true;
            }
        }
        return false;
    }

    private int getValue(Player sender) {
        return Main.playerrank.get(sender.getName()).value;
    }

    private String giveReason(String[] args) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public static void unban(String player) {
        if (Main.mysql == null) { return; }
        try {
            Main.mysql.query("DELETE FROM bannedinfo WHERE Name='" + player + "'");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean setBanned(String s, long date, String reason) {
        if (Main.mysql == null) { return false; }
        Main.mysql.insert("bannedinfo", new String[] {
                "Name", "BanReson", "BannedTo"
        }, new Object[] {
                s, reason, date
        });
        return true;
    }

    public static String getBanReason(String player) {
        if (Main.mysql == null) { return null; }
        try {
            final ResultSet rs = Main.mysql.query("SELECT * FROM bannedinfo WHERE Name='" + player + "'");
            if (rs.next()) {
                final String reason = rs.getString("BanReason");
                rs.close();
                return reason;
            }
            rs.close();
            return null;
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isBanned(String pl) {
        if (Main.mysql == null) { return false; }
        try {
            final ResultSet temp = Main.mysql.query("SELECT * FROM bannedinfo WHERE Name = '" + pl + "'");
            if (temp.first()) {
                temp.close();
                return true;
            }
            temp.close();
            return false;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getExpirationDate(String player) {
        if (Main.mysql == null) { return Long.MAX_VALUE; }
        try {
            final ResultSet temp = Main.mysql.query("SELECT * FROM bannedinfo WHERE Name = '" + player + "'");
            if (temp.first()) {
                final long me = temp.getLong("BannedTo");
                temp.close();
                return me;
            }
            temp.close();
            return Long.MAX_VALUE;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return Long.MAX_VALUE;
    }
}
