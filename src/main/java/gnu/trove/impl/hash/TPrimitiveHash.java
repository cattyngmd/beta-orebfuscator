package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;

public abstract class TPrimitiveHash
        extends THash {
    public static final byte FREE = 0;
    public static final byte FULL = 1;
    public static final byte REMOVED = 2;
    static final long serialVersionUID = 1L;
    public transient byte[] _states;

    public TPrimitiveHash() {
    }

    public TPrimitiveHash(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public TPrimitiveHash(int initialCapacity, float loadFactor) {
        initialCapacity = Math.max(1, initialCapacity);
        this._loadFactor = loadFactor;
        this.setUp(HashFunctions.fastCeil((float) initialCapacity / loadFactor));
    }

    @Override
    public int capacity() {
        return this._states.length;
    }

    @Override
    protected void removeAt(int index) {
        this._states[index] = 2;
        super.removeAt(index);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._states = new byte[capacity];
        return capacity;
    }
}

