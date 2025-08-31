package globalkit;

import kitpvp.kitpvp.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalKitManager {

    private final Main plugin;
    private final Map<UUID, String> selectedKits = new HashMap<>();
    private final File kitsFile;
    private final FileConfiguration kitsConfig;

    public GlobalKitManager(Main plugin) {
        this.plugin = plugin;
        this.kitsFile = new File(plugin.getDataFolder(), "globalkits.yml");
        if (!kitsFile.exists()) {
            try {
                kitsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
        loadKits();
    }

    public void setKit(Player player, String kitName) {
        selectedKits.put(player.getUniqueId(), kitName);
    }

    public String getKit(Player player) {
        return selectedKits.getOrDefault(player.getUniqueId(), "None"); // Default to "None" if no kit is selected
    }

    public void saveKits() {
        for (Map.Entry<UUID, String> entry : selectedKits.entrySet()) {
            kitsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            kitsConfig.save(kitsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKits() {
        for (String uuidString : kitsConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            String kitName = kitsConfig.getString(uuidString);
            selectedKits.put(uuid, kitName);
        }
    }
}
