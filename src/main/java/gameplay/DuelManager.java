package gameplay;

import org.bukkit.entity.Player;
import kitpvp.kitpvp.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.ChatColor;

public class DuelManager implements Listener {

    private static DuelManager instance;
    private final Main plugin;
    private final Map<UUID, Duel> activeDuels = new HashMap<>();

    private DuelManager(Main plugin) {
        this.plugin = plugin;
    }

    public static DuelManager getInstance(Main plugin) {
        if (instance == null) {
            instance = new DuelManager(plugin);
        }
        return instance;
    }

    public void startDuel(Player player1, Player player2) {
        // Find an available arena
        ArenaManager arenaManager = plugin.getArenaManager();
        ConfigurationSection arenas = arenaManager.getConfig().getConfigurationSection("arenas");
        if (arenas == null) {
            player1.sendMessage("No arenas available.");
            player2.sendMessage("No arenas available.");
            return;
        }

        String availableArena = null;
        for (String arenaName : arenas.getKeys(false)) {
            // For now, just take the first arena. I'll add a status system later.
            availableArena = arenaName;
            break;
        }

        if (availableArena == null) {
            player1.sendMessage("No arenas available.");
            player2.sendMessage("No arenas available.");
            return;
        }

        ConfigurationSection arena = arenaManager.getConfig().getConfigurationSection("arenas." + availableArena);
        Location spawn1 = new Location(
            Bukkit.getWorld(arena.getString("spawn1.world")),
            arena.getDouble("spawn1.x"),
            arena.getDouble("spawn1.y"),
            arena.getDouble("spawn1.z")
        );
        Location spawn2 = new Location(
            Bukkit.getWorld(arena.getString("spawn2.world")),
            arena.getDouble("spawn2.x"),
            arena.getDouble("spawn2.y"),
            arena.getDouble("spawn2.z")
        );

        player1.teleport(spawn1);
        player2.teleport(spawn2);

        player1.getInventory().clear();
        player2.getInventory().clear();

        // For now, let's assume players will use the kit selector.
        // I will add a proper kit selection GUI for duels later.
        plugin.getKitManager().giveKitSelectorToSlot(player1, 0);
        plugin.getKitManager().giveKitSelectorToSlot(player2, 0);

        // Give blocks
        ItemStack blocks = new ItemStack(Material.WHITE_WOOL, 64);
        player1.getInventory().addItem(blocks);
        player2.getInventory().addItem(blocks);

        Duel duel = new Duel(player1, player2, availableArena);
        activeDuels.put(player1.getUniqueId(), duel);
        activeDuels.put(player2.getUniqueId(), duel);

        plugin.getArenaManager().setArenaStatus(availableArena, "in-use");
        ArenaRegenManager.getInstance().startTracking(availableArena);

        player1.sendMessage("Duel starting against " + player2.getName());
        player2.sendMessage("Duel starting against " + player1.getName());
    }

    public Duel getDuel(Player player) {
        return activeDuels.get(player.getUniqueId());
    }

    public void endDuel(Duel duel) {
        activeDuels.remove(duel.getPlayer1().getUniqueId());
        activeDuels.remove(duel.getPlayer2().getUniqueId());
        plugin.getArenaManager().setArenaStatus(duel.getArenaName(), "available");
        ArenaRegenManager.getInstance().restoreArena(duel.getArenaName());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        Duel duel = getDuel(deceased);
        if (duel != null) {
            Player winner = duel.getPlayer1() == deceased ? duel.getPlayer2() : duel.getPlayer1();
            winner.sendMessage(ChatColor.GREEN + "You won the duel against " + deceased.getName() + "!");
            deceased.sendMessage(ChatColor.RED + "You lost the duel against " + winner.getName() + ".");
            endDuel(duel);
        }
    }
}
