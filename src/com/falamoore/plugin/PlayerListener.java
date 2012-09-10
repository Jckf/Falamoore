package com.falamoore.plugin;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    public static HashMap<String, ArrayList<String>> playerrace = new HashMap<String, ArrayList<String>>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy/HH/mm");
    Main main;

    public PlayerListener(Main plugin) {
        main = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (BanKick.isBanned(e.getPlayer())) {
            if (BanKick.getExpirationDate(e.getPlayer()) <= System.currentTimeMillis()) {
                BanKick.unban(e.getPlayer());
                e.allow();
            }
            Date d = new Date(BanKick.getExpirationDate(e.getPlayer()));
            e.disallow(Result.KICK_BANNED, BanKick.getBanReason(e.getPlayer().getName()) + "\nBanned untill: " + sdf.format(d));
        }
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

    // TODO GET TABLE!!

    private String getRace(Player s) {
        try {
            ResultSet temp = Main.mysql.query("SELECT * FROM playerinfo WHERE Player = '" + s.getName() + "'");
            temp.first();
            String me = temp.getString("Race");
            temp.close();
            return me;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
