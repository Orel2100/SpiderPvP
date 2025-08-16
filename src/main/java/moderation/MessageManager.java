package moderation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageManager {

    private final JavaPlugin plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String message = messagesConfig.getString(path);
        if (message == null) {
            return "Message not found: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> getMessageList(String path) {
        List<String> messages = messagesConfig.getStringList(path);
        if (messages.isEmpty()) {
            messages.add("Message not found: " + path);
            return messages;
        }
        messages.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s));
        return messages;
    }
}
