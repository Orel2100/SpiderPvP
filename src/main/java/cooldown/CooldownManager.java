package cooldown;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public void setCooldown(Player player, String ability, long seconds) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            cooldowns.put(player.getUniqueId(), new HashMap<>());
        }
        cooldowns.get(player.getUniqueId()).put(ability, System.currentTimeMillis() + (seconds * 1000));
    }

    public boolean hasCooldown(Player player, String ability) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            if (cooldowns.get(player.getUniqueId()).containsKey(ability)) {
                return cooldowns.get(player.getUniqueId()).get(ability) > System.currentTimeMillis();
            }
        }
        return false;
    }

    public long getCooldown(Player player, String ability) {
        if (hasCooldown(player, ability)) {
            return (cooldowns.get(player.getUniqueId()).get(ability) - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    public Map<String, Long> getCooldowns(Player player) {
        return cooldowns.get(player.getUniqueId());
    }
}
