package gameplay;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import kitpvp.kitpvp.Main;

public class DuelRequestManager {

    private static DuelRequestManager instance;
    private final Map<UUID, UUID> requests = new HashMap<>();
    private final Map<UUID, BukkitRunnable> requestTimeouts = new HashMap<>();
    private final Main plugin;

    private DuelRequestManager(Main plugin) {
        this.plugin = plugin;
    }

    public static DuelRequestManager getInstance(Main plugin) {
        if (instance == null) {
            instance = new DuelRequestManager(plugin);
        }
        return instance;
    }

    public void sendRequest(Player challenger, Player challenged) {
        requests.put(challenged.getUniqueId(), challenger.getUniqueId());
        challenger.sendMessage(ChatColor.GREEN + "Duel request sent to " + challenged.getName());
        challenged.sendMessage(ChatColor.GREEN + "You have a duel request from " + challenger.getName() + ". Type /duel accept or /duel deny.");

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (requests.containsKey(challenged.getUniqueId())) {
                    requests.remove(challenged.getUniqueId());
                    challenger.sendMessage(ChatColor.RED + "Your duel request to " + challenged.getName() + " has expired.");
                    challenged.sendMessage(ChatColor.RED + "Your duel request from " + challenger.getName() + " has expired.");
                }
            }
        };
        task.runTaskLater(plugin, 600); // 30 seconds timeout
        requestTimeouts.put(challenged.getUniqueId(), task);
    }

    public void acceptRequest(Player challenged) {
        UUID challengerUUID = requests.get(challenged.getUniqueId());
        if (challengerUUID == null) {
            challenged.sendMessage(ChatColor.RED + "You don't have any pending duel requests.");
            return;
        }
        Player challenger = Bukkit.getPlayer(challengerUUID);
        if (challenger == null) {
            challenged.sendMessage(ChatColor.RED + "The player who challenged you is no longer online.");
            requests.remove(challenged.getUniqueId());
            return;
        }

        requestTimeouts.get(challenged.getUniqueId()).cancel();
        requests.remove(challenged.getUniqueId());

        // For now, start a duel with default kit
        DuelManager.getInstance(plugin).startDuel(challenger, challenged, "default");
    }

    public void denyRequest(Player challenged) {
        UUID challengerUUID = requests.get(challenged.getUniqueId());
        if (challengerUUID == null) {
            challenged.sendMessage(ChatColor.RED + "You don't have any pending duel requests.");
            return;
        }
        Player challenger = Bukkit.getPlayer(challengerUUID);
        if (challenger != null) {
            challenger.sendMessage(ChatColor.RED + challenged.getName() + " has denied your duel request.");
        }
        challenged.sendMessage(ChatColor.GREEN + "You have denied the duel request.");
        requestTimeouts.get(challenged.getUniqueId()).cancel();
        requests.remove(challenged.getUniqueId());
    }
}
