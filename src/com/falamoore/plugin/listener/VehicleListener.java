package com.falamoore.plugin.listener;

import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.falamoore.plugin.Main;
import com.falamoore.plugin.conversations.WarpPrompt;
import com.falamoore.plugin.serializable.SerialCuboid;

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
        if (e.getVehicle() instanceof Boat && e.getVehicle().getPassenger() instanceof Player) {
            if (isInsideTPArea(e.getVehicle())) {
                e.getVehicle().setVelocity(e.getVehicle().getVelocity().zero());
            }
        }
    }

    private boolean isInsideTPArea(Entity e) {
        for (SerialCuboid s : Main.warps.values()) {
            if (s.isInside(e)) { return true; }
        }
        return false;
    }

    @EventHandler
    public void onPlayerEnterBoat(VehicleEnterEvent e) {
        if ((e.getEntered() instanceof Player) && (e.getVehicle() instanceof Boat)) {
            if (isInsideTPArea(e.getVehicle())) {
                final Conversation conv = Main.factory.withFirstPrompt(new WarpPrompt()).withLocalEcho(false).buildConversation((Player) e.getEntered());
                conv.begin();
            }
        }
    }
}
