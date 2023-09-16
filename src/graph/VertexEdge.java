package graph;

import java.util.Objects;

public class VertexEdge {
    private final int n;
    private final int edge;
    private int hashCode;

    public VertexEdge(int vertex, int edge) {
        this.n = vertex;
        this.edge = edge;
        this.hashCode = Objects.hash(vertex, edge);
    }

    public int getN() {
        return n;
    }

    public int getEdge() {
        return edge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VertexEdge that = (VertexEdge) o;
        return n == that.n && edge == that.edge;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "[%d,%d]".formatted(n, edge);
    }
}
