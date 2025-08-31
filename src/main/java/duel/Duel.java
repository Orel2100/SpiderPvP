package duel;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Duel {

    private final UUID player1;
    private final UUID player2;
    private final String arenaName;
    private final long startTime;

    public Duel(Player player1, Player player2, String arenaName) {
        this.player1 = player1.getUniqueId();
        this.player2 = player2.getUniqueId();
        this.arenaName = arenaName;
        this.startTime = System.currentTimeMillis();
    }

    public UUID getPlayer1() {
        return player1;
    }

    public UUID getPlayer2() {
        return player2;
    }

    public String getArenaName() {
        return arenaName;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean containsPlayer(Player player) {
        return player.getUniqueId().equals(player1) || player.getUniqueId().equals(player2);
    }

    public UUID getOpponent(Player player) {
        if (player.getUniqueId().equals(player1)) {
            return player2;
        } else if (player.getUniqueId().equals(player2)) {
            return player1;
        }
        return null;
    }
}
