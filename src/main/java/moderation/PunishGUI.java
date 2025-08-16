package moderation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class PunishGUI {

    private final Player staff;
    private final Player target;

    public PunishGUI(Player staff, Player target) {
        this.staff = staff;
        this.target = target;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 27, "Punish " + target.getName());

        // Add punishment types
        gui.setItem(10, createGuiItem(Material.PAPER, "Warn"));
        gui.setItem(12, createGuiItem(Material.IRON_SWORD, "Kick"));
        gui.setItem(14, createGuiItem(Material.BOOK, "Mute"));
        gui.setItem(16, createGuiItem(Material.BEDROCK, "Ban"));

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
