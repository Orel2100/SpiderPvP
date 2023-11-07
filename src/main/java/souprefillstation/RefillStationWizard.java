package souprefillstation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RefillStationWizard implements Listener, CommandExecutor {
    private final JavaPlugin plugin;

    public RefillStationWizard(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("giverefillstick")) {
            Player player = (Player) sender;
            ItemStack refillStick = new ItemStack(Material.STICK);
            ItemMeta meta = refillStick.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GREEN + "Refill Station Stick");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Right click to create a refill station.");
                meta.setLore(lore);
                refillStick.setItemMeta(meta);
            }
            player.getInventory().addItem(refillStick);
            player.sendMessage(ChatColor.GREEN + "You have been given the Refill Station Stick!");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block clickedBlock = event.getClickedBlock();

        if (event.getAction() == event.getAction().RIGHT_CLICK_BLOCK && item != null &&
                item.getType() == Material.STICK && item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Refill Station Stick")) {

            if (clickedBlock != null) {
                Block aboveBlock = clickedBlock.getRelative(BlockFace.UP);
                // Check if space is empty for the refill station
                if (canBuildRefillStation(aboveBlock)) {
                    buildRefillStation(aboveBlock);
                    player.sendMessage(ChatColor.GREEN + "Refill station created!");
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough space to create the refill station!");
                }
            }
            event.setCancelled(true);
        }
    }

    private boolean canBuildRefillStation(Block baseBlock) {
        // Check if there's enough space to build the station
        Block checkBlock = baseBlock.getRelative(BlockFace.UP);
        return checkBlock.getType() == Material.AIR && checkBlock.getRelative(BlockFace.UP).getType() == Material.AIR;
    }

    private void buildRefillStation(Block baseBlock) {
        // Place the base stone brick
        baseBlock.setType(Material.STONE_BRICKS);

        // Place the green wool on top
        Block woolBlock = baseBlock.getRelative(BlockFace.UP);
        woolBlock.setType(Material.GREEN_WOOL);

        // Place the stone brick on top of the green wool
        Block topBlock = woolBlock.getRelative(BlockFace.UP);
        topBlock.setType(Material.STONE_BRICKS);


        // Place signs around the green wool
        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            Block signBlock = woolBlock.getRelative(face);
            signBlock.setType(Material.OAK_WALL_SIGN);
            org.bukkit.block.data.type.WallSign signData = (org.bukkit.block.data.type.WallSign) signBlock.getBlockData();
            signData.setFacing(face);
            signBlock.setBlockData(signData);
            Sign sign = (Sign) signBlock.getState();
            sign.setLine(1, "Refill");
            sign.update();
        }
    }
}
