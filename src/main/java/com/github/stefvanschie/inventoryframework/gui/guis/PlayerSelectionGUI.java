package com.github.stefvanschie.inventoryframework.gui.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import gameplay.DuelManager;
import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerSelectionGUI extends ChestGui {

    private final Player challenger;
    private final DuelManager duelManager;
    private final PaginatedPane pages;

    public PlayerSelectionGUI(Player challenger) {
        super(6, "Challenge a Player");
        this.challenger = challenger;
        this.duelManager = Main.getInstance().getDuelManager();
        this.pages = new PaginatedPane(0, 0, 9, 5);
        initialize();
    }

    private void initialize() {
        // Populate with player heads
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.getUniqueId().equals(challenger.getUniqueId()))
                .collect(Collectors.toList());

        List<GuiItem> playerItems = new ArrayList<>();
        for (Player player : onlinePlayers) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.GREEN + player.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Click to challenge this player!");
            meta.setLore(lore);
            skull.setItemMeta(meta);

            playerItems.add(new GuiItem(skull, event -> {
                event.setCancelled(true);
                challenger.closeInventory();
                duelManager.sendDuelRequest(challenger, player);
            }));
        }

        pages.populateWithGuiItems(playerItems);
        addPane(pages);

        // Navigation bar
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.setOnClick(event -> event.setCancelled(true));
        navigation.setPriority(Pane.Priority.HIGH);

        // Previous page
        ItemStack prev = new ItemStack(Material.ARROW);
        prev.getItemMeta().setDisplayName(ChatColor.GREEN + "Previous Page");
        navigation.addItem(new GuiItem(prev, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);
                updateTitle();
                getInventory().clear();
                addPane(pages);
                addPane(navigation);
                update();
            }
        }), 0, 0);

        // Next page
        ItemStack next = new ItemStack(Material.ARROW);
        next.getItemMeta().setDisplayName(ChatColor.GREEN + "Next Page");
        navigation.addItem(new GuiItem(next, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);
                updateTitle();
                getInventory().clear();
                addPane(pages);
                addPane(navigation);
                update();
            }
        }), 8, 0);

        addPane(navigation);
        updateTitle();
    }

    private void updateTitle() {
        this.setTitle("Challenge a Player - Page " + (pages.getPage() + 1) + "/" + pages.getPages());
    }
}
