package cooldown;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import kitpvp.kitpvp.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import java.util.Map;

public class CooldownUpdater extends BukkitRunnable {

    private final Main plugin;

    public CooldownUpdater(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, Long> cooldowns = plugin.getCooldownManager().getCooldowns(player);
            if (cooldowns != null && !cooldowns.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
                    long timeLeft = (entry.getValue() - System.currentTimeMillis()) / 1000;
                    if (timeLeft > 0) {
                        sb.append(ChatColor.BOLD).append(entry.getKey()).append(": ").append(timeLeft).append("s ");
                    }
                }
                if (sb.length() > 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(sb.toString()));
                }
            }
        }
    }
}
