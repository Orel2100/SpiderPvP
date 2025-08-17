package gameplay;

import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.Queue;
import kitpvp.kitpvp.Main;

public class DuelQueueManager {

    private final Main plugin;
    private final Queue<Player> queue = new LinkedList<>();

    public DuelQueueManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        queue.add(player);
        player.sendMessage("You have been added to the duel queue.");
        checkQueue();
    }

    public void removePlayer(Player player) {
        queue.remove(player);
    }

    private void checkQueue() {
        if (queue.size() >= 2) {
            Player player1 = queue.poll();
            Player player2 = queue.poll();
            DuelManager.getInstance(plugin).startDuel(player1, player2);
        }
    }
}
