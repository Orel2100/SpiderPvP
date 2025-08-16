package moderation;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentManager {

    private final JavaPlugin plugin;
    private File punishmentsFile;
    private FileConfiguration punishmentsConfig;

    public PunishmentManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        punishmentsFile = new File(plugin.getDataFolder(), "punishments.yml");
        if (!punishmentsFile.exists()) {
            plugin.saveResource("punishments.yml", false);
        }
        punishmentsConfig = YamlConfiguration.loadConfiguration(punishmentsFile);
    }

    public FileConfiguration getConfig() {
        return punishmentsConfig;
    }

    public void saveConfig() {
        try {
            punishmentsConfig.save(punishmentsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save punishments.yml!");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        punishmentsConfig = YamlConfiguration.loadConfiguration(punishmentsFile);
    }

    // I will add methods for adding, removing, and getting punishments later.
}
