package apple.voltskiya.custom_mobs.heartbeat.tick.main;

public interface TickGiverable {
    long add(Runnable runnable);

    void remove(long uid);

    int getTickSpeed();
}
