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

    @EventHandler
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
                && block != null && (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN)) {
            Sign sign = (Sign) block.getState();
            if (sign.getLine(1).equalsIgnoreCase("REFILL")) {
                Block attachedBlock = getAttachedBlock(block);
                if (!soupAmounts.containsKey(attachedBlock)) {
                    soupAmounts.put(attachedBlock, maxSoupAmount);
                }

                if (soupAmounts.get(attachedBlock) > 0) {
                    if (player.getInventory().firstEmpty() != -1) {
                        refillSoup(player);
                        int newAmount = soupAmounts.get(attachedBlock) - 1;
                        soupAmounts.put(attachedBlock, newAmount);
                        updateStationStatus(attachedBlock, newAmount > 0 ? Material.GREEN_WOOL : Material.RED_WOOL);

                        if (newAmount == 0) {
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                soupAmounts.put(attachedBlock, maxSoupAmount);
                                updateStationStatus(attachedBlock, Material.GREEN_WOOL);
                            }, 200L);
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "FULL!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "This station is empty! Wait for it to refill.");
                }
            }
        }
    }

    private void refillSoup(Player player) {
        ItemStack soup = new ItemStack(Material.MUSHROOM_STEW);
        player.getInventory().addItem(soup);
        player.sendMessage(ChatColor.GREEN + "Soup refilled!");
    }

    private Block getAttachedBlock(Block signBlock) {
        if (signBlock.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {
            org.bukkit.block.data.type.WallSign wallSignData = (org.bukkit.block.data.type.WallSign) signBlock.getBlockData();
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
                    // Update the sign text with the current soup amount
                    sign.setLine(2, ChatColor.GREEN + "Soup: " + soupAmounts.get(block));
                    sign.update(); // Apply the changes to the sign
                }
            }
        }

        // Update the block material to indicate the station's status
        block.setType(material);
    }
}
