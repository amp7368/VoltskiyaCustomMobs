package apple.voltskiya.custom_mobs.abilities.common.micro_missile;

public class MicroMissileConfig {

    public int minTicksToLive = 17;
    public int additionalTicksToLive = 40;
    public float speed = 0.6f;
    public float accelerationSpeed = 0.3f;
    public double variability = 7;
    public int randomAccelerationAngle = 30;
    public double damageAmount = 2.5;
    private static MicroMissileConfig instance;

    public MicroMissileConfig() {
        instance = this;
    }


    public static MicroMissileConfig get() {
        return instance;
    }
}
