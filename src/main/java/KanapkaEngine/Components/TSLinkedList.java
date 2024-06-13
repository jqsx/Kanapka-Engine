package KanapkaEngine.Components;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Thread Safe Linked List<br>
 * <strong>Made by JQSx</strong>
 * @param <T>
 */
public class TSLinkedList<T> {

    private Element root;

    private int size = 0;

    public TSLinkedList() {

    }

    public final int getSize() {
        return size;
    }

    public final int getAccurateSize() {
        int s = 0;
        Element last = root;
        while (last.next != null) {
            s++;
            last = last.next;
        }
        return this.size = s;
    }

    public boolean contains(T value) {
        Element last = getRoot();
        while (last != null) {
            if (last.value.equals(value))
                return true;
            last = last.next;
        }

        return false;
    }

    public final void clear() {
        root = null;
        size = 0;
    }

    public void remove(T t) {
        Element last = getRoot();

        while (last != null) {
            if (last.value == t) {
                last.remove();
                return;
            }

            last = last.next;
        }
    }

    public void removeIf(Predicate<T> predicate) {
        Element last = getRoot();

        if (last == null) return;

        while (last != null) {
            if (predicate.test(last.getValue()))
                last.remove();

            last = last.next;
        }
    }

    public void foreach(Consumer<T> action) {
        Element last = getRoot();

        while (last != null) {
            action.accept(last.getValue());

            last = last.next;
        }
    }

    public final T get(int index) {
        int i = 0;
        Element last = root;
        while (last != null) {
            if (i == index) {
                return last.getValue();
            }
            last = last.getNext();
        }
        return null;
    }

    public final void addEnd(T value) {
        Objects.requireNonNull(value);
        if (root == null) {
            addStart(value);
            return;
        }
        Element last = root;
        while (last.next != null) {
            last = last.next;
        }
        last.next = new Element(value, last, null);
        size++;
    }

    public final void addStart(T value) {
        Objects.requireNonNull(value);
        if (root == null)
            root = new Element(value);
        else {
            root = new Element(value, null, root);
        }
        size++;
    }

    public final Element getRoot() {
        return root;
    }

    public class Element {
        private final T value;
        private Element last;
        private Element next;

        private boolean removed = false;

        protected Element(T value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        protected Element(T value, Element last, Element next) {
            this(value);
            this.last = last;
            if (last != null) {
                last.next = this;
            }
            this.next = next;
            if (next != null) {
                next.last = this;
            }
        }

        public final Element getLast() {
            return last;
        }

        public final Element getNext() {
            return next;
        }

        public final boolean hasNext() {
            return next != null;
        }

        public final boolean isRoot() {
            if (removed) return false;
            return root == this;
        }

        public final boolean isRemoved() {
            return removed;
        }

        public final void remove() {
            if (removed) return;
            if (root == this) {
                root = this.next;
                if (root != null)
                    root.last = null;
            }
            if (last != null)
                last.next = next;
            if (next != null)
                next.last = last;

            removed = true;
            size--;
        }

        public final T getValue() {
            return value;
        }

        protected final Element clone() {
            return new Element(value, last, next);
        }
    }
}
