package KanapkaEngine.Components;

import java.util.Collection;
import java.util.Iterator;

public class ImmutableCollection<T> implements Iterable<T> {
    private final Collection<T> collection;

    public ImmutableCollection(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public Iterator<T> iterator() {
        return collection.iterator();
    }
}
