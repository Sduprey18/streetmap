import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Graph {
    Map<String, double[]> nodeMap;
    Map<String, Node> nodes;
    List<Edge> edges;
    private int edgeSize;

    public Graph() {
        nodeMap = new HashMap<>();
        nodes = new HashMap<>();
        edges = new ArrayList<>();
        edgeSize = 0;
    }

    public void addNode(String name, double latitude, double longitude) {
        nodeMap.put(name, new double[]{longitude, latitude});
    }

    public double getEdgeLength(String node1, String node2) {
        double[] coord1 = nodeMap.get(node1);
        double[] coord2 = nodeMap.get(node2);

        double diffX = (coord1[0] - coord2[0]) * 53.06;
        double diffY = (coord1[1] - coord2[1]) * 68.99;

        return Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
    }

    public void addEdge(String id, String from, String to) {
        edges.add(new Edge(id, from, to, getEdgeLength(from, to)));
        edgeSize++;
    }

    public void modifyGraph() {
        Edge[] edgeArray = edges.toArray(new Edge[0]);

        for (String name : nodeMap.keySet()) {
            nodes.put(name, new Node());
        }

        edgeSize = edgeArray.length;

        for (Edge edge : edgeArray) {
            nodes.get(edge.getFrom()).getEdges().add(edge);
            nodes.get(edge.getTo()).getEdges().add(edge);
        }
    }

    public void dijkstra(String start, String end) {
        String next = start;
        nodes.get(next).setDistance(0);

        for (String name : nodes.keySet()) {
            if (nodes.get(end).isVisited()) {
                break;
            }
            
            if (next.equals("")) {
                break;
            }

            List<Edge> temp = nodes.get(next).getEdges();

            for (Edge edge : temp) {
                String neighbor = edge.getNeighbor(next);

                if (!nodes.get(neighbor).isVisited()) {
                    double dist = nodes.get(next).getDistance() + edge.getLength();

                    if (dist < nodes.get(neighbor).getDistance()) {
                        nodes.get(neighbor).setDistance(dist);
                        nodes.get(neighbor).setPrev(next);
                    }
                }
            }

            nodes.get(next).setVisited(true);
            next = getPath();
        }
    }

    public String getPath() {
        String storage = "";
        double storage2 = Double.MAX_VALUE;

        for (String key : nodes.keySet()) {
            double temp = nodes.get(key).getDistance();

            if (!(nodes.get(key).isVisited()) && temp < storage2) {
                storage2 = temp;
                storage = key;
            }
        }

        return storage;
    }

    public List<String> getShortestPath(String a, String b) {
        List<String> path = new ArrayList<>();
        dijkstra(a, b);

        if (a.equals(b)) {
            path.add(a);
            return path;
        } else if (nodes.get(b).getDistance() == Double.MAX_VALUE) {
            return path;
        } else {
            Stack<String> stack = new Stack<>();
            String temp = b;
            stack.push(b);
            nodes.get(b).setHighlighted(true);

            while (!nodes.get(temp).getPrev().equals(a)) {
                temp = nodes.get(temp).getPrev();
                nodes.get(temp).setHighlighted(true);
                stack.push(temp);
            }

            stack.push(a);
            nodes.get(a).setHighlighted(true);

            while (!stack.isEmpty()) {
                path.add(stack.pop());
            }

            return path;
        }
    }
}
