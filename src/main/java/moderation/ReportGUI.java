package moderation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class ReportGUI {

    private final Player reporter;
    private final Player target;

    public ReportGUI(Player reporter, Player target) {
        this.reporter = reporter;
        this.target = target;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 27, "Report " + target.getName());

        // Add report reasons
        gui.setItem(10, createGuiItem(Material.DIAMOND_SWORD, "Cheating/Hacking", "Using unfair advantages."));
        gui.setItem(12, createGuiItem(Material.PAPER, "Spamming", "Sending repetitive messages."));
        gui.setItem(14, createGuiItem(Material.IRON_SWORD, "Harassment", "Insulting or threatening others."));
        gui.setItem(16, createGuiItem(Material.BOOK, "Other", "For reasons not listed here."));

        // Add confirm and cancel buttons
        gui.setItem(22, createGuiItem(Material.GREEN_WOOL, ChatColor.GREEN + "Confirm Report"));
        gui.setItem(26, createGuiItem(Material.RED_WOOL, ChatColor.RED + "Cancel"));

        reporter.openInventory(gui);
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
