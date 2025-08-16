package moderation;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import kitpvp.kitpvp.Main;
import org.bukkit.configuration.ConfigurationSection;

public class PunishmentListener implements Listener {

    private final Main plugin;

    public PunishmentListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        PunishmentManager punishmentManager = plugin.getPunishmentManager();
        ConfigurationSection punishments = punishmentManager.getConfig().getConfigurationSection("punishments");
        if (punishments == null) {
            return;
        }

        for (String id : punishments.getKeys(false)) {
            ConfigurationSection punishment = punishments.getConfigurationSection(id);
            if (punishment.getString("type").equalsIgnoreCase("ban") &&
                punishment.getString("player").equals(event.getPlayer().getUniqueId().toString())) {

                long expires = punishment.getLong("expires");
                if (expires == -1 || expires > System.currentTimeMillis()) {
                    String reason = punishment.getString("reason");
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You are banned!\n" +
                                                                         ChatColor.WHITE + "Reason: " + reason);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        PunishmentManager punishmentManager = plugin.getPunishmentManager();
        ConfigurationSection punishments = punishmentManager.getConfig().getConfigurationSection("punishments");
        if (punishments == null) {
            return;
        }

        for (String id : punishments.getKeys(false)) {
            ConfigurationSection punishment = punishments.getConfigurationSection(id);
            if (punishment.getString("type").equalsIgnoreCase("mute") &&
                punishment.getString("player").equals(event.getPlayer().getUniqueId().toString())) {

                long expires = punishment.getLong("expires");
                if (expires == -1 || expires > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "You are muted.");
                }
            }
        }
    }
}
