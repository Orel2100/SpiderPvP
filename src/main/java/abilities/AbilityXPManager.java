package abilities;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityXPManager {

    private final Map<UUID, Integer> abilityXP = new HashMap<>();
    private static final int MAX_XP = 100; // The amount of XP needed to use an ability

    public int getXP(Player player) {
        return abilityXP.getOrDefault(player.getUniqueId(), 0);
    }

    public void setXP(Player player, int amount) {
        if (amount < 0) amount = 0;
        if (amount > MAX_XP) amount = MAX_XP;
        abilityXP.put(player.getUniqueId(), amount);
    }

    public void addXP(Player player, int amount) {
        int currentXP = getXP(player);
        setXP(player, currentXP + amount);
    }

    public boolean isReady(Player player) {
        return getXP(player) >= MAX_XP;
    }

    public int getMaxXP() {
        return MAX_XP;
    }

    public void clearXP(Player player) {
        abilityXP.remove(player.getUniqueId());
    }
}
