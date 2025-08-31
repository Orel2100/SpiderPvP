package abilities;

import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class XPBarUpdater extends BukkitRunnable {

    private final Main plugin;
    private final AbilityXPManager abilityXPManager;

    public XPBarUpdater(Main plugin) {
        this.plugin = plugin;
        this.abilityXPManager = plugin.getAbilityXPManager();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Only show the bar if a kit is selected
            String kit = plugin.getGlobalKitManager().getKit(player);
            if (kit.equalsIgnoreCase("None")) {
                if (player.getExp() > 0 || player.getLevel() > 0) {
                    player.setExp(0);
                    player.setLevel(0);
                }
                continue;
            }

            int currentXP = abilityXPManager.getXP(player);
            int maxXP = abilityXPManager.getMaxXP();

            if (abilityXPManager.isReady(player)) {
                // Ability is ready
                player.setLevel(100); // Level 100 to show it's full
                player.setExp(1.0f);
                // You could add a flashing effect here if desired
            } else {
                // Ability is charging
                player.setLevel(currentXP);
                float expProgress = (float) currentXP / maxXP;
                player.setExp(expProgress);
            }
        }
    }
}
