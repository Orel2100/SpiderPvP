package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import kitpvp.kitpvp.Main;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Arrays;
import org.bukkit.ChatColor;

public class GUIUpdater extends BukkitRunnable {

    private final Main plugin;

    public GUIUpdater(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().equals("1v1 Arenas")) {
                Inventory gui = player.getOpenInventory().getTopInventory();

                ArenaManager arenaManager = plugin.getArenaManager();
                ConfigurationSection arenas = arenaManager.getConfig().getConfigurationSection("arenas");
                if (arenas != null) {
                    int slot = 0;
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
                            ChatColor.GRAY + "Players: " + playerCount + "/2"
                        ));
                        arenaItem.setItemMeta(meta);
                        gui.setItem(slot, arenaItem);
                        slot++;
                    }
                }
            }
        }
    }
}
