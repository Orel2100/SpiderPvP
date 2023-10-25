package souprefillstation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class SoupRefillStation implements Listener {

    private final int maxSoupAmount = 10;
    private HashMap<Block, Integer> soupAmounts = new HashMap<>();
    private Plugin plugin;

    public SoupRefillStation(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
                && (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN)) {
            Sign sign = (Sign) block.getState();
            if (sign.getLine(1).equalsIgnoreCase("REFILL")) {
                // Initialize the block in the HashMap if it's not already present
                if (!soupAmounts.containsKey(block)) {
                    soupAmounts.put(block, maxSoupAmount);
                    updateStationStatus(block, Material.GREEN_WOOL);
                }

                // Check if the station has soup
                if (soupAmounts.get(block) > 0) {
                    refillSoup(player);
                    soupAmounts.put(block, soupAmounts.get(block) - 1);
                    if (soupAmounts.get(block) == 0) {
                        updateStationStatus(block, Material.RED_WOOL);
                        // Schedule refill after 10 seconds
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            soupAmounts.put(block, maxSoupAmount);
                            updateStationStatus(block, Material.GREEN_WOOL);
                        }, 200L); // 20 ticks = 1 second, so 200 ticks = 10 seconds
                    }
                    player.sendMessage("Soup refilled!");
                } else {
                    player.sendMessage("This station is empty! Wait for it to refill.");
                }
            }
        }
    }

    private void refillSoup(Player player) {
        ItemStack soup = new ItemStack(Material.MUSHROOM_STEW);
        player.getInventory().addItem(soup);
    }

    private void updateStationStatus(Block block, Material material) {
        org.bukkit.block.data.type.WallSign wallSignData = null;
        if (block.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {
            wallSignData = (org.bukkit.block.data.type.WallSign) block.getBlockData();
        }

        if (wallSignData != null) {
            Block attachedBlock = block.getRelative(wallSignData.getFacing().getOppositeFace());
            attachedBlock.setType(material);
        } else {
            Bukkit.getLogger().warning("Unable to determine the attached face for block at " + block.getLocation());
        }
    }

}
