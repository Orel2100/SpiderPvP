package kitpvp.kitpvp;

import KitsManager.KitManager;
import KitsManager.PremiumKitShop;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCEvents implements Listener {

    private final KitManager kitManager;
    private final PremiumKitShop premiumKitShop;

    public NPCEvents(KitManager kitManager, PremiumKitShop premiumKitShop) {
        this.kitManager = kitManager;
        this.premiumKitShop = premiumKitShop;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        if (event.getNPC().getName().equalsIgnoreCase("Kit Selector")) {
            Player player = event.getClicker();
            kitManager.openKitSelectionMenu(player);
        } else if (event.getNPC().getName().equalsIgnoreCase("Shop")) {
            Player player = event.getClicker();
            premiumKitShop.openShop(player);
        }
    }
}
