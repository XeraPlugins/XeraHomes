package me.alexprogrammerde.XeraHomes;

import co.aikar.idb.BukkitDB;
import me.alexprogrammerde.XeraHomes.commands.DelHome;
import me.alexprogrammerde.XeraHomes.commands.Home;
import me.alexprogrammerde.XeraHomes.commands.SetHome;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public final class XeraHomes extends JavaPlugin {
    public Connection connection;
    public Statement statement;
    Logger log;

    public void onEnable() {
        log = getLogger();
        this.saveDefaultConfig();

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
            home.setTabCompleter(new Home(this));

            sethome.setExecutor(new SetHome(this));
            sethome.setTabCompleter(new SetHome(this));

            delhome.setExecutor(new DelHome(this));
            delhome.setTabCompleter(new DelHome(this));
        }

        log.info("Enabled XeraHomes");
    }

    public void onDisable() {
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

