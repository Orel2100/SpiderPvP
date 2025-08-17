package gameplay;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import kitpvp.kitpvp.Main;

public class ArenaSetupListener implements Listener {

    private final Main plugin;

    public ArenaSetupListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ArenaSetupWizard wizard = ArenaSetupWizard.getWizard(player);
        if (wizard == null) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Arena Setup Tool")) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock() == null) {
            player.sendMessage(ChatColor.RED + "You must right-click a block.");
            return;
        }

        wizard.nextStep(event.getClickedBlock().getLocation());
    }
}
