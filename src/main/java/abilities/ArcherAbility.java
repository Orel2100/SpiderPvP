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

import kitpvp.kitpvp.Main;
import cooldown.CooldownManager;

public class ArcherAbility implements Listener {

    private final Main plugin;

    public ArcherAbility(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        if (hasArcherKit(player) && holdingKitItem(player, Material.BOW, ChatColor.GREEN + "Archer Bow"))
            if (!cooldownManager.hasCooldown(player, "Explosive Arrow")) {
                Arrow arrow = (Arrow) event.getProjectile();
                arrow.setCustomName("ExplosiveArrow");
                cooldownManager.setCooldown(player, "Explosive Arrow", 10);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your ability"));
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

    private boolean hasArcherKit(Player player) {
        return player.getInventory().contains(Material.BOW);
    }



    private boolean holdingKitItem(Player player, Material material, String displayName) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        return itemInHand.getType() == material && itemInHand.hasItemMeta() && displayName.equals(itemInHand.getItemMeta().getDisplayName());
    }
}
