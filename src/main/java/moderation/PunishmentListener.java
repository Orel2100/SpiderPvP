package moderation;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import kitpvp.kitpvp.Main;
import org.bukkit.configuration.ConfigurationSection;
import utils.TimeUtil;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

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
                    String staffName = Bukkit.getOfflinePlayer(UUID.fromString(punishment.getString("staff"))).getName();
                    long remaining = expires == -1 ? -1 : expires - System.currentTimeMillis();
                    String timeRemaining = TimeUtil.formatTime(remaining);

                    List<String> banMessage = plugin.getMessageManager().getMessageList("ban_message");
                    banMessage.replaceAll(s -> s.replace("{reason}", reason)
                                                 .replace("{staff}", staffName)
                                                 .replace("{time_remaining}", timeRemaining));

                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, String.join("\n", banMessage));
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
                    long remaining = expires == -1 ? -1 : expires - System.currentTimeMillis();
                    String timeRemaining = TimeUtil.formatTime(remaining);
                    String muteMessage = plugin.getMessageManager().getMessage("mute_message")
                                               .replace("{time_remaining}", timeRemaining);
                    event.getPlayer().sendMessage(muteMessage);
                }
            }
        }
    }
}
