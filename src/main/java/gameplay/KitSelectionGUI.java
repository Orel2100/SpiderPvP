package gameplay;

import KitsManager.KitManager;
import KitsManager.PremiumKitManager;
import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitSelectionGUI {

    private final Main plugin;
    private final Player player;
    private final KitManager kitManager;
    private final PremiumKitManager premiumKitManager;

    public KitSelectionGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.kitManager = plugin.getKitManager();
        this.premiumKitManager = plugin.getPremiumKitManager();
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "Select a Kit");

        // Add regular kits
        Map<String, ItemStack> regularKits = kitManager.getKits();
        int slot = 0;
        for (Map.Entry<String, ItemStack> entry : regularKits.entrySet()) {
            ItemStack kitItem = entry.getValue().clone();
            ItemMeta meta = kitItem.getItemMeta();
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GREEN + "Click to select this kit!");
            // Add hidden kit name for the listener
            lore.add(ChatColor.BLACK + "kit:" + entry.getKey());
            meta.setLore(lore);
            kitItem.setItemMeta(meta);
            gui.setItem(slot++, kitItem);
        }

        // Add separator
        ItemStack separator = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta sepMeta = separator.getItemMeta();
        sepMeta.setDisplayName(" ");
        separator.setItemMeta(sepMeta);
        for (int i = 36; i < 45; i++) {
            gui.setItem(i, separator);
        }

        // Add premium kits
        Map<String, ItemStack> premiumKits = premiumKitManager.getPremiumKits(player);
        slot = 45;
        for (Map.Entry<String, ItemStack> entry : premiumKits.entrySet()) {
            ItemStack kitItem = entry.getValue().clone();
            ItemMeta meta = kitItem.getItemMeta();
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");

            boolean hasKit = premiumKitManager.hasKit(player, entry.getKey());
            if (hasKit) {
                lore.add(ChatColor.GREEN + "Click to select this kit!");
            } else {
                lore.add(ChatColor.RED + "LOCKED");
                lore.add(ChatColor.GRAY + "Purchase at store.example.com");
            }
            // Add hidden kit name for the listener
            lore.add(ChatColor.BLACK + "kit:" + entry.getKey());
            meta.setLore(lore);
            kitItem.setItemMeta(meta);
            gui.setItem(slot++, kitItem);
        }

        player.openInventory(gui);
    }
}
