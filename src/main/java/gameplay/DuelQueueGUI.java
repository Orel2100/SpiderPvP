package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import kitpvp.kitpvp.Main;
import org.bukkit.configuration.ConfigurationSection;

public class DuelQueueGUI {

    private final Main plugin;
    private final Player player;

    public DuelQueueGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "1v1 Arenas");

        ArenaManager arenaManager = plugin.getArenaManager();
        ConfigurationSection arenas = arenaManager.getConfig().getConfigurationSection("arenas");
        if (arenas != null) {
            for (String arenaName : arenas.getKeys(false)) {
                // For now, all arenas are shown as available.
                // I will add a status system later.
                ItemStack arenaItem = new ItemStack(Material.GREEN_WOOL);
                ItemMeta meta = arenaItem.getItemMeta();
                meta.setDisplayName(arenaName);
                arenaItem.setItemMeta(meta);
                gui.addItem(arenaItem);
            }
        }
        player.openInventory(gui);
    }
}
