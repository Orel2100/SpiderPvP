package gameplay;

import kitpvp.kitpvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProfileGUI {

    private final Main plugin;

    public ProfileGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, player.getName() + "'s Stats");

        // Decorative glass panes
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, pane);
        }

        int coins = plugin.economyManager.getCoins(player);
        // Note: The scoreboard manager has its own kill/death tracking.
        // For simplicity, we can't easily get that data here without refactoring ScoreboardManager.
        // We will display a placeholder for now. A proper implementation would require making
        // the kill/death data accessible via a getter in ScoreboardManager.
        int kills = 0; // Placeholder
        int deaths = 0; // Placeholder
        String kit = plugin.getGlobalKitManager().getKit(player);

        ItemStack coinsItem = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta coinsMeta = coinsItem.getItemMeta();
        coinsMeta.setDisplayName(ChatColor.YELLOW + "Coins: " + ChatColor.GOLD + coins);
        coinsItem.setItemMeta(coinsMeta);
        gui.setItem(11, coinsItem);

        ItemStack killsItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta killsMeta = killsItem.getItemMeta();
        killsMeta.setDisplayName(ChatColor.GREEN + "Kills: " + ChatColor.WHITE + kills);
        killsItem.setItemMeta(killsMeta);
        gui.setItem(13, killsItem);

        ItemStack deathsItem = new ItemStack(Material.BONE);
        ItemMeta deathsMeta = deathsItem.getItemMeta();
        deathsMeta.setDisplayName(ChatColor.RED + "Deaths: " + ChatColor.WHITE + deaths);
        deathsItem.setItemMeta(deathsMeta);
        gui.setItem(15, deathsItem);

        ItemStack kitItem = new ItemStack(Material.CHEST);
        ItemMeta kitMeta = kitItem.getItemMeta();
        kitMeta.setDisplayName(ChatColor.AQUA + "Current Kit: " + ChatColor.WHITE + kit);
        kitItem.setItemMeta(kitMeta);
        gui.setItem(22, kitItem);


        player.openInventory(gui);
    }
}
