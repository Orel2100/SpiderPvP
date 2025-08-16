package moderation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;
import moderation.PunishGUI;
import moderation.PunishmentContext;
import moderation.PunishmentGUIManager;
import moderation.PunishmentContext;
import moderation.PunishmentGUIManager;
import moderation.ReportManagementGUI;
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
                Player target = plugin.getServer().getPlayer(reportedName);
                if (target == null) {
                    staff.sendMessage(ChatColor.RED + "The player you are trying to manage is no longer online.");
                    staff.closeInventory();
                    return;
                }

                String reportIdLine = clickedItem.getItemMeta().getLore().stream().filter(line -> line.contains("Report ID:")).findFirst().orElse(null);
                if (reportIdLine == null) return;
                String reportId = ChatColor.stripColor(reportIdLine.substring(reportIdLine.indexOf(":") + 2));

                new ReportManagementGUI(staff, target, reportId).open();
            }
        } else if (event.getView().getTitle().startsWith("Manage Report - ")) {
            event.setCancelled(true);
            Player staff = (Player) event.getWhoClicked();
            String title = event.getView().getTitle();
            String[] parts = title.split(" - ");
            String reportId = parts[1];
            String targetName = parts[2];
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                staff.sendMessage(ChatColor.RED + "The player you were managing is no longer online.");
                staff.closeInventory();
                return;
            }

            if(event.getCurrentItem() == null) return;
            if(!event.getCurrentItem().hasItemMeta()) return;
            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            if (itemName.equalsIgnoreCase("Teleport to Player")) {
                staff.teleport(target);
                staff.sendMessage(ChatColor.GREEN + "Teleported to " + target.getName());
                staff.closeInventory();
            } else if (itemName.equalsIgnoreCase("Resolve Report")) {
                plugin.getReportManager().getConfig().set(reportId, null);
                plugin.getReportManager().saveConfig();
                staff.sendMessage(ChatColor.GREEN + "Report has been resolved.");
                staff.closeInventory();
            } else if (itemName.equalsIgnoreCase("Punish Player")) {
                String reason = plugin.getReportManager().getConfig().getString(reportId + ".reason");
                PunishmentGUIManager.getInstance().setContext(staff.getUniqueId(), new PunishmentContext(target, reason));
                new PunishGUI(staff, target).open();
            } else if (itemName.equalsIgnoreCase("Close")) {
                staff.closeInventory();
            }
        }
    }
}
