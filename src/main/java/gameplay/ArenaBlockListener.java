package gameplay;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import kitpvp.kitpvp.Main;

public class ArenaBlockListener implements Listener {

    private final Main plugin;

    public ArenaBlockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Duel duel = DuelManager.getInstance(plugin).getDuel(player);
        if (duel != null) {
            ArenaRegenManager.getInstance().addChangedBlock(duel.getArenaName(), event.getBlock());
        }
    }
}
