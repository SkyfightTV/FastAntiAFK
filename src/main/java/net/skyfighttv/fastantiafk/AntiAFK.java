package net.skyfighttv.fastantiafk;

import net.skyfighttv.fastantiafk.utils.file.FileManager;
import net.skyfighttv.fastantiafk.utils.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class AntiAFK extends BukkitRunnable {
    private final HashMap<Player, Long> afkTime = new HashMap<>();
    private final HashMap<Player, Location> afkLocation = new HashMap<>();
    private final String PERMISSION;
    private final int TIME;
    private final List<String> COMMAND;

    {
        YamlConfiguration config = FileManager.getValues().get(Files.Config);
        this.PERMISSION = config.getString("permission");
        this.TIME = config.getInt("time");
        this.COMMAND = new ArrayList<>();
        config.getStringList("command").forEach(s ->
                this.COMMAND.add(ChatColor.translateAlternateColorCodes('&', s)));
    }

    @Override
    public void run() {
        Bukkit.getLogger().info("Check");
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.isOnline() || player.hasPermission(this.PERMISSION))
                return;

            Location location = this.afkLocation.putIfAbsent(player, player.getLocation());
            if (location == null)
                return;

            if(location.equals(player.getLocation())) {
                this.afkTime.putIfAbsent(player, System.currentTimeMillis()/1000);
                if (((System.currentTimeMillis()/1000) - this.afkTime.get(player)) >= this.TIME)
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            COMMAND.forEach(s ->
                                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), s.replaceAll("%player%", player.getName())));
                        }
                    }.runTask(Main.getInstance());
            } else
                this.afkTime.remove(player);

            this.afkLocation.put(player, player.getLocation());
        });
    }
}
