package com.falamoore.plugin.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.falamoore.plugin.Main;
import com.falamoore.plugin.PermissionManager;
import com.falamoore.plugin.PermissionManager.Rank;
import com.falamoore.plugin.commands.BanKick;
import com.falamoore.plugin.conversations.RacePrompt;
import com.falamoore.plugin.runnables.PotionEffects;

public class PlayerListener implements Listener {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy/HH/mm");

    @EventHandler
    public void itemPickup(PlayerPickupItemEvent e) {
        if (PermissionManager.getRank(e.getPlayer()).value == PermissionManager.Rank.TRAVELER.value) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Place(BlockPlaceEvent e) {
        if (PermissionManager.getRank(e.getPlayer()).value == PermissionManager.Rank.TRAVELER.value) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void bucket(PlayerBucketEmptyEvent e) {
        if (PermissionManager.getRank(e.getPlayer()).value <= PermissionManager.Rank.USER.value) {
            if (e.getItemStack().getType() == Material.LAVA_BUCKET) {
                e.setCancelled(true);
            }
            if (e.getItemStack().getType() == Material.WATER_BUCKET) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void ignite(BlockIgniteEvent e) {
        if (e.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            if (PermissionManager.getRank(e.getPlayer()).value <= PermissionManager.Rank.USER.value) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void Brake(BlockBreakEvent e) {
        if (PermissionManager.getRank(e.getPlayer()).value == PermissionManager.Rank.TRAVELER.value) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new PotionEffects(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().isConversing()) {
            if (e.getTo().getBlock() != e.getFrom().getBlock()) {
                e.setCancelled(e.getPlayer().isConversing());
            }
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
            // } else if
            // (BanKick.isBanned(e.getPlayer().getAddress().getHostString())) {
            // if
            // (BanKick.getExpirationDate(e.getPlayer().getAddress().getHostString())
            // <= System.currentTimeMillis()) {
            // BanKick.unban(e.getPlayer().getAddress().getHostString());
            // e.allow();
            // }
            // final Date d = new
            // Date(BanKick.getExpirationDate(e.getPlayer().getAddress().getHostString()));
            // e.disallow(Result.KICK_BANNED,
            // BanKick.getBanReason(e.getPlayer().getAddress().getHostString())
            // + "\nBanned untill: " + sdf.format(d));
        } else {
            e.allow();
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        if (!dbContains(e.getPlayer())) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                @Override
                public void run() {
                    final Conversation c = Main.factory.withFirstPrompt(new RacePrompt()).withLocalEcho(false).buildConversation(e.getPlayer());
                    c.begin();
                }
            });
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        updateLastIP(e.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Rank rank = PermissionManager.getRank(e.getPlayer());
        if (rank == Rank.USER) {
            e.setFormat(e.getPlayer() + ": " + e.getMessage());
        } else {
            e.setFormat(rank.getColor() + "[" + rank.toString() + "] " + e.getPlayer().getName() + ": " + ChatColor.WHITE + e.getMessage());
        }
    }

    private void updateLastIP(Player p) {
        try {
            Main.mysql.query("UPDATE playerinfo SET LastIP='" + p.getAddress().getAddress().getHostAddress() + "' WHERE Name='" + p.getName() + "'");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean dbContains(Player p) {
        try {
            final ResultSet rs = Main.mysql.query("SELECT * FROM playerinfo WHERE Name='" + p.getName() + "'");
            final boolean ct = rs.next();
            rs.close();
            return ct;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
