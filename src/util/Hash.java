package util;

//djb2
public class Hash {
    int hash = 5381;

    public void add(int n) {
        hash = ((hash << 5) + hash) + n;
    }

    public int hash() {
        return hash;
    }
}
