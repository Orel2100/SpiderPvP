package abilities;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class BerserkerAbility implements Listener {
    private final long BERSERKER_COOLDOWN_TIME = 10000L; // 10 seconds
    private final Map<String, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        long remainingCooldown = checkCooldown(player, "_berserker");
        if (hasBerserkerKit(player) && holdingKitItem(player, Material.DIAMOND_AXE, ChatColor.GREEN + "Berserker Axe"))
            if (remainingCooldown == 0L) {
                Vector direction = event.getRightClicked().getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                event.getRightClicked().setVelocity(direction.multiply(4));
                setCooldown(player, "_berserker");
                player.sendMessage(ChatColor.GREEN + "You've used your ability");
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Berserker ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
    }

    private long checkCooldown(Player player, String suffix) {
        String key = player.getUniqueId().toString() + suffix;
        if (!this.cooldowns.containsKey(key))
            return 0L;
        long timeSinceLastUse = System.currentTimeMillis() - this.cooldowns.get(key);
        long remainingTime = BERSERKER_COOLDOWN_TIME - timeSinceLastUse;
        return (remainingTime > 0L) ? remainingTime : 0L;
    }

    private void setCooldown(Player player, String suffix) {
        String key = player.getUniqueId().toString() + suffix;
        this.cooldowns.put(key, System.currentTimeMillis());
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
