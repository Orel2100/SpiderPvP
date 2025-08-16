package moderation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;
import org.bukkit.inventory.ItemStack;

public class ReportGUIListener implements Listener {

    private final Main plugin;
    private String selectedReason = null;

    public ReportGUIListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Report ")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String itemName = clickedItem.getItemMeta().getDisplayName();
            String targetName = event.getView().getTitle().substring(7);
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "The player you were reporting is no longer online.");
                player.closeInventory();
                return;
            }

            if (itemName.equals(ChatColor.GREEN + "Confirm Report")) {
                if (selectedReason == null) {
                    player.sendMessage(ChatColor.RED + "Please select a reason first.");
                    return;
                }
                // Save the report
                ReportManager reportManager = plugin.getReportManager();
                int reportId = reportManager.getConfig().getKeys(false).size() + 1;
                reportManager.getConfig().set(reportId + ".reporter", player.getUniqueId().toString());
                reportManager.getConfig().set(reportId + ".reported", target.getUniqueId().toString());
                reportManager.getConfig().set(reportId + ".reason", selectedReason);
                reportManager.getConfig().set(reportId + ".timestamp", System.currentTimeMillis());
                reportManager.saveConfig();

                player.sendMessage(ChatColor.GREEN + "Report for " + target.getName() + " has been submitted.");
                player.closeInventory();

                // Notify staff
                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("kitpvp.reports.notify")) {
                        onlinePlayer.sendMessage(ChatColor.YELLOW + "[Report] " + ChatColor.GOLD + player.getName() +
                                " reported " + target.getName() + " for " + selectedReason + ".");
                    }
                }

            } else if (itemName.equals(ChatColor.RED + "Cancel")) {
                player.closeInventory();
            } else {
                // It's a reason item
                selectedReason = ChatColor.stripColor(itemName);
                player.sendMessage(ChatColor.GREEN + "Selected reason: " + selectedReason);
            }
        } else if (event.getView().getTitle().equals("Reports")) {
            event.setCancelled(true);
            Player staff = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType().toString().equals("PLAYER_HEAD")) {
                String reportedName = clickedItem.getItemMeta().getDisplayName();
                staff.sendMessage(ChatColor.GREEN + "You have selected the report for " + reportedName + ".");
                staff.closeInventory();
            }
        }
    }
}
