package apple.voltskiya.custom_mobs.turret.gm;

import apple.utilities.util.Pretty;
import apple.voltskiya.custom_mobs.turret.parent.TurretTargeting;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.SlotCycleable;

public class TurretTargetingGm extends TurretTargeting {
    private TurretTargetingGmMode mode = TurretTargetingGmMode.PLAYERS_ONLY;

    @Override
    public boolean shouldTargetPlayer(Player player) {
        return mode.players();
    }

    @Override
    public boolean isTargetHostile(LivingEntity entity) {
        return mode.hostile();
    }

    @Override
    public boolean isTargetEntity(LivingEntity entity) {
        return mode.living();
    }

    public TurretTargetingGmMode getMode() {
        return mode;
    }

    public void setMode(TurretTargetingGmMode mode) {
        this.mode = mode;
    }

    public enum TurretTargetingGmMode implements SlotCycleable<TurretTargetingGmMode> {
        PLAYERS_ONLY(true, false, false, Material.PLAYER_HEAD),
        HOSTILE(false, true, false, Material.PLAYER_HEAD),
        HOSTILE_AND_PLAYERS(true, true, false, Material.PLAYER_HEAD),
        ALL_LIVING(true, true, true, Material.PLAYER_HEAD),
        NONE(false, false, false, Material.PLAYER_HEAD);

        private final boolean players;
        private final boolean hostile;
        private final boolean living;
        private final Material material;

        TurretTargetingGmMode(boolean players, boolean hostile, boolean living, Material material) {
            this.players = players;
            this.hostile = hostile;
            this.living = living;
            this.material = material;
        }

        public boolean players() {
            return players;
        }

        public boolean hostile() {
            return hostile;
        }

        public boolean living() {
            return living;
        }

        @Override
        public TurretTargetingGmMode[] valuesList() {
            return values();
        }

        @Override
        public Material itemMaterial() {
            return material;
        }

        @Override
        public String itemName() {
            return Pretty.spaceEnumWords(name());
        }
    }
}
