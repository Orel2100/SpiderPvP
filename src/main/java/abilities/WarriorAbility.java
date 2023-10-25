package abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;

public class WarriorAbility implements Listener {

    private final JavaPlugin plugin;
    private final long COOLDOWN_TIME = 15000L; // 60 seconds cooldown for demonstration
    private HashMap<Player, Long> cooldownStart = new HashMap<>();

    public WarriorAbility(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public long checkCooldown(Player player, String ability) {
        if (!cooldownStart.containsKey(player)) return 0L;
        long elapsedTime = System.currentTimeMillis() - cooldownStart.get(player);
        if (elapsedTime >= COOLDOWN_TIME) {
            cooldownStart.remove(player);
            return 0L;
        }
        return COOLDOWN_TIME - elapsedTime;
    }


    public void setCooldown(Player player, String ability) {
        cooldownStart.put(player, System.currentTimeMillis());
        player.setLevel((int) (COOLDOWN_TIME / 1000L)); // Set the XP level to the cooldown time in seconds

        // Create a task that runs every second to update the XP level
        new BukkitRunnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - cooldownStart.get(player);
                long remainingCooldown = COOLDOWN_TIME - elapsedTime;
                if (remainingCooldown <= 0) {
                    player.setLevel(0); // Reset the XP level
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Your ability is ready!")); // Notify the player
                    cooldownStart.remove(player);
                    this.cancel(); // Cancel the task
                } else {
                    player.setLevel((int) (remainingCooldown / 1000L)); // Update the XP level to the remaining cooldown time in seconds
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second (20 ticks = 1 second)

    }

    public boolean hasWarriorKit(Player player) {
        // Logic to check if the player has the Warrior kit
        return true; // Placeholder
    }

    public boolean holdingKitItem(Player player, Material material, String displayName) {
        // Logic to check if the player is holding the specified kit item
        return player.getInventory().getItemInMainHand().getType() == material &&
                player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(displayName);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        long remainingCooldown = checkCooldown(player, "_warrior");
        if (hasWarriorKit(player) && holdingKitItem(player, Material.IRON_SWORD, ChatColor.GREEN + "Warrior Sword")) {
            if (hasWarriorKit(player) && holdingKitItem(player, Material.IRON_SWORD, ChatColor.GREEN + "Warrior Sword"))
                if (remainingCooldown == 0L) {
                    // Create the visual and sound effects of a lightning strike without causing fire
                    player.getWorld().strikeLightningEffect(event.getEntity().getLocation());

                    // Deal damage to the player being hit
                    if (event.getEntity() instanceof Player) {
                        Player target = (Player) event.getEntity();
                        target.damage(5.0); // You can adjust the damage value as needed
                    }
                setCooldown(player, "_warrior");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your Warrior ability!"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Warrior ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }
}
