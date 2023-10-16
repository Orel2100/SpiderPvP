package kitpvp.kitpvp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AbilityManager implements Listener {
    private final Map<String, Long> cooldowns = new HashMap<>();

    private final long COOLDOWN_TIME = 10000L;
    private final long TELEPORT_COOLDOWN_TIME = 15000L;
    private final long WITHER_COOLDOWN_TIME = 10000L; // 10 seconds
    private final long JEDI_PUSH_COOLDOWN_TIME = 10000L; // 10 seconds
    private final ItemStack JEDI_PUSH_ITEM = new ItemStack(Material.BLAZE_ROD);





    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player)event.getEntity();
        long remainingCooldown = checkCooldown(player, "_archer");
        if (hasArcherKit(player) && holdingKitItem(player, Material.BOW, ChatColor.GREEN + "Archer Bow"))
            if (remainingCooldown == 0L) {
                Arrow arrow = (Arrow)event.getProjectile();
                arrow.setCustomName("ExplosiveArrow");
                setCooldown(player, "_archer");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your ability"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Archer ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getDamager().getCustomName() != null && event
                .getDamager().getCustomName().equals("ExplosiveArrow")) {
            Arrow arrow = (Arrow)event.getDamager();
            arrow.getWorld().createExplosion(arrow.getLocation(), 2.0F, false, false);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        long remainingCooldown = checkCooldown(player, "_berserker");
        if (hasBerserkerKit(player) && holdingKitItem(player, Material.DIAMOND_AXE, ChatColor.GREEN + "Berserker Axe"))
            if (remainingCooldown == 0L) {
                Vector direction = event.getRightClicked().getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                event.getRightClicked().setVelocity(direction.multiply(4));
                setCooldown(player, "_berserker");
                player.sendMessage(ChatColor.GREEN + "You've used your ability");
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Berserker ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
    }

    private long checkCooldown(Player player, String suffix) {
        String key = player.getUniqueId().toString() + suffix;
        if (!this.cooldowns.containsKey(key))
            return 0L;
        long timeSinceLastUse = System.currentTimeMillis() - this.cooldowns.get(key);
        long remainingTime = COOLDOWN_TIME - timeSinceLastUse;
        return (remainingTime > 0L) ? remainingTime : 0L;
    }

    private void setCooldown(Player player, String suffix) {
        String key = player.getUniqueId().toString() + suffix;
        this.cooldowns.put(key, System.currentTimeMillis());
    }

    private boolean holdingKitItem(Player player, Material material, String displayName) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == material && itemInHand.hasItemMeta()) {
            String itemName = itemInHand.getItemMeta().getDisplayName();
            return (itemName != null && itemName.equals(displayName));
        }
        return false;
    }

    private boolean hasWarriorKit(Player player) {
        return player.getInventory().contains(Material.IRON_SWORD);
    }

    private boolean hasArcherKit(Player player) {
        return player.getInventory().contains(Material.BOW);
    }

    private boolean hasBerserkerKit(Player player) {
        return player.getInventory().contains(Material.DIAMOND_AXE);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.ENDER_EYE) {
            long remainingCooldown = checkCooldown(player, "_teleport");
            if (remainingCooldown == 0L) {
                if (teleportToNearbyPlayer(player)) {
                    setCooldown(player, "_teleport");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your teleport ability!"));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "No players found within 10 blocks radius!"));
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your teleport ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }

    private boolean teleportToNearbyPlayer(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(10.0D, 10.0D, 10.0D);
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != player) {
                double distance = entity.getLocation().distance(player.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = (Player)entity;
                }
            }
        }
        if (closestPlayer != null) {
            player.teleport((Entity)closestPlayer);
            player.sendMessage(ChatColor.GREEN + "Teleported to " + closestPlayer.getName() + "!");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onWitherAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.BLAZE_ROD) {
            long remainingCooldown = checkCooldown(player, "_wither");
            if (remainingCooldown == 0L) {
                for (int i = 0; i < 3; i++) {
                    WitherSkull skull = player.launchProjectile(WitherSkull.class);
                    skull.setVelocity(player.getLocation().getDirection().multiply(2));
                    skull.setIsIncendiary(false);
                    skull.setYield(1.5F);
                }
                setCooldown(player, "_wither");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your Wither ability!"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Wither ability is on cooldown for " + (remainingCooldown / 1000L * 2) + " more seconds!"));
            }
        }
    }


    @EventHandler
    public void onAeroAbilityUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player is holding the Aero Feather
        if (itemInHand.getType() == Material.FEATHER && itemInHand.hasItemMeta() && "Aero Feather".equals(itemInHand.getItemMeta().getDisplayName())) {
            long remainingCooldown = checkCooldown(player, "_aero");
            if (remainingCooldown == 0L) {
                // Get all entities around the player within a radius of 5 blocks
                List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
                for (Entity entity : nearbyEntities) {
                    // Check if the entity is a player and not the user
                    if (entity instanceof Player && !entity.equals(player)) {
                        // Launch the player into the sky
                        entity.setVelocity(new Vector(0, 1.2, 0));
                    }
                }

                // Particle effect
                for (int i = 0; i < 360; i += 5) { // Adjust the increment for more/less density
                    double angle = i * Math.PI / 180;
                    double x = 5 * Math.cos(angle);
                    double z = 5 * Math.sin(angle);
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(x, 2, z), 1, 0, 0, 0, 0);
                }
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

                setCooldown(player, "_aero");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You've used your Aero ability!"));
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Aero ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }
    @EventHandler
    public void onJediPushAbilityUse(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // Check if the player is starting to sneak and has the Jedi Push item in their inventory
        if (event.isSneaking() && player.getInventory().contains(JEDI_PUSH_ITEM)) {
            long remainingCooldown = checkCooldown(player, "_jedi_push");
            if (remainingCooldown == 0L) {
                player.sendMessage(ChatColor.GOLD + "You used the " + ChatColor.AQUA + "Jedi Push" + ChatColor.GOLD + " Ability!");

                for (Entity nearby : player.getNearbyEntities(7, 7, 7)) {
                    if (nearby instanceof Player) {
                        knockback(player, (Player) nearby);
                        ((Player) nearby).playSound(nearby.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1F, 0.4F);
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1F, 0.4F);
                setCooldown(player, "_jedi_push");
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Your Jedi Push ability is on cooldown for " + (remainingCooldown / 1000L) + " more seconds!"));
            }
        }
    }
    private void knockback(Player player, Player target) {
        Location direction = target.getLocation().subtract(player.getLocation());
        double distance = target.getLocation().distance(player.getLocation());
        Vector velocity = direction.toVector().normalize().multiply(2 / distance).setY(1);
        target.setVelocity(velocity);
    }



}
