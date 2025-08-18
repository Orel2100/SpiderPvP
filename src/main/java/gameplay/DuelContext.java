package gameplay;

import org.bukkit.entity.Player;

public class DuelContext {
    private final Player player;
    private final String arenaName;

    public DuelContext(Player player, String arenaName) {
        this.player = player;
        this.arenaName = arenaName;
    }

    public Player getPlayer() {
        return player;
    }

    public String getArenaName() {
        return arenaName;
    }
}
