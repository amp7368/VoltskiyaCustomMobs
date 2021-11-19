package apple.voltskiya.custom_mobs.trash.old_turrets2.mobs;

import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.trash.old_turrets2.Old2TurretMobSaveable;
import apple.voltskiya.custom_mobs.trash.old_turrets2.Old2TurretType;
import apple.voltskiya.custom_mobs.trash.old_turrets2.gui.OldTurretGuiInfinite;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class Old2TurretMobInfinite extends Old2TurretMob {
    private OldTurretGuiInfinite turretGui = null;
    private Old2TargetingMode targetingMode;

    public Old2TurretMobInfinite(Player player, String typeId) {
        super(typeId);
        targetingMode = Old2TargetingMode.ALL;
    }

    public Old2TurretMobInfinite(Old2TurretMobInfiniteSaveable turretMobInfiniteSaveable, String typeId) {
        super(typeId, turretMobInfiniteSaveable);
        targetingMode = turretMobInfiniteSaveable.getTargetingMode();
    }

    public Old2TurretMobInfinite(Player player) {
        super(Old2TurretType.INFINITE.getUsername());
        targetingMode = Old2TargetingMode.ALL;
    }

    public Old2TurretMobInfinite(Old2TurretMobInfiniteSaveable turretMobInfiniteSaveable) {
        super(Old2TurretType.INFINITE.getUsername(), turretMobInfiniteSaveable);
        targetingMode = turretMobInfiniteSaveable.getTargetingMode();
    }


    @Override
    public Old2TurretMobSaveable toSaveable() {
        return new Old2TurretMobInfiniteSaveable(this);
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new OldTurretGuiInfinite(this);
        return turretGui;
    }

    @Override
    protected void updateGui() {
        if (turretGui != null) {
            if (turretGui.getInventory().getViewers().isEmpty()) turretGui = null;
            else turretGui.update();
        }
    }

    @Override
    protected DBItemStack removeArrow() {
        for (DBItemStack arrow : arrows) {
            final int count = arrow.count;
            if (arrow.type != null && arrow.type != Material.AIR && count > 0) {
                return arrow;
            }
        }
        return null;
    }

    public Old2TargetingMode getMode() {
        return targetingMode;
    }

    public void cycleMode() {
        targetingMode = targetingMode.cycle();
        updateGui();
    }

    @Override
    protected boolean shouldTarget(@Nullable Entity entity) {
        if (entity != null && entity.getScoreboardTags().contains(TURRET_TAG)) return false;
        if (entity instanceof Player player) {
            return player.getGameMode() == GameMode.SURVIVAL && targetingMode.targetsPlayers() && super.shouldTarget(entity);
        } else {
            return targetingMode.targetsMobs() && super.shouldTarget(entity);
        }
    }


    public static class Old2TurretMobInfiniteSaveable extends Old2TurretMobSaveable {
        private String uuid;
        private Old2TargetingMode targetingMode;

        public Old2TurretMobInfiniteSaveable(Old2TurretMobInfinite turretMobInfinite) {
            super(turretMobInfinite);
            this.uuid = turretMobInfinite.getUUID();
            this.targetingMode = turretMobInfinite.getMode();
        }

        public Old2TurretMobInfiniteSaveable() {
        }

        @Override
        public Old2TurretMob build() {
            return new Old2TurretMobInfinite(this);
        }

        @Override
        public String getUUID() {
            return uuid;
        }

        public Old2TargetingMode getTargetingMode() {
            return targetingMode;
        }
    }
}