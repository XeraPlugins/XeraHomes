package me.alexprogrammerde.XeraHomes;

import com.mysql.jdbc.Connection;
import me.alexprogrammerde.XeraHomes.commands.DelHome;
import me.alexprogrammerde.XeraHomes.commands.Home;
import me.alexprogrammerde.XeraHomes.commands.SetHome;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public final class XeraHomes extends JavaPlugin {
    public Connection connection;
    public Statement statement;

    public void onEnable() {
        Logger log = getLogger();
        this.saveDefaultConfig();

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection(getConfig().getString("mysql.host"), getConfig().getInt("mysql.port"), getConfig().getString("mysql.db") , getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));
                    statement = connection.createStatement();
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
            }
        };

        runnable.runTaskAsynchronously(this);

        getServer().getPluginCommand("sethome").setExecutor(new SetHome(this));
        getServer().getPluginCommand("sethome").setTabCompleter(new SetHome(this));

        getServer().getPluginCommand("home").setExecutor(new Home(this));
        getServer().getPluginCommand("home").setTabCompleter(new Home(this));

        getServer().getPluginCommand("delhome").setExecutor(new DelHome(this));
        getServer().getPluginCommand("delhome").setTabCompleter(new DelHome(this));

        log.info("Enabled XeraHomes.");
    }

    public void onDisable() {
        try {
            if (connection!=null && !connection.isClosed()){
                connection.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        getLogger().info("Disabled XeraHomes. :)");
    }

    public void openConnection(String host, int port, String database, String username, String password) throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");

            connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
        }
    }
}

