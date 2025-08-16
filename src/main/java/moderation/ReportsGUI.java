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

public class ReportsGUI {

    private final Main plugin;
    private final Player staff;

    public ReportsGUI(Main plugin, Player staff) {
        this.plugin = plugin;
        this.staff = staff;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "Reports");

        ReportManager reportManager = plugin.getReportManager();
        for (String reportId : reportManager.getConfig().getKeys(false)) {
            String reportedUUID = reportManager.getConfig().getString(reportId + ".reported");
            Player reported = Bukkit.getPlayer(UUID.fromString(reportedUUID));
            if (reported != null) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwningPlayer(reported);
                meta.setDisplayName(reported.getName());
                List<String> lore = new ArrayList<>();
                lore.add("Reported by: " + Bukkit.getOfflinePlayer(UUID.fromString(reportManager.getConfig().getString(reportId + ".reporter"))).getName());
                lore.add("Reason: " + reportManager.getConfig().getString(reportId + ".reason"));
                lore.add("Report ID: " + reportId);
                meta.setLore(lore);
                skull.setItemMeta(meta);
                gui.addItem(skull);
            }
        }
        staff.openInventory(gui);
    }
}
