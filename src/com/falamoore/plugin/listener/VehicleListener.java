package com.falamoore.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.falamoore.plugin.Main;
import com.falamoore.plugin.conversations.WarpPrompt;

public class VehicleListener implements Listener {

    @EventHandler
    public void minecartMoveEvent(VehicleMoveEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            e.getVehicle().getVelocity().multiply(2);
            if (!e.getTo().getChunk().isLoaded()) {
                e.getTo().getChunk().load(true);
            }
        }
    }

    @EventHandler
    public void boatMoveEvent(VehicleMoveEvent e) {
        if (e.getVehicle() instanceof Boat) {
            if (isInsideTPArea(e.getTo())) {
                e.getVehicle().setVelocity(e.getVehicle().getVelocity().zero());
            }
        }
    }

    private boolean isInsideTPArea(Location to) {
        final int x = to.getBlockX();
        final int y = to.getBlockY();
        final int z = to.getBlockZ();
        final World w = to.getWorld();
        for (final String s : Main.warps.keySet()) {
            final String[] l = Main.warps.get(s).split("|");
            final String[] l2 = l[0].split(",");
            final String[] l3 = l[1].split(",");
            final World w2 = Bukkit.getWorld(l2[0]);
            final int x2 = Integer.parseInt(l2[1]);
            final int y2 = Integer.parseInt(l2[2]);
            final int z2 = Integer.parseInt(l2[3]);
            final int x3 = Integer.parseInt(l3[0]);
            final int y3 = Integer.parseInt(l3[1]);
            final int z3 = Integer.parseInt(l3[2]);
            if ((w.getName().equals(w2.getName())) && ((x >= x2) && (x <= x3)) && ((z >= z2) && (z <= z3)) && ((y >= y2) && (y <= y3))) { return true; }
        }
        return false;
    }

    @EventHandler
    public void onPlayerEnterBoat(VehicleEnterEvent e) {
        if ((e.getEntered() instanceof Player) && (e.getVehicle() instanceof Boat)) {
            if (isInsideTPArea(e.getVehicle().getLocation())) {
                final Conversation conv = Main.factory.withFirstPrompt(new WarpPrompt()).withLocalEcho(false).buildConversation((Player) e.getEntered());
                conv.begin();
            }
        }
    }
}
