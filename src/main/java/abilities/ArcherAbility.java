package abilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ArcherAbility implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 10 * 1000; // 10 seconds in milliseconds

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        long remainingCooldown = checkCooldown(player);
        if (hasArcherKit(player) && holdingKitItem(player, Material.BOW, ChatColor.GREEN + "Archer Bow"))
            if (remainingCooldown == 0L) {
                Arrow arrow = (Arrow) event.getProjectile();
                arrow.setCustomName("ExplosiveArrow");
                setCooldown(player);
                hasArcherKit(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your ability"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Archer ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getDamager().getCustomName() != null && event
                .getDamager().getCustomName().equals("ExplosiveArrow")) {
            Arrow arrow = (Arrow) event.getDamager();
            arrow.getWorld().createExplosion(arrow.getLocation(), 2.0F, false, false);
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

    private boolean hasArcherKit(Player player) {
        return player.getInventory().contains(Material.BOW);
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_TIME);
    }



    private boolean holdingKitItem(Player player, Material material, String displayName) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        return itemInHand.getType() == material && itemInHand.hasItemMeta() && displayName.equals(itemInHand.getItemMeta().getDisplayName());
    }
}
