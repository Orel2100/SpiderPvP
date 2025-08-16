package moderation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class ReportManagementGUI {

    private final Player staff;
    private final Player target;
    private final String reportId;

    public ReportManagementGUI(Player staff, Player target, String reportId) {
        this.staff = staff;
        this.target = target;
        this.reportId = reportId;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 27, "Manage Report - " + reportId + " - " + target.getName());

        gui.setItem(10, createGuiItem(Material.ENDER_PEARL, "Teleport to Player"));
        gui.setItem(12, createGuiItem(Material.GREEN_WOOL, "Resolve Report"));
        gui.setItem(14, createGuiItem(Material.REDSTONE, "Punish Player"));
        gui.setItem(16, createGuiItem(Material.BARRIER, "Close"));

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
