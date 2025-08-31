package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyItemListener implements Listener {

    private final Main plugin;

    public LobbyItemListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta() || item.getItemMeta().getDisplayName() == null) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();

        if (displayName.contains("PLAY!")) {
            event.setCancelled(true);
            player.performCommand("arena");
        } else if (displayName.contains("Game Menu")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Coming soon...");
        } else if (displayName.contains("My Profile")) {
            event.setCancelled(true);
            plugin.getProfileGUI().open(player);
        }
    }
}
