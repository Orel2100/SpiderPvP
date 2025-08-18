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

public class ArenaSetupManager implements Listener, CommandExecutor {

    private final Main plugin;
    private final Map<UUID, String> staffSettingLocation = new HashMap<>();

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

        if (command.getName().equalsIgnoreCase("asetup")) {
            if (!player.hasPermission("arena.setup")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /asetup <name>");
                return true;
            }
            String arenaName = args[0];
            ArenaSetupWizard wizard = new ArenaSetupWizard(plugin, player, arenaName);
            wizard.openSetupGUI();
            return true;
        }

        if (command.getName().equalsIgnoreCase("arenasetpos")) {
            if (!player.hasPermission("arena.setup")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            String locType = staffSettingLocation.get(player.getUniqueId());
            if (locType == null) {
                player.sendMessage(ChatColor.RED + "You are not currently setting a location.");
                return true;
            }
            ArenaSetupWizard wizard = ArenaSetupWizard.getWizard(player);
            if (wizard == null) {
                player.sendMessage(ChatColor.RED + "You are not in setup mode.");
                return true;
            }
            wizard.setLocation(locType, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Set " + locType + " to your current location.");
            staffSettingLocation.remove(player.getUniqueId());
            wizard.openSetupGUI(); // Re-open the GUI
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Arena Setup:")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            if (itemName.equalsIgnoreCase("Save Arena")) {
                ArenaSetupWizard wizard = ArenaSetupWizard.getWizard(player);
                if (wizard != null && wizard.isComplete()) {
                    wizard.saveArena();
                    player.sendMessage(ChatColor.GREEN + "Arena saved!");
                    ArenaSetupWizard.removeWizard(player);
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "Please set all locations before saving.");
                }
                return;
            }

            String locType = "";
            if (itemName.contains("Spawn 1")) locType = "spawn1";
            else if (itemName.contains("Spawn 2")) locType = "spawn2";
            else if (itemName.contains("Corner 1")) locType = "corner1";
            else if (itemName.contains("Corner 2")) locType = "corner2";
            else if (itemName.contains("Spectator Spawn")) locType = "specspawn";

            if (!locType.isEmpty()) {
                staffSettingLocation.put(player.getUniqueId(), locType);
                player.sendMessage(ChatColor.GREEN + "Go to the desired location and type /arenasetpos");
                player.closeInventory();
            }
        }
    }
}
