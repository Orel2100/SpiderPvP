package globalkit;

import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KitGUIListener implements Listener {

    private final Main plugin;
    private final GlobalKitManager globalKitManager;

    public KitGUIListener(Main plugin) {
        this.plugin = plugin;
        this.globalKitManager = plugin.getGlobalKitManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Select a Global Kit")) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getItemMeta() == null || clickedItem.getItemMeta().getLore() == null) {
            return;
        }

        String kitName = null;
        for (String lore : clickedItem.getItemMeta().getLore()) {
            String strippedLore = ChatColor.stripColor(lore);
            if (strippedLore.startsWith("kit:")) {
                kitName = strippedLore.split(":")[1];
                break;
            }
        }

        if (kitName == null) {
            return;
        }

        // Check for premium kit ownership
        if (plugin.getPremiumKitManager().getPremiumKits(player).containsKey(kitName)) {
            if (!plugin.getPremiumKitManager().doesPlayerOwnKit(player, kitName)) {
                player.sendMessage(ChatColor.RED + "You do not own this kit!");
                return;
            }
        }

        globalKitManager.setKit(player, kitName);
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "You have selected the " + ChatColor.GOLD + kitName + ChatColor.GREEN + " kit as your global kit!");
    }
}
