package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DuelGUIListener implements Listener {

    private final Main plugin;
    private final DuelManager duelManager;
    private final DuelQueueManager duelQueueManager;

    // A map to keep track of open GUIs to handle pagination
    private final java.util.Map<UUID, PlayerSelectionGUI> playerSelectionGUIs = new java.util.HashMap<>();

    public DuelGUIListener(Main plugin) {
        this.plugin = plugin;
        this.duelManager = plugin.getDuelManager();
        this.duelQueueManager = plugin.getDuelQueueManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (title.equals("Select Duel Mode")) {
            event.setCancelled(true);
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            if (itemName.equals("Challenge a Player")) {
                PlayerSelectionGUI gui = new PlayerSelectionGUI(player);
                playerSelectionGUIs.put(player.getUniqueId(), gui);
                gui.open();
            } else if (itemName.equals("Random Matchmaking")) {
                new DuelQueueGUI().open(player);
            }
        } else if (title.startsWith("Challenge a Player")) {
            event.setCancelled(true);
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            PlayerSelectionGUI gui = playerSelectionGUIs.get(player.getUniqueId());
            if (gui == null) return;

            if (itemName.equals("Next Page")) {
                gui.nextPage();
            } else if (itemName.equals("Previous Page")) {
                gui.prevPage();
            } else if (itemName.equals("Close")) {
                player.closeInventory();
                playerSelectionGUIs.remove(player.getUniqueId());
            } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
                String uuidLine = clickedItem.getItemMeta().getLore().get(1); // "player:uuid-string"
                UUID targetUUID = UUID.fromString(ChatColor.stripColor(uuidLine).split(":")[1]);
                Player target = plugin.getServer().getPlayer(targetUUID);
                if (target != null) {
                    duelManager.sendDuelRequest(player, target);
                    player.closeInventory();
                }
            }
        } else if (title.equals("Join the Duel Queue")) {
            event.setCancelled(true);
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            if (itemName.equals("Join Queue")) {
                duelQueueManager.addPlayerToQueue(player);
                player.closeInventory();
            } else if (itemName.equals("Leave Queue")) {
                duelQueueManager.removePlayerFromQueue(player);
                player.closeInventory();
            }
        } else if (title.equals("Select a Kit")) {
            event.setCancelled(true);
            if (clickedItem.getItemMeta() == null || clickedItem.getItemMeta().getLore() == null) return;

            String kitName = null;
            for (String lore : clickedItem.getItemMeta().getLore()) {
                String strippedLore = ChatColor.stripColor(lore);
                if (strippedLore.startsWith("kit:")) {
                    kitName = strippedLore.split(":")[1];
                    break;
                }
            }

            if (kitName == null) return;

            // Check for premium kit ownership
            if (plugin.getPremiumKitManager().getPremiumKits(player).containsKey(kitName)) {
                if (!plugin.getPremiumKitManager().hasKit(player, kitName)) {
                    player.sendMessage(ChatColor.RED + "You do not own this kit!");
                    return;
                }
            }

            duelManager.setPlayerKit(player, kitName);
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "You have selected the " + kitName + " kit.");
        }
    }
}
