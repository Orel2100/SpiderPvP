package gameplay;

import duel.Duel;
import duel.DuelManager;
import kitpvp.kitpvp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ArenaBlockListener implements Listener {

    private final Main plugin;

    public ArenaBlockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        DuelManager duelManager = plugin.getDuelManager();
        Duel duel = duelManager.getDuel(player);
        if (duel != null) {
            // ArenaRegenManager not implemented, commenting out for now.
            // ArenaRegenManager.getInstance().addChangedBlock(duel.getArenaName(), event.getBlock());
        }
    }
}
