package KitsManager;

import kitpvp.kitpvp.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PremiumKitShop implements Listener {
    private final EconomyManager economyManager;
    private final PremiumKitManager premiumKitManager;

    private static final String SHOP_TITLE = "Buy Premium Kits";

    // Enum for Kits
    public enum Kit {
        ELITE_WARRIOR("Elite Warrior Kit", Material.DIAMOND_SWORD, 500, 3),
        ENDERMAN("Enderman Kit", Material.ENDER_EYE, 1000, 10),
        WITHER("Wither Kit", Material.WITHER_SKELETON_SKULL, 1500, 5),
        AERO("Aero Kit", Material.FEATHER, 2000, 16),
        JEDI("Jedi Kit", Material.NETHER_STAR, 2500, 12),
        BLAZE("Blaze Kit", Material.BLAZE_ROD, 3000, 14);

        private final String name;
        private final Material material;
        private final int cost;
        private final int slot; // Added slot for each kit

        Kit(String name, Material material, int cost, int slot) {
            this.name = name;
            this.material = material;
            this.cost = cost;
            this.slot = slot;
        }

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public int getCost() {
            return cost;
        }

        public int getSlot() {
            return slot;
        }
    }





    private final Map<Material, Kit> kitByMaterial = new HashMap<>();

    public PremiumKitShop(EconomyManager economyManager, PremiumKitManager premiumKitManager) {
        this.economyManager = economyManager;
        this.premiumKitManager = premiumKitManager;

        for (Kit kit : Kit.values()) {
            kitByMaterial.put(kit.getMaterial(), kit);
        }
    }

    @EventHandler
    public void onPlayerRightClickWithSign(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.OAK_SIGN)
            openShop(player);
    }

    public void openShop(Player player) {
        Inventory shop = Bukkit.createInventory(null, 27, SHOP_TITLE);
        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 13 && i != 16 && i != 24)
                shop.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        for (Kit kit : Kit.values()) {
            createShopItem(shop, kit.getSlot(), kit.getMaterial(), kit.getName(), kit.getCost(), player); // Pass player as an argument
        }
        player.openInventory(shop);
    }



    private void createShopItem(Inventory inventory, int slot, Material material, String displayName, int cost, Player player) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + displayName);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Cost: " + ChatColor.RED + cost + " coins");
        if(premiumKitManager.doesPlayerOwnKit(player, displayName)) {
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Owned");
        } else {
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.RED + "Not Owned");
        }

        // Adding the detailed description lore based on the kit
        switch (displayName) {
            case "Elite Warrior Kit":
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.GRAY + "Ability: Lightning Strike (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Diamond Sword");
                lore.add(ChatColor.RED + "Cooldown: 15s");
                break;
            case "Enderman Kit":
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.GRAY + "Ability: Teleportation (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword");
                lore.add(ChatColor.RED + "Cooldown: 15s");
                break;
            case "Wither Kit":
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.GRAY + "Ability: Withering Blast (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword & Blaze Rod");
                lore.add(ChatColor.RED + "Cooldown: 15s");
                break;
            case "Aero Kit":
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.GRAY + "Ability: Dash (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword");
                lore.add(ChatColor.RED + "Cooldown: 10s");
                break;
            case "Jedi Kit":
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.GRAY + "Ability: Force Push (Shift)");
                lore.add(ChatColor.GRAY + "Weapon: Lightsaber");
                lore.add(ChatColor.RED + "Cooldown: 10s");
                break;
            case "Blaze Kit":
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.GRAY + "Ability: Blaze Rampage (Right Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword");
                lore.add(ChatColor.RED + "Cooldown: 30s");
                break;
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }





    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getView().getTitle().equals(SHOP_TITLE)) {
                event.setCancelled(true);
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getItemMeta() != null) {
                    Kit selectedKit = kitByMaterial.get(clickedItem.getType());
                    if (selectedKit == null) return;

                    if (premiumKitManager.doesPlayerOwnKit(player, selectedKit.getName())) {
                        player.sendMessage("You already own the " + selectedKit.getName() + "!");
                    } else if (economyManager.getCoins(player) >= selectedKit.getCost()) {
                        economyManager.removeCoins(player, selectedKit.getCost());
                        premiumKitManager.addKitToPlayer(player, selectedKit.getName());
                        player.sendMessage("You've purchased the " + selectedKit.getName() + "!");
                    } else {
                        player.sendMessage("You don't have enough coins!");
                    }
                    player.closeInventory();
                }
            }
        }
    }

    public void giveShopItemToSlot(Player player, int slot) {
        ItemStack kitSelector = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = kitSelector.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "SHOP");
        kitSelector.setItemMeta(meta);
        player.getInventory().setItem(slot, kitSelector);
    }
}
