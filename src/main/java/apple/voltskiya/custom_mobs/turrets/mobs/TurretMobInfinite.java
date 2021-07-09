package apple.voltskiya.custom_mobs.turrets.mobs;

import apple.voltskiya.custom_mobs.sql.DBItemStack;
import apple.voltskiya.custom_mobs.turrets.TurretMobSaveable;
import apple.voltskiya.custom_mobs.turrets.TurretType;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiInfinite;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretMobInfinite extends TurretMob {
    private TurretGuiInfinite turretGui = null;
    private TargetingMode targetingMode;

    public TurretMobInfinite(Player player) {
        super(TurretType.INFINITE.getUsername());
        targetingMode = TargetingMode.ALL;
    }

    public TurretMobInfinite(TurretMobInfiniteSaveable turretMobInfiniteSaveable) {
        super(TurretType.INFINITE.getUsername(), turretMobInfiniteSaveable);
        targetingMode = turretMobInfiniteSaveable.getTargetingMode();
    }

    @Override
    public TurretMobSaveable toSaveable() {
        return new TurretMobInfiniteSaveable(this);
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new TurretGuiInfinite(this);
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

    public TargetingMode getMode() {
        return targetingMode;
    }

    public void cycleMode() {
        targetingMode = targetingMode.cycle();
        updateGui();
    }

    @Override
    protected boolean shouldTarget(@Nullable Entity entity) {
        if (entity != null && entity.getScoreboardTags().contains(TURRET_TAG)) return false;
        if (entity instanceof Player) {
            return targetingMode.targetsPlayers() && super.shouldTarget(entity);
        } else {
            return targetingMode.targetsMobs() && super.shouldTarget(entity);
        }
    }


    public static class TurretMobInfiniteSaveable extends TurretMobSaveable {
        private String uuid;
        private TargetingMode targetingMode;

        public TurretMobInfiniteSaveable(TurretMobInfinite turretMobInfinite) {
            super(turretMobInfinite);
            this.uuid = turretMobInfinite.getUUID();
            this.targetingMode = turretMobInfinite.getMode();
        }

        public TurretMobInfiniteSaveable() {
        }

        @Override
        public TurretMob build() {
            return new TurretMobInfinite(this);
        }

        @Override
        public String getUUID() {
            return uuid;
        }

        public TargetingMode getTargetingMode() {
            return targetingMode;
        }
    }
}