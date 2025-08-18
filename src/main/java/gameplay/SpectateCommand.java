package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import kitpvp.kitpvp.Main;

public class SpectateCommand implements CommandExecutor {

    private final Main plugin;

    public SpectateCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /spectate <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getMessageManager().getMessage("player_not_found"));
            return true;
        }

        Duel duel = DuelManager.getInstance(plugin).getDuel(target);
        if (duel == null) {
            player.sendMessage(ChatColor.RED + "This player is not in a duel.");
            return true;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(target);
        player.sendMessage(ChatColor.GREEN + "You are now spectating " + target.getName());
        DuelManager.getInstance(plugin).addSpectator(player, duel.getArenaName());

        return true;
    }
}
