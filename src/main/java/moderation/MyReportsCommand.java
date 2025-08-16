package moderation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import kitpvp.kitpvp.Main;
import moderation.MyReportsGUI;

public class MyReportsCommand implements CommandExecutor {

    private final Main plugin;

    public MyReportsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        new MyReportsGUI(plugin, player).open();

        return true;
    }
}
