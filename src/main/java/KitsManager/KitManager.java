package KitsManager;

import kitpvp.kitpvp.Main;
import kitpvp.kitpvp.NPCEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {
    private final String KIT_SELECTOR_TITLE = "Select Your Kit";
    private final Main plugin;
    private final PremiumKitManager premiumKitManager;
    private final FileConfiguration kitsConfig;

    public KitManager(PremiumKitManager premiumKitManager, Main plugin) {
        this.premiumKitManager = premiumKitManager;
        this.plugin = plugin;
        File kitsFile = new File(plugin.getDataFolder(), "Regularkits.yml");
        this.kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public void giveKitSelectorToSlot(Player player, int slot) {
        ItemStack kitSelector = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = kitSelector.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aClass Selector &7(Right Click)"));
        kitSelector.setItemMeta(meta);
        player.getInventory().setItem(slot, kitSelector);
    }

    public void handleKitSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().getType() == Material.COMMAND_BLOCK) {
            plugin.getClassSelectorGUI().openClassSelector(player);
        }
    }

    public void giveKit(Player player, String kitName) {
        player.getInventory().clear();
        String path = "kits." + kitName;
        if (!kitsConfig.contains(path)) {
            player.sendMessage(ChatColor.RED + "Kit not found.");
            return;
        }

        ConfigurationSection kitSection = kitsConfig.getConfigurationSection(path);
        for (String key : kitSection.getKeys(false)) {
            if (key.equalsIgnoreCase("items")) {
                List<Map<?, ?>> itemList = kitSection.getMapList(key);
                for (Map<?, ?> itemMap : itemList) {
                    ItemStack itemStack = createItemStackFromMap(itemMap);
                    if (itemMap.containsKey("slot")) {
                        player.getInventory().setItem((Integer) itemMap.get("slot"), itemStack);
                    } else {
                        player.getInventory().addItem(itemStack);
                    }
                }
            } else {
                // Check if the item is an armor piece and equip it automatically
                ItemStack itemStack = createItemStackFromConfig(path + "." + key);
                switch (key.toLowerCase()) {
                    case "helmet":
                        player.getInventory().setHelmet(itemStack);
                        break;
                    case "chestplate":
                        player.getInventory().setChestplate(itemStack);
                        break;
                    case "leggings":
                        player.getInventory().setLeggings(itemStack);
                        break;
                    case "boots":
                        player.getInventory().setBoots(itemStack);
                        break;
                    default:
                        player.getInventory().addItem(itemStack);
                        break;
                }
            }
        }

        fillEmptySlotsWithStew(player);
    }


    private ItemStack createItemStackFromConfig(String path) {
        Material material = Material.getMaterial(kitsConfig.getString(path + ".material"));
        if (material == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (kitsConfig.contains(path + ".name")) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', kitsConfig.getString(path + ".name")));
        }
        if (kitsConfig.contains(path + ".enchantments")) {
            kitsConfig.getConfigurationSection(path + ".enchantments").getKeys(false).forEach(enchantKey -> {
                Enchantment enchantment = Enchantment.getByName(enchantKey);
                int level = kitsConfig.getInt(path + ".enchantments." + enchantKey);
                meta.addEnchant(enchantment, level, true);
            });
        }
        item.setItemMeta(meta);
        if (kitsConfig.contains(path + ".amount")) {
            item.setAmount(kitsConfig.getInt(path + ".amount"));
        }
        return item;
    }

    private ItemStack createItemStackFromMap(Map<?, ?> map) {
        Material material = Material.getMaterial((String) map.get("material"));
        if (material == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (map.containsKey("name")) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String) map.get("name")));
        }
        if (map.containsKey("enchantments")) {
            Map<String, Integer> enchantments = (Map<String, Integer>) map.get("enchantments");
            enchantments.forEach((enchantKey, level) -> {
                Enchantment enchantment = Enchantment.getByName(enchantKey);
                if (enchantment != null) {
                    meta.addEnchant(enchantment, level, true);
                }
            });
        }
        item.setItemMeta(meta);
        if (map.containsKey("amount")) {
            item.setAmount((Integer) map.get("amount"));
        }
        return item;
    }

    private void fillEmptySlotsWithStew(Player player) {
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getItem(i) == null) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }

    public Map<String, ItemStack> getKits() {
        Map<String, ItemStack> kits = new HashMap<>();
        ConfigurationSection kitsSection = kitsConfig.getConfigurationSection("kits");
        if (kitsSection != null) {
            for (String kitName : kitsSection.getKeys(false)) {
                ItemStack displayItem = createItemStackFromConfig("kits." + kitName + ".items.0");
                kits.put(kitName, displayItem);
            }
        }
        return kits;
    }
}
