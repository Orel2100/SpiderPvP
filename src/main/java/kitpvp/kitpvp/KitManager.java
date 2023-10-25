package kitpvp.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitManager {
    private final String KIT_SELECTOR_TITLE = "Select Your Kit";

    private final String KIT_SELECTOR_NAME = "Kit Selector";

    private final PremiumKitManager premiumKitManager;

    private NPCEvents npcMenuHandler;


    private void openPremiumKitMenu(Player player) {
        this.premiumKitManager.openPremiumKitSelectionMenu(player);
    }

    public KitManager(PremiumKitManager premiumKitManager) {
        this.premiumKitManager = premiumKitManager;
    }

    public void giveKitSelectorToSlot(Player player, int slot) {
        ItemStack kitSelector = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = kitSelector.getItemMeta();
        meta.setDisplayName("Kit Selector");
        kitSelector.setItemMeta(meta);
        player.getInventory().setItem(slot, kitSelector);
    }

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player)event.getWhoClicked();
        if (!event.getView().getTitle().equals("Select Your Kit"))
            return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null)
            return;
        switch (event.getCurrentItem().getType()) {
            case IRON_SWORD:
                giveWarriorKit(player);
                player.closeInventory();
                break;
            case BOW:
                giveArcherKit(player);
                player.closeInventory();
                break;
            case DIAMOND_AXE:
                giveBerserkerKit(player);
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
        if (item.getType() == Material.NETHER_STAR && item.hasItemMeta() && "Kit Selector".equals(item.getItemMeta().getDisplayName()))
            openKitSelectionMenu(player);
    }

    public void openKitSelectionMenu(Player player) {
        Inventory kitMenu = Bukkit.createInventory(null, 27, "Select Your Kit");
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
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    private void giveWarriorKit(Player player) {
        ItemStack WarriorSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = WarriorSword.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Warrior Sword");
        WarriorSword.setItemMeta(meta);
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack[] { WarriorSword });
        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        for (int i = 0; i < 36; i++) { // Main inventory slots are from 0 to 35
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }

    private void giveArcherKit(Player player) {
        // Clear the inventory first
        player.getInventory().clear();

        // Set the Archer Bow
        ItemStack ArcherBow = new ItemStack(Material.BOW);
        ArcherBow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta meta1 = ArcherBow.getItemMeta();
        meta1.setDisplayName(ChatColor.GREEN + "Archer Bow");
        ArcherBow.setItemMeta(meta1);
        player.getInventory().addItem(ArcherBow);

        // Set the Explosive Arrow
        ItemStack Archerarrow = new ItemStack(Material.ARROW);
        ItemMeta meta = Archerarrow.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "ExplosiveArrow");
        Archerarrow.setItemMeta(meta);
        player.getInventory().setItem(9, Archerarrow);

        // Set the armor
        player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

        // Fill the remaining empty slots with MUSHROOM_STEW
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }

        // Update the player's inventory
        player.updateInventory();
    }


    private void giveBerserkerKit(Player player) {
        ItemStack berserkerAxe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = berserkerAxe.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Berserker Axe");
        berserkerAxe.setItemMeta(meta);
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack[] { berserkerAxe });
        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        for (int i = 0; i < 36; i++) { // Main inventory slots are from 0 to 35
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }
}
