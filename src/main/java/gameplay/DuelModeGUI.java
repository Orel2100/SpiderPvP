package gameplay;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.guis.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.guis.PlayerSelectionGUI;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DuelModeGUI extends ChestGui {

    private final Player player;

    public DuelModeGUI(Player player) {
        super(3, "Select Duel Mode");
        this.player = player;
        initialize();
    }

    private void initialize() {
        StaticPane pane = new StaticPane(0, 0, 9, 3);
        pane.setOnClick(event -> event.setCancelled(true));

        // Challenge Player Item
        ItemStack challengeItem = new ItemStack(Material.PLAYER_HEAD);
        challengeItem.getItemMeta().setDisplayName(ChatColor.AQUA + "Challenge a Player");
        pane.addItem(new GuiItem(challengeItem, event -> {
            new PlayerSelectionGUI(player).show(player);
        }), 3, 1);

        // Random Matchmaking Item
        ItemStack queueItem = new ItemStack(Material.COMPASS);
        queueItem.getItemMeta().setDisplayName(ChatColor.GOLD + "Random Matchmaking");
        pane.addItem(new GuiItem(queueItem, event -> {
            new DuelQueueGUI(player).show(player);
        }), 5, 1);

        addPane(pane);
    }
}
