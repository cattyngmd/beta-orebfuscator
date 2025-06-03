package gnu.trove.iterator;

public interface TPrimitiveIterator
        extends TIterator {
    @Override
    boolean hasNext();

    @Override
    void remove();
}

