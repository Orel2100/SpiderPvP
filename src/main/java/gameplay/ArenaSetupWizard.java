package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import kitpvp.kitpvp.Main;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;

public class ArenaSetupWizard {

    private static final Map<UUID, ArenaSetupWizard> wizards = new HashMap<>();
    private final Main plugin;
    private final Player player;
    private final String arenaName;
    private Location spawn1, spawn2, corner1, corner2, specSpawn;

    public ArenaSetupWizard(Main plugin, Player player, String arenaName) {
        this.plugin = plugin;
        this.player = player;
        this.arenaName = arenaName;
    }

    public void openSetupGUI() {
        wizards.put(player.getUniqueId(), this);
        Inventory gui = Bukkit.createInventory(null, 27, "Arena Setup: " + arenaName);

        gui.setItem(10, createGuiItem(Material.RED_BED, "Set Spawn 1"));
        gui.setItem(11, createGuiItem(Material.BLUE_BED, "Set Spawn 2"));
        gui.setItem(13, createGuiItem(Material.OAK_FENCE, "Set Corner 1"));
        gui.setItem(14, createGuiItem(Material.OAK_FENCE, "Set Corner 2"));
        gui.setItem(16, createGuiItem(Material.COMPASS, "Set Spectator Spawn"));
        gui.setItem(26, createGuiItem(Material.GREEN_WOOL, "Save Arena"));

        player.openInventory(gui);
    }

    public void setLocation(String locType, Location loc) {
        switch(locType) {
            case "spawn1": this.spawn1 = loc; break;
            case "spawn2": this.spawn2 = loc; break;
            case "corner1": this.corner1 = loc; break;
            case "corner2": this.corner2 = loc; break;
            case "specspawn": this.specSpawn = loc; break;
        }
    }

    public boolean isComplete() {
        return spawn1 != null && spawn2 != null && corner1 != null && corner2 != null && specSpawn != null;
    }

    public void saveArena() {
        ArenaManager arenaManager = plugin.getArenaManager();
        String path = "arenas." + arenaName;
        // save locations
        saveLoc(arenaManager, path + ".spawn1", spawn1);
        saveLoc(arenaManager, path + ".spawn2", spawn2);
        saveLoc(arenaManager, path + ".corner1", corner1);
        saveLoc(arenaManager, path + ".corner2", corner2);
        saveLoc(arenaManager, path + ".specspawn", specSpawn);
        arenaManager.saveConfig();
    }

    private void saveLoc(ArenaManager am, String path, Location loc) {
        am.getConfig().set(path + ".world", loc.getWorld().getName());
        am.getConfig().set(path + ".x", loc.getX());
        am.getConfig().set(path + ".y", loc.getY());
        am.getConfig().set(path + ".z", loc.getZ());
    }

    private ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        item.setItemMeta(meta);
        return item;
    }

    public static ArenaSetupWizard getWizard(Player player) {
        return wizards.get(player.getUniqueId());
    }

    public static void removeWizard(Player player) {
        wizards.remove(player.getUniqueId());
    }
}
