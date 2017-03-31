
package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Arbor
 * @date 2016/7/19
 */
public class GraphPanelShowPath extends JApplet implements ActionListener{
    private ArrayList<JCheckBox> e_paths = new ArrayList<>();

    protected Graph<NetworkVertex, NetworkEdge> g;
    protected VisualizationViewer<NetworkVertex, NetworkEdge> vv;
    protected DefaultModalGraphMouse<NetworkVertex, NetworkEdge> gm;
    protected PathDisplayPredicate<NetworkVertex, NetworkEdge> show_path;

    private ArrayList<String> pathList;
    private ArrayList<Color> pathColor = new ArrayList<Color>(){{
        add(Color.BLACK);
        add(Color.BLUE);
        add(new Color(121,232,113));
        add(new Color(148,70,84));
        add(new Color(232,129,34));
        add(new Color(0,150,255));
        add(new Color(153,12,232));
        add(new Color(232,159,147));
        add(new Color(55,34,72));
        add(new Color(60,120,126));
        add(Color.GREEN);
        add(Color.MAGENTA);
        add(Color.RED);
    }};
    public GraphPanelShowPath(ArrayList<String> pathList) {
        this.pathList = pathList;
    }

    public void init() {
        g = new SparseMultigraph<>();

        // add vertex
        NetworkVertex[] nodes = new NetworkVertex[100]; // 节点数

        NetworkVertex[] nullNodes = new NetworkVertex[100]; //未检测的节点数


        /*pathList = new ArrayList<String>(){{
            add("1,0,13,14,4,5");
            add("1,0,10,12,4,6");
            add("2,0,8,9,4,5");
            add("3,0,8,4,6");
            add("1,0,13,14,4,5");
            add("1,0,10,12,4,6");
            add("2,0,8,9,4,5");
            add("3,0,8,4,6");
            add("1,0,13,14,4,5");
            add("1,0,10,12,4,6");
            add("2,0,8,9,4,5");
            add("3,0,8,4,6");
        }};*/
        HashMap<String, Integer> ip2Vertice = new HashMap<String, Integer>();

        File file = new File("configs/ip2Vertice.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            reader.readLine();
            while ((tempString = reader.readLine()) != null) {
                String list[] = tempString.replaceAll(" ","").split(",");
                String node = list[0];
                for (int i = 1; i < list.length; i ++) {
                    ip2Vertice.put(list[i], Integer.parseInt(node));
                }
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }


        int pathIdCount = 1;
        for (String s : pathList){
            String[] vertice =  s.split(",");
            vertice[0] = ip2Vertice.get(vertice[0])+"";
            vertice[vertice.length-1] = ip2Vertice.get(vertice[vertice.length-1])+"";

            int countNullNodes = 0;
            boolean flag = false;
            for (int i = 0; i < vertice.length - 1; i++) {
                int startId;
                int endId;
                NetworkVertex startNode;
                NetworkVertex endNode;

                if (vertice[i].equals("*")) {
                    if (flag) {
                        flag = false;
                    } else
                        countNullNodes++;
                    if (nullNodes[countNullNodes]==null) {
                        NetworkVertex n;
                        n = new NetworkVertex(-1 * countNullNodes);
                        nullNodes[countNullNodes] = n;
                        g.addVertex(n);
                    }
                    startNode = nullNodes[countNullNodes];
                } else {
                    startId = Integer.parseInt(vertice[i]);
                    if (nodes[startId]==null) {
                        NetworkVertex n;
                        n = new NetworkVertex(startId);
                        nodes[startId] = n;
                        g.addVertex(n);
                    }
                    startNode = nodes[startId];
                }

                if (vertice[i+1].equals("*")) {
                    if (!flag) {
                        flag = true;
                        countNullNodes++;
                    }
                    if (nullNodes[countNullNodes]==null) {
                        NetworkVertex n;
                        n = new NetworkVertex(-1 * countNullNodes);
                        nullNodes[countNullNodes] = n;
                        g.addVertex(n);
                    }
                    endNode = nullNodes[countNullNodes];
                } else {
                    endId = Integer.parseInt(vertice[i+1]);
                    if (nodes[endId]==null) {
                        NetworkVertex n;
                        n = new NetworkVertex(endId);
                        nodes[endId] = n;
                        g.addVertex(n);
                    }
                    endNode = nodes[endId];
                }

                if (g.findEdge(startNode, endNode) == null)
                    g.addEdge(new NetworkEdge(0, startNode, endNode), startNode, endNode, EdgeType.UNDIRECTED);
                g.addEdge(new NetworkEdge(pathIdCount, startNode, endNode), startNode, endNode, EdgeType.DIRECTED);
            }
            pathIdCount ++;
        }


        System.out.println("The graph = " + g.toString());
        Layout<NetworkVertex, NetworkEdge> layout = new FRLayout<>(g);
//        layout.setSize(new Dimension(600, 600));

//        BasicVisualizationServer<Integer, String> vv = new BasicVisualizationServer<Integer, String>(layout);
        vv = new VisualizationViewer<NetworkVertex, NetworkEdge>(layout);
//        vv.setPreferredSize(new Dimension(650, 650));

//        vv.getRenderContext().setEdgeStrokeTransformer();
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(new Function<NetworkVertex, Paint>() {
            @Nullable
            @Override
            public Paint apply(@Nullable NetworkVertex networkVertex) {
                return Color.YELLOW;
            }
        });
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

//        vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.quadCurve(g));

        /*vv.getRenderContext().setParallelEdgeIndexFunction(new EdgeIndexFunction<NetworkVertex, NetworkEdge>() {
            @Override
            public int getIndex(Graph<NetworkVertex, NetworkEdge> graph, NetworkEdge s) {
                int index = 0;
                index = s.getPathID();

                return index;
            }

            @Override
            public void reset(Graph<NetworkVertex, NetworkEdge> graph, NetworkEdge s) {

            }

            @Override
            public void reset() {

            }
        });*/

        vv.getRenderContext().setEdgeShapeTransformer(new Function<NetworkEdge, Shape>() {
            @Nullable
            @Override
            public Shape apply(@Nullable NetworkEdge s) {
                if (g.getEdgeType(s).equals(EdgeType.UNDIRECTED)) {
//                    vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(g));
                    return EdgeShape.line(g).apply(s);
                } else if (g.getEdgeType(s).equals(EdgeType.DIRECTED)) {
                    EdgeShape.QuadCurve transformer = EdgeShape.quadCurve(g);
                    transformer.setEdgeIndexFunction(new EdgeIndexFunction<NetworkVertex, NetworkEdge>() {
                        @Override
                        public int getIndex(Graph<NetworkVertex, NetworkEdge> graph, NetworkEdge s) {
                            int index = 0;
                            index = s.getPathID();

                            return index;
                        }

                        @Override
                        public void reset(Graph<NetworkVertex, NetworkEdge> graph, NetworkEdge s) {

                        }

                        @Override
                        public void reset() {

                        }
                    });

                    return transformer.apply(s);
                }
                return null;
            }
        });

        vv.getRenderContext().setEdgeDrawPaintTransformer(new Function<NetworkEdge, Paint>() {
            @Nullable
            @Override
            public Paint apply(@Nullable NetworkEdge networkEdge) {
                Paint p = Color.BLACK;
                if (networkEdge.getPathID() < pathColor.size())
                    p = pathColor.get(networkEdge.getPathID()%pathColor.size());
                /*switch (networkEdge.getPathID()){
                    case 1:
                        p = Color.BLUE;
                        break;
                    case 2:
                        p = Color.GREEN;
                        break;
                    case 3:
                        p = Color.MAGENTA;
                        break;
                }*/
                return p;
            }
        });

        vv.getRenderContext().setEdgeStrokeTransformer(new Function<NetworkEdge, Stroke>() {
            @Nullable
            @Override
            public Stroke apply(@Nullable NetworkEdge networkEdge) {
                if (g.getEdgeType(networkEdge).equals(EdgeType.UNDIRECTED))
                    return  new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f);
                else
                    return new BasicStroke(2.0f);
//                return null;
            }
        });
//        vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.quadCurve(g));

        gm = new DefaultModalGraphMouse<NetworkVertex, NetworkEdge>();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);

        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);


        jp.add(scrollPane, BorderLayout.CENTER);
        addBottomControls( jp );

        vv.getRenderContext().setEdgeIncludePredicate(show_path);

        getContentPane().add(jp);
    }

    protected void addBottomControls(final JPanel jp) {
        final JPanel control_panel = new JPanel();
        jp.add(control_panel, BorderLayout.EAST);
        control_panel.setLayout(new BorderLayout());

        // add zoom controls
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });

        JPanel zoomPanel = new JPanel();
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        plus.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomPanel.add(plus);
        minus.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomPanel.add(minus);

        // add path checkBox
        JPanel show_edge_panel = new JPanel(new GridLayout(0,2));
        show_edge_panel.setBorder(BorderFactory.createTitledBorder("Show path"));
        for (int i = 0; i < pathList.size(); i++) {
            JCheckBox e_show_path = new JCheckBox(i+1+"");
            e_show_path.addActionListener(this);
            e_show_path.setSelected(true);
            e_paths.add(e_show_path);
            show_edge_panel.add(e_show_path);

            Label legend = new Label("------");
            legend.setForeground(pathColor.get((i+1)%pathColor.size()));
            show_edge_panel.add(legend);
        }

        boolean[] array = new boolean[pathList.size()];
        Arrays.fill(array, true);
        show_path = new PathDisplayPredicate<>(array);

        control_panel.add(zoomPanel, BorderLayout.SOUTH);
        control_panel.add(show_edge_panel, BorderLayout.CENTER);
        control_panel.add(gm.getModeComboBox(), BorderLayout.NORTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractButton source = (AbstractButton)e.getSource();
        if (e_paths.contains(source)) {
            int pathId = Integer.parseInt(source.getText()) - 1;
            show_path.showPath(pathId, source.isSelected());
        }

        vv.repaint();

    }

    private final static class PathDisplayPredicate<V,E>
            implements Predicate<Context<Graph<V,E>,E>>
            //extends AbstractGraphPredicate<V,E>
    {
        protected boolean[] show_path;

        public PathDisplayPredicate(boolean[] show_path)
        {
            this.show_path = show_path;
        }

        public void showPath(int i, boolean b) {
            this.show_path[i] = b;
        }

        public boolean apply(Context<Graph<V,E>,E> context)
        {
            Graph<V,E> graph = context.graph;
            E e = context.element;
            int pathId = 0;
            if (e instanceof NetworkEdge) {
                pathId = ((NetworkEdge) e).getPathID();
            }

            if (graph.getEdgeType(e) == EdgeType.DIRECTED && show_path[pathId-1])
                return true;
            if (graph.getEdgeType(e) == EdgeType.UNDIRECTED)
                return true;
            return false;
        }
    }

    public static void main(String[] args) {
//        AddNodeDemo and = new AddNodeDemo();
//        AnimatingAddNodeDemo and = new AnimatingAddNodeDemo();
//        ClusteringDemo and = new ClusteringDemo();
//        EdgeLabelDemo and = new EdgeLabelDemo();
//        ShortestPathDemo and = new ShortestPathDemo();
       /* GraphPanelShowPath view = new GraphPanelShowPath();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(view);

        view.init();

        frame.pack();
        frame.setVisible(true);*/
    }
}


class NetworkVertex {
    private int id;
    private String ip;

    public NetworkVertex(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (id < 0)
            return "*";
        return id+"";
    }

}

class NetworkEdge {
    private int pathID; // 无向边为0
    private NetworkVertex start;
    private NetworkVertex end;

    public NetworkEdge(int id, NetworkVertex start, NetworkVertex end) {
        this.pathID = id;
        this.start = start;
        this.end = end;
    }

    public int getPathID() {
        return pathID;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

