package kitpvp.kitpvp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.Arrays;

public class PremiumKitShop implements Listener {
    private final EconomyManager economyManager;
    private final PremiumKitManager premiumKitManager;

    private static final String SHOP_TITLE = "Buy Premium Kits";

    public PremiumKitShop(EconomyManager economyManager, PremiumKitManager premiumKitManager) {
        this.economyManager = economyManager;
        this.premiumKitManager = premiumKitManager;
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
        createShopItem(shop, 10, Material.DIAMOND_SWORD, "Elite Warrior Kit", 500);
        createShopItem(shop, 13, Material.ENDER_EYE, "Enderman Kit", 1000);
        createShopItem(shop, 16, Material.WITHER_SKELETON_SKULL, "Wither Kit", 1500);
        createShopItem(shop, 24, Material.FEATHER, "Aero Kit", 2000);
        createShopItem(shop, 21, Material.NETHER_STAR, "Jedi Kit", 2500);
        player.openInventory(shop);
    }

    private void createShopItem(Inventory inventory, int slot, Material material, String displayName, int cost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + displayName);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Cost: " + cost + " coins"));
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().equals(SHOP_TITLE))
            return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null)
            return;

        String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        int cost = getKitCost(kitName);

        if (premiumKitManager.doesPlayerOwnKit(player, kitName)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You already own the " + kitName + "!"));
        } else if (economyManager.getCoins(player) >= cost) {
            economyManager.removeCoins(player, cost);
            premiumKitManager.addKitToPlayer(player, kitName);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've purchased the " + kitName + "!"));
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough coins!"));
        }
        player.closeInventory();
    }

    private int getKitCost(String kitName) {
        switch (kitName) {
            case "Elite Warrior Kit":
                return 500;
            case "Enderman Kit":
                return 1000;
            case "Wither Kit":
                return 1500;
            case "Aero Kit":
                return 2000;
            case "Jedi Kit":
                return 2500;
            default:
                return 0;
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
