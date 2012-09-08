package com.falamoore.plugin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.falamoore.plugin.database.MySQL;

public class PlayerListener implements Listener {

    final MySQL mysql;
    public static HashMap<String, ArrayList<String>> playerrace = new HashMap<String, ArrayList<String>>();
    Main main;
    
    
    public PlayerListener(Main plugin) {
        main = plugin;
        mysql = plugin.mysql;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String race = getRace(e.getPlayer());
        ArrayList<String> newmap = playerrace.get(race);
        newmap.add(e.getPlayer().getName());
        playerrace.put(race, newmap);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String race = getRace(e.getPlayer());
        ArrayList<String> newmap = playerrace.get(race);
        newmap.remove(e.getPlayer().getName());
        playerrace.put(race, newmap);
    }

    private String getRace(Player s) {
        try {
            ResultSet temp = mysql.query("SELECT * FROM inserttablehere WHERE Player = '" + s.getName() + "'");
            temp.first();
            return temp.getString("Race");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
