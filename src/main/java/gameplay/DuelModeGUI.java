package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DuelModeGUI {

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Select Duel Mode");
        gui.setItem(11, createGuiItem(Material.DIAMOND_SWORD, "Join Random Queue"));
        gui.setItem(15, createGuiItem(Material.COMPASS, "View Arenas"));
        player.openInventory(gui);
    }

    private ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
