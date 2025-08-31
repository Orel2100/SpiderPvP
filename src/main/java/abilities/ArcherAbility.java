package abilities;

import kitpvp.kitpvp.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class ArcherAbility implements Listener {

    private final Main plugin;
    private final AbilityXPManager abilityXPManager;

    public ArcherAbility(Main plugin) {
        this.plugin = plugin;
        this.abilityXPManager = plugin.getAbilityXPManager();
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        String kit = plugin.getGlobalKitManager().getKit(player);

        if (!kit.equalsIgnoreCase("archer")) return;

        if (abilityXPManager.isReady(player)) {
            Arrow arrow = (Arrow) event.getProjectile();
            arrow.setCustomName("ExplosiveArrow");

            // Reset XP after using the ability
            abilityXPManager.setXP(player, 0);

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You used Explosive Arrow!"));
        } else {
            int currentXP = abilityXPManager.getXP(player);
            int maxXP = abilityXPManager.getMaxXP();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Ability not ready! " + ChatColor.GOLD + "[" + currentXP + "/" + maxXP + "]"));
            // We don't cancel the event, so they can still shoot a normal arrow.
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getDamager().getCustomName() != null &&
                event.getDamager().getCustomName().equals("ExplosiveArrow")) {
            Arrow arrow = (Arrow) event.getDamager();
            arrow.getWorld().createExplosion(arrow.getLocation(), 2.0F, false, false);
            // Make sure the custom named arrow doesn't persist
            arrow.setCustomName(null);
        }
    }
}
