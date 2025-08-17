package gameplay;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private int step = 1;
    private Location spawn1, spawn2, corner1, corner2;

    public ArenaSetupWizard(Main plugin, Player player, String arenaName) {
        this.plugin = plugin;
        this.player = player;
        this.arenaName = arenaName;
    }

    public void start() {
        wizards.put(player.getUniqueId(), this);
        ItemStack setupTool = new ItemStack(Material.STICK);
        ItemMeta meta = setupTool.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Arena Setup Tool");
        setupTool.setItemMeta(meta);
        player.getInventory().addItem(setupTool);
        player.sendMessage(ChatColor.GREEN + "Right-click to set spawn point 1.");
    }

    public void nextStep(Location location) {
        switch (step) {
            case 1:
                spawn1 = location;
                player.sendMessage(ChatColor.GREEN + "Spawn point 1 set. Right-click to set spawn point 2.");
                step++;
                break;
            case 2:
                spawn2 = location;
                player.sendMessage(ChatColor.GREEN + "Spawn point 2 set. Right-click to set corner 1.");
                step++;
                break;
            case 3:
                corner1 = location;
                player.sendMessage(ChatColor.GREEN + "Corner 1 set. Right-click to set corner 2.");
                step++;
                break;
            case 4:
                corner2 = location;
                saveArena();
                player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " has been created!");
                player.getInventory().remove(player.getItemInHand());
                removeWizard(player);
                break;
        }
    }

    private void saveArena() {
        ArenaManager arenaManager = plugin.getArenaManager();
        String path = "arenas." + arenaName;
        arenaManager.getConfig().set(path + ".spawn1.world", spawn1.getWorld().getName());
        arenaManager.getConfig().set(path + ".spawn1.x", spawn1.getX());
        arenaManager.getConfig().set(path + ".spawn1.y", spawn1.getY());
        arenaManager.getConfig().set(path + ".spawn1.z", spawn1.getZ());
        arenaManager.getConfig().set(path + ".spawn2.world", spawn2.getWorld().getName());
        arenaManager.getConfig().set(path + ".spawn2.x", spawn2.getX());
        arenaManager.getConfig().set(path + ".spawn2.y", spawn2.getY());
        arenaManager.getConfig().set(path + ".spawn2.z", spawn2.getZ());
        arenaManager.getConfig().set(path + ".corner1.world", corner1.getWorld().getName());
        arenaManager.getConfig().set(path + ".corner1.x", corner1.getX());
        arenaManager.getConfig().set(path + ".corner1.y", corner1.getY());
        arenaManager.getConfig().set(path + ".corner1.z", corner1.getZ());
        arenaManager.getConfig().set(path + ".corner2.world", corner2.getWorld().getName());
        arenaManager.getConfig().set(path + ".corner2.x", corner2.getX());
        arenaManager.getConfig().set(path + ".corner2.y", corner2.getY());
        arenaManager.getConfig().set(path + ".corner2.z", corner2.getZ());
        arenaManager.saveConfig();
    }

    public static ArenaSetupWizard getWizard(Player player) {
        return wizards.get(player.getUniqueId());
    }

    public static void removeWizard(Player player) {
        wizards.remove(player.getUniqueId());
    }
}
