package gameplay;

import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.Queue;
import kitpvp.kitpvp.Main;
import org.bukkit.ChatColor;

public class DuelQueueManager {

    private final Main plugin;
    private final Queue<Player> queue = new LinkedList<>();

    public DuelQueueManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToQueue(Player player) {
        if (queue.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already in the queue.");
            return;
        }
        queue.add(player);
        player.sendMessage(ChatColor.GREEN + "You have joined the duel queue.");
        checkForMatch();
    }

    public void removePlayerFromQueue(Player player) {
        if (!queue.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are not in the queue.");
            return;
        }
        queue.remove(player);
        player.sendMessage(ChatColor.GREEN + "You have left the duel queue.");
    }

    private void checkForMatch() {
        if (queue.size() >= 2) {
            Player player1 = queue.poll();
            Player player2 = queue.poll();

            if (player1 == null || !player1.isOnline() || player2 == null || !player2.isOnline()) {
                // One of the players logged off, requeue the other if they are online
                if (player1 != null && player1.isOnline()) queue.add(player1);
                if (player2 != null && player2.isOnline()) queue.add(player2);
                return;
            }

            player1.sendMessage(ChatColor.GREEN + "Match found! You will be dueling against " + player2.getName());
            player2.sendMessage(ChatColor.GREEN + "Match found! You will be dueling against " + player1.getName());

            // Defaulting to "Warrior" kit for now as per the new simplified flow.
            // The full kit selection will happen right before the duel starts.
            plugin.getDuelManager().startDuel(player1, player2, "Warrior");
        }
    }

    public boolean isInQueue(Player player) {
        return queue.contains(player);
    }
}
