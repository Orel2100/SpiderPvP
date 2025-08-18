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
import org.bukkit.scheduler.BukkitRunnable;
import kitpvp.kitpvp.Main;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.GameMode;
import economy.EloManager;
import java.util.Random;
import kitpvp.kitpvp.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DuelManager implements Listener {

    private static DuelManager instance;
    private final Main plugin;
    private final Map<UUID, Duel> activeDuels = new HashMap<>();
    private final Map<UUID, UUID> duelRequests = new HashMap<>(); // Challenged -> Challenger
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Map<UUID, String> duelSelectedKits = new HashMap<>();

    /**
     * Manages the 1v1 dueling system.
     * This class is a singleton.
     */
    private DuelManager(Main plugin) {
        this.plugin = plugin;
    }

    public static DuelManager getInstance(Main plugin) {
        if (instance == null) {
            instance = new DuelManager(plugin);
        }
        return instance;
    }

    public void startDuel(Player player1, Player player2, String kitName) {
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
            if (plugin.getArenaManager().getArenaStatus(arenaName) == ArenaStatus.AVAILABLE) {
                availableArena = arenaName;
                break;
            }
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

        Duel duel = new Duel(player1, player2, availableArena);
        activeDuels.put(player1.getUniqueId(), duel);
        activeDuels.put(player2.getUniqueId(), duel);

        plugin.getArenaManager().setArenaStatus(availableArena, ArenaStatus.COUNTDOWN);

        new KitSelectionGUI(plugin, player1).open();
        new KitSelectionGUI(plugin, player2).open();
    }

    public Duel getDuel(Player player) {
        return activeDuels.get(player.getUniqueId());
    }

    public void setPlayerKit(Player player, String kitName) {
        duelSelectedKits.put(player.getUniqueId(), kitName);
        Duel duel = getDuel(player);
        if (duel != null) {
            Player otherPlayer = duel.getPlayer1() == player ? duel.getPlayer2() : duel.getPlayer1();
            if (duelSelectedKits.containsKey(otherPlayer.getUniqueId())) {
                startCountdown(duel);
            }
        }
    }

    public void endDuel(Duel duel) {
        activeDuels.remove(duel.getPlayer1().getUniqueId());
        activeDuels.remove(duel.getPlayer2().getUniqueId());
        plugin.getArenaManager().setArenaStatus(duel.getArenaName(), ArenaStatus.REGENERATING);
        ArenaRegenManager.getInstance().restoreArena(duel.getArenaName());
        plugin.getArenaManager().setArenaStatus(duel.getArenaName(), ArenaStatus.AVAILABLE);
    }

    private void startCountdown(Duel duel) {
        Player player1 = duel.getPlayer1();
        Player player2 = duel.getPlayer2();
        String arenaName = duel.getArenaName();

        plugin.getKitManager().giveKit(player1, duelSelectedKits.get(player1.getUniqueId()));
        plugin.getKitManager().giveKit(player2, duelSelectedKits.get(player2.getUniqueId()));

        // Give blocks
        ItemStack blocks = new ItemStack(Material.WHITE_WOOL, 64);
        player1.getInventory().addItem(blocks);
        player2.getInventory().addItem(blocks);

        frozenPlayers.add(player1.getUniqueId());
        frozenPlayers.add(player2.getUniqueId());

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    player1.sendMessage(ChatColor.GREEN + "Duel starting in " + countdown + "...");
                    player2.sendMessage(ChatColor.GREEN + "Duel starting in " + countdown + "...");
                    countdown--;
                } else {
                    player1.sendMessage(ChatColor.GREEN + "Duel started!");
                    player2.sendMessage(ChatColor.GREEN + "Duel started!");
                    plugin.getArenaManager().setArenaStatus(arenaName, ArenaStatus.FIGHTING);
                    frozenPlayers.remove(player1.getUniqueId());
                    frozenPlayers.remove(player2.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public Duel getDuelByArenaName(String arenaName) {
        for (Duel duel : activeDuels.values()) {
            if (duel.getArenaName().equals(arenaName)) {
                return duel;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        Duel duel = getDuel(deceased);
        if (duel != null) {
            Player winner = duel.getPlayer1() == deceased ? duel.getPlayer2() : duel.getPlayer1();
            EloManager eloManager = plugin.getEloManager();
            int eloChange = new Random().nextInt(3) + 23; // 23, 24, or 25
            int winnerElo = eloManager.getElo(winner) + eloChange;
            int loserElo = eloManager.getElo(deceased) - eloChange;
            eloManager.setElo(winner, winnerElo);
            eloManager.setElo(deceased, loserElo);

            winner.sendMessage(ChatColor.GREEN + "You won the duel against " + deceased.getName() + "! (+" + eloChange + " Elo)");
            deceased.sendMessage(ChatColor.RED + "You lost the duel against " + winner.getName() + ". (-" + eloChange + " Elo)");
            endDuel(duel);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setTo(event.getFrom());
        }
    }

    public void sendDuelRequest(Player challenger, Player challenged) {
        if (duelRequests.containsKey(challenged.getUniqueId())) {
            challenger.sendMessage(ChatColor.RED + "That player already has a pending duel request.");
            return;
        }

        duelRequests.put(challenged.getUniqueId(), challenger.getUniqueId());
        challenger.sendMessage(ChatColor.GREEN + "You have challenged " + challenged.getName() + " to a duel.");

        TextComponent message = new TextComponent(challenger.getName() + " has challenged you to a duel. ");
        TextComponent accept = new TextComponent("[ACCEPT]");
        accept.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept the duel!").create()));

        TextComponent deny = new TextComponent("[DENY]");
        deny.setColor(net.md_5.bungee.api.ChatColor.RED);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel deny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to deny the duel!").create()));

        message.addExtra(accept);
        message.addExtra(" ");
        message.addExtra(deny);

        challenged.spigot().sendMessage(message);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (duelRequests.containsKey(challenged.getUniqueId()) && duelRequests.get(challenged.getUniqueId()).equals(challenger.getUniqueId())) {
                    duelRequests.remove(challenged.getUniqueId());
                    challenger.sendMessage(ChatColor.RED + "Your duel request to " + challenged.getName() + " has expired.");
                    challenged.sendMessage(ChatColor.RED + "Your duel request from " + challenger.getName() + " has expired.");
                }
            }
        }.runTaskLater(plugin, 1200L); // 60 seconds
    }

    public void acceptDuelRequest(Player challenged) {
        if (!duelRequests.containsKey(challenged.getUniqueId())) {
            challenged.sendMessage(ChatColor.RED + "You don't have any pending duel requests.");
            return;
        }

        UUID challengerUUID = duelRequests.get(challenged.getUniqueId());
        Player challenger = Bukkit.getPlayer(challengerUUID);

        if (challenger == null || !challenger.isOnline()) {
            challenged.sendMessage(ChatColor.RED + "The player who challenged you is no longer online.");
            duelRequests.remove(challenged.getUniqueId());
            return;
        }

        duelRequests.remove(challenged.getUniqueId());

        // For now, we'll just start a duel with a default kit. Kit selection will be added later.
        startDuel(challenger, challenged, "Warrior");
    }

    public void denyDuelRequest(Player challenged) {
        if (!duelRequests.containsKey(challenged.getUniqueId())) {
            challenged.sendMessage(ChatColor.RED + "You don't have any pending duel requests.");
            return;
        }

        UUID challengerUUID = duelRequests.get(challenged.getUniqueId());
        Player challenger = Bukkit.getPlayer(challengerUUID);

        if (challenger != null && challenger.isOnline()) {
            challenger.sendMessage(ChatColor.RED + challenged.getName() + " has denied your duel request.");
        }

        challenged.sendMessage(ChatColor.GREEN + "You have denied the duel request.");
        duelRequests.remove(challenged.getUniqueId());
    }
}
