package abilities;

import kitpvp.kitpvp.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
    private final Map<String, Long> cooldowns = new HashMap<>();

    private final Main plugin;

    public WitherAbility(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWitherAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.BLAZE_ROD) {
            long remainingCooldown = checkCooldown(player, "_wither");
            if (remainingCooldown == 0L) {
                Player target = getClosestTarget(player, 50); // 50 is the max distance to search for a player
                if (target != null) {
                    new BukkitRunnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count >= 3) {
                                this.cancel();
                                return;
                            }
                            WitherSkull skull = player.launchProjectile(WitherSkull.class);
                            skull.setVelocity(player.getLocation().getDirection().multiply(2));
                            skull.setIsIncendiary(false);
                            skull.setYield(1.5F);
                            skull.getWorld().createExplosion(skull.getLocation(), 2.0F, false, false);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (skull.isDead() || target.isDead()) {
                                        this.cancel();
                                        return;
                                    }
                                    Vector direction = target.getLocation().add(0, 1, 0).toVector().subtract(skull.getLocation().toVector()).normalize().multiply(0.5);
                                    skull.setVelocity(direction);
                                }
                            }.runTaskTimer(plugin, 0L, 1L); // adjust as needed

                            count++;
                        }
                    }.runTaskTimer(plugin, 0L, 10L); // 20 ticks = 1 second

                    setCooldown(player, "_wither");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your Wither ability!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "No target found!"));
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Wither ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }

    private Player getClosestTarget(Player player, double maxDistance) {
        Player closestTarget = null;
        double closestDistance = maxDistance;
        for (Entity entity : player.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
            if (entity instanceof Player && entity != player) {
                double distance = entity.getLocation().distance(player.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestTarget = (Player) entity;
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
