package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClassSelectorGUIListener implements Listener {

    private final Main plugin;
    private final ClassSelectorGUI classSelectorGUI;

    public ClassSelectorGUIListener(Main plugin, ClassSelectorGUI classSelectorGUI) {
        this.plugin = plugin;
        this.classSelectorGUI = classSelectorGUI;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (title.equals("Class Selector")) {
            handleClassSelector(player, clickedItem, event);
        } else if (title.equals("Normal Classes") || title.equals("Hero Classes")) {
            handleClassList(player, clickedItem, event);
        }
    }

    private void handleClassSelector(Player player, ItemStack clickedItem, InventoryClickEvent event) {
        event.setCancelled(true);
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (displayName.contains("Normal Classes")) {
            classSelectorGUI.openClassesList(player, ClassSelectorGUI.ClassType.NORMAL);
        } else if (displayName.contains("Hero Classes")) {
            classSelectorGUI.openClassesList(player, ClassSelectorGUI.ClassType.HERO);
        }
    }

    private void handleClassList(Player player, ItemStack clickedItem, InventoryClickEvent event) {
        event.setCancelled(true);
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        // A simple way to get the kit name is from the display name of the item.
        String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        // For Hero kits, we might need to check if the player owns it first.
        // This logic is simplified for now.
        plugin.getGlobalKitManager().setKit(player, kitName);
        player.sendMessage(ChatColor.GREEN + "You have selected the " + kitName + " kit as your global kit!");
        player.closeInventory();
    }
}
