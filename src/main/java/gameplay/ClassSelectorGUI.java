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

    public ClassSelectorGUI(Main plugin) {
        this.plugin = plugin;
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
        currentKitMeta.setDisplayName(ChatColor.YELLOW + "Current Class: " + ChatColor.GOLD + currentKit);
        currentKitItem.setItemMeta(currentKitMeta);
        gui.setItem(13, currentKitItem);

        player.openInventory(gui);
    }

    public void openClassesList(Player player, ClassType type) {
        String title = (type == ClassType.NORMAL) ? "Normal Classes" : "Hero Classes";
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // GUI Redesign: Add filler panes
        if(type == ClassType.HERO) {
            ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta paneMeta = pane.getItemMeta();
            paneMeta.setDisplayName(" ");
            pane.setItemMeta(paneMeta);
            for (int i = 0; i < gui.getSize(); i++) {
                gui.setItem(i, pane);
            }
        }

        Map<String, ItemStack> kits;
        if (type == ClassType.NORMAL) {
            kits = plugin.getKitManager().getKits();
        } else {
            kits = plugin.getPremiumKitManager().getPremiumKits(player);
        }

        int slot = 0;
        if(type == ClassType.HERO) {
             slot = 10; // Start in a nicer position for hero kits
        }

        for (Map.Entry<String, ItemStack> entry : kits.entrySet()) {
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44) {
                 slot += 2; // Skip to next row for hero kits
            }

            ItemStack kitItem = entry.getValue().clone();
            ItemMeta meta = kitItem.getItemMeta();

            // NPE Fix: Check if meta is null, which can happen with Material.AIR
            if (meta == null) {
                plugin.getLogger().warning("Could not create display item for kit '" + entry.getKey() + "' because its material is invalid or AIR.");
                continue; // Skip this invalid kit
            }

            meta.setDisplayName(ChatColor.GREEN + entry.getKey());

            List<String> lore = new ArrayList<>();
            if(meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            lore.add("");

            if (type == ClassType.HERO) {
                boolean hasKit = plugin.getPremiumKitManager().doesPlayerOwnKit(player, entry.getKey());
                if (hasKit) {
                    lore.add(ChatColor.YELLOW + "Click to select this kit!");
                    meta.setDisplayName(ChatColor.GREEN + entry.getKey());
                } else {
                    lore.add(ChatColor.RED + "LOCKED");
                    lore.add(ChatColor.GRAY + "Purchase at the store!");
                    meta.setDisplayName(ChatColor.RED + entry.getKey());
                }
            } else {
                lore.add(ChatColor.YELLOW + "Click to select this kit!");
            }

            meta.setLore(lore);
            kitItem.setItemMeta(meta);
            gui.setItem(slot++, kitItem);
        }

        player.openInventory(gui);
    }
}
