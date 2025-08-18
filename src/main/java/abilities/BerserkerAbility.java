package abilities;

import kitpvp.kitpvp.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import cooldown.CooldownManager;
import kitpvp.kitpvp.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BerserkerAbility implements Listener {
    private final Main plugin;

    public BerserkerAbility(Main plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        if (hasBerserkerKit(player) && holdingKitItem(player, Material.DIAMOND_AXE, ChatColor.GREEN + "Berserker Axe (Right Click)"))
            if (!cooldownManager.hasCooldown(player, "Berserker")) {
                Vector direction = event.getRightClicked().getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                event.getRightClicked().setVelocity(direction.multiply(4));
                spawnWall(event.getRightClicked().getLocation(), direction);
                cooldownManager.setCooldown(player, "Berserker", 10);
                player.sendMessage(ChatColor.GREEN + "You've used your ability");
            }
    }

    private void spawnWall(Location location, Vector direction) {
        World world = location.getWorld();
        Location wallLocation = location.add(direction.multiply(2)); // 2 blocks in front of the entity

        for (int y = 0; y < 3; y++) { // 3 blocks high
            for (int x = -1; x <= 1; x++) { // 3 blocks wide
                Block block = world.getBlockAt(wallLocation.clone().add(x, y, 0));
                block.setType(Material.STONE);
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (int y = 0; y < 3; y++) {
                for (int x = -1; x <= 1; x++) {
                    Block block = world.getBlockAt(wallLocation.clone().add(x, y, 0));
                    block.setType(Material.AIR);
                }
            }
        }, 100L); // Wall disappears after 5 seconds (100 ticks)
    }




    private boolean holdingKitItem(Player player, Material material, String displayName) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == material && itemInHand.hasItemMeta()) {
            String itemName = itemInHand.getItemMeta().getDisplayName();
            return (itemName != null && itemName.equals(displayName));
        }
        return false;
    }

    private boolean hasBerserkerKit(Player player) {
        return player.getInventory().contains(Material.DIAMOND_AXE);
    }
}
