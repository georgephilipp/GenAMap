package views.network;

import datamodel.Edge;
import datamodel.Trait;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections15.Transformer;

/**
 *
 * control panel for the network graph. This controls much of the visualizations
 * that go on in the network view
 * @author rcurtis
 * @author Sharath
 * @author jmofatt
 */
public class NetworkControlPanel extends JPanel
{
    /**
     * The visualization view that shows the JUNG graph of traits
     */
    private VisualizationViewer<Trait, Edge> vv;
    /**
     * The networkview and the network control panel are, understandably, tightly coupled
     */
    private NetworkView nv;
    /**
     * A pointer to the layout chooser to allow for the changing to different layouts
     */
    private LayoutChooser lc;
    /**
     * The background color of the JUNG view
     */
    private final Color BACKCOLOR = new Color(246, 248, 254);
    /**
     * current network threshold
     */
    private double networkThresh = 1e-300; //current network threshold
    /**
     * highest value of all edges
     */
    private double threshMax = 0; //highest value of all edges
    /**
     * index of the edge with the highest value <= networkThresh
     */
    private int threshIndex = 0; //index of the edge with highest value <= networkThresh
    /**
     * the structure we got from the database that lists all the edges
     */
    private ArrayList<Edge> netStruct;
    /**
     * the current layout class to use to display the nodes
     */
    public static Class<? extends Layout> layoutClass;
    /**
     * Whether or not the edge labels should be turned on.
     */
    private static boolean edgeLabelsON = false;
    /**
     * Whether or not the edges between nodes should be weighted or not
     */
    private static boolean edgeWeightsON = true;
    /**
     * Whether or not the labels for the vertices should be turned on
     */
    private static boolean vertexLabelsON = true;
    /**
     * The current mode should always be picking, I think.
     */
    private Mode mode = Mode.PICKING;

    /**
     * The class that weights the edges
     */
    private class EdgeWeightedStroker implements Transformer<Edge, Stroke>
    {
        /**
         * Is weighted stroking enabled
         */
        private boolean enabled = true;
        /**
         * The stroke used to create the edges
         */
        private final Stroke basicStroke = new BasicStroke();

        /** constructor
         *
         * @param e
         */
        public EdgeWeightedStroker(boolean e)
        {
            enabled = e;
        }

        public Stroke transform(Edge e)
        {
            if (e == null)
            {
                throw new NullPointerException();
            }

            if (enabled)
            {
                float val = (float) (3 * (java.lang.Math.abs(e.weight) - networkThresh) / (threshMax * 1.01 - networkThresh));
                return new BasicStroke(val, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            }
            return basicStroke;
        }
    }

    /**
     * contructor, initializes some values.
     *
     * @param nv : NetworkView on which the layout is placed
     * @param layout : graph layout manager
     * @param netStruct : ArrayList of the members of the graph.
     */
    public NetworkControlPanel(NetworkView nv, ArrayList<Edge> netStruct)
    {
        //super(layout);
        if (nv == null || netStruct == null)
        {
            throw new NullPointerException();
        }

        setup(nv, netStruct);
    }

    /**
     * Set up is called from network view when we are ready for some things
     * to start happening. This is what gets the visualization all set up
     * and ready to go
     * @param nv
     * @param netStruct
     */
    public void setup(NetworkView nv, ArrayList<Edge> netStruct)
    {
        this.removeAll();
        this.netStruct = netStruct;
        Collections.sort(this.netStruct, //sort netStruct by edge weight to make threshold changes easier
                new Comparator<Edge>()
        {

            public int compare(Edge e1, Edge e2)
            {
                if (java.lang.Math.abs(e1.weight) < java.lang.Math.abs(e2.weight))
                {
                    return -1;
                }
                else
                {
                    if (java.lang.Math.abs(e1.weight) == java.lang.Math.abs(e2.weight))
                    {
                        return 0;
                    }
                    else
                    {
                        return 1;
                    }
                }
            }
        });
        for (int i = 0; i < this.netStruct.size(); i++)
        {
            while (i > 0
                    && (netStruct.get(i).weight == netStruct.get(i - 1).weight
                    || Math.abs(netStruct.get(i).weight) < Math.abs(netStruct.get(i - 1).weight)))
            {
                netStruct.get(i).weight += netStruct.get(i).weight * 1e-4;
            }

        }
        this.nv = nv;
        //setMinimumSize(new Dimension(100, 200));
        //setMaximumSize(new Dimension(200, 300));
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(800, 30));
        this.setBackground(BACKCOLOR);
        this.threshIndex = 0;
        this.networkThresh = 1e-300;
        this.threshMax = 0;
        layoutClass = CircleLayout.class;
        //this.revalidate();
        //this.updateUI();
    }

    /**
     * adds the functionality to the control panel.
     * @param vv : VisualizationViewer which is controled by this.
     */
    public void init(VisualizationViewer<Trait, Edge> vv, boolean isSameNodeSet)
    {
        if (vv == null)
        {
            throw new NullPointerException();
        }
        this.vv = vv;
        EdgeLabelStringer els = new EdgeLabelStringer(edgeLabelsON);
        vv.getRenderContext().setEdgeLabelTransformer(els);
        vv.getRenderContext().setEdgeStrokeTransformer(new EdgeWeightedStroker(edgeWeightsON));

        if (!isSameNodeSet)
        {
            vv.getRenderContext().setVertexLabelTransformer(new VertexLabelStringer(vertexLabelsON));
            vv.getRenderer().getVertexLabelRenderer().setPosition(Position.E);
            //addZoomControls();
            //addViewController();
            addThreshControls();
            //addLayoutChooser(vv.getGraphLayout().getGraph());
            //addButtons();

            //set mode
            final AntiScrollGraphMouseListener graphMouse = new AntiScrollGraphMouseListener();
            graphMouse.setMode(mode);
            vv.setGraphMouse(graphMouse);
        }
        else
        {
            addThreshControls();
        }
    }

    /**
     *
     * @return the current network edge threshold
     */
    public double getThresh()
    {
        return networkThresh;
    }

    /**
     * adds zoom in/out control buttons.
     */
    private void addZoomControls()
    {
        final ScalingControl scaler = new CrossoverScalingControl();
        JPanel zoomControls = new JPanel();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JLabel zoomlab = new JLabel();
        zoomlab.setText("Zoom");
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });

        zoomControls.add(plus);
        zoomControls.add(zoomlab);
        zoomControls.add(minus);

        zoomControls.setBackground(BACKCOLOR);
        add(zoomControls);

    }

    /**
     * adds button to switch between node/edge selection mode and viewing mode.
     * adds slider and scroller to determining network edge threshold.
     */
    private void addViewController()
    {
        JPanel viewControls = new JPanel();

        JButton reset = new JButton("reset");
        reset.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                if (e == null)
                {
                    throw new NullPointerException();
                }
                lc.actionPerformed(e);
            }
        });

        viewControls.add(reset);

        final AntiScrollGraphMouseListener graphMouse = new AntiScrollGraphMouseListener();
        graphMouse.setMode(Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        final JToggleButton modebtn = new JToggleButton();
        modebtn.setText("PICKING MODE");
        modebtn.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                if (modebtn.isSelected())
                {
                    modebtn.setText("TRANSFORM MODE");
                    graphMouse.setMode(Mode.TRANSFORMING);
                }
                else
                {
                    modebtn.setText("PICKING MODE");
                    graphMouse.setMode(Mode.PICKING);
                }
            }
        });

        viewControls.add(modebtn);

        viewControls.setBackground(BACKCOLOR);

        add(viewControls);
        //adjustEdges(networkThresh, value);
    }

    /**
     *
     * adds the Threshold control spinner and slider
     */
    private void addThreshControls()
    {
        JPanel thresholdJP = new JPanel();
        thresholdJP.setBackground(BACKCOLOR);

        thresholdJP.add(new JLabel("Network Threshold"));

        if (netStruct.size() > 0)
        {
            threshMax = Math.abs(netStruct.get(netStruct.size() - 1).weight);
        }

        double value = threshMax * 2 / 3;
        final int prec = 1000; //threshold slider resolution

        final JSlider slider = new JSlider();
        //slider.setSize(50, 30);
        slider.setBackground(BACKCOLOR);
        final JSpinner spinner = new JSpinner();

        final SpinnerNumberModel model = new SpinnerNumberModel(value, 0, threshMax, threshMax / 10);
        spinner.setBackground(BACKCOLOR);
        spinner.setModel(model);
        spinner.setEditor(new JSpinner.NumberEditor(spinner, ""));
        //spinner.setSize(100, 50);
        //spinner.setBounds(spinner.getX(), spinner.getY(), spinner.getWidth() * 10, spinner.getHeight());
        spinner.addChangeListener(new ChangeListener()
        {

            public void stateChanged(ChangeEvent e)
            {
                slider.setValue(Double.valueOf((Double) model.getValue() * prec / threshMax).intValue());
                //adjustEdges(networkThresh, ((double)val)/((double)prec));

            }
        });
        slider.setMaximum(prec);
        slider.setMinimum(0);
        slider.setValue(2 * prec / 3);
        slider.addChangeListener(new ChangeListener()
        {

            public void stateChanged(ChangeEvent e)
            {
                double val = slider.getValue() * threshMax / prec;
                model.setValue(val);
                adjustEdges(networkThresh, val);
            }
        });
        thresholdJP.add(slider);
        thresholdJP.add(spinner);
        add(thresholdJP, BorderLayout.EAST);
        adjustEdges(networkThresh, value);
    }

    /**
     * hides edges below the network threshold
     * @param oldT : previous threshold setting
     * @param newT : new threshold setting
     */
    private void adjustEdges(double oldT, double newT)
    {
        if (oldT < 0 || newT < 0)
        {
            throw new IllegalArgumentException();
        }
        networkThresh = newT;

        if (oldT < newT)
        { //remove edges
            for (; java.lang.Math.abs(netStruct.get(threshIndex).weight) < networkThresh && threshIndex < netStruct.size(); threshIndex++)
            {
                vv.getGraphLayout().getGraph().removeEdge(netStruct.get(threshIndex));
            }
        }
        else
        {
            if (oldT > newT && netStruct.size() > 0)
            { //add edges
                for (; java.lang.Math.abs(netStruct.get(threshIndex).weight) > networkThresh && threshIndex > 0; threshIndex--)
                {
                    (vv.getGraphLayout().getGraph()).addEdge(netStruct.get(threshIndex),
                            netStruct.get(threshIndex).getT1(),
                            netStruct.get(threshIndex).getT2());
                }
                if (threshIndex < 0)
                {
                    threshIndex++;
                }
            }
        }

        vv.repaint();
    }

    /**
     *
     * @param layoutC : layout to set current layout class to.
     */
    public void setLayoutClass(Class<? extends Layout> layoutC)
    {
        if (layoutC == null)
        {
            throw new NullPointerException();
        }
        layoutClass = layoutC;
    }

    /**
     * turns off vertex labels
     */
    public void turnOffVertexLabels()
    {
        ((VertexLabelStringer) vv.getRenderContext().getVertexLabelTransformer()).enabled = false;
        nv.updateUI();
        vertexLabelsON = false;
    }

    /**
     * turns on vertex labels
     */
    public void turnOnVertexLabels()
    {
        ((VertexLabelStringer) vv.getRenderContext().getVertexLabelTransformer()).enabled = true;
        nv.updateUI();
        vertexLabelsON = true;
    }

    /**
     * turns on edge labels
     */
    public void turnOnEdgeLabels()
    {
        ((EdgeLabelStringer) vv.getRenderContext().getEdgeLabelTransformer()).enabled = true;
        nv.updateUI();
        edgeLabelsON = true;
    }

    /**
     * turns off edge labels
     */
    public void turnOffEdgeLabels()
    {
        ((EdgeLabelStringer) vv.getRenderContext().getEdgeLabelTransformer()).enabled = false;
        nv.updateUI();
        edgeLabelsON = false;
    }

    /**
     * turns on edge weights
     */
    public void turnOnEdgeWeights()
    {
        ((EdgeWeightedStroker) vv.getRenderContext().getEdgeStrokeTransformer()).enabled = true;
        nv.updateUI();
        edgeWeightsON = true;
    }

    /**
     * turns off edge weights
     */
    public void turnOffEdgeWeights()
    {
        ((EdgeWeightedStroker) vv.getRenderContext().getEdgeStrokeTransformer()).enabled = false;
        nv.updateUI();
        edgeWeightsON = false;
    }

    /**
     * resets the view back to its default
     */
    public void resetView()
    {
        setLayout(layoutClass);
    }

    /**
     * updates the view with a fresh look in the new layout
     * @param layoutC the layout to set the visualization to.
     */
    public void setLayout(Class<? extends Layout> layoutC)
    {
        try
        {
            Object[] constructorArgs =
            {
                vv.getGraphLayout().getGraph()
            };
            Constructor<? extends Layout> constructor = layoutC.getConstructor(new Class[]
                    {
                        Graph.class
                    });
            Object o = constructor.newInstance(constructorArgs);
            Layout l = (Layout) o;
            layoutClass = layoutC;
            l.setInitializer(vv.getGraphLayout());
            l.setSize(vv.getSize());

            LayoutTransition<Trait, Edge> lt =
                    new LayoutTransition<Trait, Edge>(vv, vv.getGraphLayout(), l);
            Animator animator = new Animator(lt);
            animator.start();
            vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
            vv.repaint();
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    /**
     * applys the current layout to a new graph
     * @param g : new graph to be layed out
     * @return layout of the new graph in same format as the current layout.
     */
    public Layout<Trait, Edge> getNewLayout(Graph g, Dimension size)
    {
        if (g == null)
        {
            throw new NullPointerException();
        }

        Layout<Trait, Edge> layout = new CircleLayout(g);
        try
        {
            Constructor<? extends Layout> layoutConstructor =
                    layoutClass.getConstructor(new Class[]
                    {
                        Graph.class
                    });
            Object[] constructorArgs =
            {
                g
            };
            layout = layoutConstructor.newInstance(constructorArgs);
            layout.setSize(size);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return layout;

    }

    /**
     * Are the vertex labels currently turned on?
     * @return
     */
    public boolean getVertexLabelsON()
    {
        return vertexLabelsON;
    }

    /**
     * Are the edge labels currently turned on?
     * @return
     */
    public boolean getEdgeLabelsON()
    {
        return edgeLabelsON;
    }

    /**
     * Are the edge weights currently turned on?
     * @return
     */
    public boolean getEdgeWeightsON()
    {
        return edgeWeightsON;
    }

    /**
     * What is the current layout class?
     * @return
     */
    public Class<? extends Layout> getLayoutClass()
    {
        return layoutClass;
    }
}
