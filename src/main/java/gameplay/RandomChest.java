package gameplay;

import kitpvp.kitpvp.EconomyManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RandomChest implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private ArrayList<Location> chestLocations = new ArrayList<>();
    private HashMap<ItemStack, Double> rewardChances = new HashMap<>();
    private FileConfiguration chestConfig;
    private File chestFile;
    private boolean chestSpawned = false;

    private EconomyManager economyManager;





    public RandomChest(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;

        // Schedule chest spawning every 1 minute
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::spawnRandomChest, 0L, 1200L);

        // Load chest locations from the config
        createChestConfig();
        loadChestLocations();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.ENDER_CHEST) {
            giveRandomReward(event.getPlayer());
            block.getWorld().strikeLightningEffect(block.getLocation());
            block.getRelative(BlockFace.DOWN).setType(Material.AIR); // Remove the stone brick
            block.setType(Material.AIR); // Remove the chest after giving reward
            chestSpawned = false;
        }
    }

    private void giveRandomReward(Player player) {
        System.out.println("Inside giveRandomReward method."); // Debug message

        double chance = new Random().nextDouble();
        int coinsToAdd = 0;

        // Determine coins to add based on chances
        if (chance < 0.05) { // 5% chance
            coinsToAdd = 40; // Give 100 coins
        } else if (chance < 0.15) { // 10% chance
            coinsToAdd = 25; // Give 50 coins
        } else if (chance < 0.30) { // 15% chance
            coinsToAdd = 15; // Give 25 coins
        } else { // 70% chance
            coinsToAdd = 5; // Give 10 coins
        }

        economyManager.addCoins(player, coinsToAdd);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "You've earned " + coinsToAdd + " coins from the chest!"));
    }





    public void spawnRandomChest() {
        if (chestLocations.isEmpty() || chestSpawned) return;

        Location randomLocation = chestLocations.get(new Random().nextInt(chestLocations.size()));
        randomLocation.getBlock().setType(Material.STONE_BRICKS);
        randomLocation.getBlock().getRelative(BlockFace.UP).setType(Material.ENDER_CHEST);
        chestSpawned = true;
    }

    @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
            Player player = (Player) sender;
            Location loc = player.getLocation();

            if (command.getName().equalsIgnoreCase("setchestlocation")) {
                // Check if player has permission
                if (!player.hasPermission("randomchest.setlocation")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }

                // Check if player is standing on a block
                if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You must be standing on a block to set a chest location!");
                    return true;
                }

                // Check if there's already a chest at that location
                if (loc.getBlock().getType() == Material.CHEST || loc.getBlock().getType() == Material.ENDER_CHEST) {
                    player.sendMessage(ChatColor.RED + "There's already a chest at this location!");
                    return true;
                }

                chestLocations.add(loc);
                chestConfig.set(String.valueOf(chestLocations.size()), loc);
                saveChestConfig();
                player.sendMessage(ChatColor.GREEN + "Chest location set!");
                return true;
        } else if (command.getName().equalsIgnoreCase("removechestlocation") && sender instanceof Player) {
            if (args.length == 1) {
                try {
                    int index = Integer.parseInt(args[0]);
                    if (chestConfig.contains(String.valueOf(index))) {
                        chestConfig.set(String.valueOf(index), null);
                        saveChestConfig();
                        loadChestLocations();
                        sender.sendMessage(ChatColor.GREEN + "Removed chest location " + index);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Chest location " + index + " does not exist.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number format!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /removechestlocation <number>");
            }
            return true;
        }
        return false;
    }

    public void createChestConfig() {
        chestFile = new File(plugin.getDataFolder(), "chestlocations.yml");
        if (!chestFile.exists()) {
            chestFile.getParentFile().mkdirs();
            plugin.saveResource("chestlocations.yml", false);
        }

        chestConfig = new YamlConfiguration();
        try {
            chestConfig.load(chestFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveChestConfig() {
        try {
            chestConfig.save(chestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChestLocations() {
        chestLocations.clear();
        for (String key : chestConfig.getKeys(false)) {
            chestLocations.add((Location) chestConfig.get(key));
        }
    }
}
