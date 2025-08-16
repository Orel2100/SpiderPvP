package moderation;

import org.bukkit.entity.Player;

public class PunishmentContext {
    private final Player target;
    private final String reason;

    public PunishmentContext(Player target, String reason) {
        this.target = target;
        this.reason = reason;
    }

    public Player getTarget() {
        return target;
    }

    public String getReason() {
        return reason;
    }
}
