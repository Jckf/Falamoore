package com.falamoore.plugin.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import com.falamoore.plugin.Main;

public class BlockListener implements Listener {
    @EventHandler
    public void leafDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
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
        Main.QueryQueue.add("INSERT INTO blocklog (Name, x, y, z, Action) VALUES ('" + e.getPlayer().getName() + "', '" + e.getBlock().getX() + "', '" + e.getBlock().getY() + "', '" + e.getBlock().getZ() + "', 'placed')");
    }

    @EventHandler
    public void Place(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("block.place")) {
            e.setCancelled(true);
        }
        final Block b = e.getBlockPlaced();
        Main.QueryQueue.add("INSERT INTO blocklog (Name, x, y, z, Action) VALUES ('" + e.getPlayer().getName() + "', '" + b.getX() + "', '" + b.getY() + "', '" + b.getZ() + ", 'placed')");
    }

}
