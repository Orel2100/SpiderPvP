package abilities;

import kitpvp.kitpvp.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class WitherAbility implements Listener {
    private final long WITHER_COOLDOWN_TIME = 15000L; // 15 seconds
    private final double MAX_TARGET_RANGE = 50.0; // Max range to acquire a target
    private final double HOMING_RANGE = 50.0; // Max homing range for the skulls
    private final Map<String, Long> cooldowns = new HashMap<>();

    private final Main plugin;

    public WitherAbility(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.BLAZE_ROD && itemInHand.getItemMeta().getDisplayName().equals("Homing Skull (Right Click)")) {
            long remainingCooldown = checkCooldown(player, "_wither");
            if (remainingCooldown == 0L) {
                Player target = getClosestTarget(player, MAX_TARGET_RANGE);
                if (target != null) {
                    launchWitherSkulls(player, target);
                    setCooldown(player, "_wither");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "You've used your Wither ability!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "No target found!"));
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Your Wither ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }

    private void launchWitherSkulls(Player player, Player target) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 3 || player.getLocation().distance(target.getLocation()) > HOMING_RANGE) {
                    this.cancel();
                    return;
                }
                WitherSkull skull = player.launchProjectile(WitherSkull.class);
                skull.setVelocity(player.getLocation().getDirection().multiply(2));
                skull.setIsIncendiary(false);
                skull.setYield(1.5F);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (skull.isDead() || target.isDead() || skull.getLocation().distance(target.getLocation()) > HOMING_RANGE) {
                            this.cancel();
                            return;
                        }
                        Vector direction = target.getLocation().add(0, 1, 0).toVector().subtract(skull.getLocation().toVector()).normalize().multiply(0.5);
                        skull.setVelocity(direction);
                    }
                }.runTaskTimer(plugin, 0L, 1L);

                count++;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 second
    }

    private Player getClosestTarget(Player player, double maxDistance) {
        Player closestTarget = null;
        double closestDistance = maxDistance;
        Vector playerDirection = player.getLocation().getDirection();

        for (Entity entity : player.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
            if (entity instanceof Player && entity != player) {
                Location targetLocation = entity.getLocation();
                Vector toTarget = targetLocation.subtract(player.getLocation()).toVector();

                // Check if the target is within the player's field of view
                if (toTarget.normalize().dot(playerDirection) > 0.5) {
                    double distance = toTarget.length();
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestTarget = (Player) entity;
                    }
                }
            }
        }
        return closestTarget;
    }

    private long checkCooldown(Player player, String suffix) {
        String key = player.getUniqueId().toString() + suffix;
        if (!this.cooldowns.containsKey(key))
            return 0L;
        long timeSinceLastUse = System.currentTimeMillis() - this.cooldowns.get(key);
        long remainingTime = WITHER_COOLDOWN_TIME - timeSinceLastUse;
        return (remainingTime > 0L) ? remainingTime : 0L;
    }

    private void setCooldown(Player player, String suffix) {
        String key = player.getUniqueId().toString() + suffix;
        this.cooldowns.put(key, System.currentTimeMillis());
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            event.setFire(false); // Ensure it doesn't start fires
            event.setRadius(0);   // Set the explosion radius to 0 to prevent block damage
        }
    }
}
