package abilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlazeAbility implements Listener {

    private final JavaPlugin plugin;
    private final long COOLDOWN = 30 * 1000; // 30 seconds in milliseconds
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private final Map<Block, Material> destroyedBlocks = new HashMap<>();

    public BlazeAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.BLAZE_ROD && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Blaze Rampage (Right Click)")) {
            if (!cooldowns.containsKey(player) || System.currentTimeMillis() - cooldowns.get(player) > COOLDOWN) {
                spawnBlazes(player);
            } else {
                long timeLeft = (cooldowns.get(player) + COOLDOWN - System.currentTimeMillis()) / 1000;
                player.sendMessage(ChatColor.RED + "Ability is on cooldown! " + timeLeft + " seconds remaining.");
            }
        }
    }

    private void spawnBlazes(Player player) {
        double yaw = Math.toRadians(player.getLocation().getYaw() + 90); // Convert yaw to radians and adjust by 90 degrees
        double x = Math.cos(yaw);
        double z = Math.sin(yaw);

        Location rightSide = player.getLocation().add(x * 3, 3, z * 3); // 3 blocks above and 3 blocks to the right
        Location leftSide = player.getLocation().subtract(x * 3, -3, z * 3); // 3 blocks above and 3 blocks to the left

        Blaze blaze1 = (Blaze) player.getWorld().spawnEntity(rightSide, EntityType.BLAZE);

        List<Blaze> blazes = new ArrayList<>();
        blazes.add(blaze1);

        cooldowns.put(player, System.currentTimeMillis()); // Start cooldown immediately after blazes spawn
        player.sendMessage(ChatColor.RED + "Blaze Rampage is now on cooldown!");

        // Make blazes follow the player and check for target constantly
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!blaze1.isValid() || !player.isOnline() || player.isDead()) {
                    for (Blaze blaze : blazes) {
                        blaze.remove();
                    }
                    this.cancel();
                    return;
                }
                double newYaw = Math.toRadians(player.getLocation().getYaw() + 90);
                double newX = Math.cos(newYaw);
                double newZ = Math.sin(newYaw);

                Location newRightSide = player.getLocation().add(newX * 3, 3, newZ * 3);
                Location newLeftSide = player.getLocation().subtract(newX * 3, -3, newZ * 3);

                blaze1.teleport(newRightSide);
                blaze1.setCustomName(ChatColor.GREEN + player.getName() + "'s" + "Blaze");


                // Ensure blazes don't target the player who summoned them or each other
                for (Blaze blaze : blazes) {
                    if (blaze.getTarget() == player || blaze.getTarget() == blaze1 ) {
                        blaze.setTarget(null);
                    }
                    // Find the nearest target (excluding the player who summoned the blazes and other blazes)
                    Entity target = blaze.getNearbyEntities(20, 20, 20).stream()
                            .filter(e -> (e instanceof Player || e instanceof Monster) && e != player && e != blaze1 )
                            .findFirst().orElse(null);

                    if (target != null) {
                        blaze.setTarget((LivingEntity) target);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10); // Update every half-second

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Blaze blaze : blazes) {
                    blaze.remove();
                }
            }
        }.runTaskLater(plugin, 20 * 20); // Despawn after 20 seconds
    }





    @EventHandler
    public void onFireballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();
            if (fireball.getShooter() instanceof Blaze) {
                Location loc = fireball.getLocation();
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            Block block = loc.clone().add(x, y, z).getBlock();
                            if (block.getType() != Material.AIR) {
                                destroyedBlocks.put(block, block.getType());
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
                restoreBlocks(); // Call the restoreBlocks method here to ensure blocks are restored after 7 seconds
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (destroyedBlocks.containsKey(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (destroyedBlocks.containsKey(block)) {
                event.setCancelled(true);
            }
        }
    }



    public void restoreBlocks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Block, Material> entry : destroyedBlocks.entrySet()) {
                    entry.getKey().setType(entry.getValue());
                }
                destroyedBlocks.clear();
            }
        }.runTaskLater(plugin, 7 * 20); // Restore after 7 seconds
    }
}
