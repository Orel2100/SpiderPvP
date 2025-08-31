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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClassSelectorGUI {

    public enum ClassType {
        NORMAL,
        HERO
    }

    private final Main plugin;
    // Pre-defined slots for a nicer layout
    private static final List<Integer> KIT_SLOTS = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25);

    public ClassSelectorGUI(Main plugin) {
        this.plugin = plugin;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void openClassSelector(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Class Selector");

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, pane);
        }

        ItemStack normalClasses = new ItemStack(Material.IRON_SWORD);
        ItemMeta normalMeta = normalClasses.getItemMeta();
        normalMeta.setDisplayName(ChatColor.GREEN + "Normal Classes");
        normalMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to see the Normal Classes."));
        normalClasses.setItemMeta(normalMeta);
        gui.setItem(11, normalClasses);

        ItemStack heroClasses = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta heroMeta = heroClasses.getItemMeta();
        heroMeta.setDisplayName(ChatColor.AQUA + "Hero Classes");
        heroMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to see the Hero (Premium) Classes."));
        heroClasses.setItemMeta(heroMeta);
        gui.setItem(15, heroClasses);

        String currentKit = plugin.getGlobalKitManager().getKit(player);
        ItemStack currentKitItem = new ItemStack(Material.PAPER);
        ItemMeta currentKitMeta = currentKitItem.getItemMeta();
        currentKitMeta.setDisplayName(ChatColor.YELLOW + "Current Class: " + ChatColor.GOLD + capitalize(currentKit));
        currentKitItem.setItemMeta(currentKitMeta);
        gui.setItem(13, currentKitItem);

        player.openInventory(gui);
    }

    public void openClassesList(Player player, ClassType type) {
        String title = (type == ClassType.NORMAL) ? "Normal Classes" : "Hero Classes";
        Inventory gui = Bukkit.createInventory(null, 45, title); // 5 rows for better spacing

        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, pane);
        }

        Map<String, ItemStack> kits;
        if (type == ClassType.NORMAL) {
            kits = plugin.getKitManager().getKits();
        } else {
            kits = plugin.getPremiumKitManager().getPremiumKits(player);
        }

        int slotIndex = 0;
        for (Map.Entry<String, ItemStack> entry : kits.entrySet()) {
            if (slotIndex >= KIT_SLOTS.size()) break;

            ItemStack kitItem = entry.getValue().clone();
            ItemMeta meta = kitItem.getItemMeta();

            if (meta == null) {
                plugin.getLogger().warning("Could not create display item for kit '" + entry.getKey() + "' because its material is invalid or AIR.");
                continue;
            }

            String kitName = capitalize(entry.getKey());

            List<String> lore = new ArrayList<>();
            if(meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            lore.add("");

            if (type == ClassType.HERO) {
                boolean hasKit = plugin.getPremiumKitManager().doesPlayerOwnKit(player, entry.getKey());
                if (hasKit) {
                    meta.setDisplayName(ChatColor.GREEN + kitName);
                    lore.add(ChatColor.YELLOW + "Click to select this kit!");
                } else {
                    meta.setDisplayName(ChatColor.RED + kitName);
                    lore.add(ChatColor.RED + "LOCKED");
                    lore.add(ChatColor.GRAY + "Purchase at the store!");
                }
            } else {
                meta.setDisplayName(ChatColor.GREEN + kitName);
                lore.add(ChatColor.YELLOW + "Click to select this kit!");
            }

            meta.setLore(lore);
            kitItem.setItemMeta(meta);
            gui.setItem(KIT_SLOTS.get(slotIndex), kitItem);
            slotIndex++;
        }

        player.openInventory(gui);
    }
}
