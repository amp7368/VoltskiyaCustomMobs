package apple.voltskiya.custom_mobs.turrets.mobs;

import apple.voltskiya.custom_mobs.turrets.TurretMobSaveable;
import apple.voltskiya.custom_mobs.turrets.TurretType;
import apple.voltskiya.custom_mobs.turrets.gui.TurretGuiGM;
import org.bukkit.entity.Player;
import voltskiya.apple.utilities.util.gui.InventoryGui;

public class TurretMobGM extends TurretMobInfinite {
    private TurretGuiGM turretGui = null;
    private double damage = 1;
    private int pierceLevel = 0;
    private int knockback = 0;
    private int flame = 0;

    public TurretMobGM(Player player) {
        super(player, TurretType.GM.getUsername());
    }

    public TurretMobGM(TurretMobGMSaveable saveable) {
        super(saveable, TurretType.GM.getUsername());
        this.damage = saveable.getDamage();
        this.pierceLevel = saveable.getPierceLevel();
        this.knockback = saveable.getKnockback();
        this.flame = saveable.getFlame();
    }

    @Override
    public TurretMobSaveable toSaveable() {
        return super.toSaveable();
    }

    @Override
    protected InventoryGui getTurretGui(Player player) {
        if (turretGui == null)
            turretGui = new TurretGuiGM(this);
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

    public static class TurretMobGMSaveable extends TurretMobInfiniteSaveable {
        private double damage;
        private int pierceLevel;
        private int knockback;
        private int flame;

        public TurretMobGMSaveable(TurretMobGM turretMobInfinite) {
            super(turretMobInfinite);
            this.damage = turretMobInfinite.getDamage();
            this.pierceLevel = turretMobInfinite.getPierceLevel();
            this.knockback = turretMobInfinite.getKnockback();
            this.flame = turretMobInfinite.getFlame();
        }

        public TurretMobGMSaveable() {

        }

        @Override
        public TurretMob build() {
            return new TurretMobGM(this);
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
