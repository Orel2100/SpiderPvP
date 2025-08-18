package gameplay;

import com.github.stefvanschie.inventoryframework.gui.guis.PlayerSelectionGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import kitpvp.kitpvp.Main;

public class DuelCommand implements CommandExecutor {

    private final Main plugin;
    private final DuelManager duelManager;

    public DuelCommand(Main plugin) {
        this.plugin = plugin;
        this.duelManager = plugin.getDuelManager();
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
            new DuelModeGUI(player).show(player);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                duelManager.acceptDuelRequest(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                duelManager.denyDuelRequest(player);
                return true;
            }
        }

        player.sendMessage(ChatColor.RED + "Usage: /duel");
        return true;
    }
}
