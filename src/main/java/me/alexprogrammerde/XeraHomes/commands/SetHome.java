package me.alexprogrammerde.XeraHomes.commands;

import me.alexprogrammerde.XeraHomes.XeraHomes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SetHome implements CommandExecutor, TabExecutor {
    XeraHomes main;

    public SetHome(XeraHomes main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();

            if (args.length > 0) {
                String world = player.getLocation().getWorld().getName();
                String x = String.valueOf(player.getLocation().getX());
                String y = String.valueOf(player.getLocation().getY());
                String z = String.valueOf(player.getLocation().getZ());
                String yaw = String.valueOf(player.getLocation().getYaw());
                String pitch = String.valueOf(player.getLocation().getPitch());

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
                                player.sendMessage("Theres already a home with that name! remove it first!");
                            } else {
                                main.statement.executeUpdate("INSERT INTO " + name + " (name, world, x, y, z, yaw, pitch) VALUES ('" + args[0] + "', '" + world + "', '" + x + "', '" + y + "', '" + z + "', '" + yaw + "', '" + pitch + "');");
                                player.sendMessage("Saved home " + args[0]);
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
        } else {
            sender.sendMessage("You need to be online to do that!");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}