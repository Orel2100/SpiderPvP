package kitpvp.kitpvp;

import java.io.File;
import java.util.*;

import abilities.*;
import gameplay.ArenaCommand;
import gameplay.RandomChest;
import gameplay.SetArenaSpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import particles.ParticleEffectManager;
import particles.ParticleGUINPC;
import particles.Particlecommand;
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

    private final HashSet<UUID> playersInCombat = new HashSet<>();

    private ScoreboardManager scoreboardManager;

    SoupRefillStation soupRefillStation = new SoupRefillStation(this);

    private RandomChest randomChest;



    private Scoreboard s;
    private ParticleEffectManager particleEffectManager;
    private ParticleGUINPC particleGUINPC;









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
        kitManager = new KitManager(premiumKitManager);
        premiumKitShop = new PremiumKitShop(economyManager, premiumKitManager);
        NPCEvents = new NPCEvents(kitManager, premiumKitShop);
        scoreboardManager = new ScoreboardManager(this);

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(NPCEvents, this);
        Bukkit.getPluginManager().registerEvents(premiumKitManager, this);
        Bukkit.getPluginManager().registerEvents(premiumKitShop, this);
        Bukkit.getPluginManager().registerEvents(particleEffectManager, this);
        Bukkit.getPluginManager().registerEvents(particleGUINPC, this);

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
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> player.spigot().respawn(), 1L);
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
    public void onPlayerDeathMSG(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        Player killer = deceased.getKiller();

        // If the death was caused by another player
        if (killer != null) {
            String[] deathMessages = {
                    ChatColor.GREEN + deceased.getName() + ChatColor.GRAY + " met " + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + "'s wrath.",
                    ChatColor.GREEN + deceased.getName() + ChatColor.GRAY + " was outplayed by " + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + ".",
                    ChatColor.GREEN + killer.getName() + ChatColor.GRAY + " silenced " + ChatColor.GREEN + deceased.getName() + ChatColor.GRAY + ".",
                    ChatColor.GREEN + deceased.getName() + ChatColor.GRAY + " fell to " + ChatColor.GREEN + killer.getName() + ChatColor.GRAY + "'s cunning.",
                    ChatColor.GREEN + killer.getName() + ChatColor.GRAY + " claimed " + ChatColor.GREEN + deceased.getName() + ChatColor.GRAY + "'s fate."
            };

            // Randomly select one of the death messages
            String randomDeathMessage = deathMessages[new Random().nextInt(deathMessages.length)];
            event.setDeathMessage(randomDeathMessage);
        } else {
            // Ignore all other death messages
            event.setDeathMessage(null);
        }
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
            if (this.playersInCombat.contains(player.getUniqueId())) {
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

    @EventHandler
    public void onPlayerCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player damaged = (Player)event.getEntity();
            this.playersInCombat.add(damager.getUniqueId());
            this.playersInCombat.add(damaged.getUniqueId());
            if (event.getFinalDamage() >= damaged.getHealth()) {
                this.scoreboardManager.addKill(damager);
                this.scoreboardManager.addDeath(damaged);
            }
            Bukkit.getScheduler().runTaskLater(this, () -> {
                this.playersInCombat.remove(damager.getUniqueId());
                this.playersInCombat.remove(damaged.getUniqueId());
            }, 300L);

        }
    }


}
