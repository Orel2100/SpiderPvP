package souprefillstation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
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


    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(1).equalsIgnoreCase("REFILL")) {
            Block block = event.getBlock();
            Block attachedBlock = getAttachedBlock(block);
            if (!soupAmounts.containsKey(attachedBlock)) {
                soupAmounts.put(attachedBlock, maxSoupAmount);
            }
            event.setLine(2, ChatColor.GREEN + "Soup: " + soupAmounts.get(attachedBlock));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
                && (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN)) {
            Sign sign = (Sign) block.getState();
            if (sign.getLine(1).equalsIgnoreCase("REFILL")) {
                Block attachedBlock = getAttachedBlock(block);
                if (!soupAmounts.containsKey(attachedBlock)) {
                    soupAmounts.put(attachedBlock, maxSoupAmount);
                    updateStationStatus(attachedBlock, Material.GREEN_WOOL);
                }

                if (soupAmounts.get(attachedBlock) > 0) {
                    refillSoup(player);
                    soupAmounts.put(attachedBlock, soupAmounts.get(attachedBlock) - 1);
                    sign.setLine(2, "Soup: " + soupAmounts.get(attachedBlock)); // Live update
                    sign.update();

                    if (soupAmounts.get(attachedBlock) == 0) {
                        updateStationStatus(attachedBlock, Material.RED_WOOL);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            soupAmounts.put(attachedBlock, maxSoupAmount);
                            updateStationStatus(attachedBlock, Material.GREEN_WOOL);
                        }, 200L);
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

    private Block getAttachedBlock(Block signBlock) {
        org.bukkit.block.data.type.WallSign wallSignData = null;
        if (signBlock.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {
            wallSignData = (org.bukkit.block.data.type.WallSign) signBlock.getBlockData();
        }
        if (wallSignData != null) {
            return signBlock.getRelative(wallSignData.getFacing().getOppositeFace());
        }
        return null;
    }


    private void updateStationStatus(Block block, Material material) {
        // Update all signs attached to the block
        for (BlockFace face : BlockFace.values()) {
            Block relative = block.getRelative(face);
            if (relative.getType() == Material.OAK_SIGN || relative.getType() == Material.OAK_WALL_SIGN) {
                Sign sign = (Sign) relative.getState();
                if (sign.getLine(1).equalsIgnoreCase("REFILL")) {
                    // Update the sign text with green color
                    sign.setLine(2, ChatColor.GREEN + "Soup: " + soupAmounts.get(block));
                    sign.update(); // This is important to apply the changes to the sign
                }
            }
        }

        // Directly update the block type to indicate the station status
        block.setType(material);
    }


}
