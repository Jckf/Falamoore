package com.falamoore.plugin.serializable;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class WarpCuboid implements Serializable {

    private static final long serialVersionUID = -1L;

    private final int x, y, z, x1, y1, z1;
    private final String world;

    public WarpCuboid(String world, int x, int y, int z, int x1, int y1, int z1) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public Location getCenterLocation() {
        return new Location(Bukkit.getWorld(world), (x + x1), (y + y1), (z + z1));
    }

    public int centerX() {
        return x + x1;
    }

    public int centerY() {
        return y + y1;
    }

    public int centerZ() {
        return y + y1;
    }

    public boolean isInside(Entity e) {
        final int ex = e.getLocation().getBlockX();
        final int ey = e.getLocation().getBlockY();
        final int ez = e.getLocation().getBlockZ();
        if ((ex >= x) && (ex <= x1)) {
            if ((ey >= y) && (ex <= y1)) {
                if ((ez >= z) && (ez <= z1)) { return true; }
            }
        }
        return false;
    }
}
