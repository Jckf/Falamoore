package com.falamoore.plugin;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.java.JavaPlugin;

import com.falamoore.plugin.PermissionManager.Rank;
import com.falamoore.plugin.commands.BanKick;
import com.falamoore.plugin.commands.EnviromentalControll;
import com.falamoore.plugin.commands.Maintenance;
import com.falamoore.plugin.commands.PromoteDemote;
import com.falamoore.plugin.database.MySQL;
import com.falamoore.plugin.listener.PlayerListener;
import com.falamoore.plugin.listener.VehicleListener;
import com.falamoore.plugin.runnables.PotionEffects;
import com.falamoore.plugin.serializable.SerialCuboid;

public class Main extends JavaPlugin {

    public static HashMap<String, String> playerrace = new HashMap<String, String>();
    public static HashMap<String, Rank> playerrank = new HashMap<String, Rank>();
    public static HashMap<String, SerialCuboid> warps = new HashMap<String, SerialCuboid>();

    public static MySQL mysql;
    BanKick bankick;
    PromoteDemote promdem;
    Maintenance maintenance;
    EnviromentalControll envc;
    public static ConversationFactory factory;
    public static boolean maintnance = false;
    public static Main plugin;

    @Override
    public void onDisable() {
        if (mysql.isConnected()) {
            mysql.close();
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        createConfig();
        activateMySQL();
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new VehicleListener(), this);
        activateConversations();
        registerCommands();
        activateEffects();
        // /////////////////////DEBUG////////////////////
        playerrank.put("stelar7", Rank.valueOf("TRAVELER"));
        playerrace.put("stelar7", "elf");
        // /////////////////////DEBUG////////////////////
    }

    private void activateConversations() {
        factory = new ConversationFactory(this);
        warps.put("Redcrest", new SerialCuboid("world", -3, -3, -3, 3, 3, 3));
        warps.put("Ermiron", new SerialCuboid("world", 4, 4, 4, 10, 10, 10));
        warps.put("Karaz Ankor", new SerialCuboid("world", 11, 11, 11, 17, 17, 17));
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

        mysql = new MySQL(getConfig().getString("database.host"), getConfig().getString("database.port"), getConfig().getString("database.database"), getConfig().getString("database.username"),
                getConfig().getString("database.password"));

        mysql.open();
        try {
            if ((mysql == null) || !mysql.isConnected()) {
                getLogger().warning("MySQL not configured propperly!!");
                return;
            }
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
        maintenance = new Maintenance();
        envc = new EnviromentalControll();

        getCommand("ban").setExecutor(bankick);
        getCommand("ipban").setExecutor(bankick);
        getCommand("tempipban").setExecutor(bankick);
        getCommand("kick").setExecutor(bankick);
        getCommand("tempban").setExecutor(bankick);
        getCommand("promote").setExecutor(promdem);
        getCommand("demote").setExecutor(promdem);
        getCommand("maintenance").setExecutor(maintenance);
        getCommand("env").setExecutor(envc);
    }
}
