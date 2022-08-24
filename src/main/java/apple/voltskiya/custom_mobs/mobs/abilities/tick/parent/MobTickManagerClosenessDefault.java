package apple.voltskiya.custom_mobs.mobs.abilities.tick.parent;

public enum MobTickManagerClosenessDefault implements MobTickCloseness {
    CLOSE1(30, 1),
    CLOSE2(40, 2),
    CLOSE3(50, 3),
    CLOSE4(60, 4),
    CLOSE5(70, 5),
    CLOSE6(80, 10),
    CLOSE7(90, 12),
    CLOSE8(100, 20),
    CLOSE9(200, 60);
    private final double distance;
    private final int ticksPerRun;

    MobTickManagerClosenessDefault(double distance, int ticksPerRun) {
        this.distance = distance;
        this.ticksPerRun = ticksPerRun;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public int getTicksPerRun() {
        return ticksPerRun;
    }
}
