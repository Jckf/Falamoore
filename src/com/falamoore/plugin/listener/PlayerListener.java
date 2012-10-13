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
import org.bukkit.permissions.PermissionAttachment;

import com.falamoore.plugin.Main;
import com.falamoore.plugin.commands.BanKick;
import com.falamoore.plugin.conversations.RacePrompt;
import com.falamoore.plugin.runnables.PotionEffects;

public class PlayerListener implements Listener {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy/HH/mm");

    @EventHandler
    public void itemPickup(PlayerPickupItemEvent e) {
        if (!e.getPlayer().hasPermission("item.pickup")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Place(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("block.place")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void bucket(PlayerBucketEmptyEvent e) {
        if (!e.getPlayer().hasPermission("bucket.empty")) {
            switch (e.getItemStack().getType()) {
                case LAVA_BUCKET:
                case WATER_BUCKET:
                    e.setCancelled(true);
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void ignite(BlockIgniteEvent e) {
        if (e.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            if (!e.getPlayer().hasPermission("flint.use")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void Brake(BlockBreakEvent e) {
        if (!e.getPlayer().hasPermission("block.brake")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new PotionEffects());
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
        if (Main.maintenance_enabled) {
            if (!e.getPlayer().hasPermission("maintenance.join")) {
                e.disallow(Result.KICK_OTHER, "Maintenance in progress!");
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
            e.disallow(Result.KICK_BANNED, BanKick.getBanReason(e.getPlayer().getName()) + "\nBanned until: " + sdf.format(d));
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
                    Main.factory.withFirstPrompt(new RacePrompt()).withLocalEcho(false).buildConversation(e.getPlayer()).begin();
                }
            });
        }
        if (!Main.permissions.containsKey(e.getPlayer().getName())) {
            final PermissionAttachment attachment = e.getPlayer().addAttachment(Main.plugin);
            Main.permissions.put(e.getPlayer().getName(), attachment);
        } else {
            final PermissionAttachment attachment = e.getPlayer().addAttachment(Main.plugin);
            for (final String s : attachment.getPermissions().keySet()) {
                attachment.setPermission(s, attachment.getPermissions().get(s));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        updateLastIP(e.getPlayer());
        e.getPlayer().removeAttachment(Main.permissions.get(e.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final String rank = Main.rank.get(e.getPlayer());
        if (rank.equalsIgnoreCase("user")) {
            e.setFormat(e.getPlayer() + ": " + e.getMessage());
        } else {
            e.setFormat(getColor(rank) + "[" + rank + "] " + e.getPlayer().getName() + ": " + ChatColor.WHITE + e.getMessage());
        }
    }

    private void updateLastIP(Player p) {
        try {
            if (!Main.mysql.isConnected()) {
                Main.mysql.open();
            }
            Main.mysql.query("UPDATE playerinfo SET LastIP='" + p.getAddress().getAddress().getHostAddress() + "' WHERE Name='" + p.getName() + "'");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean dbContains(Player p) {
        try {
            if (!Main.mysql.isConnected()) {
                Main.mysql.open();
            }
            final ResultSet rs = Main.mysql.query("SELECT * FROM playerinfo WHERE Name='" + p.getName() + "'");
            final boolean ct = rs.next();
            rs.close();
            return ct;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ChatColor getColor(String rank) {
        switch (rank.toUpperCase()) {
            case "GUIDE":
                return ChatColor.DARK_AQUA;
            case "EMPEROR":
                return ChatColor.GREEN;
            case "JARL":
                return ChatColor.BLUE;
            case "TRAVELER":
                return ChatColor.GRAY;
            default:
                return ChatColor.WHITE;
        }
    }
}
