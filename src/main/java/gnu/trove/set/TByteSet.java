package gnu.trove.set;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;

import java.util.Collection;

public interface TByteSet
        extends TByteCollection {
    @Override
    byte getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(byte var1);

    @Override
    TByteIterator iterator();

    @Override
    byte[] toArray();

    @Override
    byte[] toArray(byte[] var1);

    @Override
    boolean add(byte var1);

    @Override
    boolean remove(byte var1);

    @Override
    boolean containsAll(Collection<?> var1);

    @Override
    boolean containsAll(TByteCollection var1);

    @Override
    boolean containsAll(byte[] var1);

    @Override
    boolean addAll(Collection<? extends Byte> var1);

    @Override
    boolean addAll(TByteCollection var1);

    @Override
    boolean addAll(byte[] var1);

    @Override
    boolean retainAll(Collection<?> var1);

    @Override
    boolean retainAll(TByteCollection var1);

    @Override
    boolean retainAll(byte[] var1);

    @Override
    boolean removeAll(Collection<?> var1);

    @Override
    boolean removeAll(TByteCollection var1);

    @Override
    boolean removeAll(byte[] var1);

    @Override
    void clear();

    @Override
    boolean forEach(TByteProcedure var1);

    @Override
    boolean equals(Object var1);

    @Override
    int hashCode();
}

