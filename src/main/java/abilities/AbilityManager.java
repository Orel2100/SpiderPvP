package abilities;

import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityManager {

    private final Main plugin;

    public AbilityManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addXP(Player player, int xp) {
        if (player.getLevel() >= 100) {
            return; // Already full
        }

        player.setLevel(player.getLevel() + xp);

        if (player.getLevel() >= 100) {
            player.setLevel(100);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            player.sendTitle(ChatColor.YELLOW + "Ability Ready!", "", 10, 40, 10);
            flashXPBar(player);
        }

        player.setExp((float) player.getLevel() / 100.0f);
    }

    public void resetXP(Player player) {
        player.setLevel(0);
        player.setExp(0f);
    }

    private void flashXPBar(Player player) {
        new BukkitRunnable() {
            int i = 0;
            boolean on = false;
            @Override
            public void run() {
                if (i >= 6) { // Flash 3 times (on/off)
                    if(player.isOnline() && player.getLevel() >= 100) {
                        player.setExp(1.0f);
                    }
                    this.cancel();
                    return;
                }
                if (player.isOnline() && player.getLevel() >= 100) {
                    player.setExp(on ? 1.0f : 0.0f);
                    on = !on;
                } else {
                    this.cancel();
                }
                i++;
            }
        }.runTaskTimer(plugin, 0, 10L);
    }
}
