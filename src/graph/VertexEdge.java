package graph;

import java.util.Objects;

public class VertexEdge {
    private final int n;
    private final Object edge;
    private int hashCode;

    public VertexEdge(int vertex, Object edge) {
        this.n = vertex;
        this.edge = edge;
        this.hashCode = Objects.hash(vertex, edge);
    }

    public int getN() {
        return n;
    }

    public Object getEdge() {
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
        if (edge == null)
            return "[%d,null]".formatted(n);
        else
            return "[%d,%s]".formatted(n, edge.toString());
    }
}
