package views.network;

import datamodel.Edge;
import datamodel.Trait;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

/**
 * This class, from the JUNG examples, allows us to switch between different
 * layouts when exploring the results. 
 * @author JUNG
 */
public final class LayoutChooser implements ActionListener
{
    private final JComboBox jcb;
    private final VisualizationViewer<Trait,Edge> vv;
    private final NetworkControlPanel ncp;
    Graph graph;

    /**
     * constructor, initializes members.
     * @param jcb : JComboBox used to select layout.
     * @param vv : VisualizationViewer on which the layout is drawn
     * @param g :
     * @param ncp : NetworkControlPanel on which jcb is located
     */
    public LayoutChooser(JComboBox jcb, VisualizationViewer<Trait,Edge> vv, Graph g, NetworkControlPanel ncp)
    {
        super();
        if(jcb==null || vv==null || g==null || ncp==null)
            throw new NullPointerException();
        this.jcb = jcb;
        this.vv = vv;
        this.ncp = ncp;
        graph = g;
    }

    /**
     * Switches layouts when one is selected.
     * @param arg0
     */
    public void actionPerformed(ActionEvent arg0)
    {
        Object[] constructorArgs =
            { graph};

        Class<? extends Layout> layoutC =
            (Class<? extends Layout>) jcb.getSelectedItem();
//            Class lay = layoutC;
        try
        {
            Constructor<? extends Layout> constructor = layoutC
                    .getConstructor(new Class[] {Graph.class});
            Object o = constructor.newInstance(constructorArgs);
            Layout l = (Layout) o;
            ncp.setLayoutClass(layoutC);
            l.setInitializer(vv.getGraphLayout());
            l.setSize(vv.getSize());

            LayoutTransition<Trait,Edge> lt =
                    new LayoutTransition<Trait,Edge>(vv, vv.getGraphLayout(), l);
            Animator animator = new Animator(lt);
            animator.start();
            vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
            vv.repaint();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return array of Classes of different layout choices.
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Layout>[] getCombos()
    {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(KKLayout.class);
        layouts.add(FRLayout.class);
        layouts.add(CircleLayout.class);
        layouts.add(SpringLayout.class);
        layouts.add(SpringLayout2.class);
        layouts.add(ISOMLayout.class);
        return layouts.toArray(new Class[0]);
    }

    public JComboBox getJCB()
    {
        return jcb;
    }
}
