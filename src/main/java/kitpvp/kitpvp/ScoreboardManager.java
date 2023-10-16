package kitpvp.kitpvp;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private final Main plugin;

    private final HashMap<UUID, Integer> playerKills = new HashMap<>();

    private final HashMap<UUID, Integer> playerDeaths = new HashMap<>();

    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("kitpvp", "dummy", ChatColor.RED + "SpiderPvP Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int coins = this.plugin.economyManager.getCoins(player);
        int kills = ((Integer)this.playerKills.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue();
        int deaths = ((Integer)this.playerDeaths.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue();
        float kd = (deaths == 0) ? kills : (kills / deaths);
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
        this.playerKills.put(player.getUniqueId(), Integer.valueOf(((Integer)this.playerKills.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue() + 1));
        updateScoreboard(player);
    }

    public void addDeath(Player player) {
        this.playerDeaths.put(player.getUniqueId(), Integer.valueOf(((Integer)this.playerDeaths.getOrDefault(player.getUniqueId(), Integer.valueOf(0))).intValue() + 1));
        updateScoreboard(player);
    }
}
