package com.falamoore.plugin.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.falamoore.plugin.Main;
import com.falamoore.plugin.PermissionManager;
import com.falamoore.plugin.PermissionManager.Rank;
import com.falamoore.plugin.commands.BanKick;

public class PlayerListener implements Listener {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy/HH/mm");

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {

            @Override
            public void run() {
                final String race = getRace(e.getPlayer());
                if (race.equalsIgnoreCase("Elf")) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1400, 2));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1400, 2));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1400, 1));
                } else if (race.equalsIgnoreCase("Dwarf")) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1400, 2));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1400, 2));
                }
            }
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().isConversing()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (Main.maintnance) {
            if (PermissionManager.getRank(e.getPlayer()).value <= PermissionManager.Rank.JARL.value) {
                e.disallow(Result.KICK_OTHER, "Maintanance in progress");
            } else {
                e.allow();
            }
        }
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
        if (!Main.mysql.isConnected()) { return; }
        Main.playerrank.put(e.getPlayer().getName(), getRank(e.getPlayer()));
        final String race = getRace(e.getPlayer());
        Main.playerrace.put(e.getPlayer().getName(), getRace(e.getPlayer()));
        if (race.equalsIgnoreCase("Elf")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1400, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1400, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1400, 1));
        } else if (race.equalsIgnoreCase("Dwarf")) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1400, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1400, 2));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!Main.mysql.isConnected()) { return; }

        updateLastIP(e.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Rank rank = Main.playerrank.get(e.getPlayer().getName());
        if (rank == Rank.USER) {
            e.setFormat(e.getPlayer() + ": " + e.getMessage());
        } else {
            e.setFormat(rank.getColor() + "[" + rank.toString() + "] " + e.getPlayer().getName() + ": " + ChatColor.WHITE + e.getMessage());
        }
    }

    private void updateLastIP(Player p) {
        if (Main.mysql == null) { return; }
        try {
            Main.mysql.query("UPDATE playerinfo SET LastIP='" + p.getAddress().getAddress().getHostAddress() + "' WHERE Name='" + p.getName() + "'");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private Rank getRank(Player s) {
        if (Main.playerrank.containsKey(s.getName())) { return Main.playerrank.get(s.getName()); }
        try {
            final ResultSet temp = Main.mysql.query("SELECT * FROM playerinfo WHERE Player = '" + s.getName() + "'");
            temp.first();
            final String me = temp.getString("Rank");
            temp.close();
            return Rank.valueOf(me);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRace(Player s) {
        if (Main.playerrace.containsKey(s.getName())) { return Main.playerrace.get(s.getName()); }
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
