package net.pistonmaster.xerahomes.commands;

import net.pistonmaster.xerahomes.XeraHomes;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
import java.util.Objects;

public class SetHome implements CommandExecutor, TabExecutor {
    XeraHomes main;

    public SetHome(XeraHomes main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();

            if (args.length > 0) {
                String world = Objects.requireNonNull(player.getLocation().getWorld()).getName();
                String x = String.valueOf(player.getLocation().getX());
                String y = String.valueOf(player.getLocation().getY());
                String z = String.valueOf(player.getLocation().getZ());
                String yaw = String.valueOf(player.getLocation().getYaw());
                String pitch = String.valueOf(player.getLocation().getPitch());

                BukkitRunnable savedata = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            main.statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + uuid + "(  " +
                                    "  uuid            MEDIUMTEXT NOT NULL," +
                                    "  world           MEDIUMTEXT NOT NULL," +
                                    "  x               MEDIUMTEXT NOT NULL," +
                                    "  y               MEDIUMTEXT NOT NULL," +
                                    "  z               MEDIUMTEXT NOT NULL," +
                                    "  yaw             MEDIUMTEXT NOT NULL," +
                                    "  pitch           MEDIUMTEXT NOT NULL);");

                            ResultSet result = main.statement.executeQuery("SELECT uuid FROM " + uuid + ";");
                            List<String> homes = new ArrayList<>();

                            while (result.next()) {
                                String uuid = result.getString("uuid");
                                homes.add(uuid);
                            }

                            if (homes.contains(args[0])) {
                                player.sendMessage("There is already a home with that uuid!");
                            } else {
                                boolean general = player.hasPermission("xerahomes.default");
                                boolean donator1 = player.hasPermission("xerahomes.donator1");
                                boolean donator2 = player.hasPermission("xerahomes.donator2");
                                boolean admin = player.hasPermission("xerahomes.admin");

                                if (!admin) {
                                    if (donator2 && homes.size() >= 15) {
                                        player.spigot().sendMessage(new ComponentBuilder("You have too many homes! Donators get more homes. Do: ").append("/donate").event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/donate")).create());
                                        return;
                                    } else if (donator1 && homes.size() >= 10) {
                                        player.spigot().sendMessage(new ComponentBuilder("You have too many homes! Donators get more homes. Do: ").append("/donate").event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/donate")).create());
                                        return;
                                    } else if (general && homes.size() >= 5) {
                                        player.spigot().sendMessage(new ComponentBuilder("You have too many homes! Donators get more homes. Do: ").append("/donate").event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/donate")).create());
                                        return;
                                    }
                                }

                                main.statement.executeUpdate("INSERT INTO " + uuid + " (uuid, world, x, y, z, yaw, pitch) VALUES ('" + args[0] + "', '" + world + "', '" + x + "', '" + y + "', '" + z + "', '" + yaw + "', '" + pitch + "');");
                                player.sendMessage("Saved home " + args[0]);
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                };

                savedata.runTaskAsynchronously(main);
            } else {
                sender.sendMessage("No home name given!");
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
