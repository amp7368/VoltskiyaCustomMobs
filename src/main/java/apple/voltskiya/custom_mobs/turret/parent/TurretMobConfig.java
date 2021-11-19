package apple.voltskiya.custom_mobs.turret.parent;

import ycm.yml.manager.fields.YcmField;

public class TurretMobConfig {
    @YcmField
    public double maxHealth = 200;
    @YcmField
    public double maxSight = 75;
    @YcmField
    public double maxRotationAngle = 45;
    @YcmField
    public double shotSpeed = 4;
    @YcmField
    public double minSight = 3;
    @YcmField
    public double defaultDamage = 3;
    @YcmField
    public int healthPerRepair = 10;

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public double getMaxSight() {
        return this.maxSight;
    }

    public double maxRotationAngle() {
        return this.maxRotationAngle;
    }

    public double getMinSight() {
        return minSight;
    }

    public double getShotSpeed() {
        return shotSpeed;
    }

    public double getDefaultDamage() {
        return defaultDamage;
    }

    public int getHealthPerRepair() {
        return healthPerRepair;
    }

    public int getArrowSlots() {
        return 6;
    }

    public int getArrowStackSize() {
        return 32;
    }
}
