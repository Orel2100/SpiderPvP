package gameplay;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import kitpvp.kitpvp.Main;
import gameplay.KitSelectionGUI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;

public class DuelGUIListener implements Listener {

    private final Main plugin;

    public DuelGUIListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("1v1 Arenas")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String arenaName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (event.isRightClick()) {
                if (plugin.getArenaManager().getArenaStatus(arenaName) == ArenaStatus.FIGHTING) {
                    Location specSpawn = plugin.getArenaManager().getSpectatorSpawn(arenaName);
                    if (specSpawn != null) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(specSpawn);
                        DuelManager.getInstance(plugin).addSpectator(player, arenaName);
                        player.sendMessage(ChatColor.GREEN + "You are now spectating.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Spectator spawn not set for this arena.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "This arena is not in a duel right now.");
                }
                player.closeInventory();
            } else {
                DuelGUIManager.getInstance().setContext(player.getUniqueId(), new DuelContext(player, arenaName));
                new KitSelectionGUI(plugin, player).open();
            }
        } else if (event.getView().getTitle().equals("Select a Kit")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            DuelContext context = DuelGUIManager.getInstance().getContext(player.getUniqueId());
            if (context == null) return;
            String arenaName = context.getArenaName();

            boolean isPremium = plugin.getPremiumKitManager().getPremiumKits(player).containsKey(kitName);

            if (isPremium) {
                if (plugin.getPremiumKitManager().doesPlayerOwnKit(player, kitName)) {
                    plugin.getDuelQueueManager().addPlayer(player, kitName, arenaName);
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.RED + "You don't own this kit!");
                }
            } else {
                plugin.getDuelQueueManager().addPlayer(player, kitName, arenaName);
                player.closeInventory();
            }
            DuelGUIManager.getInstance().removeContext(player.getUniqueId());
        } else if (event.getView().getTitle().equals("Select Duel Mode")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (itemName.equalsIgnoreCase("Join Random Queue")) {
                plugin.getDuelQueueManager().addPlayer(player, "default", "random");
                player.closeInventory();
            } else if (itemName.equalsIgnoreCase("View Arenas")) {
                new DuelQueueGUI(plugin, player).open();
            }
        }
    }
}
