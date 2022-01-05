package apple.voltskiya.custom_mobs.util.ticking;

public interface TickGiverable {
    long add(Runnable runnable);

    void remove(long uid);

    int getTickSpeed();
}
