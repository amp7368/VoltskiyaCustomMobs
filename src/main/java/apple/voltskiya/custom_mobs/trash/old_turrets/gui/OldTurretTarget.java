package apple.voltskiya.custom_mobs.trash.old_turrets.gui;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class OldTurretTarget {
    private static final List<TurretTargetType> targetMapping = new ArrayList<>();

    public enum TurretTargetType {
        NONE("No targeting", 0, Material.WHITE_STAINED_GLASS_PANE, false, false),
        MOBS("Only mobs", 1, Material.ZOMBIE_HEAD, false, true),
        PLAYERS("Only players", 2, Material.NETHERITE_PICKAXE, true, false),
        ALL("All", 3, Material.RED_TERRACOTTA, true, true);

        private final int index;
        private final String displayName;
        private final Material material;
        private final boolean targetsPlayers;
        private final boolean targetsMobs;

        TurretTargetType(String displayName, int index, Material material, boolean targetsPlayers, boolean targetsMobs) {
            this.index = index;
            this.displayName = displayName;
            this.material = material;
            this.targetsMobs = targetsMobs;
            this.targetsPlayers = targetsPlayers;
            while (targetMapping.size() <= index)
                targetMapping.add(null);
            targetMapping.set(index, this);
        }

        public TurretTargetType next() {
            if (targetMapping.size() == index + 1) {
                return targetMapping.get(0);
            }
            return targetMapping.get(index + 1);
        }

        public String display() {
            return displayName;
        }

        public List<String> lore() {
            return null;
        }

        public Material material() {
            return material;
        }

        public boolean isTargetsPlayers() {
            return targetsPlayers;
        }

        public boolean isTargetsMobs() {
            return targetsMobs;
        }
    }
}
