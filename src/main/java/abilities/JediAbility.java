package abilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.UUID;

public class JediAbility implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 10 * 1000; // 10 seconds in milliseconds

    private final ItemStack JEDI_PUSH_ITEM = new ItemStack(Material.END_PORTAL_FRAME);


    @EventHandler
    public void onJediPushAbilityUse(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // Check if the player is starting to sneak and has the Jedi Push item in their inventory
        if (event.isSneaking() && player.getInventory().contains(JEDI_PUSH_ITEM)) {
            long remainingCooldown = checkCooldown(player);
            if (remainingCooldown == 0L) {
                player.sendMessage(ChatColor.GOLD + "You used the " + ChatColor.AQUA + "Jedi Push" + ChatColor.GOLD + " Ability!");

                for (Entity nearby : player.getNearbyEntities(7, 7, 7)) {
                    if (nearby instanceof Player) {
                        knockback(player, (Player) nearby);
                        ((Player) nearby).playSound(nearby.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1F, 0.4F);
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1F, 0.4F);
                setCooldown(player);
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Jedi Push ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
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

    private void knockback(Player player, Player target) {
        Location direction = target.getLocation().subtract(player.getLocation());
        double distance = target.getLocation().distance(player.getLocation());
        Vector velocity = direction.toVector().normalize().multiply(2 / distance).setY(1);
        target.setVelocity(velocity);
    }
}
