package abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IceMageAbility implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> frostNovaCooldowns;
    private final HashMap<Location, BlockData> originalBlocks;
    private final int radius = 5; // Radius of the ice circle
    private final int duration = 140; // Duration in ticks (7 seconds)

    public IceMageAbility(JavaPlugin plugin) {
        this.plugin = plugin;
        this.frostNovaCooldowns = new HashMap<>();
        this.originalBlocks = new HashMap<>();
        // Register the class as an event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && hasFrostNovaItem(player) && isCooldownOver(player)) {
            createIceCircle(player.getLocation());
            setCooldown(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Block block = event.getTo().getBlock();
        if (block.getType() == Material.ICE || block.getType() == Material.FROSTED_ICE) {
            Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 2, 1)); // 2 seconds of slowness
        }
    }

    private boolean hasFrostNovaItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        return mainHandItem != null && mainHandItem.getType() == Material.BLUE_ICE && mainHandItem.hasItemMeta() &&
                mainHandItem.getItemMeta().hasDisplayName() && mainHandItem.getItemMeta().getDisplayName().contains("Frost Nova");
    }

    private boolean isCooldownOver(Player player) {
        return !frostNovaCooldowns.containsKey(player.getUniqueId()) ||
                System.currentTimeMillis() - frostNovaCooldowns.get(player.getUniqueId()) >= 10000; // 10 second cooldown
    }

    private void setCooldown(Player player) {
        frostNovaCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(ChatColor.AQUA + "Frost Nova is now on cooldown.");
    }

    private void createIceCircle(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        // Temporary change blocks to ice in a circle
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) { // Inside the circle
                    Location loc = new Location(world, centerX + x, centerY - 1, centerZ + z); // -1 to affect the ground level
                    Block block = loc.getBlock();
                    if (block.getType().isSolid()) { // Check that we're not replacing non-solid blocks
                        originalBlocks.put(loc, block.getBlockData()); // Save the original block data
                        block.setType(Material.ICE, false); // Set to ice without applying physics
                    }
                }
            }
        }

        // Schedule the ice to revert back to original blocks after the duration
        new BukkitRunnable() {
            @Override
            public void run() {
                revertIceCircle();
            }
        }.runTaskLater(plugin, duration);
    }

    private void revertIceCircle() {
        for (Map.Entry<Location, BlockData> entry : originalBlocks.entrySet()) {
            entry.getKey().getBlock().setBlockData(entry.getValue(), false); // Revert to original block data without physics
        }
        originalBlocks.clear(); // Clear the map to prevent memory leaks
    }
}
