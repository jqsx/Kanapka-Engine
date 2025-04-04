package KanapkaEngine.Components;

import java.util.Iterator;
import java.util.List;

public final class ImmutableList<T> implements Iterable<T> {
    private final List<T> list;

    public ImmutableList(final List<T> list) {
        list.isEmpty();
        this.list = list;
    }

    public T get(int i) {
        return list.get(i);
    }

    public int size() {
        return list.size();
    }

    public boolean contains(T v) {
        return list.contains(v);
    }

    @Override
    public Iterator<T> iterator() {
        return new ImmutableIterator();
    }

    private class ImmutableIterator implements Iterator<T> {
        int index= 0;

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public T next() {
            T v = list.get(index);
            index++;
            return v;
        }
    }
}
