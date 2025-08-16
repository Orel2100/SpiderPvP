package moderation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentGUIManager {
    private static PunishmentGUIManager instance;
    private final Map<UUID, PunishmentContext> punishmentContexts = new HashMap<>();

    private PunishmentGUIManager() {}

    public static PunishmentGUIManager getInstance() {
        if (instance == null) {
            instance = new PunishmentGUIManager();
        }
        return instance;
    }

    public void setContext(UUID staffUUID, PunishmentContext context) {
        punishmentContexts.put(staffUUID, context);
    }

    public PunishmentContext getContext(UUID staffUUID) {
        return punishmentContexts.get(staffUUID);
    }

    public void removeContext(UUID staffUUID) {
        punishmentContexts.remove(staffUUID);
    }
}
