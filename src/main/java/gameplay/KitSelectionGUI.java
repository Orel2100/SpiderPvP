package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import kitpvp.kitpvp.Main;
import java.util.Map;

public class KitSelectionGUI {

    private final Main plugin;
    private final Player player;

    public KitSelectionGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "Select a Kit");

        Map<String, ItemStack> kits = plugin.getKitManager().getKits();
        for (Map.Entry<String, ItemStack> entry : kits.entrySet()) {
            gui.addItem(entry.getValue());
        }

        player.openInventory(gui);
    }
}
