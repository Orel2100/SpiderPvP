package gameplay;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;
import gameplay.KitSelectionGUI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

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
            new KitSelectionGUI(plugin, player).open();
        } else if (event.getView().getTitle().equals("Select a Kit")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            plugin.getDuelQueueManager().addPlayer(player, kitName);
            player.closeInventory();
        }
    }
}
