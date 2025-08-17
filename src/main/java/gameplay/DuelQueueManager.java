package gameplay;

import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.Queue;
import kitpvp.kitpvp.Main;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelQueueManager {

    private final Main plugin;
    private final Queue<Player> queue = new LinkedList<>();
    private final Map<UUID, String> selectedKits = new HashMap<>();

    public DuelQueueManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player, String kitName) {
        queue.add(player);
        selectedKits.put(player.getUniqueId(), kitName);
        player.sendMessage("You have been added to the duel queue with the " + kitName + " kit.");
        checkQueue();
    }

    public void removePlayer(Player player) {
        queue.remove(player);
        selectedKits.remove(player.getUniqueId());
    }

    private void checkQueue() {
        if (queue.size() >= 2) {
            Player player1 = queue.poll();
            Player player2 = queue.poll();
            String kit1 = selectedKits.get(player1.getUniqueId());
            String kit2 = selectedKits.get(player2.getUniqueId());
            // For now, both players use the kit of the first player.
            // I can add a more advanced system later to let each player use their own kit.
            DuelManager.getInstance(plugin).startDuel(player1, player2, kit1);
            selectedKits.remove(player1.getUniqueId());
            selectedKits.remove(player2.getUniqueId());
        }
    }
}
