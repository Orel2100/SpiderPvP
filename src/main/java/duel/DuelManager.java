package duel;

import gameplay.ArenaManager;
import gameplay.ArenaStatus;
import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DuelManager {

    private final Main plugin;
    private final Map<UUID, Duel> activeDuels = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> duelRequests = new ConcurrentHashMap<>(); // Challenged -> Challenger

    public DuelManager(Main plugin) {
        this.plugin = plugin;
    }

    public void sendDuelRequest(Player challenger, Player target) {
        if (duelRequests.containsKey(target.getUniqueId())) {
            challenger.sendMessage(ChatColor.RED + "That player already has a pending duel request.");
            return;
        }
        if (activeDuels.values().stream().anyMatch(d -> d.containsPlayer(challenger) || d.containsPlayer(target))) {
            challenger.sendMessage(ChatColor.RED + "Either you or the target player is already in a duel.");
            return;
        }

        duelRequests.put(target.getUniqueId(), challenger.getUniqueId());
        challenger.sendMessage(ChatColor.GREEN + "You have sent a duel request to " + target.getName() + ".");
        target.sendMessage(ChatColor.GREEN + challenger.getName() + " has challenged you to a 1v1 duel!");
        target.sendMessage(ChatColor.GREEN + "Type " + ChatColor.YELLOW + "/duel accept" + ChatColor.GREEN + " or " + ChatColor.YELLOW + "/duel deny" + ChatColor.GREEN + ".");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (duelRequests.get(target.getUniqueId()) == challenger.getUniqueId()) {
                    duelRequests.remove(target.getUniqueId());
                    challenger.sendMessage(ChatColor.RED + "Your duel request to " + target.getName() + " has expired.");
                    target.sendMessage(ChatColor.RED + "The duel request from " + challenger.getName() + " has expired.");
                }
            }
        }.runTaskLater(plugin, 1200L); // 60 seconds
    }

    public void acceptDuelRequest(Player target) {
        if (!duelRequests.containsKey(target.getUniqueId())) {
            target.sendMessage(ChatColor.RED + "You do not have any pending duel requests.");
            return;
        }
        UUID challengerUUID = duelRequests.remove(target.getUniqueId());
        Player challenger = Bukkit.getPlayer(challengerUUID);
        if (challenger == null || !challenger.isOnline()) {
            target.sendMessage(ChatColor.RED + "The player who challenged you is no longer online.");
            return;
        }
        startDuel(challenger, target);
    }

    public void denyDuelRequest(Player target) {
        if (!duelRequests.containsKey(target.getUniqueId())) {
            target.sendMessage(ChatColor.RED + "You do not have any pending duel requests.");
            return;
        }
        UUID challengerUUID = duelRequests.remove(target.getUniqueId());
        Player challenger = Bukkit.getPlayer(challengerUUID);
        if (challenger != null) {
            challenger.sendMessage(ChatColor.RED + target.getName() + " has denied your duel request.");
        }
        target.sendMessage(ChatColor.GREEN + "You have denied the duel request.");
    }

    public void startDuel(Player player1, Player player2) {
        ArenaManager arenaManager = plugin.getArenaManager();
        Optional<String> availableArena = arenaManager.findAvailableArena();

        if (!availableArena.isPresent()) {
            player1.sendMessage(ChatColor.RED + "There are no available arenas for a duel right now. Please wait.");
            player2.sendMessage(ChatColor.RED + "There are no available arenas for a duel right now. Please wait.");
            return;
        }
        String arenaName = availableArena.get();
        arenaManager.setArenaStatus(arenaName, ArenaStatus.FIGHTING);

        Location spawn1 = arenaManager.getSpawn1(arenaName);
        Location spawn2 = arenaManager.getSpawn2(arenaName);

        if (spawn1 == null || spawn2 == null) {
             player1.sendMessage(ChatColor.RED + "The arena is not configured correctly. Please contact an admin.");
             player2.sendMessage(ChatColor.RED + "The arena is not configured correctly. Please contact an admin.");
             arenaManager.setArenaStatus(arenaName, ArenaStatus.AVAILABLE);
             return;
        }

        player1.teleport(spawn1);
        player2.teleport(spawn2);

        // Equip global kits
        equipGlobalKit(player1);
        equipGlobalKit(player2);

        Duel duel = new Duel(player1, player2, arenaName);
        activeDuels.put(player1.getUniqueId(), duel);
        activeDuels.put(player2.getUniqueId(), duel);

        player1.sendMessage(ChatColor.GREEN + "Duel against " + player2.getName() + " has started!");
        player2.sendMessage(ChatColor.GREEN + "Duel against " + player1.getName() + " has started!");
    }

    private void equipGlobalKit(Player player) {
        String kitName = plugin.getGlobalKitManager().getKit(player);
        if (kitName.equals("None")) {
            player.getInventory().clear();
            // Give default items if no kit is selected
            player.getInventory().addItem(new org.bukkit.inventory.ItemStack(org.bukkit.Material.IRON_SWORD));
        } else {
             boolean isPremium = plugin.getPremiumKitManager().getPremiumKits(player).containsKey(kitName);
             if (isPremium) {
                 plugin.getPremiumKitManager().giveKit(player, kitName);
             } else {
                 plugin.getKitManager().giveKit(player, kitName);
             }
        }
    }

    public void endDuel(Player loser, Player winner) {
        Duel duel = activeDuels.get(loser.getUniqueId());
        if (duel == null) return;

        activeDuels.remove(loser.getUniqueId());
        activeDuels.remove(winner.getUniqueId());

        plugin.getArenaManager().setArenaStatus(duel.getArenaName(), ArenaStatus.AVAILABLE);

        // Optional: Teleport players back to spawn
        loser.teleport(loser.getWorld().getSpawnLocation());
        winner.teleport(winner.getWorld().getSpawnLocation());

        winner.sendMessage(ChatColor.GREEN + "You won the duel against " + loser.getName() + "!");
        loser.sendMessage(ChatColor.RED + "You lost the duel against " + winner.getName() + ".");
    }

    public Duel getDuel(Player player) {
        return activeDuels.get(player.getUniqueId());
    }
}
