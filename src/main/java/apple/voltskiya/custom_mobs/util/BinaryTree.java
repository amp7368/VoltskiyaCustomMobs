package apple.voltskiya.custom_mobs.util;

import java.util.Comparator;
import java.util.List;

public class BinaryTree<T> {
    private BinaryTree<T> left;
    private BinaryTree<T> right;
    private T value;

    public BinaryTree(T value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }

    /**
     * creates a binary tree
     *
     * @param objects a sorted by hashcode list of objects
     */
    public BinaryTree(List<T> objects) {
        objects.sort(Comparator.comparingInt(Object::hashCode));
        final int size = objects.size();
        if (size == 0) {
            this.value = null;
            this.left = null;
            this.right = null;
        }
        if (size == 1) {
            this.value = objects.get(0);
            this.left = null;
            this.right = null;
        } else if (size == 2) {
            this.value = objects.get(1);
            this.left = new BinaryTree<>(objects.get(0));
            this.right = null;
        } else {
            int mid = size / 2;
            this.value = objects.get(mid);
            this.left = new BinaryTree<>(objects.subList(0, mid));
            this.right = new BinaryTree<>(objects.subList(mid + 1, size));
        }
    }

    public BinaryTree() {
        this.value = null;
        this.left = null;
        this.right = null;
    }


    public void insert(T value) {
        insert(value, value.hashCode());
    }

    private void insert(T value, int hashCode) {
        if (this.value == null) {
            this.value = value;
            return;
        }
        final int myHash = this.value.hashCode();
        if (myHash > hashCode) {
            if (left == null) {
                left = new BinaryTree<>(value);
            } else {
                left.insert(value, hashCode);
            }
        } else {
            if (right == null) {
                right = new BinaryTree<>(value);
            } else {
                right.insert(value, hashCode);
            }
        }
    }

    public T remove(T value) {
        if (value == null || this.value == null) return null;
        final BinaryTree<T> removal = remove(value, value.hashCode());
        return removal == null ? null : removal.value;
    }

    private BinaryTree<T> remove(T value, int hashCode) {
        if (this.value == null) return null;
        final int myHash = this.value.hashCode();
        if (myHash == hashCode) {
            if (value.equals(this.value)) {
                return this;
            } else {
                BinaryTree<T> removal = left.remove(value, hashCode);
                if (removal == null) {
                    removal = right.remove(value, hashCode);
                    if (removal == null) return null;
                    else {
                        if (this.right == removal) {
                            this.right = null;
                        }
                        return removal;
                    }
                } else {
                    if (this.left == removal) {
                        this.left = null;
                    }
                    return removal;
                }
            }
        } else if (myHash > hashCode) {
            if (left == null) return null;
            BinaryTree<T> removal = left.remove(value, hashCode);
            if (removal == null) return null;
            else if (this.left == removal) {
                this.left = null;
            }
            return removal;
        } else {
            if (right == null) return null;
            BinaryTree<T> removal = right.remove(value, hashCode);
            if (removal == null) return null;
            else if (this.right == removal) {
                this.right = null;
            }
            return removal;
        }
    }

    public T getLowest() {
        if (left == null) return value;
        return left.getLowest();
    }

    public T getHighest() {
        if (right == null) return value;
        return right.getHighest();
    }

    public boolean contains(T value) {
        return contains(value, value.hashCode());
    }

    private boolean contains(T value, int hashCode) {
        final int myHash = this.value.hashCode();
        if (myHash == hashCode) {
            return this.value.equals(value) ||
                    (this.right != null && this.right.contains(value, hashCode)) ||
                    (this.left != null && this.left.contains(value, hashCode));
        } else if (myHash > hashCode) {
            return this.left != null && this.left.contains(value, hashCode);
        } else {
            return this.right != null && this.right.contains(value, hashCode);
        }
    }
}
