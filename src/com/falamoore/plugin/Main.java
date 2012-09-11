package com.falamoore.plugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.falamoore.plugin.PermissionManager.Rank;
import com.falamoore.plugin.database.MySQL;

public class Main extends JavaPlugin {
    
    public static HashMap<String, ArrayList<String>> playerrace = new HashMap<String, ArrayList<String>>();
    public static HashMap<String, Rank> playerrank = new HashMap<String, Rank>();

    static MySQL mysql;
    BanKick bankick;
    PromoteDemote promdem;

    @Override
    public void onDisable() {
        mysql.close();
    }

    @Override
    public void onEnable() {
        createConfig();
        activateMySQL();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new MinecartListener(), this);
        registerCommands();
        activateEffects();
    }

    private void activateEffects() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (final String s : playerrace.keySet()) {
                    for (final String player : playerrace.get(s)) {
                        if (s.equalsIgnoreCase("Elf")) {
                            getServer().getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
                            getServer().getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2));
                            getServer().getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 300, 1));
                        } else if (s.equalsIgnoreCase("Dwarf")) {
                            getServer().getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 2));
                            getServer().getPlayer(player).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 2));
                        }
                    }
                }
            }
        }, 0, 300);
    }

    private void createConfig() {
        getConfig().addDefault("database.host", "IP HERE");
        getConfig().addDefault("database.port", "PORT HERE");
        getConfig().getString("database.database", "DATABASE HERE");
        getConfig().getString("database.username", "USERNAME HERE");
        getConfig().getString("database.password", "PASSWORD HERE");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void activateMySQL() {
        mysql = new MySQL(getConfig().getString("database.host"), getConfig().getString("database.port"), getConfig().getString("database.database"), getConfig().getString("database.username"),
                getConfig().getString("database.password"));
        try {
            mysql.query("CREATE TABLE IF NOT EXISTS bannedinfo (id INT(11) PRIMARY KEY, Name VARCHAR(130), BannedTo BIGINT, BanReason VARCHAR(130))");
            mysql.query("CREATE TABLE IF NOT EXISTS playerinfo (id INT(11) PRIMARY KEY, Name VARCHAR(130), Race VARCHAR(130), LastIP VARCHAR(130), Rank VARCHAR(130))");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public MySQL getMySQL() {
        return mysql;
    }

    private void registerCommands() {
        bankick = new BanKick();
        promdem = new PromoteDemote();
        
        getCommand("ban").setExecutor(bankick);
        getCommand("ipban").setExecutor(bankick);
        getCommand("iptempban").setExecutor(bankick);
        getCommand("kick").setExecutor(bankick);
        getCommand("tempban").setExecutor(bankick);
        getCommand("promote").setExecutor(promdem);
        getCommand("demote").setExecutor(promdem);
    }
}
