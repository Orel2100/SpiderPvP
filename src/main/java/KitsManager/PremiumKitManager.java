package KitsManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import economy.EconomyManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PremiumKitManager implements Listener {
    private final JavaPlugin plugin;

    private HashMap<UUID, HashSet<String>> playerKitOwnership = new HashMap<>();

    private final EconomyManager economyManager;

    private String PREMIUM_KIT_SELECTOR_TITLE = "Select Your Premium Kit";

    private String PREMIUM_KIT_SELECTOR_NAME = "Premium Kit Selector";

    private File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    public boolean doesPlayerOwnKit(Player player, String kitName) {
        boolean ownsKit = this.playerKitOwnership.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(kitName);
        return ownsKit;
    }

    public void addKitToPlayer(Player player, String kitName) {
        this.playerKitOwnership.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(kitName);
        FileConfiguration kitOwnershipConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "kitOwnership.yml"));
        List<String> playerKits = kitOwnershipConfig.getStringList(player.getUniqueId().toString());
        if (playerKits == null) {
            playerKits = new ArrayList<>();
        }
        playerKits.add(kitName);
        kitOwnershipConfig.set(player.getUniqueId().toString(), playerKits);
        try {
            kitOwnershipConfig.save(new File(plugin.getDataFolder(), "kitOwnership.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public PremiumKitManager(EconomyManager economyManager, JavaPlugin plugin) {
        this.PREMIUM_KIT_SELECTOR_TITLE = "Select Your Premium Kit";
        this.PREMIUM_KIT_SELECTOR_NAME = "Premium Kit Selector";
        this.economyManager = economyManager;
        this.plugin = plugin;
    }

    public void givePremiumKitSelector(Player player, int slot) {
        ItemStack kitSelector = new ItemStack(Material.EMERALD);
        ItemMeta meta = kitSelector.getItemMeta();
        meta.setDisplayName("Premium Kit Selector");
        kitSelector.setItemMeta(meta);
        player.getInventory().setItem(slot, kitSelector);
    }


    @EventHandler
    public void handleKitSelectionClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().equals("Select Your Premium Kit"))
            return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null)
            return;
        switch (event.getCurrentItem().getType()) {
            case DIAMOND_SWORD:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Elite Warrior Kit")) {
                    if (doesPlayerOwnKit(player, "Elite Warrior Kit")) {
                        giveEliteWarriorKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Elite Warrior kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Elite Warrior Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case ENDER_EYE:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Enderman Kit")) {
                    if (doesPlayerOwnKit(player, "Enderman Kit")) {
                        giveEndermanKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Enderman kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Enderman Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case WITHER_SKELETON_SKULL:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Wither Kit")) {
                    if (doesPlayerOwnKit(player, "Wither Kit")) {
                        giveWitherKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Wither kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Wither Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case FEATHER:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Aero Kit")) {
                    if (doesPlayerOwnKit(player, "Aero Kit")) {
                        giveAeroKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Aero kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Aero Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case NETHER_STAR:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Jedi Kit")) {
                    if (doesPlayerOwnKit(player, "Jedi Kit")) {
                        giveJediKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Jedi kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Jedi Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case BLAZE_ROD:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Blaze Kit")) {
                    if (doesPlayerOwnKit(player, "Blaze Kit")) {
                        giveBlazeKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the BLaze Kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You dont own the Blaze Kit"));
                    }
                    player.closeInventory();
                }
                break;

        }
    }


    public void saveKitOwnership() {
        try {
            File file = new File(getDataFolder(), "kitOwnership.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (UUID uuid : this.playerKitOwnership.keySet())
                config.set(uuid.toString(), new ArrayList(this.playerKitOwnership.get(uuid)));
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKitOwnership() {
        try {
            File file = new File(getDataFolder(), "kitOwnership.yml");
            if (!file.exists())
                return;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String uuidStr : config.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                List<String> kitList = config.getStringList(uuidStr);
                this.playerKitOwnership.put(uuid, new HashSet<>(kitList));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals(this.plugin.getName()))
            saveKitOwnership();
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals(this.plugin.getName()))
            loadKitOwnership();
    }


    public void ensureKitOwnershipFileExists() {
        if (!this.plugin.getDataFolder().exists())
            this.plugin.getDataFolder().mkdirs();
        File file = new File(this.plugin.getDataFolder(), "kitOwnership.yml");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private int getKitPrice(String kitName) {
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
            case "Blaze Kit":
                return 3000;
            default:
                return 0;
        }
    }


    public void openPremiumKitSelectionMenu(Player player) {
        Inventory kitMenuPremium = Bukkit.createInventory(null, 27, PREMIUM_KIT_SELECTOR_TITLE);

        // Set the glass panes in all slots initially
        for (int i = 0; i < 27; i++) {
            kitMenuPremium.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Update the method calls to include the player parameter and correct slots
        createKitMenuItem(kitMenuPremium, 3, Material.DIAMOND_SWORD, ChatColor.GREEN + "Elite Warrior Kit", player);
        createKitMenuItem(kitMenuPremium, 10, Material.ENDER_EYE, ChatColor.GREEN +"Enderman Kit", player);
        createKitMenuItem(kitMenuPremium, 5, Material.WITHER_SKELETON_SKULL, ChatColor.GREEN +"Wither Kit", player);
        createKitMenuItem(kitMenuPremium, 16, Material.FEATHER, ChatColor.GREEN +"Aero Kit", player);
        createKitMenuItem(kitMenuPremium, 12, Material.NETHER_STAR, ChatColor.GREEN + "Jedi Kit", player);
        createKitMenuItem(kitMenuPremium, 14, Material.BLAZE_ROD, ChatColor.GREEN + "Blaze Kit", player);

        player.openInventory(kitMenuPremium);
    }

    private void createKitMenuItem(Inventory inventory, int slot, Material material, String displayName, Player player) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        // Create a list to hold the lore
        List<String> lore = new ArrayList<>();

        // Add lore based on the kit name
        switch (displayName) {
            case "Elite Warrior Kit":
                lore.add(ChatColor.GRAY + "Ability: Lightning Strike (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Diamond Sword");
                lore.add(ChatColor.RED + "Cooldown: 15s");
                break;
            case "Enderman Kit":
                lore.add(ChatColor.GRAY + "Ability: Teleportation (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword");
                lore.add(ChatColor.RED + "Cooldown: 15s");
                break;
            case "Wither Kit":
                lore.add(ChatColor.GRAY + "Ability: Withering Blast (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword & Blaze Rod");
                lore.add(ChatColor.RED + "Cooldown: 15s");
                break;
            case "Aero Kit":
                lore.add(ChatColor.GRAY + "Ability: Dash (Right-Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword");
                lore.add(ChatColor.RED + "Cooldown: 10s");
                break;
            case "Jedi Kit":
                lore.add(ChatColor.GRAY + "Ability: Force Push (Shift)");
                lore.add(ChatColor.GRAY + "Weapon: Lightsaber");
                lore.add(ChatColor.RED + "Cooldown: 10s");
                break;
            case "Blaze Kit":
                lore.add(ChatColor.GRAY + "Ability: Blaze Rampage (Right Click)");
                lore.add(ChatColor.GRAY + "Weapon: Iron Sword");
                lore.add(ChatColor.RED + "Cooldown: 30s");
                break;
        }

        // Add ownership status to the lore
        if (doesPlayerOwnKit(player, ChatColor.stripColor(displayName))) {
            lore.add(ChatColor.GREEN + "Owned");
        } else {
            lore.add(ChatColor.RED + "Not Owned");
        }

        // Set the lore to the item meta
        meta.setLore(lore);
        item.setItemMeta(meta);

        // Place the item in the inventory
        inventory.setItem(slot, item);
    }


    public void giveEliteWarriorKit(Player player) {
        player.getInventory().clear();
        setArmor(player, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
        giveAbilityItem(player, Material.IRON_SWORD, ChatColor.GREEN  + "Elite Warrior Sword");
        fillWithStew(player);
    }

    // Enderman Kit - Chainmail Armor, Iron Sword, Teleportation Ability
    public void giveEndermanKit(Player player) {
        player.getInventory().clear();
        setArmor(player, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);
        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        giveAbilityItem(player, Material.ENDER_EYE, "Teleport (Right Click)");
        fillWithStew(player);
    }

    // Wither Kit - Chainmail Armor, Sharpness I Stone Sword, Withering Blast Ability
    public void giveWitherKit(Player player) {
        player.getInventory().clear();
        setArmor(player, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);
        giveItemWithEnchant(player, Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 1);
        giveAbilityItem(player, Material.BLAZE_ROD, "Homing Skull (Right Click)");
        fillWithStew(player);
    }

    // Aero Kit - Silver Colored Leather Armor, Sharpness I Stone Sword, Dash Ability
    public void giveAeroKit(Player player) {
        player.getInventory().clear();
        setColoredArmor(player, Color.SILVER);
        giveItemWithEnchant(player, Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 1);
        giveAbilityItem(player, Material.FEATHER, "Dash (Right Click)");
        fillWithStew(player);
    }

    // Jedi Kit - Maroon Colored Leather Armor, Sharpness I Iron Sword, Force Push Ability
    private void giveJediKit(Player player) {
        player.getInventory().clear();
        setColoredArmor(player, Color.MAROON);
        giveItemWithEnchant(player, Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 1);
        giveAbilityItem(player, Material.LEGACY_ENDER_PORTAL_FRAME, "Force Push (Shift)");
        fillWithStew(player);
    }

    // Blaze Kit - Gold Armor, Sharpness I Iron Sword, Blaze Rampage Ability
    private void giveBlazeKit(Player player) {
        player.getInventory().clear();
        setArmor(player, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS);
        giveItemWithEnchant(player, Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 1);
        giveAbilityItem(player, Material.BLAZE_ROD, ChatColor.GOLD + "Blaze Rampage (Right Click)");
        fillWithStew(player);
    }

    // Helper method to set armor
    private void setArmor(Player player, Material helmet, Material chestplate, Material leggings, Material boots) {
        player.getInventory().setHelmet(new ItemStack(helmet));
        player.getInventory().setChestplate(new ItemStack(chestplate));
        player.getInventory().setLeggings(new ItemStack(leggings));
        player.getInventory().setBoots(new ItemStack(boots));
    }

    // Helper method to set colored leather armor
    private void setColoredArmor(Player player, Color color) {
        ItemStack[] armor = new ItemStack[]{
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_BOOTS)
        };
        for (ItemStack item : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        player.getInventory().setArmorContents(armor);
    }

    // Helper method to give an item with enchantment
    private void giveItemWithEnchant(Player player, Material material, Enchantment enchantment, int level) {
        ItemStack item = new ItemStack(material);
        item.addEnchantment(enchantment, level);
        player.getInventory().addItem(item);
    }

    // Helper method to give an ability item with custom name
    private void giveAbilityItem(Player player, Material material, String abilityName) {
        ItemStack abilityItem = new ItemStack(material);
        ItemMeta meta = abilityItem.getItemMeta();
        meta.setDisplayName(abilityName);
        abilityItem.setItemMeta(meta);
        player.getInventory().addItem(abilityItem);
    }

    // Helper method to fill inventory with Mushroom Stew, excluding the off-hand slot
    private void fillWithStew(Player player) {
        for (int i = 0; i < 36; i++) { // Main inventory slots are from 0 to 35
            if (player.getInventory().getItem(i) == null) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
        // Ensure off-hand is not filled with stew
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
    }
}
