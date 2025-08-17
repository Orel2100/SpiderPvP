package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import kitpvp.kitpvp.Main;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Arrays;
import org.bukkit.ChatColor;

public class DuelQueueGUI {

    private final Main plugin;
    private final Player player;

    public DuelQueueGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "1v1 Arenas");

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        ArenaManager arenaManager = plugin.getArenaManager();
        ConfigurationSection arenas = arenaManager.getConfig().getConfigurationSection("arenas");
        if (arenas != null) {
            for (String arenaName : arenas.getKeys(false)) {
                ArenaStatus status = plugin.getArenaManager().getArenaStatus(arenaName);
                Material material;
                switch (status) {
                    case COUNTDOWN:
                        material = Material.ORANGE_WOOL;
                        break;
                    case FIGHTING:
                        material = Material.RED_WOOL;
                        break;
                    case REGENERATING:
                        material = Material.PINK_WOOL;
                        break;
                    default:
                        material = Material.GREEN_WOOL;
                        break;
                }
                ItemStack arenaItem = new ItemStack(material);
                ItemMeta meta = arenaItem.getItemMeta();
                meta.setDisplayName(arenaName);

                int playerCount = 0;
                Duel duel = DuelManager.getInstance(plugin).getDuelByArenaName(arenaName);
                if (duel != null) {
                    playerCount = 2;
                }

                meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Status: " + status.toString(),
                    ChatColor.GRAY + "Players: " + playerCount + "/2",
                    "",
                    ChatColor.GREEN + "Click to join the queue!"
                ));
                arenaItem.setItemMeta(meta);
                gui.addItem(arenaItem);
            }
        }
        player.openInventory(gui);
    }
}
