package com.falamoore.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    public static HashMap<String, ArrayList<String>> playerrace = new HashMap<String, ArrayList<String>>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy/HH/mm");
    Main main;

    public PlayerListener(Main plugin) {
        main = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (BanKick.isBanned(e.getPlayer().getName())) {
            if (BanKick.getExpirationDate(e.getPlayer().getName()) <= System.currentTimeMillis()) {
                BanKick.unban(e.getPlayer().getName());
                e.allow();
            }
            final Date d = new Date(BanKick.getExpirationDate(e.getPlayer().getName()));
            e.disallow(Result.KICK_BANNED, BanKick.getBanReason(e.getPlayer().getName()) + "\nBanned untill: " + sdf.format(d));
        } else if (BanKick.isBanned(e.getPlayer().getAddress().getAddress().getHostAddress())) {
            if (BanKick.getExpirationDate(e.getPlayer().getAddress().getAddress().getHostAddress()) <= System.currentTimeMillis()) {
                BanKick.unban(e.getPlayer().getAddress().getAddress().getHostAddress());
                e.allow();
            }
            final Date d = new Date(BanKick.getExpirationDate(e.getPlayer().getAddress().getAddress().getHostAddress()));
            e.disallow(Result.KICK_BANNED, BanKick.getBanReason(e.getPlayer().getAddress().getAddress().getHostAddress()) + "\nBanned untill: " + sdf.format(d));
        } else {
            e.allow();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final String race = getRace(e.getPlayer());
        final ArrayList<String> newmap = playerrace.get(race);
        newmap.add(e.getPlayer().getName());
        playerrace.put(race, newmap);
        if (race.equalsIgnoreCase("Elf")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 300, 1));
        } else if (race.equalsIgnoreCase("Dwarf")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 2));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        final String race = getRace(e.getPlayer());
        final ArrayList<String> newmap = playerrace.get(race);
        newmap.remove(e.getPlayer().getName());
        playerrace.put(race, newmap);
        updateLastIP(e.getPlayer());
    }

    private void updateLastIP(Player p) {
        try {
            Main.mysql.query("UPDATE playerinfo SET LastIP='"+p.getAddress().getAddress().getHostAddress()+"' WHERE Name='"+p.getName()+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getRace(Player s) {
        try {
            final ResultSet temp = Main.mysql.query("SELECT * FROM playerinfo WHERE Player = '" + s.getName() + "'");
            temp.first();
            final String me = temp.getString("Race");
            temp.close();
            return me;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
