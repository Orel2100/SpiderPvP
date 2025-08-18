package gameplay;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.guis.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DuelQueueGUI extends ChestGui {

    private final DuelQueueManager duelQueueManager;
    private final Player player;

    public DuelQueueGUI(Player player) {
        super(3, "Join the Duel Queue");
        this.player = player;
        this.duelQueueManager = Main.getInstance().getDuelQueueManager();
        initialize();
    }

    private void initialize() {
        StaticPane pane = new StaticPane(0, 0, 9, 3);
        pane.setOnClick(event -> event.setCancelled(true));

        // Join Queue Item
        ItemStack joinItem = new ItemStack(Material.GREEN_WOOL);
        joinItem.getItemMeta().setDisplayName(ChatColor.GREEN + "Join Queue");
        pane.addItem(new GuiItem(joinItem, event -> {
            player.closeInventory();
            duelQueueManager.addPlayerToQueue(player);
        }), 3, 1);

        // Leave Queue Item
        ItemStack leaveItem = new ItemStack(Material.RED_WOOL);
        leaveItem.getItemMeta().setDisplayName(ChatColor.RED + "Leave Queue");
        pane.addItem(new GuiItem(leaveItem, event -> {
            player.closeInventory();
            duelQueueManager.removePlayerFromQueue(player);
        }), 5, 1);

        addPane(pane);
    }
}
