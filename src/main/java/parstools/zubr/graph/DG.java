package parstools.zubr.graph;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/***
 * Implementation of a directed graph
 */

public class DG {
    private final ArrayList<Vertex> vertices;

    // Graph vertices start at 0 but some subGraphs might have vertices that start at another int
    // Carry a map of original labels -> Example: startVertex = 7 is converted to 0 in the class
    private boolean hasLabel = false;
    private Map<Integer, Integer> originalToLabel;
    private Map<Integer, Integer> labelToOriginal;



    public DG (int vertexCount) {
        if (vertexCount < 0) throw new IllegalArgumentException("Vertex count can't be smaller than 0.");
        vertices = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            vertices.add(new Vertex(i));
        }
    }

    public DG (List<Integer> vertexLabels, int vertexCount) {
        this(vertexCount);
        hasLabel = true;
        this.originalToLabel = new HashMap<>();
        this.labelToOriginal = new HashMap<>();

        for (int i = 0; i < vertexLabels.size(); i++) {
            this.originalToLabel.put(i, vertexLabels.get(i));
            this.labelToOriginal.put(vertexLabels.get(i), i);
        }
    }

    public void addEdge(int from, int to, Object label) {
        assert(from>=0 && to >=0);
        if (hasLabel) {
            from = labelToOriginal.get(from);
            to = labelToOriginal.get(to);
        }
        assert(from>=0 && to >=0);
        vertices.get(from).addEdge(from, to, label);
    }

    public Vertex getVertex(int id) {
        return vertices.get(id);
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    public boolean hasLabel() {
        return this.hasLabel;
    }

    public int getLabel(int original) {
        return originalToLabel.get(original);
    }

    // Create a graph from a .txt file
    public static DG createGraphFromFile(String filename) {
        Path file = Paths.get(filename);

        try {
            List<int[]> parseResult =
                    Files.readAllLines(Paths.get(filename))
                            .stream()
                            .map(x -> x.split(" "))
                            .flatMap(Arrays::stream)
                            .filter(x -> !x.isEmpty())
                            .map(x -> x.replaceAll("\\{", "").replaceAll("}", "")
                                    .replaceAll(" ", ""))
                            .map(x -> x.split(","))
                            .map(x -> new int[]{ Integer.parseInt(x[0]), Integer.parseInt(x[1]) })
                            .collect(Collectors.toList());

            int maxIndex = parseResult.stream().flatMapToInt(Arrays::stream).max().getAsInt();
            DG graph = new DG(maxIndex + 1);

            int edgeCnt = 0;
            for(int[] arr : parseResult) {
                graph.addEdge(arr[0], arr[1], edgeCnt);
                edgeCnt++;
            }

            return graph;
        } catch (NoSuchFileException e) {
            throw new RuntimeException("There is no file at path: " + file.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new RuntimeException("An error has occurred. File path: " + file.toAbsolutePath().toString());
        }
    }

    public void display() {
        // Check if there vertex labels available
        if (hasLabel) {
            for (Vertex v : vertices) {
                for (Edge e : v.edges) {
                    System.out.println((originalToLabel.get(e.from)+1) + " -> " + (originalToLabel.get(e.to)+1));
                }
            }

        } else {
            for (Vertex v : vertices) {
                for (Edge e : v.edges) {
                    System.out.println(e.toString());
                }
            }
        }
    }


    private boolean DFS(Vertex node, Set<Vertex> visited, Set<Vertex> stack) {
        if (stack.contains(node)) {
            return true;  // Node is in stack, is cycle
        }
        if (visited.contains(node)) {
            return false; // Node precessed, no cycle
        }

        visited.add(node);
        stack.add(node);

        List<Vertex> adjList = node.getAdjacentVertices();
        for (Vertex neighbor : adjList) {
            if (DFS(neighbor, visited, stack)) {
                return true;
            }
        }

        stack.remove(node);
        return false;
    }

    public boolean detectCycle(int index) {
        Set<Vertex> visited = new HashSet<>();
        Set<Vertex> stack = new HashSet<>();
        Vertex node = vertices.get(index);
        visited.add(node);
        stack.add(node);
        List<Vertex> adjList = node.getAdjacentVertices();
        for (Vertex neighbor : adjList) {
            if (DFS(neighbor, visited, stack)) {
                return true;
            }
        }
        stack.remove(node);
        return false;
    }

    // ### Vertex ###
    public class Vertex {
        private int id;
        private ArrayList<Edge> edges;

        public Vertex (int id) {
            this.id = id;
            this.edges = new ArrayList<>();
        }

        public void addEdge(int from, int to, Object label) {
            edges.add(new Edge(from, to, label));
        }

        public ArrayList<Vertex> getAdjacentVertices() {
            ArrayList<Vertex> vertices = new ArrayList<>();
            for (Edge e : getEdges()) {
                vertices.add(getVertex(e.to));
            }
            return vertices;
        }

        public int getId() {
            return id;
        }

        public ArrayList<Edge> getEdges() {
            return edges;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return id == vertex.getId();
        }

    }

    // ### Edge ###
    public class Edge {
        private int from;
        private int to;
        private Object label;

        public Edge (int from, int to, Object label) {
            this.from = from;
            this.to = to;
            this.label = label;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public Object getLabel() {
            return label;
        }

        @Override
        public String toString() {
            if (hasLabel) {
                return originalToLabel.get(from) + " -> " + originalToLabel.get(to);
            } else {
                return from + " -> " + to;
            }
        }
    }
}
