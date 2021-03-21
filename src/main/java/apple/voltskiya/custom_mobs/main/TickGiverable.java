package apple.voltskiya.custom_mobs.main;

public interface TickGiverable {
    long add(Runnable runnable);

    void remove(long uid);

    int getTickSpeed();
}
