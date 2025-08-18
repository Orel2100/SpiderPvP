package gameplay;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelGUIManager {
    private static DuelGUIManager instance;
    private final Map<UUID, DuelContext> duelContexts = new HashMap<>();

    private DuelGUIManager() {}

    public static DuelGUIManager getInstance() {
        if (instance == null) {
            instance = new DuelGUIManager();
        }
        return instance;
    }

    public void setContext(UUID playerUUID, DuelContext context) {
        duelContexts.put(playerUUID, context);
    }

    public DuelContext getContext(UUID playerUUID) {
        return duelContexts.get(playerUUID);
    }

    public void removeContext(UUID playerUUID) {
        duelContexts.remove(playerUUID);
    }
}
