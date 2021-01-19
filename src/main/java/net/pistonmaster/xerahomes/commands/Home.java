package net.pistonmaster.xerahomes.commands;

import net.pistonmaster.xerahomes.XeraHomes;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
            String uuid = player.getUniqueId().toString();

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

                        if (args.length > 0) {
                            ResultSet result = main.statement.executeQuery("SELECT uuid FROM " + uuid + ";");
                            List<String> homes = new ArrayList<>();

                            while (result.next()) {
                                String uuid = result.getString("uuid");
                                homes.add(uuid);
                            }

                            if (homes.contains(args[0])) {
                                ResultSet home = main.statement.executeQuery("SELECT * FROM " + uuid + " WHERE uuid='" + args[0] + "'");
                                home.next();
                                BukkitRunnable teleport = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Location loc = new Location(Bukkit.getWorld(home.getString("world")), Double.parseDouble(home.getString("x")), Double.parseDouble(home.getString("y")), Double.parseDouble(home.getString("z")), Float.parseFloat(home.getString("yaw")), Float.parseFloat(home.getString("pitch")));
                                            Location playerloc = player.getLocation();

                                            boolean isspawnx = playerloc.getBlockX() < 1000 && playerloc.getBlockX() > -1000;
                                            boolean isspawnz = playerloc.getBlockZ() < 1000 && playerloc.getBlockZ() > -1000;

                                            if (isspawnx && isspawnz && !player.hasPermission("xerahomes.admin")) {
                                                player.sendMessage(playerloc.getBlockX() + "" + playerloc.getBlockZ());
                                                player.sendMessage("" + (playerloc.getBlockX() < 1000 && playerloc.getBlockX() > -1000));
                                                player.sendMessage("" + (playerloc.getBlockZ() < 1000 && playerloc.getBlockZ() > -1000));
                                                player.sendMessage("You need to be 1000 blocks away from spawn to do that!");
                                            } else {
                                                player.teleport(loc);
                                                player.sendMessage("Teleported to " + args[0]);
                                            }
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                    }
                                };

                                teleport.runTask(main);
                            } else {
                                player.sendMessage("Theres no home with that name!");
                            }
                        } else {
                            ComponentBuilder builder = new ComponentBuilder("Homes: ");

                            ResultSet result = main.statement.executeQuery("SELECT uuid FROM " + uuid + ";");
                            List<String> homes = new ArrayList<>();

                            while (result.next()) {
                                String uuid = result.getString("uuid");
                                homes.add(uuid);
                            }

                            boolean first = true;

                            for (String str : homes) {
                                if (first) {
                                    first = false;
                                    builder.append(str).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home " + str));
                                } else {
                                    builder.append(", ").append(str).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home " + str));
                                }
                            }

                            player.spigot().sendMessage(builder.create());
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            };

            savedata.runTaskAsynchronously(main);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();

            try {
                main.statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + uuid + "(  " +
                        "  uuid            MEDIUMTEXT NOT NULL," +
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
                    ResultSet result = main.statement.executeQuery("SELECT uuid FROM " + uuid + ";");
                    List<String> homes = new ArrayList<>();

                    try {
                        while (result.next()) {
                            String home = result.getString("uuid");

                            homes.add(home);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    List<String> completion = new ArrayList<>();

                    StringUtil.copyPartialMatches(args[0], homes, completion);

                    Collections.sort(completion);

                    return completion;
                }
            } catch (SQLException throwables){
                throwables.printStackTrace();
            }
        }

        return new ArrayList<>();
    }
}
