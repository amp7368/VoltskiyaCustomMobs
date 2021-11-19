package apple.voltskiya.custom_mobs.trash.old_turrets2.mobs;

import apple.voltskiya.custom_mobs.trash.old_turrets2.Old2TurretMobSaveable;
import apple.voltskiya.custom_mobs.trash.old_turrets2.Old2TurretType;
import apple.voltskiya.custom_mobs.trash.old_turrets2.gui.OldTurretGuiGM;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class Old2TurretMobGM extends Old2TurretMobInfinite {
    private OldTurretGuiGM turretGui = null;
    private double damage = 1;
    private int pierceLevel = 0;
    private int knockback = 0;
    private int flame = 0;

    public Old2TurretMobGM(Player player) {
        super(player, Old2TurretType.GM.getUsername());
    }

    public Old2TurretMobGM(Old2TurretMobGMSaveable saveable) {
        super(saveable, Old2TurretType.GM.getUsername());
        this.damage = saveable.getDamage();
        this.pierceLevel = saveable.getPierceLevel();
        this.knockback = saveable.getKnockback();
        this.flame = saveable.getFlame();
    }

    @Override
    public Old2TurretMobSaveable toSaveable() {
        return super.toSaveable();
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new OldTurretGuiGM(this);
        return turretGui;
    }

    @Override
    protected void updateGui() {
        super.updateGui();
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public int getFlame() {
        return flame;
    }

    @Override
    public int getKnockback() {
        return knockback;
    }

    @Override
    public int getPierceLevel() {
        return pierceLevel;
    }

    public void incrementDamage(double increment) {
        this.damage += increment;
    }


    public void incrementFlame(int increment) {
        this.flame += increment;
    }

    public void incrementKnockback(int increment) {
        this.knockback += increment;
    }

    public void incrementPierceLevel(int increment) {
        this.pierceLevel += increment;
    }

    public static class Old2TurretMobGMSaveable extends Old2TurretMobInfiniteSaveable {
        private double damage;
        private int pierceLevel;
        private int knockback;
        private int flame;

        public Old2TurretMobGMSaveable(Old2TurretMobGM turretMobInfinite) {
            super(turretMobInfinite);
            this.damage = turretMobInfinite.getDamage();
            this.pierceLevel = turretMobInfinite.getPierceLevel();
            this.knockback = turretMobInfinite.getKnockback();
            this.flame = turretMobInfinite.getFlame();
        }

        public Old2TurretMobGMSaveable() {

        }

        @Override
        public Old2TurretMob build() {
            return new Old2TurretMobGM(this);
        }

        public double getDamage() {
            return damage;
        }

        public int getPierceLevel() {
            return pierceLevel;
        }

        public int getKnockback() {
            return knockback;
        }

        public int getFlame() {
            return flame;
        }
    }
}
