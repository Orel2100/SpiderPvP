package moderation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;
import moderation.DurationGUI;
import moderation.PunishmentContext;
import moderation.PunishmentGUIManager;
import moderation.PunishmentContext;
import moderation.PunishmentGUIManager;
import org.bukkit.inventory.ItemStack;
import moderation.PunishmentManager;

public class PunishGUIListener implements Listener {

    private final Main plugin;

    public PunishGUIListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Punish ")) {
            event.setCancelled(true);
            Player staff = (Player) event.getWhoClicked();
            if(event.getCurrentItem() == null) return;
            if(!event.getCurrentItem().hasItemMeta()) return;
            String punishmentType = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            if (!staff.hasPermission("kitpvp.punish." + punishmentType.toLowerCase())) {
                staff.sendMessage(ChatColor.RED + "You don't have permission to " + punishmentType.toLowerCase() + " players.");
                return;
            }

            if (punishmentType.equalsIgnoreCase("Mute") || punishmentType.equalsIgnoreCase("Ban")) {
                new DurationGUI(staff, plugin.getServer().getPlayer(event.getView().getTitle().substring(7)), punishmentType).open();
            } else {
                Player target = plugin.getServer().getPlayer(event.getView().getTitle().substring(7));
                if (target == null) {
                    staff.sendMessage(ChatColor.RED + "The player you were punishing is no longer online.");
                    staff.closeInventory();
                    return;
                }
                // For kick and warn, execute directly
                PunishmentManager punishmentManager = plugin.getPunishmentManager();
                int punishmentId = punishmentManager.getConfig().getConfigurationSection("punishments") == null ? 1 : punishmentManager.getConfig().getConfigurationSection("punishments").getKeys(false).size() + 1;
                punishmentManager.getConfig().set("punishments." + punishmentId + ".player", target.getUniqueId().toString());
                punishmentManager.getConfig().set("punishments." + punishmentId + ".staff", staff.getUniqueId().toString());
                punishmentManager.getConfig().set("punishments." + punishmentId + ".type", punishmentType);

            PunishmentContext context = PunishmentGUIManager.getInstance().getContext(staff.getUniqueId());
            String reason = "No reason specified.";
            if (context != null) {
                reason = context.getReason();
                PunishmentGUIManager.getInstance().removeContext(staff.getUniqueId());
            }
            punishmentManager.getConfig().set("punishments." + punishmentId + ".reason", reason);

                punishmentManager.getConfig().set("punishments." + punishmentId + ".expires", -1);
                punishmentManager.saveConfig();

                staff.sendMessage(ChatColor.GREEN + "You have " + punishmentType.toLowerCase() + "ed " + target.getName());
                plugin.getServer().broadcastMessage(ChatColor.RED + target.getName() + " has been " + punishmentType.toLowerCase() + "ed by " + staff.getName() + ".");
                if (punishmentType.equalsIgnoreCase("kick")) {
                    target.kickPlayer(ChatColor.RED + "You have been kicked.");
                } else if (punishmentType.equalsIgnoreCase("warn")) {
                    target.sendMessage(ChatColor.RED + "You have been warned.");
                }
                staff.closeInventory();
            }
        } else if (event.getView().getTitle().endsWith(" - Duration")) {
            event.setCancelled(true);
            Player staff = (Player) event.getWhoClicked();
            String title = event.getView().getTitle();
            String[] parts = title.split(" - ");
            String punishmentType = parts[0];
            String targetName = parts[1];
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                staff.sendMessage(ChatColor.RED + "The player you were punishing is no longer online.");
                staff.closeInventory();
                return;
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String durationStr = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            long durationMillis = -1; // Permanent
            if (!durationStr.equalsIgnoreCase("Permanent")) {
                if (durationStr.equalsIgnoreCase("1 Hour")) durationMillis = System.currentTimeMillis() + (60 * 60 * 1000);
                if (durationStr.equalsIgnoreCase("1 Day")) durationMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
                if (durationStr.equalsIgnoreCase("7 Days")) durationMillis = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
                if (durationStr.equalsIgnoreCase("30 Days")) durationMillis = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
            }

            // Save the punishment
            PunishmentManager punishmentManager = plugin.getPunishmentManager();
            int punishmentId = punishmentManager.getConfig().getConfigurationSection("punishments") == null ? 1 : punishmentManager.getConfig().getConfigurationSection("punishments").getKeys(false).size() + 1;
            punishmentManager.getConfig().set("punishments." + punishmentId + ".player", target.getUniqueId().toString());
            punishmentManager.getConfig().set("punishments." + punishmentId + ".staff", staff.getUniqueId().toString());
            punishmentManager.getConfig().set("punishments." + punishmentId + ".type", punishmentType);

            PunishmentContext context = PunishmentGUIManager.getInstance().getContext(staff.getUniqueId());
            String reason = "No reason specified.";
            if (context != null) {
                reason = context.getReason();
                PunishmentGUIManager.getInstance().removeContext(staff.getUniqueId());
            }
            punishmentManager.getConfig().set("punishments." + punishmentId + ".reason", reason);

            punishmentManager.getConfig().set("punishments." + punishmentId + ".expires", durationMillis);
            punishmentManager.saveConfig();

            staff.sendMessage(ChatColor.GREEN + "You have " + punishmentType.toLowerCase() + "ed " + target.getName() + ".");
            plugin.getServer().broadcastMessage(ChatColor.RED + target.getName() + " has been " + punishmentType.toLowerCase() + "ed by " + staff.getName() + ".");

            if (punishmentType.equalsIgnoreCase("ban")) {
                target.kickPlayer(ChatColor.RED + "You have been banned.");
            }

            staff.closeInventory();
        }
    }
}
