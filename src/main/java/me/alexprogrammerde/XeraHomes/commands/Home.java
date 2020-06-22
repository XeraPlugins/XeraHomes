package me.alexprogrammerde.XeraHomes.commands;

import me.alexprogrammerde.XeraHomes.XeraHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home implements CommandExecutor, TabExecutor {
    XeraHomes main;

    public Home(XeraHomes main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();

            if (args.length > 0) {
                BukkitRunnable savedata = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            main.statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + name + "(  " +
                                    "  name            MEDIUMTEXT NOT NULL," +
                                    "  world           MEDIUMTEXT NOT NULL," +
                                    "  x               MEDIUMTEXT NOT NULL," +
                                    "  y               MEDIUMTEXT NOT NULL," +
                                    "  z               MEDIUMTEXT NOT NULL," +
                                    "  yaw             MEDIUMTEXT NOT NULL," +
                                    "  pitch           MEDIUMTEXT NOT NULL);");

                            ResultSet result = main.statement.executeQuery("SELECT name FROM " + name + ";");
                            List<String> homes = new ArrayList<>();

                            while (result.next()) {
                                String name = result.getString("name");
                                homes.add(name);
                            }

                            if (homes.contains(args[0])) {
                                ResultSet home = main.statement.executeQuery("SELECT * FROM " + name + " WHERE name='" + args[0] + "'");
                                home.next();
                                BukkitRunnable teleport = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            player.teleport(new Location(Bukkit.getWorld(home.getString("world")), Double.parseDouble(home.getString("x")), Double.parseDouble(home.getString("y")), Double.parseDouble(home.getString("z")), Float.parseFloat(home.getString("yaw")), Float.parseFloat(home.getString("pitch"))));
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                        player.sendMessage("Teleported to " + args[0]);
                                    }
                                };

                                teleport.runTask(main);
                            } else {
                                player.sendMessage("Theres no home with that name!");
                            }

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                };

                savedata.runTaskAsynchronously(main);
            } else {
                sender.sendMessage("No home name given");
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();

            try {
                main.statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + name + "(  " +
                        "  name            MEDIUMTEXT NOT NULL," +
                        "  world           MEDIUMTEXT NOT NULL," +
                        "  x               MEDIUMTEXT NOT NULL," +
                        "  y               MEDIUMTEXT NOT NULL," +
                        "  z               MEDIUMTEXT NOT NULL," +
                        "  yaw             MEDIUMTEXT NOT NULL," +
                        "  pitch           MEDIUMTEXT NOT NULL);");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (args.length == 1) {
                    ResultSet result = main.statement.executeQuery("SELECT name FROM " + name + ";");
                    List<String> homes = new ArrayList<>();

                    try {
                        while (result.next()) {
                            String home = result.getString("name");

                            homes.add(home);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    List<String> completion = new ArrayList<>();

                    StringUtil.copyPartialMatches(args[0], homes, completion);

                    Collections.sort(completion);

                    return homes;
                }
            } catch(SQLException throwables){
                throwables.printStackTrace();
            }
        }

        return new ArrayList<>();
    }
}
