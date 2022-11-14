package fr.viazel.speedrunnervshunter.utils;

import fr.viazel.speedrunnervshunter.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private YamlConfiguration yamlConfiguration;
    private File file;

    public ConfigFile() {
        file = new File(Main.getInstance().getDataFolder(), "config.yml");
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void setDefaultSpawn(Location location) {
        yamlConfiguration.set("spawn.world", "world");
        yamlConfiguration.set("spawn.x", location.getBlockX());
        yamlConfiguration.set("spawn.y", location.getBlockY());
        yamlConfiguration.set("spawn.z", location.getBlockZ());
        yamlConfiguration.set("spawn.yaw", location.getYaw());
        yamlConfiguration.set("spawn.pitch", location.getPitch());
        save();
    }

    public Location getDefaultSpawnLocation() {

        if(yamlConfiguration.getString("spawn").equals("null")) {
            throw new RuntimeException("You have to set your spawn location !");
        }

        Location l = new Location(Bukkit.getWorld(yamlConfiguration.getString("spawn.world")),
                                  yamlConfiguration.getInt("spawn.x"),
                                  yamlConfiguration.getInt("spawn.y"),
                                  yamlConfiguration.getInt("spawn.z"),
                     (float) yamlConfiguration.getDouble("spawn.yaw"),
                     (float) yamlConfiguration.getDouble("spawn.pitch"));

        return l;
    }

    private void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
