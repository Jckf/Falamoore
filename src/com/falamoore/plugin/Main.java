package com.falamoore.plugin;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.java.JavaPlugin;

import com.falamoore.plugin.commands.BanKick;
import com.falamoore.plugin.commands.EnviromentalControll;
import com.falamoore.plugin.commands.Maintenance;
import com.falamoore.plugin.commands.PromoteDemote;
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

    public static MySQL mysql;
    BanKick bankick;
    PromoteDemote promdem;
    Maintenance maintenance;
    StabSamtale ss;
    EnviromentalControll envc;
    public static ConversationFactory factory;
    public static boolean maintnance = false;
    public static Main plugin;

    @Override
    public void onDisable() {
        if (mysql.isConnected()) {
            mysql.close();
        }
        getServer().getScheduler().cancelTasks(this);
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
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new VehicleListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
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

        mysql = new MySQL(getConfig().getString("database.host"), getConfig().getString("database.port"), getConfig().getString("database.database"), getConfig().getString("database.username"), getConfig().getString("database.password"));

        mysql.open();
        try {
            if ((mysql == null) || !mysql.isConnected()) {
                getLogger().warning("MySQL not configured propperly!!");
                return;
            }
            mysql.query("CREATE TABLE IF NOT EXISTS bannedinfo (id INT(11) PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(130), BannedTo BIGINT, BanReason VARCHAR(130))");
            mysql.query("CREATE TABLE IF NOT EXISTS playerinfo (id INT(11) PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(130), Race VARCHAR(130), LastIP VARCHAR(130), Rank VARCHAR(130))");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        bankick = new BanKick();
        promdem = new PromoteDemote();
        maintenance = new Maintenance();
        envc = new EnviromentalControll();
        ss = new StabSamtale();

        getCommand("ban").setExecutor(bankick);
        getCommand("ipban").setExecutor(bankick);
        getCommand("tempipban").setExecutor(bankick);
        getCommand("kick").setExecutor(bankick);
        getCommand("tempban").setExecutor(bankick);
        getCommand("promote").setExecutor(promdem);
        getCommand("demote").setExecutor(promdem);
        getCommand("maintenance").setExecutor(maintenance);
        getCommand("env").setExecutor(envc);
        getCommand("ss").setExecutor(ss);
    }
}
