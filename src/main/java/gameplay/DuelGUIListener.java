package gameplay;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;

public class DuelGUIListener implements Listener {

    private final Main plugin;

    public DuelGUIListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("1v1 Arenas")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            plugin.getDuelQueueManager().addPlayer(player);
            player.closeInventory();
        }
    }
}
