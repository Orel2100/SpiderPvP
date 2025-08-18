package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class DuelModeGUI {

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Select Duel Mode");

        // Fill border with glass panes
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < gui.getSize(); i++) {
            if (i < 10 || i > 16 || i % 9 == 0 || (i + 1) % 9 == 0) {
                gui.setItem(i, filler);
            }
        }

        ItemStack challengeItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta challengeMeta = challengeItem.getItemMeta();
        challengeMeta.setDisplayName(ChatColor.AQUA + "Challenge a Player");
        challengeMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Challenge a specific player to a duel."));
        challengeItem.setItemMeta(challengeMeta);
        gui.setItem(12, challengeItem);

        ItemStack queueItem = new ItemStack(Material.COMPASS);
        ItemMeta queueMeta = queueItem.getItemMeta();
        queueMeta.setDisplayName(ChatColor.GOLD + "Random Matchmaking");
        queueMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Join a queue for a random opponent."));
        queueItem.setItemMeta(queueMeta);
        gui.setItem(14, queueItem);

        player.openInventory(gui);
    }
}
