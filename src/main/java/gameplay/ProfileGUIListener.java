package gameplay;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProfileGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            // The title of the ProfileGUI is "<PlayerName>'s Stats"
            if (event.getView().getTitle().contains("'s Stats")) {
                event.setCancelled(true);
            }
        }
    }
}
