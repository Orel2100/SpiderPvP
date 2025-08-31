package kitpvp.kitpvp;

import KitsManager.PremiumKitShop;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCEvents implements Listener {

    private final Main plugin;
    private final PremiumKitShop premiumKitShop;

    public NPCEvents(Main plugin, PremiumKitShop premiumKitShop) {
        this.plugin = plugin;
        this.premiumKitShop = premiumKitShop;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        if (event.getNPC().getName().equalsIgnoreCase("Kit Selector")) {
            Player player = event.getClicker();
            plugin.getClassSelectorGUI().openClassSelector(player);
        } else if (event.getNPC().getName().equalsIgnoreCase("Shop")) {
            Player player = event.getClicker();
            premiumKitShop.openShop(player);
        }
    }
}
