import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StreetMap extends JComponent {
    private Graph graph = new Graph();
    private double minx = 200;
    private double maxy = 0;
    private double maxx = -200;
    private double miny = 100;

    public StreetMap(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));
            String s;
            while ((s = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(s);
                List<String> splited = new ArrayList<>();
                while (st.hasMoreTokens()) {
                    splited.add(st.nextToken());
                }
                if (splited.get(0).equals("i")) {
                    graph.addNode(splited.get(1), Double.parseDouble(splited.get(2)),
                            Double.parseDouble(splited.get(3)));
                    updateMinMax(Double.parseDouble(splited.get(3)), Double.parseDouble(splited.get(2)));
                } else if (splited.get(0).equals("r")) {
                    graph.addEdge(splited.get(1), splited.get(2), splited.get(3));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMinMax(double x, double y) {
        if (minx > x) {
            minx = x;
        }
        if (maxx < x) {
            maxx = x;
        }
        if (maxy < y) {
            maxy = y;
        }
        if (miny > y) {
            miny = y;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        for (Edge e : graph.edges) {
            setEdgeColorAndStroke(e, g2);

            double x1 = (720 / (maxx - minx)) * ((graph.nodeMap.get(e.getFrom())[0]) - minx) + 100;
            double x2 = (720 / (maxx - minx)) * ((graph.nodeMap.get(e.getTo())[0]) - minx) + 100;
            double y1 = (-720 / (maxy - miny)) * ((graph.nodeMap.get(e.getFrom())[1]) - maxy) + 100;
            double y2 = (-720 / (maxy - miny)) * ((graph.nodeMap.get(e.getTo())[1]) - maxy) + 100;

            Line2D line = new Line2D.Double(x1, y1, x2, y2);
            g2.draw(line);
        }
    }

    private void setEdgeColorAndStroke(Edge e, Graphics2D g2) {
        if ((graph.nodes.get(e.getFrom()).isHighlighted()) && (graph.nodes.get(e.getTo()).isHighlighted())) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2.5f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f));
        } else {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.2f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f));
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(1080, 1080);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the input file name as a command-line argument.");
            return;
        }

        String fileName = args[0];
        StreetMap visualizer = new StreetMap(fileName);
        visualizer.graph.modifyGraph();

        if (Arrays.asList(args).contains("show")) {
            if (Arrays.asList(args).contains("directions")) {
                String from = args[3];
                String to = args[4];
                List<String> temp = visualizer.graph.getShortestPath(from, to);
                if (temp.size() == 0) {
                    System.out.println("No path between " + from + " and " + to + ".");
                } else {
                    System.out.println("Shortest path: " + temp);
                    System.out.println("Total distance: " + visualizer.graph.nodes.get(to).getDistance() + " miles.");
                }
            }
            JFrame frame = new JFrame("Street Mapping");
            frame.add(visualizer);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } else if (Arrays.asList(args).contains("directions")) {
            String from = args[2];
            String to = args[3];
            List<String> temp = visualizer.graph.getShortestPath(from, to);
            if (temp.size() == 0) {
                System.out.println("No path between " + from + " and " + to + ".");
            } else {
                System.out.println("Shortest path: " + temp);
                System.out.println("Total distance: " + visualizer.graph.nodes.get(to).getDistance() + " miles.");
            }
        }
    }
}
