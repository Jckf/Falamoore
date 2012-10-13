package com.falamoore.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.falamoore.plugin.commands.BanKick;
import com.falamoore.plugin.commands.EnvironmentControl;
import com.falamoore.plugin.commands.Maintenance;
import com.falamoore.plugin.commands.PermRank;
import com.falamoore.plugin.commands.StabSamtale;
import com.falamoore.plugin.database.MySQL;
import com.falamoore.plugin.listener.BlockListener;
import com.falamoore.plugin.listener.PlayerListener;
import com.falamoore.plugin.listener.VehicleListener;
import com.falamoore.plugin.runnables.PotionEffects;
import com.falamoore.plugin.serializable.SerialWarp;
import com.falamoore.plugin.serializable.WarpCuboid;

public class Main extends JavaPlugin {
    public static ArrayList<WarpCuboid> warps = new ArrayList<WarpCuboid>();
    public static HashMap<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
    public static HashMap<String, String> rank = new HashMap<String, String>();
    public static HashMap<String, String> race = new HashMap<String, String>();

    public static MySQL mysql;
    BanKick bankick;
    PermRank permrank;
    Maintenance maintenance;
    StabSamtale ss;
    EnvironmentControl envc;
    public static ConversationFactory factory;
    public static boolean maintenance_enabled = false;
    public static Main plugin;

    @Override
    public void onDisable() {
        if (mysql.isConnected()) {
            mysql.close();
        }
        getServer().getScheduler().cancelTasks(this);
        saveStuff();
    }

    @Override
    public void onEnable() {
        plugin = this;
        createConfig();
        activateMySQL();
        registerListeners();
        activateConversations();
        registerCommands();
        activateEffects();
        loadStuff();
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new VehicleListener(), this);
        pm.registerEvents(new BlockListener(), this);
    }

    private void activateConversations() {
        // ///////////////////////
        // WARP LOCATIONS NEEDED//
        // CLICK BOAT IN HERE:
        warps.add(new WarpCuboid("world", -3, -3, -3, 3, 3, 3));
        // SELECT LOCATION FROM HERE:
        SerialWarp.add("Redcrest", new Location(Bukkit.getWorld("world"), 0, 0, 0));
        // ///////////////////////
        factory = new ConversationFactory(this);
    }

    private void activateEffects() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PotionEffects(), 0, 700);
    }

    private void createConfig() {
        getConfig().addDefault("database.host", "IP HERE");
        getConfig().addDefault("database.port", "PORT HERE");
        getConfig().addDefault("database.database", "DATABASE HERE");
        getConfig().addDefault("database.username", "USERNAME HERE");
        getConfig().addDefault("database.password", "PASSWORD HERE");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void activateMySQL() {
        mysql = new MySQL(
                getConfig().getString("database.host"), 
                getConfig().getString("database.port"),
                getConfig().getString("database.database"), 
                getConfig().getString("database.username"), 
                getConfig().getString("database.password")
                );

        mysql.open();
        try {
            if (!mysql.isConnected()) {
                getLogger().warning("MySQL not configured properly!");
                return;
            }
            mysql.query("CREATE TABLE IF NOT EXISTS bannedinfo (id INT(11) PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(130), BannedTo BIGINT, BanReason VARCHAR(130))");
            mysql.query("CREATE TABLE IF NOT EXISTS playerinfo (id INT(11) PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(130), Race VARCHAR(130), LastIP VARCHAR(130))");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        bankick = new BanKick();
        permrank = new PermRank();
        maintenance = new Maintenance();
        envc = new EnvironmentControl();
        ss = new StabSamtale();

        getCommand("ban").setExecutor(bankick);
        getCommand("perm").setExecutor(permrank);
        getCommand("ipban").setExecutor(bankick);
        getCommand("kick").setExecutor(bankick);
        getCommand("tempban").setExecutor(bankick);
        getCommand("rank").setExecutor(permrank);
        getCommand("maintenance").setExecutor(maintenance);
        getCommand("env").setExecutor(envc);
        getCommand("ss").setExecutor(ss);
    }

    @SuppressWarnings("unchecked")
    public void loadStuff() {
        try {
            final File perm = new File(getDataFolder(), "permissions.falamoore"), 
                    ran = new File(getDataFolder(), "ranks.falamoore"), 
                    rac = new File(getDataFolder(), "races.falamoore");
            final ObjectInputStream oos1 = new ObjectInputStream(new FileInputStream(perm));
            final ObjectInputStream oos2 = new ObjectInputStream(new FileInputStream(ran));
            final ObjectInputStream oos3 = new ObjectInputStream(new FileInputStream(rac));
            permissions = (HashMap<String, PermissionAttachment>) oos1.readObject();
            rank = (HashMap<String, String>) oos2.readObject();
            race = (HashMap<String, String>) oos3.readObject();
            oos1.close();
            oos2.close();
            oos3.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void saveStuff() {
        try {
            final File perm = new File(getDataFolder(), "permissions.falamoore"), 
                    ran = new File(getDataFolder(), "ranks.falamoore"),
                    rac = new File(getDataFolder(), "races.falamoore");
            final ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(perm));
            final ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(ran));
            final ObjectOutputStream oos3 = new ObjectOutputStream(new FileOutputStream(rac));
            oos1.writeObject(permissions);
            oos2.writeObject(rank);
            oos3.writeObject(race);
            oos1.flush();
            oos1.close();
            oos2.flush();
            oos2.close();
            oos3.flush();
            oos3.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
