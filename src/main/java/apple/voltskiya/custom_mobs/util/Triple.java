package apple.voltskiya.custom_mobs.util;

public class Triple<X, Y, Z> {
    private X x;
    private Y y;
    private Z z;

    public Triple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public Z getZ() {
        return z;
    }

    public void setZ(Z z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s,%s>",
                x == null ? "null" : x.toString(),
                y == null ? "null" : y.toString(),
                z == null ? "null" : z.toString()
        );
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash += x.hashCode();
        hash += y.hashCode();
        hash += z.hashCode();
        return (int) (hash % Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            Triple t = (Triple) obj;
            return this.x.equals(t.x) && this.y.equals(t.y) && this.z.equals(t.z);
        }
        return false;
    }
}
