package com.falamoore.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanKick implements CommandExecutor {

    Main plugin;

    public BanKick(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ban")) {
            if (args.length >= 2) {
                OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                if (pl == null) {
                    sender.sendMessage("No such player has played on the server before!");
                    return true;
                }
                if (isBanned(pl)) {
                    sender.sendMessage("This player is already banned!");
                    return true;
                }
                if (pl.isOnline()) {
                    Player pl2 = (Player) pl;
                    pl2.kickPlayer("You are banned!");
                }
                if (!setBanned(pl, Long.MAX_VALUE, giveReason(args))) {
                    sender.sendMessage("Something went wrong when banning " + pl.getName());
                    return true;
                }
                return true;
            } else {
                sender.sendMessage("Wrong usage!");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("tempban")) {
            if (args.length >= 2) {
                OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
                Calendar d = Calendar.getInstance();
                try {
                    String[] ds = args[1].split("/");
                    int[] dateint = new int[5];
                    int i = 0;
                    for (String s : ds) {
                        dateint[i] = Integer.parseInt(s);
                        i++;
                    }
                    d.set(dateint[2] + 1900, dateint[1], dateint[0], dateint[3], dateint[4]);
                } catch (Exception e) {
                    sender.sendMessage("Invalid date format!");
                    sender.sendMessage("dd/mm/yy/hh/mm");
                    return true;
                }
                if (pl == null) {
                    sender.sendMessage("No such player has played on the server before!");
                    return true;
                }
                if (isBanned(pl)) {
                    sender.sendMessage("This player is already banned!");
                    return true;
                }
                if (d.getTimeInMillis() <= System.currentTimeMillis()) {
                    sender.sendMessage("Something went wrong!");
                    sender.sendMessage("Contact the plugin author!");
                    return true;
                }
                if (pl.isOnline()) {
                    Player pl2 = (Player) pl;
                    pl2.kickPlayer("You are banned!");
                }
                if (!setBanned(pl, d.getTimeInMillis(), giveReason(args))) {
                    sender.sendMessage("Something went wrong when banning " + pl.getName());
                    return true;
                }
                return true;
            } else {
                sender.sendMessage("Wrong usage!");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("kick")) {
            if (args.length >= 1) {
                Player pl = Bukkit.getPlayer(args[0]);
                pl.kickPlayer("You are kicked!\nReason:" + giveReason(args));
                return true;
            }
        }
        return false;
    }
    
    private String giveReason(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1;i<args.length;i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    // TODO GET TABLE!!
    
    public static void unban(Player player) {
        try {
            Main.mysql.query("UPDATE playerinfo SET Banned='false', BanReason=''BannedTo='0' WHERE Player='" + player.getName() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean setBanned(OfflinePlayer pl, long date, String reason) {
        try {
            Main.mysql.query("UPDATE playerinfo SET Banned='true', BanReason='"+reason+"'BannedTo='" + date + "' WHERE Player='" + pl.getName() + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String getBanReason(String player) {
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM playerinfo WHERE Plyer='"+player+"'");
            rs.next();
            String reason = rs.getString("BanReason");
            rs.close();
            return reason;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isBanned(OfflinePlayer pl) {
        try {
            ResultSet temp = Main.mysql.query("SELECT * FROM playerinfo WHERE Player = '" + pl.getName() + "'");
            temp.first();
            boolean me = temp.getBoolean("Banned");
            temp.close();
            return me;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getExpirationDate(Player player) {
        try {
            ResultSet temp = Main.mysql.query("SELECT * FROM playerinfo WHERE Player = '" + player.getName() + "'");
            temp.first();
            long me = temp.getLong("BannedTo");
            temp.close();
            return me;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
