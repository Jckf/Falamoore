package com.falamoore.plugin.serializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SerialWarp implements Serializable {

    private static final long serialVersionUID = -5344795846034961718L;
    private static HashMap<String, String> locations = new HashMap<String, String>();
    
    public static Set<String> keySet() {
        return locations.keySet();
    }
    
    public static Location getLocation(String s) {
        return stringToLoc(s);
    }
    
    public static void add(String name, Location loc) {
        locations.put(name, locToString(loc));
    }
    
    public static World getWorld(String name) {
        return Bukkit.getWorld(locations.get(name).split(",.--.,")[0]);
    }
    
    public static int getX(String name) {
        return Integer.parseInt(locations.get(name).split(",.--.,")[1]);
    }

    public static int getY(String name) {
        return Integer.parseInt(locations.get(name).split(",.--.,")[2]);
    }

    public static int getZ(String name) {
        return Integer.parseInt(locations.get(name).split(",.--.,")[3]);
    }
    
    private static String locToString(Location to) {
        return to.getWorld() + ",.--.," + to.getBlockX() + ",.--.," + to.getBlockY() + ",.--.," + to.getBlockZ();
    }
    
    private static Location stringToLoc(String s) {
        return new Location(getWorld(s), getX(s), getY(s), getZ(s));
    }
}
