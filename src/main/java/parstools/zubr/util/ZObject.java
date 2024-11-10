package parstools.zubr.util;

import prasanthj.hasher.XXHash;

public abstract class ZObject {
    protected long hash;

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] longToByteArray(long value) {
        return new byte[] {
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public long computeHash() {
        return deepHash(0);
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
