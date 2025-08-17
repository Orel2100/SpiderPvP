package moderation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import kitpvp.kitpvp.Main;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishGUIListener implements Listener {

    private final Main plugin;
    private final Map<UUID, PunishmentInfo> staffAwaitingReason = new HashMap<>();

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
                staff.sendMessage(plugin.getMessageManager().getMessage("no_permission"));
                return;
            }

            Player target = plugin.getServer().getPlayer(event.getView().getTitle().substring(7));
            if (target == null) {
                staff.sendMessage(plugin.getMessageManager().getMessage("player_not_found"));
                staff.closeInventory();
                return;
            }

            if (punishmentType.equalsIgnoreCase("Mute") || punishmentType.equalsIgnoreCase("Ban")) {
                new DurationGUI(staff, target, punishmentType).open();
            } else {
                staffAwaitingReason.put(staff.getUniqueId(), new PunishmentInfo(target, punishmentType, -1));
                staff.sendMessage(ChatColor.GREEN + "Please type the reason for the " + punishmentType.toLowerCase() + " in the chat.");
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
                staff.sendMessage(plugin.getMessageManager().getMessage("player_not_found"));
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

            staffAwaitingReason.put(staff.getUniqueId(), new PunishmentInfo(target, punishmentType, durationMillis));
            staff.sendMessage(ChatColor.GREEN + "Please type the reason for the " + punishmentType.toLowerCase() + " in the chat.");
            staff.closeInventory();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player staff = event.getPlayer();
        if (staffAwaitingReason.containsKey(staff.getUniqueId())) {
            event.setCancelled(true);
            PunishmentInfo punishmentInfo = staffAwaitingReason.get(staff.getUniqueId());
            String reason = event.getMessage();

            Player target = punishmentInfo.getTarget();
            String punishmentType = punishmentInfo.getPunishmentType();
            long duration = punishmentInfo.getDuration();

            PunishmentManager punishmentManager = plugin.getPunishmentManager();
            int punishmentId = punishmentManager.getConfig().getConfigurationSection("punishments") == null ? 1 : punishmentManager.getConfig().getConfigurationSection("punishments").getKeys(false).size() + 1;
            punishmentManager.getConfig().set("punishments." + punishmentId + ".player", target.getUniqueId().toString());
            punishmentManager.getConfig().set("punishments." + punishmentId + ".staff", staff.getUniqueId().toString());
            punishmentManager.getConfig().set("punishments." + punishmentId + ".type", punishmentType);
            punishmentManager.getConfig().set("punishments." + punishmentId + ".reason", reason);
            punishmentManager.getConfig().set("punishments." + punishmentId + ".expires", duration);
            punishmentManager.saveConfig();

            staff.sendMessage(ChatColor.GREEN + "You have " + punishmentType.toLowerCase() + "ed " + target.getName() + " for: " + reason);
            plugin.getServer().broadcastMessage(ChatColor.RED + target.getName() + " has been " + punishmentType.toLowerCase() + "ed by " + staff.getName() + ".");

            if (punishmentType.equalsIgnoreCase("kick")) {
                target.kickPlayer(ChatColor.RED + "You have been kicked for: " + reason);
            } else if (punishmentType.equalsIgnoreCase("ban")) {
                target.kickPlayer(ChatColor.RED + "You have been banned for: " + reason);
            } else if (punishmentType.equalsIgnoreCase("warn")) {
                target.sendMessage(ChatColor.RED + "You have been warned for: " + reason);
            }

            staffAwaitingReason.remove(staff.getUniqueId());
        }
    }

    private static class PunishmentInfo {
        private final Player target;
        private final String punishmentType;
        private final long duration;

        public PunishmentInfo(Player target, String punishmentType, long duration) {
            this.target = target;
            this.punishmentType = punishmentType;
            this.duration = duration;
        }

        public Player getTarget() {
            return target;
        }

        public String getPunishmentType() {
            return punishmentType;
        }

        public long getDuration() {
            return duration;
        }
    }
}
