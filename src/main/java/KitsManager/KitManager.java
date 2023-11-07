package KitsManager;

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
import java.util.List;
import java.util.Map;

public class KitManager {
    private final String KIT_SELECTOR_TITLE = "Select Your Kit";
    private final PremiumKitManager premiumKitManager;
    private final FileConfiguration kitsConfig;

    public KitManager(PremiumKitManager premiumKitManager, File dataFolder) {
        this.premiumKitManager = premiumKitManager;
        File kitsFile = new File(dataFolder, "Regularkits.yml");
        this.kitsConfig = YamlConfiguration.loadConfiguration(kitsFile);
    }

    public void giveKitSelectorToSlot(Player player, int slot) {
        ItemStack kitSelector = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = kitSelector.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Kit Selector"));
        kitSelector.setItemMeta(meta);
        player.getInventory().setItem(slot, kitSelector);
    }

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().equals(KIT_SELECTOR_TITLE)) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        switch (event.getCurrentItem().getType()) {
            case IRON_SWORD:
                giveKit(player, "warrior");
                player.closeInventory();
                break;
            case BOW:
                giveKit(player, "archer");
                player.closeInventory();
                break;
            case DIAMOND_AXE:
                giveKit(player, "berserker");
                player.closeInventory();
                break;
            case EMERALD:
                this.premiumKitManager.openPremiumKitSelectionMenu(player);
                break;
        }
    }

    public void handleKitSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.NETHER_STAR ){
            openKitSelectionMenu(player);
        }
    }

    public void openKitSelectionMenu(Player player) {
        Inventory kitMenu = Bukkit.createInventory(null, 27, KIT_SELECTOR_TITLE);
        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 13 && i != 16)
                kitMenu.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        createKitMenuItem(kitMenu, 10, Material.IRON_SWORD, "Warrior Kit");
        createKitMenuItem(kitMenu, 13, Material.BOW, "Archer Kit");
        createKitMenuItem(kitMenu, 16, Material.DIAMOND_AXE, "Berserker Kit");
        createKitMenuItem(kitMenu, 19, Material.EMERALD, "Premium Kits");
        player.openInventory(kitMenu);
    }

    private void createKitMenuItem(Inventory inventory, int slot, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    private void giveKit(Player player, String kitName) {
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
                    player.getInventory().addItem(createItemStackFromMap(itemMap));
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
}
