package gameplay;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import kitpvp.kitpvp.Main;
import gameplay.DuelModeGUI;
import gameplay.DuelQueueGUI;
import org.bukkit.Bukkit;

public class DuelCommand implements CommandExecutor {

    private final Main plugin;

    public DuelCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getCombatTasks().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't use this command while in combat!");
            return true;
        }

        if (args.length == 0) {
            new DuelModeGUI().open(player);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                DuelRequestManager.getInstance(plugin).acceptRequest(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                DuelRequestManager.getInstance(plugin).denyRequest(player);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getMessageManager().getMessage("player_not_found"));
                return true;
            }
            DuelRequestManager.getInstance(plugin).sendRequest(player, target);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /duel [player]");
        return true;
    }
}
