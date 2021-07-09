package apple.voltskiya.custom_mobs.turrets.mobs;

import apple.voltskiya.custom_mobs.turrets.TurretMobSaveable;
import apple.voltskiya.custom_mobs.turrets.TurretType;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiPlayer;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretMobPlayer extends TurretMob {
    private TurretGuiPlayer turretGui = null;

    public TurretMobPlayer(Player player) {
        super(TurretType.PLAYER.getUsername());
    }

    public TurretMobPlayer(TurretMobPlayerSaveable turretMobPlayerSaveable) {
        super(TurretType.PLAYER.getUsername(), turretMobPlayerSaveable);
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

    public static class TurretMobPlayerSaveable extends TurretMobSaveable {
        private String uuid;

        public TurretMobPlayerSaveable(TurretMobPlayer turretMobPlayer) {
            super(turretMobPlayer);
            this.uuid = turretMobPlayer.getUUID();
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
    }
}
