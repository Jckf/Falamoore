package com.falamoore.plugin;

import java.sql.ResultSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.falamoore.plugin.database.MySQL;

public class BanKick implements CommandExecutor {
    
    Main plugin;
    MySQL mysql;
    
    public BanKick(Main plugin) {
        this.plugin = plugin;
        this.mysql = plugin.mysql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return false;
    }
    
    public long getExpirationDate(Player player) {
        try {
            ResultSet temp = mysql.query("SELECT * FROM inserttablehere WHERE Player = '"+player.getName()+"'");
            temp.first();
            return temp.getLong("Date");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
