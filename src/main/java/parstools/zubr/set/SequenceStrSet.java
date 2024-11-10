package parstools.zubr.set;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SequenceStrSet implements Iterable<SequenceStr> {
    HashSet<SequenceStr> set;
    public SequenceStrSet() {
        set = new HashSet<>();
    }

    public SequenceStrSet(SequenceStrSet source) {
        set = new HashSet<>(source.set);
    }

    public SequenceStrSet diff(SequenceStrSet ss) {
        SequenceStrSet result = new SequenceStrSet(this);
        result.set.removeAll(ss.set);
        return result;
    }

    public void add(SequenceStr seq) {
        set.add(seq);
    }

    @Override
    public Iterator<SequenceStr> iterator() {
        return new SequenceStrIterator();
    }

    public int size() {
        return set.size();
    }

    private class SequenceStrIterator implements Iterator<SequenceStr> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < set.size();
        }

        @Override
        public SequenceStr next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return set.iterator().next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int n = 0;
        for (SequenceStr seq : set) {
            if (n > 0)
                sb.append(" ");
            sb.append(seq.toString());
            n++;
        }
        sb.append("]");
        return sb.toString();
    }
}