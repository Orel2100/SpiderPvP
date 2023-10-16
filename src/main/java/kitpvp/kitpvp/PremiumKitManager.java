package kitpvp.kitpvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
        return ((HashSet) this.playerKitOwnership.getOrDefault(player.getUniqueId(), new HashSet<>())).contains(kitName);
    }

    public void addKitToPlayer(Player player, String kitName) {
        ((HashSet<String>) this.playerKitOwnership.computeIfAbsent(player.getUniqueId(), k -> new HashSet())).add(kitName);
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
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Elite Warrior Kit")) {
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
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Enderman Kit")) {
                    if (doesPlayerOwnKit(player, "Enderman Kit")) {
                        giveEndermankit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Enderman kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Enderman Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case WITHER_SKELETON_SKULL:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Wither Kit")) {
                    if (doesPlayerOwnKit(player, "Wither Kit")) {
                        giveWitherkit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Wither kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Wither Kit!"));
                    }
                    player.closeInventory();
                }
                break;
            case FEATHER:
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Aero Kit")) {
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
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Jedi Kit")) {
                    if (doesPlayerOwnKit(player, "Jedi Kit")) {
                        giveJediKit(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've equipped the Jedi kit!"));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You don't own the Jedi Kit!"));
                    }
                    player.closeInventory();
                }
                break;

        }
    }

    public void handleKitSelection(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.EMERALD && item.hasItemMeta() && "Premium Kit Selector".equals(item.getItemMeta().getDisplayName()))
            openPremiumKitSelectionMenu(player);
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

    public void openPremiumKitSelectionMenu(Player player) {
        Inventory kitMenuPremium = Bukkit.createInventory(null, 27, "Select Your Premium Kit");
        for (int i = 0; i < 27; i++) {
            if (i != 10 && i != 13 && i != 16 && i != 24)
                kitMenuPremium.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        createKitMenuItem(kitMenuPremium, 10, Material.DIAMOND_SWORD, "Elite Warrior Kit");
        createKitMenuItem(kitMenuPremium, 13, Material.ENDER_EYE, "Enderman Kit");
        createKitMenuItem(kitMenuPremium, 16, Material.WITHER_SKELETON_SKULL, "Wither Kit");
        createKitMenuItem(kitMenuPremium, 24, Material.FEATHER, "Aero Kit");
        createKitMenuItem(kitMenuPremium, 21, Material.NETHER_STAR, "Force Knight Kit");
        player.openInventory(kitMenuPremium);
    }

    private void createKitMenuItem(Inventory inventory, int slot, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    public void giveEliteWarriorKit(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD)});
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }

    public void giveEndermankit(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.IRON_SWORD)});
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ENDER_EYE)});
        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }

    public void giveWitherkit(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.IRON_SWORD)});
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.BLAZE_ROD)});
        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }

    public void giveAeroKit(Player player) {
        player.getInventory().clear();
        ItemStack aeroSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta aeroSwordMeta = aeroSword.getItemMeta();
        aeroSwordMeta.setDisplayName(ChatColor.AQUA + "Aero Sword");
        aeroSword.setItemMeta(aeroSwordMeta);
        player.getInventory().addItem(aeroSword);
        ItemStack aeroFeather = new ItemStack(Material.FEATHER);
        ItemMeta aeroFeatherMeta = aeroFeather.getItemMeta();
        aeroFeatherMeta.setDisplayName("Aero Feather");
        aeroFeather.setItemMeta(aeroFeatherMeta);
        player.getInventory().addItem(aeroFeather);
        player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.AQUA + "You've equipped the Aero kit!"));
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_STEW));
            }
        }
    }

    private void giveJediKit(Player player) {
        // Lightsaber
        ItemStack lightsaber = new ItemStack(Material.IRON_SWORD);
        player.getInventory().addItem(lightsaber);

        // Jedi Robes (Brown Leather Armor)
        ItemStack jediRobeChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) jediRobeChestplate.getItemMeta();
        chestplateMeta.setColor(Color.MAROON); // Brown color for the Jedi robe
        jediRobeChestplate.setItemMeta(chestplateMeta);
        player.getInventory().setChestplate(jediRobeChestplate);

        // Force Push (Blaze Rod)
        ItemStack forcePush = new ItemStack(Material.BLAZE_ROD);
        player.getInventory().addItem(forcePush);
    }
}
