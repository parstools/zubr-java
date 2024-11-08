package util;

//djb2
public class Hash {
    int hash = 5381;

    public static int intHash(int n) {
        final int m = 0x80000000;
        final int a = 1103515245;
        final int c = 12345;
        return a * n + c;
    }

    public static int intXor(int hash, int n) {
        return hash ^ intHash(n);
    }

    public void add(int n) {
        hash = ((hash >>> 5) | (hash << (Integer.SIZE - 5))) ^ intHash(n);
    }

    public void addString(String str) {
        hash = str.hashCode();
    }

    public void xor(int n) {
        hash = intXor(hash, n);
    }

    public int hash() {
        return hash;
    }

}
