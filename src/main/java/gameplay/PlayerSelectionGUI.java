package gameplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerSelectionGUI {

    private final Player viewer;
    private final List<Player> onlinePlayers;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 45; // 5 rows of 9 slots

    public PlayerSelectionGUI(Player viewer) {
        this.viewer = viewer;
        this.onlinePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.getUniqueId().equals(viewer.getUniqueId()))
                .collect(Collectors.toList());
    }

    public void open() {
        int totalPages = (int) Math.ceil((double) onlinePlayers.size() / ITEMS_PER_PAGE);
        Inventory inv = Bukkit.createInventory(null, 54, "Challenge a Player (" + (currentPage + 1) + "/" + totalPages + ")");

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, onlinePlayers.size());

        for (int i = startIndex; i < endIndex; i++) {
            Player target = onlinePlayers.get(i);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName(ChatColor.GREEN + target.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to challenge " + target.getName());
            // Store the target's UUID in a lore line to be parsed by the listener
            lore.add(ChatColor.BLACK + "player:" + target.getUniqueId().toString());
            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.addItem(skull);
        }

        // Navigation buttons
        if (currentPage > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
            prev.setItemMeta(prevMeta);
            inv.setItem(48, prev);
        }

        if (endIndex < onlinePlayers.size()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GREEN + "Next Page");
            next.setItemMeta(nextMeta);
            inv.setItem(50, next);
        }

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        close.setItemMeta(closeMeta);
        inv.setItem(49, close);

        viewer.openInventory(inv);
    }

    public void nextPage() {
        int totalPages = (int) Math.ceil((double) onlinePlayers.size() / ITEMS_PER_PAGE);
        if (currentPage + 1 < totalPages) {
            currentPage++;
            open();
        }
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            open();
        }
    }
}
