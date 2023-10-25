package abilities;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.UUID;

public class AeroAbility implements Listener {

    private final String AERO_FEATHER_NAME = "Aero Feather";
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 10 * 1000; // 10 seconds in milliseconds

    @EventHandler
    public void onAeroAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player is holding the Aero Feather
        if (itemInHand.getType() == Material.FEATHER && itemInHand.hasItemMeta() && AERO_FEATHER_NAME.equals(itemInHand.getItemMeta().getDisplayName())) {
            long remainingCooldown = checkCooldown(player);
            if (remainingCooldown == 0L) {
                // Dash the player forward
                Vector direction = player.getLocation().getDirection().normalize();
                player.setVelocity(direction.multiply(2.5)); // Adjust the multiplier for longer/shorter dash

                // Particle effect
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

                setCooldown(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your Aero Dash ability!"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Aero Dash ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }

    private long checkCooldown(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeLeft = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            if (timeLeft > 0) {
                return timeLeft;
            }
        }
        return 0L;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_TIME);
    }
}
