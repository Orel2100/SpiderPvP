package kitpvp.kitpvp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        e.getDrops().clear();
    }

    @EventHandler
    public void onPlayerUseEnderEye(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_EYE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerUseStew(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player right-clicked with a Mushroom Stew
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && itemInHand.getType() == Material.MUSHROOM_STEW) {
            // Remove the Mushroom Stew from the player's hand
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            player.getInventory().setItemInMainHand(itemInHand.getAmount() > 0 ? itemInHand : null);

            // Refill 2 hearts
            double newHealth = Math.min(player.getHealth() + 4, player.getMaxHealth()); // Ensure not to exceed max health
            player.setHealth(newHealth);
        }
    }
    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }



}
