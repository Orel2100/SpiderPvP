package duel;

import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {

    private final Main plugin;

    public DuelCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        DuelManager duelManager = plugin.getDuelManager();

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /duel <player|accept|deny>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "accept":
                duelManager.acceptDuelRequest(player);
                break;
            case "deny":
                duelManager.denyDuelRequest(player);
                break;
            default:
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage(ChatColor.RED + "Player not found or is not online.");
                    return true;
                }
                if (target == player) {
                    player.sendMessage(ChatColor.RED + "You cannot duel yourself.");
                    return true;
                }
                duelManager.sendDuelRequest(player, target);
                break;
        }

        return true;
    }
}
