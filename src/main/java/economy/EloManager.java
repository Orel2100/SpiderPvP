package economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EloManager {

    private final JavaPlugin plugin;
    private File eloFile;
    private FileConfiguration eloConfig;

    public EloManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        eloFile = new File(plugin.getDataFolder(), "elo.yml");
        if (!eloFile.exists()) {
            plugin.saveResource("elo.yml", false);
        }
        eloConfig = YamlConfiguration.loadConfiguration(eloFile);
    }

    public int getElo(Player player) {
        return eloConfig.getInt("players." + player.getUniqueId().toString(), 1000);
    }

    public void setElo(Player player, int elo) {
        eloConfig.set("players." + player.getUniqueId().toString(), elo);
        saveConfig();
    }

    public void saveConfig() {
        try {
            eloConfig.save(eloFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save elo.yml!");
            e.printStackTrace();
        }
    }
}
