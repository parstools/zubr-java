package graph;

import java.util.Objects;

public class VertexEdge {
    private final int vertex;
    private final int edge;
    private int hashCode;

    public VertexEdge(int vertex, int edge) {
        this.vertex = vertex;
        this.edge = edge;
        this.hashCode = Objects.hash(vertex, edge);
    }

    public int getVertex() {
        return vertex;
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
        return vertex == that.vertex && edge == that.edge;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "[%d,%d]".formatted(vertex, edge);
    }
}
