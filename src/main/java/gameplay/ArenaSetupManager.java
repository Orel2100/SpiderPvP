package gameplay;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;
import kitpvp.kitpvp.Main;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaSetupManager implements Listener, CommandExecutor {

    private final Main plugin;

    public ArenaSetupManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("arena")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("setup")) {
                if (!player.hasPermission("arena.setup")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /arena setup <name>");
                    return true;
                }
                String arenaName = args[1];
                new ArenaSetupWizard(plugin, player, arenaName).start();
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ArenaSetupWizard wizard = ArenaSetupWizard.getWizard(player);
        if (wizard == null) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        event.setCancelled(true);
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Save Arena")) {
            if (wizard.isComplete()) {
                wizard.saveArena();
                player.sendMessage(ChatColor.GREEN + "Arena saved!");
            } else {
                player.sendMessage(ChatColor.RED + "Please set all locations before saving.");
            }
        } else {
            wizard.setLocation(itemName, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Set " + itemName + " to your current location.");
        }
    }
}
