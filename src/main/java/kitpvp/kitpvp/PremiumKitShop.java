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

public class PremiumKitShop implements Listener {
    private final EconomyManager economyManager;

    private final PremiumKitManager premiumKitManager;

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
        Inventory shop = Bukkit.createInventory(null, 27, "Buy Premium Kits");
        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 13 && i != 16 && i !=24)
                shop.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        createShopItem(shop, 10, Material.DIAMOND_SWORD, "Elite Warrior Kit", 500);
        createShopItem(shop, 13, Material.ENDER_EYE, ChatColor.GRAY + "Enderman Kit", 1000);
        createShopItem(shop, 16, Material.WITHER_SKELETON_SKULL, "Wither Kit", 1500);
        createShopItem(shop, 24, Material.FEATHER, "Aero Kit", 2000);
        createShopItem(shop, 21, Material.NETHER_STAR, "Force Knight Kit", 2500);
        player.openInventory(shop);
    }

    private void createShopItem(Inventory inventory, int slot, Material material, String displayName, int cost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + displayName + " - " + ChatColor.GRAY + cost + " coins");
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player)event.getWhoClicked();
        if (!event.getView().getTitle().equals("Buy Premium Kits"))
            return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null)
            return;
        switch (event.getCurrentItem().getType()) {
            case DIAMOND_SWORD:
                if (this.premiumKitManager.doesPlayerOwnKit(player, "Elite Warrior Kit")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You already own the Elite Warrior Kit!"));
                    player.closeInventory();
                    return;
                }
                if (this.economyManager.getCoins(player) >= 500) {
                    this.economyManager.removeCoins(player, 500);
                    this.premiumKitManager.addKitToPlayer(player, "Elite Warrior Kit");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've purchased the Elite Warrior Kit!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough coins!"));
                }
                player.closeInventory();
                break;
            case ENDER_EYE:
                if (this.premiumKitManager.doesPlayerOwnKit(player, "Enderman Kit")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You already own the Enderman Kit!"));
                    player.closeInventory();
                    return;
                }
                if (this.economyManager.getCoins(player) >= 1000) {
                    this.economyManager.removeCoins(player, 1000);
                    this.premiumKitManager.addKitToPlayer(player, "Enderman Kit");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've purchased the Enderman Kit!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough coins!"));
                }
                player.closeInventory();
                break;
            case WITHER_SKELETON_SKULL:
                if (this.premiumKitManager.doesPlayerOwnKit(player, "Wither Kit")){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You already own the Wither Kit!"));
                    player.closeInventory();
                    return;
                }
                if (this.economyManager.getCoins(player) >= 1500) {
                    this.economyManager.removeCoins(player, 1500);
                    this.premiumKitManager.addKitToPlayer(player, "Wither Kit");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've purchased the Wither Kit!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough coins!"));
                }
                player.closeInventory();
                break;
            case FEATHER:
                if (this.premiumKitManager.doesPlayerOwnKit(player, "Aero Kit")){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You already own the Aero Kit!"));
                    player.closeInventory();
                    return;
                }
                if (this.economyManager.getCoins(player) >= 2000) {
                    this.economyManager.removeCoins(player, 2000);
                    this.premiumKitManager.addKitToPlayer(player, "Aero Kit");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've purchased the Aero Kit!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough coins!"));
                }
                player.closeInventory();
                break;
            case NETHER_STAR:
                if (this.premiumKitManager.doesPlayerOwnKit(player, "Jedi Kit")){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You already own the Force Knight Kit!"));
                    player.closeInventory();
                    return;
                }
                if (this.economyManager.getCoins(player) >= 2000) {
                    this.economyManager.removeCoins(player, 2000);
                    this.premiumKitManager.addKitToPlayer(player, "Jedi Kit");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've purchased the Force Knight Kit!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You don't have enough coins!"));
                }
                player.closeInventory();
                break;
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
