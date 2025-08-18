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
import kitpvp.kitpvp.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaCommand implements CommandExecutor, Listener {

    private final Main plugin;
    private final Random random = new Random();

    public ArenaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("setup")) {
                if (!player.hasPermission("arena.setup")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /arena setup <name>");
                    return true;
                }
                String arenaName = args[1];
                new ArenaSetupWizard(plugin, player, arenaName).start();
                return true;
            }
        }

        if (!player.hasPermission("kitpvp.arena")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        File arenaFile = new File(plugin.getDataFolder(), "arenalocations.yml");
        FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        if (!arenaConfig.isConfigurationSection("spawns")) {
            player.sendMessage(ChatColor.RED + "No spawn points set for the arena.");
            return true;
        }

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ArenaSetupWizard wizard = ArenaSetupWizard.getWizard(player);
        if (wizard == null) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        event.setCancelled(true);
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Save Arena")) {
            if (wizard.isComplete()) {
                wizard.saveArena();
                player.sendMessage(ChatColor.GREEN + "Arena saved!");
            } else {
                player.sendMessage(ChatColor.RED + "Please set all locations before saving.");
            }
        } else {
            wizard.setLocation(itemName, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Set " + itemName + " to your current location.");
        }
    }
}
