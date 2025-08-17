package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import kitpvp.kitpvp.Main;

public class GUIUpdater extends BukkitRunnable {

    private final Main plugin;

    public GUIUpdater(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equals("1v1 Arenas")) {
                new DuelQueueGUI(plugin, player).open();
            }
        }
    }
}
