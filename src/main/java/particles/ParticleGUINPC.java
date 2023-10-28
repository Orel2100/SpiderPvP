package particles;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ParticleGUINPC implements Listener {

    private final ParticleEffectManager particleEffectManager;

    public ParticleGUINPC(ParticleEffectManager particleEffectManager) {
        this.particleEffectManager = particleEffectManager;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        if (event.getNPC().getName().equalsIgnoreCase("Particle Effects")) {
            Player player = event.getClicker();
            this.particleEffectManager.openGUI(player);
        }
    }
}
