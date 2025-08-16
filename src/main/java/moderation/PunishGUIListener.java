package moderation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;

public class PunishGUIListener implements Listener {

    private final Main plugin;

    public PunishGUIListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Punish ")) {
            event.setCancelled(true);
            Player staff = (Player) event.getWhoClicked();
            if(event.getCurrentItem() == null) return;
            if(!event.getCurrentItem().hasItemMeta()) return;
            String punishmentType = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            if (!staff.hasPermission("kitpvp.punish." + punishmentType.toLowerCase())) {
                staff.sendMessage(ChatColor.RED + "You don't have permission to " + punishmentType.toLowerCase() + " players.");
                return;
            }

            staff.sendMessage(ChatColor.GREEN + "You selected to " + punishmentType + " " + event.getView().getTitle().substring(7));

            // For now, just close the inventory.
            // I will implement the reason/duration GUI next.
            staff.closeInventory();
        }
    }
}
