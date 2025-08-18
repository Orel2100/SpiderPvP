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

import kitpvp.kitpvp.Main;
import cooldown.CooldownManager;

public class AeroAbility implements Listener {

    private final String AERO_FEATHER_NAME = "Aero Feather";
    private final Main plugin;

    public AeroAbility(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAeroAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player is holding the Aero Feather
        if (itemInHand.getType() == Material.FEATHER && itemInHand.hasItemMeta() && AERO_FEATHER_NAME.equals(itemInHand.getItemMeta().getDisplayName())) {
            CooldownManager cooldownManager = plugin.getCooldownManager();
            if (!cooldownManager.hasCooldown(player, "Aero Dash")) {
                // Dash the player forward
                Vector direction = player.getLocation().getDirection().normalize();
                player.setVelocity(direction.multiply(1.5)); // Adjust the multiplier for longer/shorter dash

                // Particle effect
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

                cooldownManager.setCooldown(player, "Aero Dash", 10);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your Aero Dash ability!"));
            }
        }
    }
}
