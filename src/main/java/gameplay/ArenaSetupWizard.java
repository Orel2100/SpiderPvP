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
    private final ItemStack[] playerInventory;
    private Location spawn1, spawn2, corner1, corner2;

    public ArenaSetupWizard(Main plugin, Player player, String arenaName) {
        this.plugin = plugin;
        this.player = player;
        this.arenaName = arenaName;
        this.playerInventory = player.getInventory().getContents();
    }

    public void start() {
        wizards.put(player.getUniqueId(), this);
        player.getInventory().clear();

        player.getInventory().setItem(0, createSetupItem(Material.RED_BED, "Set Spawn 1"));
        player.getInventory().setItem(1, createSetupItem(Material.BLUE_BED, "Set Spawn 2"));
        player.getInventory().setItem(2, createSetupItem(Material.OAK_FENCE, "Set Corner 1"));
        player.getInventory().setItem(3, createSetupItem(Material.OAK_FENCE, "Set Corner 2"));
        player.getInventory().setItem(8, createSetupItem(Material.GREEN_WOOL, "Save Arena"));

        player.sendMessage(ChatColor.GREEN + "Arena setup started for " + arenaName + ". Right-click with the items to set the positions.");
    }

    public void setLocation(String locType, Location loc) {
        switch(locType) {
            case "Set Spawn 1": this.spawn1 = loc; break;
            case "Set Spawn 2": this.spawn2 = loc; break;
            case "Set Corner 1": this.corner1 = loc; break;
            case "Set Corner 2": this.corner2 = loc; break;
        }
    }

    public boolean isComplete() {
        return spawn1 != null && spawn2 != null && corner1 != null && corner2 != null;
    }

    public void saveArena() {
        ArenaManager arenaManager = plugin.getArenaManager();
        String path = "arenas." + arenaName;
        saveLoc(arenaManager, path + ".spawn1", spawn1);
        saveLoc(arenaManager, path + ".spawn2", spawn2);
        saveLoc(arenaManager, path + ".corner1", corner1);
        saveLoc(arenaManager, path + ".corner2", corner2);
        arenaManager.saveConfig();
        finish();
    }

    private void saveLoc(ArenaManager am, String path, Location loc) {
        am.getConfig().set(path + ".world", loc.getWorld().getName());
        am.getConfig().set(path + ".x", loc.getX());
        am.getConfig().set(path + ".y", loc.getY());
        am.getConfig().set(path + ".z", loc.getZ());
    }

    private ItemStack createSetupItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        item.setItemMeta(meta);
        return item;
    }

    public void finish() {
        player.getInventory().clear();
        player.getInventory().setContents(playerInventory);
        removeWizard(player);
    }

    public static ArenaSetupWizard getWizard(Player player) {
        return wizards.get(player.getUniqueId());
    }

    public static void removeWizard(Player player) {
        wizards.remove(player.getUniqueId());
    }
}
