package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ArenaListCommand implements CommandExecutor {

    private final Main plugin;
    private final ArenaManager arenaManager;

    public ArenaListCommand(Main plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("prisoncore.arenalist")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        ConfigurationSection arenas = arenaManager.getConfig().getConfigurationSection("arenas");
        if (arenas == null || arenas.getKeys(false).isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "There are no arenas configured.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "--- Arena List ---");
        for (String arenaName : arenas.getKeys(false)) {
            ArenaStatus status = arenaManager.getArenaStatus(arenaName);
            ChatColor statusColor = ChatColor.GREEN;
            if (status == ArenaStatus.FIGHTING || status == ArenaStatus.COUNTDOWN) {
                statusColor = ChatColor.RED;
            } else if (status == ArenaStatus.REGENERATING) {
                statusColor = ChatColor.YELLOW;
            }
            player.sendMessage(ChatColor.WHITE + arenaName + ": " + statusColor + status.toString());
        }

        return true;
    }
}
