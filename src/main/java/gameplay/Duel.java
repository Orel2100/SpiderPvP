package gameplay;

import org.bukkit.entity.Player;

public class Duel {
    private final Player player1;
    private final Player player2;
    private final String arenaName;

    public Duel(Player player1, Player player2, String arenaName) {
        this.player1 = player1;
        this.player2 = player2;
        this.arenaName = arenaName;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public String getArenaName() {
        return arenaName;
    }
}
