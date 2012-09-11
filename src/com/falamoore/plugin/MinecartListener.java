package com.falamoore.plugin;

import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class MinecartListener implements Listener {

    @EventHandler
    public void minecartMoveEvent(VehicleMoveEvent e) {
        if (e.getVehicle() instanceof Minecart) {
            e.getVehicle().getVelocity().multiply(2);
            if (!e.getTo().getChunk().isLoaded()) {
                e.getTo().getChunk().load(true);
            }
        }
    }
}
