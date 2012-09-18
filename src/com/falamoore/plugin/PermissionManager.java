package com.falamoore.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PermissionManager {

    public enum Rank {

        TRAVELER(0),
        USER(1),
        GUIDE(2),
        JARL(3),
        EMPEROR(4);
        public int value;

        Rank(int i) {
            this.value = i;
        }

        public Rank getHigherRank() {
            switch (this) {
                case GUIDE:
                    return JARL;
                case EMPEROR:
                    return EMPEROR;
                case JARL:
                    return EMPEROR;
                case TRAVELER:
                    return USER;
                case USER:
                    return GUIDE;
                default:
                    return USER;
            }
        }

        public Rank getLowerRank() {
            switch (this) {
                case GUIDE:
                    return EMPEROR;
                case EMPEROR:
                    return JARL;
                case JARL:
                    return GUIDE;
                case TRAVELER:
                    return TRAVELER;
                case USER:
                    return TRAVELER;
                default:
                    return USER;
            }
        }

        public ChatColor getColor() {
            switch (this) {
                case GUIDE:
                    return ChatColor.DARK_AQUA;
                case EMPEROR:
                    return ChatColor.GREEN;
                case JARL:
                    return ChatColor.BLUE;
                case TRAVELER:
                    return ChatColor.GRAY;
                case USER:
                    return ChatColor.WHITE;
                default:
                    return ChatColor.WHITE;
            }
        }

        @Override
        public String toString() {
            return this.name().substring(0, 1) + this.name().substring(1, this.name().length()).toLowerCase();
        }
    }

    public static void setNewRank(OfflinePlayer p, Rank r) {
        try {
            Main.mysql.query("UPDATE playerinfo SET Rank='" + r.toString() + "' WHERE Name='" + p.getName() + "'");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static Rank getRank(OfflinePlayer p) {
        try {
            final ResultSet rs = Main.mysql.query("SELECT * FROM playerinfo WHERE Name='" + p.getName() + "'");
            rs.next();
            final String s = rs.getString("Rank");
            rs.close();
            return Rank.valueOf(s);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRace(Player p) {
        try {
            final ResultSet rs = Main.mysql.query("SELECT * FROM playerinfo WHERE Name='" + p.getName() + "'");
            if (rs.next()) {
                final String s = rs.getString("Race");
                rs.close();
                return s;
            }
            return null;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
