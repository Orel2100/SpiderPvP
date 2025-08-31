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
        if (!title.equals("Class Selector") && !title.equals("Normal Classes") && !title.equals("Hero Classes")) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR || !clickedItem.hasItemMeta()) {
            return;
        }

        if (title.equals("Class Selector")) {
            handleClassSelector(player, clickedItem);
        } else {
            handleClassList(player, clickedItem, title);
        }
    }

    private void handleClassSelector(Player player, ItemStack clickedItem) {
        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.contains("Normal Classes")) {
            classSelectorGUI.openClassesList(player, ClassSelectorGUI.ClassType.NORMAL);
        } else if (displayName.contains("Hero Classes")) {
            classSelectorGUI.openClassesList(player, ClassSelectorGUI.ClassType.HERO);
        }
    }

    private void handleClassList(Player player, ItemStack clickedItem, String title) {
        String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (title.equals("Hero Classes")) {
            if (!plugin.getPremiumKitManager().doesPlayerOwnKit(player, kitName)) {
                player.sendMessage(ChatColor.RED + "You do not own this kit! Purchase it from the shop.");
                player.closeInventory();
                return;
            }
        }

        plugin.getGlobalKitManager().setKit(player, kitName);
        player.sendMessage(ChatColor.GREEN + "You have selected the " + ChatColor.YELLOW + kitName + ChatColor.GREEN + " kit as your global kit!");
        player.closeInventory();
    }
}
