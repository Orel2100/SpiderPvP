package abilities;

import kitpvp.kitpvp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AbilityListener implements Listener {

    private final Main plugin;
    private final AbilityManager abilityManager;

    public AbilityListener(Main plugin) {
        this.plugin = plugin;
        this.abilityManager = plugin.getAbilityManager();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        String kit = plugin.getGlobalKitManager().getKit(damager);

        // We use the player's level as their ability charge. If it's 100, it's full.
        if (damager.getLevel() >= 100) {
            return;
        }

        if (kit.equalsIgnoreCase("archer")) {
            // Grant 5 XP per hit for Archer, requires 20 hits for a full charge
            abilityManager.addXP(damager, 5);
        }
        // TODO: Add cases for other kits here in the future
    }
}
