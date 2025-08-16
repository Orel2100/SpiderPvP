package moderation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class DurationGUI {

    private final Player staff;
    private final Player target;
    private final String punishmentType;

    public DurationGUI(Player staff, Player target, String punishmentType) {
        this.staff = staff;
        this.target = target;
        this.punishmentType = punishmentType;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 27, punishmentType + " - " + target.getName() + " - Duration");

        // Add duration options
        gui.setItem(10, createGuiItem(Material.CLOCK, "1 Hour"));
        gui.setItem(11, createGuiItem(Material.CLOCK, "1 Day"));
        gui.setItem(12, createGuiItem(Material.CLOCK, "7 Days"));
        gui.setItem(13, createGuiItem(Material.CLOCK, "30 Days"));
        gui.setItem(15, createGuiItem(Material.BARRIER, "Permanent"));

        staff.openInventory(gui);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
