package apple.voltskiya.custom_mobs.ai.aggro;

public class AmnesiaAggro {

    private long lastUpdated = 0;

    public void changeTarget() {
        this.lastUpdated = System.currentTimeMillis();
    }

    public boolean isRecentTargetChange(long recentMillis) {
        return System.currentTimeMillis() - this.lastUpdated <= recentMillis;
    }
}
