package moderation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import kitpvp.kitpvp.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class MyReportsGUI {

    private final Main plugin;
    private final Player player;

    public MyReportsGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "Your Reports");

        ReportManager reportManager = plugin.getReportManager();
        ConfigurationSection reports = reportManager.getConfig().getConfigurationSection("");
        if (reports != null) {
            for (String reportId : reports.getKeys(false)) {
                if (reports.getString(reportId + ".reporter").equals(player.getUniqueId().toString())) {
                    String reportedUUID = reports.getString(reportId + ".reported");
                    Player reported = Bukkit.getPlayer(UUID.fromString(reportedUUID));
                    if (reported != null) {
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta meta = (SkullMeta) skull.getItemMeta();
                        meta.setOwningPlayer(reported);
                        meta.setDisplayName(reported.getName());
                        List<String> lore = new ArrayList<>();
                        lore.add("Reason: " + reports.getString(reportId + ".reason"));
                        lore.add("Status: Pending"); // For now, all reports are pending
                        meta.setLore(lore);
                        skull.setItemMeta(meta);
                        gui.addItem(skull);
                    }
                }
            }
        }
        player.openInventory(gui);
    }
}
