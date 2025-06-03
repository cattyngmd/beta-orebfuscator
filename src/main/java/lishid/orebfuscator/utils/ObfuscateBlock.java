package lishid.orebfuscator.utils;

public class ObfuscateBlock {
    public int x;
    public int y;
    public int z;

    ObfuscateBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean compare(int X, int Y, int Z) {
        return this.x == X && this.y == Y && this.z == Z;
    }
}

