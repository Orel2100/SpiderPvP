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
import java.util.Random;
import java.util.Set;

public class ArenaCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public ArenaCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        File arenaFile = new File(plugin.getDataFolder(), "arenalocations.yml");
        FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        Set<String> spawnKeys = arenaConfig.getConfigurationSection("spawns").getKeys(false);
        if (spawnKeys.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No spawn points set for the arena.");
            return true;
        }

        int randomSpawnNumber = random.nextInt(spawnKeys.size()) + 1;
        String path = "spawns." + randomSpawnNumber;

        Location spawnLocation = new Location(
                plugin.getServer().getWorld(arenaConfig.getString(path + ".world")),
                arenaConfig.getDouble(path + ".x"),
                arenaConfig.getDouble(path + ".y"),
                arenaConfig.getDouble(path + ".z")
        );

        player.teleport(spawnLocation);
        player.sendMessage(ChatColor.GREEN + "Teleported to the arena!");

        return true;
    }
}
