package gameplay;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class ArenaManager {

    private final JavaPlugin plugin;
    private File arenaFile;
    private FileConfiguration arenaConfig;
    private final Map<String, ArenaStatus> arenaStatus = new HashMap<>();

    public ArenaManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        arenaFile = new File(plugin.getDataFolder(), "arenalocations.yml");
        if (!arenaFile.exists()) {
            try {
                arenaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
    }

    public FileConfiguration getConfig() {
        return arenaConfig;
    }

    public void saveConfig() {
        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save arenalocations.yml!");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
    }

    public void setArenaStatus(String arenaName, ArenaStatus status) {
        arenaStatus.put(arenaName, status);
    }

    public ArenaStatus getArenaStatus(String arenaName) {
        return arenaStatus.getOrDefault(arenaName, ArenaStatus.AVAILABLE);
    }

    public Location getSpectatorSpawn(String arenaName) {
        ConfigurationSection arena = getConfig().getConfigurationSection("arenas." + arenaName);
        if (arena == null) return null;
        return new Location(
            Bukkit.getWorld(arena.getString("specspawn.world")),
            arena.getDouble("specspawn.x"),
            arena.getDouble("specspawn.y"),
            arena.getDouble("specspawn.z")
        );
    }
}
