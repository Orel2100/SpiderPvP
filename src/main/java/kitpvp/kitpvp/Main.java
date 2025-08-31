package kitpvp.kitpvp;

import java.io.File;
import java.util.*;

import KitsManager.KitManager;
import KitsManager.PremiumKitManager;
import KitsManager.PremiumKitShop;
import abilities.AbilityXPListener;
import abilities.AbilityXPManager;
import abilities.XPBarUpdater;
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
import duel.DuelCommand;
import moderation.UnbanCommand;
import duel.Duel;
import duel.DuelManager;
import gameplay.ArenaManager;
import gameplay.ArenaBlockListener;
import gameplay.LobbyItemListener;
import gameplay.ClassSelectorGUI;
import gameplay.ClassSelectorGUIListener;
import gameplay.ProfileGUI;
import gameplay.ProfileGUIListener;
import globalkit.GlobalKitManager;
import cooldown.CooldownManager;
import economy.EloManager;
import cooldown.CooldownUpdater;
import gameplay.GUIUpdater;
import globalkit.KitCommand;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
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
    private GlobalKitManager globalKitManager;
    private EloManager eloManager;
    private CooldownManager cooldownManager;
    private DuelManager duelManager;
    private ProfileGUI profileGUI;
    private ClassSelectorGUI classSelectorGUI;
    private AbilityXPManager abilityXPManager;





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
        kitManager = new KitManager(premiumKitManager, this);
        premiumKitShop = new PremiumKitShop(economyManager, premiumKitManager);
        NPCEvents = new NPCEvents(this, premiumKitShop);
        scoreboardManager = new ScoreboardManager(this);
        punishmentManager = new PunishmentManager(this);
        reportManager = new ReportManager(this);
        messageManager = new MessageManager(this);
        arenaManager = new ArenaManager(this);
        globalKitManager = new GlobalKitManager(this);
        eloManager = new EloManager(this);
        cooldownManager = new CooldownManager();
        duelManager = new DuelManager(this);
        profileGUI = new ProfileGUI(this);
        classSelectorGUI = new ClassSelectorGUI(this);
        abilityXPManager = new AbilityXPManager();

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ClassSelectorGUIListener(this, classSelectorGUI), this);
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(NPCEvents, this);
        Bukkit.getPluginManager().registerEvents(premiumKitManager, this);
        Bukkit.getPluginManager().registerEvents(premiumKitShop, this);
        Bukkit.getPluginManager().registerEvents(particleEffectManager, this);
        Bukkit.getPluginManager().registerEvents(particleGUINPC, this);
        getServer().getPluginManager().registerEvents(new ReportGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishmentListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new LobbyItemListener(this), this);
        getServer().getPluginManager().registerEvents(new ProfileGUIListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityXPListener(this), this);

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
        ArenaCommand arenaCommand = new ArenaCommand(this);
        this.getCommand("arena").setExecutor(arenaCommand);
        getServer().getPluginManager().registerEvents(arenaCommand, this);

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
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("duel").setExecutor(new DuelCommand(this));

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
        new CooldownUpdater(this).runTaskTimer(this, 0, 20);
        new XPBarUpdater(this).runTaskTimer(this, 0, 5); // Run every 5 ticks for a smooth update
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
        pm.registerEvents(new AeroAbility(this), this);
        pm.registerEvents(new ArcherAbility(this), this);
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

    public GlobalKitManager getGlobalKitManager() {
        return globalKitManager;
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

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public PremiumKitManager getPremiumKitManager() {
        return premiumKitManager;
    }

    public ProfileGUI getProfileGUI() {
        return profileGUI;
    }

    public ClassSelectorGUI getClassSelectorGUI() {
        return classSelectorGUI;
    }

    public AbilityXPManager getAbilityXPManager() {
        return abilityXPManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getPlayer().getInventory().clear();
        giveLobbyItems(p);
        this.scoreboardManager.updateScoreboard(p);
        e.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.GRAY + p.getDisplayName());
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Duel duel = duelManager.getDuel(player);
        if (duel != null) {
            Player killer = player.getKiller();
            // If there's no killer, the other player is the winner
            Player winner = (killer != null) ? killer : Bukkit.getPlayer(duel.getOpponent(player));
            if (winner != null) {
                duelManager.endDuel(player, winner);
            }
            event.setDeathMessage(null); // Or a custom duel death message
            return; // Stop further processing
        }

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
        globalKitManager.saveKits();



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
        // this.kitManager.handleInventoryClick(event); // Removed as method was deleted
        this.premiumKitShop.handleInventoryClick(event);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        this.kitManager.handleKitSelection(event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        // Use a short delay to prevent items being cleared by other plugins on respawn
        Bukkit.getScheduler().runTaskLater(this, () -> giveLobbyItems(p), 1L);
    }

    private void giveLobbyItems(Player p) {
        p.getInventory().clear();

        // Slot 0: Game Menu (Compass)
        ItemStack gameMenu = new ItemStack(Material.COMPASS);
        ItemMeta gameMenuMeta = gameMenu.getItemMeta();
        gameMenuMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aGame Menu &7(Right Click)"));
        gameMenu.setItemMeta(gameMenuMeta);
        p.getInventory().setItem(0, gameMenu);

        // Slot 1: My Profile (Player Skull)
        ItemStack profile = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta profileMeta = (SkullMeta) profile.getItemMeta();
        profileMeta.setOwningPlayer(p);
        profileMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aMy Profile &7(Right Click)"));
        profile.setItemMeta(profileMeta);
        p.getInventory().setItem(1, profile);

        // Slot 4: PLAY! (Cake)
        ItemStack playItem = new ItemStack(Material.CAKE);
        ItemMeta playMeta = playItem.getItemMeta();
        playMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cPLAY! &7(Right Click)"));
        playItem.setItemMeta(playMeta);
        p.getInventory().setItem(4, playItem);

        // Slot 7: Shop (Emerald)
        this.premiumKitShop.giveShopItemToSlot(p, 7);

        // Slot 8: Class Selector (Command Block)
        this.kitManager.giveKitSelectorToSlot(p, 8);

        p.updateInventory();
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
                giveLobbyItems(player);
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
