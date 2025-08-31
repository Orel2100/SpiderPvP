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

import java.util.Optional;
import java.util.Set;

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

    public Optional<String> findAvailableArena() {
        ConfigurationSection arenas = getConfig().getConfigurationSection("arenas");
        if (arenas == null) {
            return Optional.empty();
        }
        Set<String> arenaNames = arenas.getKeys(false);
        return arenaNames.stream()
                .filter(name -> getArenaStatus(name) == ArenaStatus.AVAILABLE)
                .findFirst();
    }

    public Location getSpawn1(String arenaName) {
        return getSpawnLocation(arenaName, "spawn1");
    }

    public Location getSpawn2(String arenaName) {
        return getSpawnLocation(arenaName, "spawn2");
    }

    private Location getSpawnLocation(String arenaName, String spawnKey) {
        ConfigurationSection spawnSection = getConfig().getConfigurationSection("arenas." + arenaName + "." + spawnKey);
        if (spawnSection == null) return null;

        String worldName = spawnSection.getString("world");
        if (worldName == null || Bukkit.getWorld(worldName) == null) return null;

        return new Location(
                Bukkit.getWorld(worldName),
                spawnSection.getDouble("x"),
                spawnSection.getDouble("y"),
                spawnSection.getDouble("z"),
                (float) spawnSection.getDouble("yaw"),
                (float) spawnSection.getDouble("pitch")
        );
    }
}
