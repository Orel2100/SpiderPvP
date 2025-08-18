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
    private final Map<String, Queue<Player>> arenaQueues = new HashMap<>();
    private final Queue<Player> randomQueue = new LinkedList<>();
    private final Map<UUID, String> selectedKits = new HashMap<>();

    public DuelQueueManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player, String kitName, String arenaName) {
        if (arenaName.equalsIgnoreCase("random")) {
            randomQueue.add(player);
        } else {
            arenaQueues.computeIfAbsent(arenaName, k -> new LinkedList<>()).add(player);
        }
        selectedKits.put(player.getUniqueId(), kitName);
        player.sendMessage("You have been added to the duel queue.");
        checkQueues();
    }

    public void removePlayer(Player player) {
        randomQueue.remove(player);
        arenaQueues.values().forEach(q -> q.remove(player));
        selectedKits.remove(player.getUniqueId());
    }

    private void checkQueues() {
        // Check random queue
        if (randomQueue.size() >= 2) {
            Player player1 = randomQueue.poll();
            Player player2 = randomQueue.poll();
            // find any available arena
            String kit1 = selectedKits.get(player1.getUniqueId());
            DuelManager.getInstance(plugin).startDuel(player1, player2, kit1);
            selectedKits.remove(player1.getUniqueId());
            selectedKits.remove(player2.getUniqueId());
        }

        // Check per-arena queues
        for (Map.Entry<String, Queue<Player>> entry : arenaQueues.entrySet()) {
            Queue<Player> queue = entry.getValue();
            if (queue.size() >= 2) {
                Player player1 = queue.poll();
                Player player2 = queue.poll();
                String kit1 = selectedKits.get(player1.getUniqueId());
                DuelManager.getInstance(plugin).startDuel(player1, player2, kit1);
                selectedKits.remove(player1.getUniqueId());
                selectedKits.remove(player2.getUniqueId());
            }
        }
    }
}
