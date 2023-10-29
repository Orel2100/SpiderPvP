package particles;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleEffectManager implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> particleTasks = new HashMap<>();

    public ParticleEffectManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public enum ParticleEffect {
        HEART_SHAPE("Heart Shape", "REDSTONE", "kitpvp.effect.heart"),
        WINGS("Wings", "FEATHER", "kitpvp.effect.wings"),
        FIRE_WATER_WINGS("Fire & Water Wings", "FEATHER", "kitpvp.effect.firewaterwings"),
        SPIRAL("Spiral", "ENDER_PEARL", "kitpvp.effect.spiral"),
        FIRE_RING("Fire Ring", "BLAZE_POWDER", "kitpvp.effect.firering"),
        GALAXY_ORBIT("Enchanted Orbit", "ENCHATING_TABLE", "kitpvp.effect.enchant");

        private final String displayName;
        private final String iconMaterial;
        private final String permission;

        ParticleEffect(String displayName, String iconMaterial, String permission) {
            this.displayName = displayName;
            this.iconMaterial = iconMaterial;
            this.permission = permission;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getIconMaterial() {
            return Material.valueOf(iconMaterial);
        }

        public String getPermission() {
            return permission;
        }
    }

    public void displayParticleEffect(Player player, ParticleEffect effect) {
        if (!player.hasPermission(effect.getPermission())) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this effect!");
            return;
        }
        if (particleTasks.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(particleTasks.get(player.getUniqueId()));
        }
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            switch (effect) {
                case HEART_SHAPE:
                    displayHeartShape(player);
                    break;
                case WINGS:
                    displayWings(player);
                    break;
                case FIRE_WATER_WINGS:
                    displayFireWaterWings(player);
                    break;
                case SPIRAL:
                    displaySpiral(player);
                    break;
                case FIRE_RING:
                    displayFireRing(player);
                    break;
                case GALAXY_ORBIT:
                    displayGalaxyOrbit(player);
                    break;
            }
        }, 0L, 5L);
        particleTasks.put(player.getUniqueId(), taskId);
    }

    private void displayHeartShape(Player player) {
        player.spawnParticle(Particle.HEART, player.getLocation().add(0, 2, 0), 5);
    }

    private void displayWings(Player player) {
        final Location loc = player.getEyeLocation().subtract(0.0, 0.3, 0.0);
        loc.setPitch(0.0f);
        loc.setYaw(player.getEyeLocation().getYaw());
        final Vector v1 = loc.getDirection().normalize().multiply(-0.3);
        v1.setY(0);
        loc.add(v1);

        for (double i = -10.0; i < 6.2; i += 0.1) {
            final double var = Math.sin(i / 12.0);
            final double x = Math.sin(i) * (Math.exp(Math.cos(i)) - 2.0 * Math.cos(4.0 * i) - Math.pow(var, 5.0)) / 2.0;
            final double z = Math.cos(i) * (Math.exp(Math.cos(i)) - 2.0 * Math.cos(4.0 * i) - Math.pow(var, 5.0)) / 2.0;

            final Vector v2 = new Vector(-x, 0.0, -z);
            rotateAroundAxisX(v2, (loc.getPitch() + 90.0f) * 0.017453292f);
            rotateAroundAxisY(v2, -loc.getYaw() * 0.017453292f);

            player.spawnParticle(Particle.REDSTONE, loc.clone().add(v2), 1, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1));
        }
    }

    private void displayFireWaterWings(Player player) {
        final Location loc = player.getEyeLocation().subtract(0.0, 0.3, 0.0);
        loc.setPitch(0.0f);
        loc.setYaw(player.getEyeLocation().getYaw());
        final Vector v1 = loc.getDirection().normalize().multiply(-0.3);
        v1.setY(0);
        loc.add(v1);

        for (double i = -10.0; i < 6.2; i += 0.1) {
            final double var = Math.sin(i / 12.0);
            final double x = Math.sin(i) * (Math.exp(Math.cos(i)) - 2.0 * Math.cos(4.0 * i) - Math.pow(var, 5.0)) / 2.0;
            final double z = Math.cos(i) * (Math.exp(Math.cos(i)) - 2.0 * Math.cos(4.0 * i) - Math.pow(var, 5.0)) / 2.0;

            final Vector v2 = new Vector(-x, 0.0, -z);
            rotateAroundAxisX(v2, (loc.getPitch() + 90.0f) * 0.017453292f);
            rotateAroundAxisY(v2, -loc.getYaw() * 0.017453292f);

            Color color;
            if (x > 0) { // Right wing (red gradient)
                color = Color.fromRGB(255, (int) (255 - (255 * (i + 10) / 16.2)), (int) (255 - (255 * (i + 10) / 16.2)));
            } else { // Left wing (blue gradient)
                color = Color.fromRGB((int) (255 - (255 * (i + 10) / 16.2)), (int) (255 - (255 * (i + 10) / 16.2)), 255);
            }

            player.spawnParticle(Particle.REDSTONE, loc.clone().add(v2), 1, new Particle.DustOptions(color, 1));
        }
    }

    private void displayGalaxyOrbit(Player player) {
        Location loc = player.getLocation().add(0, 2, 0); // Positioned slightly above the player's head
        double radius = 0.75; // Radius of the halo

        // Halo with dropping enchanting particles
        for (int i = 0; i < 36; i++) {
            double angle = 2 * Math.PI * i / 36;
            double x = radius * Math.sin(angle);
            double z = radius * Math.cos(angle);

            Location haloPoint = loc.clone().add(x, 0, z);

            // Enchanting particles dropping from the halo
            player.spawnParticle(Particle.ENCHANTMENT_TABLE, haloPoint, 5, 0, -0.5, 0, 0);
        }
    }




    private void displaySpiral(Player player) {
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10;
            double x = Math.cos(angle);
            double z = Math.sin(angle);
            player.spawnParticle(Particle.END_ROD, player.getLocation().add(x, 2 + 0.1 * i, z), 1);
        }
    }

    private void displayFireRing(Player player) {
        for (int i = 0; i < 36; i++) {
            double angle = 2 * Math.PI * i / 36;
            double x = 1.5 * Math.cos(angle);
            double z = 1.5 * Math.sin(angle);
            player.spawnParticle(Particle.FLAME, player.getLocation().add(x, 2, z), 1, 0, 0, 0, 0);
        }
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Select Wing Style");
        for (ParticleEffect effect : ParticleEffect.values()) {
            if (player.hasPermission(effect.getPermission())) {
                ItemStack item = new ItemStack(effect.getIconMaterial());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(effect.getDisplayName());
                item.setItemMeta(meta);
                gui.addItem(item);
            }
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getView().getTitle().equals("Select Wing Style")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                for (ParticleEffect effect : ParticleEffect.values()) {
                    if (displayName.equals(effect.getDisplayName())) {
                        displayParticleEffect(player, effect);
                        player.closeInventory();
                        return;
                    }
                }
            }
        }
    }

    public void disableParticlesForPlayer(Player player) {
        if (particleTasks.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(particleTasks.get(player.getUniqueId()));
            particleTasks.remove(player.getUniqueId());
        }
    }

    public static final Vector rotateAroundAxisX(final Vector v, final double angle) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        final double y = v.getY() * cos - v.getZ() * sin;
        final double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static final Vector rotateAroundAxisY(final Vector v, final double angle) {
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        final double x = v.getX() * cos + v.getZ() * sin;
        final double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }
}
