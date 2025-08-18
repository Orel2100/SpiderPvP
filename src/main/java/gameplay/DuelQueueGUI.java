package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class DuelQueueGUI {

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Join the Duel Queue");

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

        // Join Queue Item
        ItemStack joinItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta joinMeta = joinItem.getItemMeta();
        joinMeta.setDisplayName(ChatColor.GREEN + "Join Queue");
        joinMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Find a random opponent."));
        joinItem.setItemMeta(joinMeta);
        gui.setItem(12, joinItem);

        // Leave Queue Item
        ItemStack leaveItem = new ItemStack(Material.RED_WOOL);
        ItemMeta leaveMeta = leaveItem.getItemMeta();
        leaveMeta.setDisplayName(ChatColor.RED + "Leave Queue");
        leaveMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Exit the matchmaking queue."));
        leaveItem.setItemMeta(leaveMeta);
        gui.setItem(14, leaveItem);

        player.openInventory(gui);
    }
}
