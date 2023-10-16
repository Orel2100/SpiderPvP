package kitpvp.kitpvp;

import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyManager implements Listener {
    private final HashMap<UUID, Integer> playerCoins = new HashMap<>();

    private final JavaPlugin plugin;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, (Plugin)plugin);
    }

    public int getCoins(Player player) {
        return ((Integer)this.playerCoins.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue();
    }

    public void addCoins(Player player, int amount) {
        this.playerCoins.put(player.getUniqueId(), Integer.valueOf(getCoins(player) + amount));
        saveCoins(player);
    }

    public void removeCoins(Player player, int amount) {
        this.playerCoins.put(player.getUniqueId(), Integer.valueOf(getCoins(player) - amount));
        saveCoins(player);
    }

    public void saveCoins(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        config.set("coins." + player.getUniqueId().toString(), Integer.valueOf(getCoins(player)));
        this.plugin.saveConfig();
    }

    public void loadCoins(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        int coins = config.getInt("coins." + player.getUniqueId().toString(), 0);
        this.playerCoins.put(player.getUniqueId(), Integer.valueOf(coins));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadCoins(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveCoins(event.getPlayer());
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().equals(this.plugin))
            for (Player player : this.plugin.getServer().getOnlinePlayers())
                loadCoins(player);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            if (event.getEntity() instanceof Player) {
                addCoins(killer, 10);
                killer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You've earned 10 coins for the kill"));
            }
        }
    }
}
