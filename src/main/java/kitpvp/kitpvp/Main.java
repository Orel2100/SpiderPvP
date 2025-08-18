package kitpvp.kitpvp;

import java.io.File;
import java.util.*;

import KitsManager.KitManager;
import KitsManager.PremiumKitManager;
import KitsManager.PremiumKitShop;
import abilities.*;
import economy.EconomyCommands;
import economy.EconomyManager;
import gameplay.ArenaCommand;
import gameplay.RandomChest;
import gameplay.SetArenaSpawnCommand;
import moderation.PunishmentManager;
import moderation.ReportGUIListener;
import moderation.PunishCommand;
import moderation.ReportManager;
import moderation.MessageManager;
import moderation.PunishmentListener;
import moderation.ReportsCommand;
import moderation.MyReportsCommand;
import moderation.PunishGUIListener;
import moderation.ReportCommand;
import moderation.UnbanCommand;
import gameplay.ArenaManager;
import gameplay.ArenaSetupListener;
import gameplay.DuelCommand;
import gameplay.ArenaBlockListener;
import gameplay.DuelQueueManager;
import gameplay.DuelManager;
import gameplay.DuelGUIListener;
import economy.EloManager;
import gameplay.GUIUpdater;
import gameplay.SpectateCommand;
import moderation.UnmuteCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import particles.ParticleEffectManager;
import particles.ParticleGUINPC;
import particles.Particlecommand;
import souprefillstation.RefillStationWizard;
import souprefillstation.SoupRefillStation;


public class Main extends JavaPlugin implements Listener {
    FileConfiguration config;

    private NPCEvents NPCEvents;

    private static Main instance;






    public EconomyManager economyManager;

    private PremiumKitManager premiumKitManager;

    private PremiumKitShop premiumKitShop;

    private KitManager kitManager;

    private Map<UUID, Integer> coinMap = new HashMap<>();

    private final Map<UUID, BukkitTask> combatTasks = new HashMap<>();

    private ScoreboardManager scoreboardManager;

    SoupRefillStation soupRefillStation = new SoupRefillStation(this);

    private RandomChest randomChest;



    private Scoreboard s;
    private ParticleEffectManager particleEffectManager;
    private ParticleGUINPC particleGUINPC;


    private PunishmentManager punishmentManager;
    private ReportManager reportManager;
    private MessageManager messageManager;
    private ArenaManager arenaManager;
    private DuelQueueManager duelQueueManager;
    private EloManager eloManager;





    @Override
    public void onEnable() {
        instance = this;

        // Load configuration
        if (!(new File(getDataFolder(), "config.yml")).exists()) {
            saveDefaultConfig();
        }
        config = getConfig();

        // Handle online players on plugin enable
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getPlayer().getInventory().clear();
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendTitle(ChatColor.GREEN + "SPIDERPVP", ChatColor.GREEN + "RESTARTED", 10, 100, 50);
        }

        // Initialize ParticleEffectManager and ParticleGUINPC
        particleEffectManager = new ParticleEffectManager(this);
        particleGUINPC = new ParticleGUINPC(particleEffectManager);

        // Register the particle command
        getCommand("disableparticles").setExecutor(new Particlecommand(particleEffectManager));

        // Initialize other managers and handlers
        economyManager = new EconomyManager(this);
        premiumKitManager = new PremiumKitManager(economyManager, this);
        kitManager = new KitManager(premiumKitManager, this.getDataFolder());
        premiumKitShop = new PremiumKitShop(economyManager, premiumKitManager);
        NPCEvents = new NPCEvents(kitManager, premiumKitShop);
        scoreboardManager = new ScoreboardManager(this);
        punishmentManager = new PunishmentManager(this);
        reportManager = new ReportManager(this);
        messageManager = new MessageManager(this);
        arenaManager = new ArenaManager(this);
        duelQueueManager = new DuelQueueManager(this);
        eloManager = new EloManager(this);

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(NPCEvents, this);
        Bukkit.getPluginManager().registerEvents(premiumKitManager, this);
        Bukkit.getPluginManager().registerEvents(premiumKitShop, this);
        Bukkit.getPluginManager().registerEvents(particleEffectManager, this);
        Bukkit.getPluginManager().registerEvents(particleGUINPC, this);
        getServer().getPluginManager().registerEvents(new ReportGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishmentListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaSetupListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaBlockListener(this), this);
        getServer().getPluginManager().registerEvents(DuelManager.getInstance(this), this);
        getServer().getPluginManager().registerEvents(new DuelGUIListener(this), this);

        // Load kit ownership
        premiumKitManager.ensureKitOwnershipFileExists();
        premiumKitManager.loadKitOwnership();

        // Register economy commands
        EconomyCommands economyCommands = new EconomyCommands(economyManager);
        getCommand("coins").setExecutor(economyCommands);
        getCommand("addcoins").setExecutor(economyCommands);
        getCommand("removecoins").setExecutor(economyCommands);

        RandomChest randomChest = new RandomChest(this, economyManager);
        getCommand("setchestlocation").setExecutor(randomChest);
        getCommand("removechestlocation").setExecutor(randomChest);
        getServer().getPluginManager().registerEvents(randomChest, this);


        //Arena Commands
        this.getCommand("setarenaspawn").setExecutor(new SetArenaSpawnCommand(this));
        this.getCommand("arena").setExecutor(new ArenaCommand(this));

        //kits saving
        saveDefaultKitsConfig();


        //Refill Station
        RefillStationWizard refillStationWizard = new RefillStationWizard(this);
        getCommand("giverefillstick").setExecutor(refillStationWizard);
        getServer().getPluginManager().registerEvents(refillStationWizard, this);

        // Moderation commands
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("reports").setExecutor(new ReportsCommand(this));
        getCommand("punish").setExecutor(new PunishCommand(this));
        getCommand("myreports").setExecutor(new MyReportsCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("duel").setExecutor(new DuelCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));

        // Register abilities
        registerEventsAbilities();

        // Load coin data
        loadCoinData();

        // Update scoreboards for online players
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                scoreboardManager.updateScoreboard(player);
            }
        }, 0L, 20L);

        new GUIUpdater(this).runTaskTimer(this, 0, 40);
    }


    private void saveDefaultKitsConfig() {
        File kitsFile = new File(getDataFolder(), "Regularkits.yml");
        if (!kitsFile.exists()) {
            saveResource("Regularkits.yml", false);
        }
    }


    private void registerEventsAbilities() {
        PluginManager pm = getServer().getPluginManager();

        // Registering all the ability classes
        pm.registerEvents(new AeroAbility(), this);
        pm.registerEvents(new ArcherAbility(), this);
        pm.registerEvents(new BerserkerAbility(this), this);
        pm.registerEvents(new EndermanAbility(), this);
        pm.registerEvents(new JediAbility(), this);
        pm.registerEvents(new BlazeAbility(this), this);
        pm.registerEvents(new WitherAbility(this), this);
        pm.registerEvents(new SoupRefillStation(this), this);
        getServer().getPluginManager().registerEvents(particleEffectManager, this);
        getServer().getPluginManager().registerEvents(particleGUINPC, this);

    }





    public static Main getInstance() {
        return instance;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public DuelQueueManager getDuelQueueManager() {
        return duelQueueManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public Map<UUID, BukkitTask> getCombatTasks() {
        return combatTasks;
    }

    public EloManager getEloManager() {
        return eloManager;
    }

    public PremiumKitManager getPremiumKitManager() {
        return premiumKitManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getPlayer().getInventory().clear();
        this.kitManager.giveKitSelectorToSlot(p, 4);
        this.premiumKitShop.giveShopItemToSlot(p, 0);
        this.scoreboardManager.updateScoreboard(p);
        e.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + p.getDisplayName());
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Combat tag removal
        if (combatTasks.containsKey(player.getUniqueId())) {
            combatTasks.get(player.getUniqueId()).cancel();
            combatTasks.remove(player.getUniqueId());
        }
        Player killer = player.getKiller();
        if (killer != null) {
            if (combatTasks.containsKey(killer.getUniqueId())) {
                combatTasks.get(killer.getUniqueId()).cancel();
                combatTasks.remove(killer.getUniqueId());
                killer.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
            }
            // Death message
            String[] deathMessages = {
                    ChatColor.GREEN + player.getName() + ChatColor.GRAY + " met " + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + "'s wrath.",
                    ChatColor.GREEN + player.getName() + ChatColor.GRAY + " was outplayed by " + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + ".",
                    ChatColor.GREEN + killer.getName() + ChatColor.GRAY + " silenced " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + ".",
                    ChatColor.GREEN + player.getName() + ChatColor.GRAY + " fell to " + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + "'s cunning.",
                    ChatColor.GREEN + killer.getName() + ChatColor.GRAY + " claimed " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + "'s fate."
            };
            String randomDeathMessage = deathMessages[new Random().nextInt(deathMessages.length)];
            event.setDeathMessage(randomDeathMessage);
        } else {
            event.setDeathMessage(null);
        }



        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> player.spigot().respawn(), 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatTasks.containsKey(player.getUniqueId())) {
            combatTasks.get(player.getUniqueId()).cancel();
            combatTasks.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(this))
            saveCoinData();
    }

    public void onDisable() {
        saveCoinData();
        this.premiumKitManager.saveKitOwnership();
        scoreboardManager.saveData();



    }

    private void loadCoinData() {
        for (String uuid : this.config.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(uuid);
                int coins = this.config.getInt(uuid);
                this.coinMap.put(playerUUID, Integer.valueOf(coins));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid UUID found in config: " + uuid);
            }
        }
    }

    private void saveCoinData() {
        for (Map.Entry<UUID, Integer> entry : this.coinMap.entrySet())
            this.config.set(((UUID)entry.getKey()).toString(), entry.getValue());
        saveConfig();
    }

    public void setCoins(Player player, int amount) {
        this.config.set(player.getUniqueId().toString(), Integer.valueOf(amount));
        saveConfig();
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        this.kitManager.handleInventoryClick(event);
        this.premiumKitShop.handleInventoryClick(event);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.kitManager.handleKitSelection(event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        this.kitManager.giveKitSelectorToSlot(p, 4);
        this.premiumKitShop.giveShopItemToSlot(p, 0);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("fly")) {
            if (player.hasPermission("essentials.fly")) {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.GREEN + "Flying disabled!");
                } else {
                    player.setAllowFlight(true);
                    player.sendMessage(ChatColor.GREEN + "Flying enabled!");
                }
                return true;
            }
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("setspawn")) {
            if (player.hasPermission("essentials.setspawn")) {
                Location loc = player.getLocation();
                player.getWorld().setSpawnLocation(loc);
                player.sendMessage(ChatColor.GREEN + "Spawn location set!");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (this.combatTasks.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You can't use this command while in combat!");
                return true;
            }
            if (player.hasPermission("essentials.spawn")) {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to spawn!");
                player.getInventory().clear();
                this.kitManager.giveKitSelectorToSlot(player, 4);
                this.premiumKitShop.giveShopItemToSlot(player, 0);
            } else {
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            }
            return true;
        }
        return false;
    }

    private void setInCombat(Player player) {
        if (combatTasks.containsKey(player.getUniqueId())) {
            combatTasks.get(player.getUniqueId()).cancel();
        } else {
            player.sendMessage(ChatColor.YELLOW + "You are now in combat!");
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(this, () -> {
            combatTasks.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
        }, 300L); // 15 seconds

        combatTasks.put(player.getUniqueId(), task);
    }

    @EventHandler
    public void onPlayerCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player damaged = (Player)event.getEntity();

            setInCombat(damager);
            setInCombat(damaged);

            if (event.getFinalDamage() >= damaged.getHealth()) {
                this.scoreboardManager.addKill(damager);
                this.scoreboardManager.addDeath(damaged);
            }
        }
    }


}
