package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import kitpvp.kitpvp.Main;
import java.util.Map;
import org.bukkit.Material;

public class KitSelectionGUI {

    private final Main plugin;
    private final Player player;

    public KitSelectionGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "Select a Kit");

        // Add regular kits
        Map<String, ItemStack> regularKits = plugin.getKitManager().getKits();
        for (Map.Entry<String, ItemStack> entry : regularKits.entrySet()) {
            gui.addItem(entry.getValue());
        }

        // Add separator
        for (int i = 27; i < 36; i++) {
            gui.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Add premium kits
        Map<String, ItemStack> premiumKits = plugin.getPremiumKitManager().getPremiumKits(player);
        for (Map.Entry<String, ItemStack> entry : premiumKits.entrySet()) {
            gui.addItem(entry.getValue());
        }

        player.openInventory(gui);
    }
}
