package gameplay;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SetArenaSpawnCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SetArenaSpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("arena.setspawn")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        File arenaFile = new File(plugin.getDataFolder(), "arenalocations.yml");
        FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        int spawnNumber = 1; // Default to 1
        if (arenaConfig.isConfigurationSection("spawns")) {
            spawnNumber = arenaConfig.getConfigurationSection("spawns").getKeys(false).size() + 1;
        }

        String path = "spawns." + spawnNumber;

        arenaConfig.set(path + ".world", player.getWorld().getName());
        arenaConfig.set(path + ".x", player.getLocation().getX());
        arenaConfig.set(path + ".y", player.getLocation().getY());
        arenaConfig.set(path + ".z", player.getLocation().getZ());

        try {
            arenaConfig.save(arenaFile);
            player.sendMessage(ChatColor.GREEN + "Spawn point set successfully at position " + spawnNumber + "!");
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "An error occurred while saving the spawn point.");
            e.printStackTrace();
        }

        return true;
    }
}
