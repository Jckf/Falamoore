package com.falamoore.plugin.serializable;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class SerialCuboid implements Serializable {

    private static final long serialVersionUID = 2994391803139795388L;

    private final int x, y, z, x1, y1, z1;
    private final String world;

    public SerialCuboid(String world, int x, int y, int z, int x1, int y1, int z1) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public Location getCenterLocation() {
        return new Location(Bukkit.getWorld(world), (x - x1), (y - y1), (z - z1));
    }

    public boolean isInside(Entity e) {
        int ex = e.getLocation().getBlockX();
        int ey = e.getLocation().getBlockY();
        int ez = e.getLocation().getBlockZ();
        if (ex >= x && ex <= x1) {
            if (ey >= y && ex <= y1) {
                if (ez >= z && ez <= z1) { return true; }
            }
        }
        return false;
    }
}
