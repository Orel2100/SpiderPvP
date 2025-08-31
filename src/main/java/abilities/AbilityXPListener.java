package abilities;

import kitpvp.kitpvp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AbilityXPListener implements Listener {

    private final Main plugin;
    private final AbilityXPManager abilityXPManager;

    public AbilityXPListener(Main plugin) {
        this.plugin = plugin;
        this.abilityXPManager = plugin.getAbilityXPManager();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        String kit = plugin.getGlobalKitManager().getKit(damager);

        // Only grant XP if the ability is not already ready
        if (abilityXPManager.isReady(damager)) {
            return;
        }

        if (kit.equalsIgnoreCase("archer")) {
            // Grant 5 XP per hit for Archer, requires 20 hits for a full charge
            abilityXPManager.addXP(damager, 5);
        }
        // TODO: Add cases for other kits here in the future
    }
}
