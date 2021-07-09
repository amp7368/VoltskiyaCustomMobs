package apple.voltskiya.custom_mobs.turrets.mobs;

import apple.voltskiya.custom_mobs.turrets.TurretMobSaveable;
import apple.voltskiya.custom_mobs.turrets.TurretType;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretMobPlayer extends TurretMob {
    private TurretGuiPlayer turretGui = null;
    private TargetingMode targetingMode;

    public TurretMobPlayer(Player player) {
        super(TurretType.PLAYER.getUsername());
        targetingMode = TargetingMode.ALL;
    }

    public TurretMobPlayer(TurretMobPlayerSaveable turretMobPlayerSaveable) {
        super(TurretType.PLAYER.getUsername(), turretMobPlayerSaveable);
        targetingMode = turretMobPlayerSaveable.getTargetingMode();
    }

    @Override
    public TurretMobSaveable toSaveable() {
        return new TurretMobPlayerSaveable(this);
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new TurretGuiPlayer(this);
        return turretGui;
    }

    @Override
    protected void updateGui() {
        if (turretGui != null) {
            if (turretGui.getInventory().getViewers().isEmpty()) turretGui = null;
            else turretGui.update();
        }
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
        if (entity instanceof Player) {
            return targetingMode.targetsPlayers && super.shouldTarget(entity);
        } else {
            return targetingMode.targetsMobs && super.shouldTarget(entity);
        }
    }

    public enum TargetingMode {
        NONE(0, "None", false, false),
        PLAYERS(1, "Players", true, false),
        MOBS(2, "Mobs", false, true),
        ALL(3, "All", true, true);

        private static TargetingMode[] modes = null;
        private final int index;
        private final String prettyName;
        private final boolean targetsPlayers;
        private final boolean targetsMobs;

        TargetingMode(int index, String prettyName, boolean targetsPlayers, boolean targetsMobs) {
            this.index = index;
            this.prettyName = prettyName;
            this.targetsPlayers = targetsPlayers;
            this.targetsMobs = targetsMobs;
        }

        private static TargetingMode[] getModes() {
            if (modes == null) {
                modes = new TargetingMode[values().length];
                for (TargetingMode mode : values()) {
                    modes[mode.index] = mode;
                }
            }
            return modes;
        }

        public TargetingMode cycle() {
            return getModes()[(index + 1) % values().length];
        }

        public String pretty() {
            return prettyName;
        }
    }

    public static class TurretMobPlayerSaveable extends TurretMobSaveable {
        private String uuid;
        private TargetingMode targetingMode;

        public TurretMobPlayerSaveable(TurretMobPlayer turretMobPlayer) {
            super(turretMobPlayer);
            this.uuid = turretMobPlayer.getUUID();
            this.targetingMode = turretMobPlayer.getMode();
        }

        public TurretMobPlayerSaveable() {
        }

        @Override
        public TurretMob build() {
            return new TurretMobPlayer(this);
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
