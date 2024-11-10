package parstools.zubr.util;

//import net.openhft.hashing.XxHash;

import prasanthj.hasher.XXHash;

import java.nio.ByteBuffer;

public abstract class ZObject {
    private long hash;
    public long computeHash(boolean deep) {
        if (deep)
            hash = deepHash(0);
        else
            hash = shallowHash(0);
        return hash;
    }

    public long getHash() {
        return hash;
    }

    protected long shallowHash(long seed) {
        byte[] data = getBytes();
        hash = XXHash.hash64(data, data.length, seed);
        return hash;
    }

    protected static final int INT_SIZE = 4;
    protected static final int LONG_SIZE = 8;

    protected abstract byte[] getBytes();

    protected abstract long deepHash(long seed);
}
