package kitpvp.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.UUID;

public class ScoreboardManager {
    private final Main plugin;
    private final HashMap<UUID, Integer> playerKills = new HashMap<>();
    private final HashMap<UUID, Integer> playerDeaths = new HashMap<>();

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("kitpvp", "dummy", ChatColor.RED + "SpiderPvP Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int coins = this.plugin.economyManager.getCoins(player);
        int kills = this.playerKills.getOrDefault(player.getUniqueId(), 0);
        int deaths = this.playerDeaths.getOrDefault(player.getUniqueId(), 0);
        float kd = (deaths == 0) ? kills : (float) kills / deaths;

        Score coinsScore = objective.getScore(ChatColor.GRAY + "Coins: " + coins);
        coinsScore.setScore(3);
        Score killsScore = objective.getScore(ChatColor.GRAY + "Kills: " + kills);
        killsScore.setScore(2);
        Score kdScore = objective.getScore(ChatColor.GRAY + "K/D Ratio: " + kd);
        kdScore.setScore(1);
        Score onlineScore = objective.getScore(ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());
        onlineScore.setScore(0);

        player.setScoreboard(scoreboard);
    }

    public void addKill(Player player) {
        this.playerKills.put(player.getUniqueId(), this.playerKills.getOrDefault(player.getUniqueId(), 0) + 1);
        updateScoreboard(player);
    }

    public void addDeath(Player player) {
        this.playerDeaths.put(player.getUniqueId(), this.playerDeaths.getOrDefault(player.getUniqueId(), 0) + 1);
        updateScoreboard(player);
    }

    public void saveData() {
        for (UUID uuid : playerKills.keySet()) {
            plugin.getConfig().set("kills." + uuid.toString(), playerKills.get(uuid));
        }
        for (UUID uuid : playerDeaths.keySet()) {
            plugin.getConfig().set("deaths." + uuid.toString(), playerDeaths.get(uuid));
        }
        plugin.saveConfig();
    }

    public void loadData() {
        if (plugin.getConfig().contains("kills")) {
            for (String uuidString : plugin.getConfig().getConfigurationSection("kills").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                playerKills.put(uuid, plugin.getConfig().getInt("kills." + uuidString));
            }
        }
        if (plugin.getConfig().contains("deaths")) {
            for (String uuidString : plugin.getConfig().getConfigurationSection("deaths").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                playerDeaths.put(uuid, plugin.getConfig().getInt("deaths." + uuidString));
            }
        }
    }
}
