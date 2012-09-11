package com.falamoore.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class PermissionManager {

    public enum Rank {

        TRAVELER(0), USER(1), JARL(2), ARCHDUKE(3), EMPEROR(4);
        public int value;

        Rank(int i) {
            this.value = i;
        }
        
        public Rank getHigherRank() {
            switch(this) {
                case ARCHDUKE:
                    return EMPEROR;
                case EMPEROR:
                    return EMPEROR;
                case JARL:
                    return ARCHDUKE;
                case TRAVELER:
                    return USER;
                case USER:
                    return JARL;
                default:
                    return USER;
            }
        }
        
        public Rank getLowerRank() {
            switch(this) {
                case ARCHDUKE:
                    return JARL;
                case EMPEROR:
                    return ARCHDUKE;
                case JARL:
                    return USER;
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
                case ARCHDUKE:
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
        if (Main.mysql == null) return;
        try {
            Main.mysql.query("UPDATE playerinfo SET Rank='" + r.toString() + "' WHERE Name='" + p.getName() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Rank getRank(OfflinePlayer p) {
        if (Main.mysql == null) return null;
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM playerinfo WHERE Name='" + p.getName() + "'");
            rs.next();
            String s = rs.getString("Rank");
            rs.close();
            return Rank.valueOf(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
