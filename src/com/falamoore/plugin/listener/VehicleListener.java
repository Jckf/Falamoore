package com.falamoore.plugin.listener;

import org.bukkit.Bukkit;
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
    public void onPlayerEnterBoat(VehicleEnterEvent e) {
        if ((e.getEntered() instanceof Player) && (e.getVehicle() instanceof Boat)) {
            final World w = e.getVehicle().getWorld();
            final int x = e.getVehicle().getLocation().getBlockX();
            final int y = e.getVehicle().getLocation().getBlockY();
            final int z = e.getVehicle().getLocation().getBlockZ();
            for (final String s : Main.warps.keySet()) {
                final String[] l = Main.warps.get(s).split(",");
                final World w2 = Bukkit.getWorld(l[0]);
                final int x2 = Integer.parseInt(l[1]);
                final int y2 = Integer.parseInt(l[2]);
                final int z2 = Integer.parseInt(l[3]);
                System.out.print(w + "=" + w2 + " " + x + "=" + x2 + " " + y + "=" + y2 + " " + z + "=" + z2);
                if ((w == w2) && (x == x2) && (z == z2) && (y >= y2)) {
                    final Conversation conv = Main.factory.withFirstPrompt(new WarpPrompt()).withLocalEcho(false).buildConversation((Player) e.getEntered());
                    conv.begin();
                }
            }
        }
    }
}
