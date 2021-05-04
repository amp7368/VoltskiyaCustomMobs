package apple.voltskiya.custom_mobs.util.data_structures;

public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s>", key == null ? "null" : key.toString(), value == null ? "null" : value.toString());
    }

    @Override
    public int hashCode() {
        long hash = 0;
        hash += key.hashCode();
        hash += value.hashCode();
        return (int) (hash % Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair t = (Pair) obj;
            return this.key.equals(t.key) && this.value.equals(t.value);
        }
        return false;
    }
}

