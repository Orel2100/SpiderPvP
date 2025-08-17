package gameplay;

import org.bukkit.Location;
import org.bukkit.block.Block;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaRegenManager {

    private static ArenaRegenManager instance;
    private final Map<String, List<Block>> changedBlocks = new HashMap<>();

    private ArenaRegenManager() {}

    public static ArenaRegenManager getInstance() {
        if (instance == null) {
            instance = new ArenaRegenManager();
        }
        return instance;
    }

    public void startTracking(String arenaName) {
        changedBlocks.put(arenaName, new ArrayList<>());
    }

    public void addChangedBlock(String arenaName, Block block) {
        if (changedBlocks.containsKey(arenaName)) {
            changedBlocks.get(arenaName).add(block);
        }
    }

    public void restoreArena(String arenaName) {
        if (changedBlocks.containsKey(arenaName)) {
            for (Block block : changedBlocks.get(arenaName)) {
                block.setType(org.bukkit.Material.AIR);
            }
            changedBlocks.remove(arenaName);
        }
    }
}
