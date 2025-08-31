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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ArcherAbility implements Listener {

    private final Main plugin;
    private final AbilityManager abilityManager;
    private final Set<UUID> chargedExplosiveShot = new HashSet<>();

    public ArcherAbility(Main plugin) {
        this.plugin = plugin;
        this.abilityManager = plugin.getAbilityManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != Material.BOW) {
            return;
        }

        String kit = plugin.getGlobalKitManager().getKit(player);
        if (!kit.equalsIgnoreCase("archer")) {
            return;
        }

        if (player.getLevel() >= 100) {
            chargedExplosiveShot.add(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Explosive Arrow Charged!"));
        } else {
            int currentXP = player.getLevel();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Ability not ready! " + ChatColor.GOLD + "[" + currentXP + "/100]"));
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (chargedExplosiveShot.contains(player.getUniqueId())) {
            Arrow arrow = (Arrow) event.getProjectile();
            arrow.setCustomName("ExplosiveArrow");

            // Reset XP and remove from charged set
            abilityManager.resetXP(player);
            chargedExplosiveShot.remove(player.getUniqueId());

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "You fired an Explosive Arrow!"));
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getCustomName() != null && arrow.getCustomName().equals("ExplosiveArrow")) {
                arrow.getWorld().createExplosion(arrow.getLocation(), 2.0F, false, false);
                arrow.setCustomName(null);
            }
        }
    }
}
