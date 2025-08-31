package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import kitpvp.kitpvp.KitManager;
import kitpvp.kitpvp.PremiumKitManager;

import java.util.Arrays;
import java.util.Map;

public class ClassSelectorGUI {

    public enum ClassType {
        NORMAL,
        HERO
    }

    private final Main plugin;

    public ClassSelectorGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openClassSelector(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Class Selector");

        // Fill with decorative panes
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, pane);
        }

        // Normal Classes item
        ItemStack normalClasses = new ItemStack(Material.IRON_SWORD);
        ItemMeta normalMeta = normalClasses.getItemMeta();
        normalMeta.setDisplayName(ChatColor.GREEN + "Normal Classes");
        normalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to see the Normal Classes."));
        normalClasses.setItemMeta(normalMeta);
        gui.setItem(11, normalClasses);

        // Hero Classes item
        ItemStack heroClasses = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta heroMeta = heroClasses.getItemMeta();
        heroMeta.setDisplayName(ChatColor.AQUA + "Hero Classes");
        heroMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to see the Hero (Premium) Classes."));
        heroClasses.setItemMeta(heroMeta);
        gui.setItem(15, heroClasses);

        // Current Class display
        String currentKit = plugin.getGlobalKitManager().getKit(player);
        ItemStack currentKitItem = new ItemStack(Material.PAPER);
        ItemMeta currentKitMeta = currentKitItem.getItemMeta();
        currentKitMeta.setDisplayName(ChatColor.YELLOW + "Current Class: " + ChatColor.GOLD + currentKit);
        currentKitItem.setItemMeta(currentKitMeta);
        gui.setItem(13, currentKitItem);

        player.openInventory(gui);
    }

    public void openClassesList(Player player, ClassType type) {
        String title = (type == ClassType.NORMAL) ? "Normal Classes" : "Hero Classes";
        Inventory gui = Bukkit.createInventory(null, 54, title);

        Map<String, ItemStack> kits;
        if (type == ClassType.NORMAL) {
            kits = plugin.getKitManager().getKits();
        } else {
            kits = plugin.getPremiumKitManager().getPremiumKits(player);
        }

        int slot = 0;
        for (Map.Entry<String, ItemStack> entry : kits.entrySet()) {
            ItemStack kitItem = entry.getValue().clone();
            ItemMeta meta = kitItem.getItemMeta();
            meta.setDisplayName(ChatColor.RESET + entry.getKey());
            // You can add more lore here, like kit description or if it's owned (for hero kits)
            kitItem.setItemMeta(meta);
            gui.setItem(slot++, kitItem);
        }

        player.openInventory(gui);
    }
}
