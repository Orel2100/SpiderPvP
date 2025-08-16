package moderation;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ReportManager {

    private final JavaPlugin plugin;
    private File reportsFile;
    private FileConfiguration reportsConfig;

    public ReportManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        reportsFile = new File(plugin.getDataFolder(), "reports.yml");
        if (!reportsFile.exists()) {
            plugin.saveResource("reports.yml", false);
        }
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
    }

    public FileConfiguration getConfig() {
        return reportsConfig;
    }

    public void saveConfig() {
        try {
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save reports.yml!");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
    }

    // I will add methods for adding, removing, and getting reports later.
}
