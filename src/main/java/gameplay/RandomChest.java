package gameplay;

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

    public RandomChest(JavaPlugin plugin) {
        this.plugin = plugin;

        // Define rewards and their chances here
        rewardChances.put(new ItemStack(Material.DIAMOND), 0.5);
        rewardChances.put(new ItemStack(Material.GOLD_INGOT), 0.3);
        rewardChances.put(new ItemStack(Material.IRON_INGOT), 0.2);

        // Schedule chest spawning every 1 minute
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::spawnRandomChest, 0L, 1200L);

        // Load chest locations from the config
        createChestConfig();
        loadChestLocations();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.ENDER_CHEST) {
            giveRandomReward(event.getPlayer());
            block.getRelative(BlockFace.DOWN).setType(Material.AIR); // Remove the stone brick
            block.setType(Material.AIR); // Remove the chest after giving reward
            block.getWorld().strikeLightningEffect(block.getLocation().getBlock().getLocation());
            block.getWorld().setGameRule(GameRule.DO_FIRE_TICK, true);

        }
    }

    private void giveRandomReward(Player player) {
        double chance = new Random().nextDouble();
        double cumulativeChance = 0.0;
        for (ItemStack item : rewardChances.keySet()) {
            cumulativeChance += rewardChances.get(item);
            if (chance <= cumulativeChance) {
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GREEN + "You received a reward!");
                return;
            }
        }
    }

    public void spawnRandomChest() {
        if (chestLocations.isEmpty()) return;

        Location randomLocation = chestLocations.get(new Random().nextInt(chestLocations.size()));
        randomLocation.getBlock().setType(Material.STONE_BRICKS);
        randomLocation.add(0, 1, 0).getBlock().setType(Material.ENDER_CHEST);

    }

    public void removeChestLocation(int index) {
        if (index >= 0 && index < chestLocations.size()) {
            Location locationToRemove = chestLocations.remove(index);
            saveChestConfig(); // Save the updated list to the YML file
            Bukkit.getLogger().info("Removed chest location at " + locationToRemove.toString());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setchestlocation") && sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();
            chestLocations.add(loc);
            chestConfig.set("chest" + chestLocations.size(), loc);
            saveChestConfig();
            player.sendMessage(ChatColor.GREEN + "Chest location set!");
            return true;
        }     else if (command.getName().equalsIgnoreCase("removechestlocation") && sender instanceof Player) {
        if (args.length == 1) {
            try {
                int index = Integer.parseInt(args[0]) - 1; // Subtract 1 because lists are 0-indexed
                removeChestLocation(index);
                sender.sendMessage(ChatColor.GREEN + "Removed chest location " + (index + 1));
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
        for (String key : chestConfig.getKeys(false)) {
            chestLocations.add((Location) chestConfig.get(key));
        }
    }
}
