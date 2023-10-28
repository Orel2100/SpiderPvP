package kitpvp.kitpvp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommands implements CommandExecutor {
    private final EconomyManager economyManager;

    public EconomyCommands(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("coins")) {
            player.sendMessage("You have " + this.economyManager.getCoins(player) + " coins.");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("addcoins")) {
            int amount;
            if (!player.hasPermission("kitpvp.addcoins")) {
                player.sendMessage("You don't have permission to use this command.");
                return true;
            }
            if (args.length != 2) {
                player.sendMessage("Usage: /addcoins <player> <amount>");
                return true;
            }
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("Player not found!");
                return true;
            }
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount!");
                return true;
            }
            this.economyManager.addCoins(target, amount);
            player.sendMessage("Added " + amount + " coins to " + target.getName() + ".");
            target.sendMessage(ChatColor.GRAY + "You were rewarded " + ChatColor.RED + amount + ChatColor.GRAY + " coins");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("removecoins")) {
            int amount;
            if (!player.hasPermission("kitpvp.removecoins")) {
                player.sendMessage("You don't have permission to use this command.");
                return true;
            }
            if (args.length != 2) {
                player.sendMessage("Usage: /removecoins <player> <amount>");
                return true;
            }
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("Player not found!");
                return true;
            }
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount!");
                return true;
            }
            this.economyManager.removeCoins(target, amount);
            player.sendMessage("Removed " + amount + " coins from " + target.getName() + ".");
            return true;
        }
        return false;
    }
}
