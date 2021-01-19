package net.pistonmaster.xerahomes;

import co.aikar.idb.BukkitDB;
import net.pistonmaster.xerahomes.commands.DelHome;
import net.pistonmaster.xerahomes.commands.Home;
import net.pistonmaster.xerahomes.commands.SetHome;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public final class XeraHomes extends JavaPlugin {
    public Connection connection;
    public Statement statement;

    @Override
    public void onEnable() {
        Logger log = getLogger();
        saveDefaultConfig();

        try {
            openConnection(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"), getConfig().getString("mysql.db") , getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        PluginCommand home = getServer().getPluginCommand("home");
        PluginCommand sethome = getServer().getPluginCommand("sethome");
        PluginCommand delhome = getServer().getPluginCommand("delhome");

        if (home != null && sethome != null && delhome != null) {
            home.setExecutor(new Home(this));
            if (getConfig().getBoolean("tab")) {
                home.setTabCompleter(new Home(this));
            }

            sethome.setExecutor(new SetHome(this));
            if (getConfig().getBoolean("tab")) {
                sethome.setTabCompleter(new SetHome(this));
            }

            delhome.setExecutor(new DelHome(this));
            if (getConfig().getBoolean("tab")) {
                delhome.setTabCompleter(new DelHome(this));
            }
        }

        log.info("Enabled XeraHomes");
    }

    @Override
    public void onDisable() {
        Logger log = getLogger();
        log.info("Disabled XeraHomes");
    }

    public void openConnection(String host, int port, String database, String username, String password) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            connection = BukkitDB.createHikariDatabase(this, username, password, database, host + ":" + port).getConnection();
        }
    }
}

