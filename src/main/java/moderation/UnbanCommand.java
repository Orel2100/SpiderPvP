package moderation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import kitpvp.kitpvp.Main;
import org.bukkit.configuration.ConfigurationSection;

public class UnbanCommand implements CommandExecutor {

    private final Main plugin;

    public UnbanCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kitpvp.unban")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("no_permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("player_not_found"));
            return true;
        }

        PunishmentManager punishmentManager = plugin.getPunishmentManager();
        ConfigurationSection punishments = punishmentManager.getConfig().getConfigurationSection("punishments");
        if (punishments != null) {
            for (String id : punishments.getKeys(false)) {
                if (punishments.getString(id + ".player").equals(target.getUniqueId().toString()) &&
                    punishments.getString(id + ".type").equalsIgnoreCase("ban")) {
                    punishments.set(id, null);
                    punishmentManager.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "You have unbanned " + target.getName() + ".");
                    return true;
                }
            }
        }

        sender.sendMessage(ChatColor.RED + "This player is not banned.");
        return true;
    }
}
