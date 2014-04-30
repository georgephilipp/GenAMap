package views.network.threeway;

import datamodel.ThreeWayVisualizationSettings;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

/**
 * This class implements laying out the nodes in a 3 way association view.
 * We want complete control over this layout because we have the new
 * behavior with the two types of nodes ... thus we build off of JUNG view's
 * implementation. This class forms some kind of KK-layout for the traits,
 * and then orders the genes based on size. 
 * @author rcurtis
 */
public class ThreeWayLayout extends AbstractLayout<ThreeWayGraphNode, ThreeWayGraphEdge> implements IterativeContext, Observer
{
    ////////////////////////////////////////////
    //     VARIABLES THAT CAN BE CHANGED      //
    ////////////////////////////////////////////
    /**
     * This variable is set to true if it the layout is to display a kk layout, if
     * false, it displays a force-directed (spring) layout
     */
    private boolean isKKLayout;
    /**
     * The stretch parameter for the spring layout.
     */
    protected double stretch = 0.80;
    /**
     * KKLayout - A multiplicative factor which partly specifies the "preferred" length of an edge (L).
     */
    private double length_factor = 0.9;
    /**
     * SpringLayout - the repulsion distance between nodes
     */
    protected int repulsion_range_sq = 60 * 60;
    /**
     * SpringLayout - needs to be refactored
     */
    protected double correlation_force_multiplier = 12.0 / 3.0;
    /**
     * The number of iterations that need to be completed for the layout to
     * settle into an appropriate layout. 
     */
    private int maxIterations = 1000;
    /**
     * KKLayout - A multiplicative factor which specifies the fraction of the graph's diameter to be
     * used as the inter-vertex distance between disconnected vertices.
     */
    private double disconnected_multiplier = 0.5;
    /**
     * In the Spring Layout - the force by association edges. 
     */
    private double association_force_multiplier = 4.0 / 3.0;
    /**
     * SpringLayout - The repulsion factor of how hard genes repulse eachother
     */
    private double gene_repulsion_factor = 50;
    /**
     * SpringLayout - How hard traits repulse each other.
     */
    private double trait_repulsion_factor = 2;
    ////////////////////////////////////////////
    //     VARIABLES HIDDEN FROM THE USER     //
    ////////////////////////////////////////////
    /**
     * A variable to ensure that step isn't called twice in a row before one completes
     *  I am not sure how they do their threading ... 
     */
    private boolean inProcess = false;
    /**
     * This value ensure that we do not start stepping before
     * initialization has taken place
     */
    private boolean isInited = false;
    /**
     * A variable that determines where the genes will be placed in the
     * right hand side of the graph. 
     */
    private ThreeWayGeneGroupNode[] order;
    /**
     * This is a pointer to the settings that control what we show here in this
     * visualization object
     */
    private ThreeWayVisualizationSettings settings;
    /**
     * This is the counter that can be reset when we want the graph to update
     * itself again. 
     */
    private int currentIteration;
    /**
     * The distance between traits and trait groups in the KK Layout
     */
    private double[][] dm;     // distance matrix
    /**
     * The collection of traits and trait groups for the KK Layout
     */
    private ThreeWayGraphNode[] vertices;
    /**
     * The xy location data for the KK Layout. 
     */
    private Point2D[] xydata;
    /**
     * Ideal lenght of an edge - set during initialization ... I think
     */
    private double L;			// the ideal length of an edge
    /**
     * KK Layout arbitrary constant
     */
    private double K = 1;		// arbitrary const number
    private boolean adjustForGravity = true;
    private boolean exchangeVertices = true;
    private double EPSILON = 0.000005d;
    /**
     * Retrieves graph distances between vertices of the visible graph
     */
    protected Distance<ThreeWayGraphNode> distance;
    /**
     * A class used to store information about how the spring layout moves. 
     */
    protected static class SpringVertexData
    {
        protected double edgedx;
        protected double edgedy;
        protected double repulsiondx;
        protected double repulsiondy;
        /** movement speed, x */
        protected double dx;
        /** movement speed, y */
        protected double dy;
    }
    /**
     * For the force-directed layout we keep track of the distance with this map I think ...
     */
    protected Map<ThreeWayGraphNode, SpringVertexData> springVertexData =
            LazyMap.decorate(new HashMap<ThreeWayGraphNode, SpringVertexData>(),
            new Factory<SpringVertexData>()
            {
                public SpringVertexData create()
                {
                    return new SpringVertexData();
                }
            });

    //////////////////////////////////////////
    //      CONSTRUCTOR AND OVERRIDES       //
    //////////////////////////////////////////
    /**
     * Creates an instance for the specified graph and distance metric.
     */
    public ThreeWayLayout(Graph<ThreeWayGraphNode, ThreeWayGraphEdge> g, ThreeWayVisualizationSettings settings, Dimension sz)
    {
        super(g);
        this.distance = new ThreeWayDistance(g);
        this.size = sz;
        this.settings = settings;
        settings.addObserver(this);
        update(null, null);
    }

    /**
     * This one is an incremental visualization.
     */
    public boolean isIncremental()
    {
        return true;
    }

    /**
     * Returns true once the current iteration has passed the maximum count.
     */
    public boolean done()
    {
        //we want it to keep calling step in case the user decides to update parms
        return false;
    }

    /**
     * We dont' do anything with this so we can control when it is called. 
     */
    public void initialize()
    {
    }

    public void reset()
    {
    }

    @Override //I've tried to avoid this method as it has caused me some problems. 
    public void setSize(Dimension size)
    {
        if (initialized == false)
        {
            setInitializer(new RandomLocationTransformer<ThreeWayGraphNode>(size));
        }
        super.setSize(size);
    }

    //////////////////////////////////////////
    //      MY METHODS                      //
    //////////////////////////////////////////
    /**
     * The KKLayout needs a bunch of variables initialized, and the gene groups
     * all set up. We do that in this method. This method must be completed
     * before the step method begins to be called. This method initializes all
     * the KKLayout variables. 
     */
    public void init()
    {
        currentIteration = 0;
        if (!this.isKKLayout) // no initialization for Spring Layout.
        {
            isInited = true;
            return;
        }
        isInited = false;

        if (graph != null && size != null)
        {

            double height = size.getHeight();
            double width = size.getWidth() * .70;

            Collection<ThreeWayGraphNode> verts = graph.getVertices();
            int sz = 0;
            for (ThreeWayGraphNode twgn : verts)
            {
                if (twgn instanceof ThreeWayGeneGraphObject)
                {
                    continue;
                }
                sz++;
            }
            this.vertices = new ThreeWayGraphNode[sz];
            int n = sz;//graph.getVertexCount();
            dm = new double[n][n];

            int idx = 0;
            for (ThreeWayGraphNode twgn : verts)
            {
                if (twgn instanceof ThreeWayGeneGraphObject)
                {
                    continue;
                }
                vertices[idx++] = (twgn);
            }
            xydata = new Point2D[n];

            order = new ThreeWayGeneGroupNode[10];
            for (ThreeWayGraphNode gg : verts)
            {
                if (gg instanceof ThreeWayGeneNode)
                {
                    gg = ((ThreeWayGeneNode) gg).getGroup();
                    int i;
                    for (i = 0; i < 10; i++)
                    {
                        if (order[i] == gg)
                        {
                            i = 22;
                        }
                    }
                    if (i > 15)
                    {
                        continue;
                    }
                }
                if (gg instanceof ThreeWayGeneGroupNode)
                {
                    for (int i = 0; i < 10; i++)
                    {
                        if (order[i] == null || order[i].getNoGenes() < ((ThreeWayGeneGroupNode) gg).getNoGenes())
                        {
                            for (int j = 9; j > i; j--)
                            {
                                order[j] = order[j - 1];
                            }
                            order[i] = (ThreeWayGeneGroupNode) gg;
                            break;
                        }
                    }
                }
            }

            // assign IDs to all visible vertices
            Random generator = new Random();
            while (true)
            {
                try
                {
                    int index = 0;
                    int index2 = 10;
                    for (ThreeWayGraphNode v : graph.getVertices())
                    {
                        if (v instanceof ThreeWayGeneGroupNode ||
                                v instanceof ThreeWayGeneNode)
                        {
                            index2 = setUpGeneLocations(v, order, index2);
                        }
                        else
                        {
                            Point2D xyd = transform(v);
                            vertices[index] = v;
                            xydata[index] = xyd;
                            if (xyd.getX() == 0 && xyd.getY() == 0)
                            {
                                xydata[index].setLocation(generator.nextInt(size.width), generator.nextInt(size.height));
                            }
                            index++;
                        }
                    }
                    break;
                }
                catch (ConcurrentModificationException cme)
                {
                    cme.printStackTrace();
                }
            }

            double diameter = .55;

            double L0 = Math.min(height, width);
            L = (L0 / diameter) * length_factor;  // length_factor used to be hardcoded to 0.9

            for (int i = 0; i < n - 1; i++)
            {
                for (int j = i + 1; j < n; j++)
                {
                    Number d_ij = distance.getDistance(vertices[i], vertices[j]);
                    Number d_ji = distance.getDistance(vertices[j], vertices[i]);
                    double dist = diameter * disconnected_multiplier;
                    if (d_ij != null)
                    {
                        dist = Math.min(d_ij.doubleValue(), dist);
                    }
                    if (d_ji != null)
                    {
                        dist = Math.min(d_ji.doubleValue(), dist);
                    }
                    dm[i][j] = dm[j][i] = dist;
                }
            }
            isInited = true;
        }
    }

    /**
     *  This is the method where all the work gets done. It is called automatically
     * and updates for both the Spring and KKLayouts. 
     */
    public void step()
    {
        if (!isInited || this.currentIteration > this.maxIterations || inProcess)
        {
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException ex)
            {
            }
            return;
        }

        inProcess = true;
        if (!isKKLayout) // we are running Spring Layout
        {
            for (int cntr = 0; cntr < 75; cntr++)
            {
                currentIteration++;
                try
                {
                    for (ThreeWayGraphNode v : getGraph().getVertices())
                    {
                        SpringVertexData svd = springVertexData.get(v);
                        if (svd == null)
                        {
                            continue;
                        }
                        svd.dx /= 4;
                        svd.dy /= 4;
                        svd.edgedx = svd.edgedy = 0;
                        svd.repulsiondx = svd.repulsiondy = 0;
                    }
                }
                catch (ConcurrentModificationException cme)
                {
                    step();
                }

                relaxEdges();
                calculateRepulsion();
                moveNodes();
            }
        }
        else
        {
            try
            {
                int index2 = 0;
                for (ThreeWayGraphNode v : graph.getVertices())
                {
                    if (v instanceof ThreeWayGeneGroupNode ||
                            v instanceof ThreeWayGeneNode)
                    {
                        index2 = setUpGeneLocations(v, order, index2);
                    }
                }
                int deltax = 5;
                int deltay = 5;
                boolean isX = true;
                boolean isPosX = true;
                boolean isPosY = true;
                int cnt = 0;
                Random r = new Random();
                for (ThreeWayGraphNode v : graph.getVertices())
                {
                    if (v instanceof ThreeWayGeneNode)
                    {
                        int factx = 1;
                        int facty = 1;
                        if (!isPosX)
                        {
                            factx = -1;
                        }
                        if (!isPosY)
                        {
                            facty = -1;
                        }
                        if (cnt == 1 && isX)
                        {
                            factx = 0;
                        }
                        else if (cnt == 1)
                        {
                            facty = 0;
                        }
                        this.setLocation(v, this.getX(v) + deltax * factx, this.getY(v) + deltay * facty);
                        if (isX && cnt > 2)
                        {
                            deltax += 3 * r.nextInt(6);
                            isX = false;
                            isPosY = !isPosY;
                            cnt = 1;
                        }
                        else if (cnt++ > 2)
                        {
                            isX = true;
                            deltay += 3 * r.nextInt(6);
                            isPosX = !isPosX;
                            cnt = 1;
                        }
                    }
                }
                for (int cntr = 0; cntr < 25; cntr++)
                {
                    currentIteration++;
                    double energy = calcEnergy(xydata);
                    if (Double.isNaN(energy))
                    {
                        Random generator = new Random();
                        for (int index = 0; index < xydata.length; index++)
                        {
                            xydata[index].setLocation(generator.nextInt(size.width), generator.nextInt(size.height));
                        }
                        continue;
                    }
                    int n = getGraph().getVertexCount();
                    if (n == 0)
                    {
                        continue;
                    }

                    double maxDeltaM = 0;
                    int pm = -1;            // the node having max deltaM
                    for (int i = 0; i < n && i < vertices.length; i++)
                    {
                        if (isLocked(vertices[i]))
                        {
                            continue;
                        }
                        double deltam = calcDeltaM(i, xydata);

                        if (maxDeltaM < deltam)
                        {
                            maxDeltaM = deltam;
                            pm = i;
                        }
                    }
                    if (pm == -1)
                    {
                        continue;
                    }

                    for (int i = 0; i < 100; i++)
                    {
                        double[] dxy = calcDeltaXY(pm, xydata);
                        double x = xydata[pm].getX() + dxy[0];
                        double y = xydata[pm].getY() + dxy[1];
                        if (x < 0)
                        {
                            x = 0;
                        }
                        if (x > this.size.width)
                        {
                            x = size.width;
                        }
                        if (y < 0)
                        {
                            y = 0;
                        }
                        if (y > this.size.height)
                        {
                            y = size.height;
                        }
                        if (dxy[0] == 0 && dxy[1] == 0)
                        {
                            x += 1;
                            y += 1;
                        }
                        xydata[pm].setLocation(x, y);

                        double deltam = calcDeltaM(pm, xydata);
                        if (deltam < EPSILON)
                        {
                            break;
                        }
                    }

                    if (adjustForGravity)
                    {
                        adjustForGravity();
                    }

                    if (exchangeVertices && maxDeltaM < EPSILON)
                    {
                        energy = calcEnergy(xydata);
                        for (int i = 0; i < n - 1; i++)
                        {
                            if (isLocked(vertices[i]))
                            {
                                continue;
                            }
                            for (int j = i + 1; j < n && j < vertices.length; j++)
                            {
                                if (isLocked(vertices[j]))
                                {
                                    continue;
                                }
                                double xenergy = calcEnergyIfExchanged(i, j, xydata);
                                if (energy > xenergy)
                                {
                                    double sx = xydata[i].getX();
                                    double sy = xydata[i].getY();
                                    xydata[i].setLocation(xydata[j]);
                                    xydata[j].setLocation(sx, sy);
                                    inProcess = false;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                //e.printStackTrace();
            }
            finally
            {
            }
        }
        inProcess = false;
    }

    /**
     * KKLayout - shift all vertices so that the center of gravity is located at
     * the center of the screen.
     */
    public void adjustForGravity()
    {
        Dimension d = getSize();
        double height = d.getHeight();
        double width = d.getWidth() * .75;
        double gx = 0;
        double gy = 0;
        for (int i = 0; i <
                xydata.length; i++)
        {
            gx += xydata[i].getX();
            gy +=
                    xydata[i].getY();
        }
        gx /= xydata.length;
        gy /=
                xydata.length;
        double diffx = width / 2 - gx - width / 6;
        double diffy = height / 2 - gy;
        for (int i = 0; i <
                xydata.length; i++)
        {
            double xx = xydata[i].getX() + diffx;
            if (xx < 0)
            {
                xx = 0;
            }
            if (xx > size.width)
            {
                xx = width;
            }
            double yy = xydata[i].getY() + diffy;
            if (yy < 0)
            {
                yy = 0;
            }
            if (yy > size.height)
            {
                yy = height;
            }
            xydata[i].setLocation(xx, yy);
        }
    }

    /**
     * KKLayout - Determines a step to new position of the vertex m.
     */
    private double[] calcDeltaXY(int m, Point2D[] xydata)
    {
        double dE_dxm = 0;
        double dE_dym = 0;
        double d2E_d2xm = 0;
        double d2E_dxmdym = 0;
        double d2E_dymdxm = 0;
        double d2E_d2ym = 0;

        for (int i = 0; i <
                vertices.length; i++)
        {
            if (i != m)
            {

                double dist = dm[m][i];
                double l_mi = L * dist;
                double k_mi = K / (dist * dist);
                double dx = xydata[m].getX() - xydata[i].getX();
                if (dx == 0)
                {
                    dx += .0001;
                }
                double dy = xydata[m].getY() - xydata[i].getY();
                if (dy == 0)
                {
                    dy += .0001;
                }
                double d = Math.sqrt(dx * dx + dy * dy);
                if (d == 0)
                {
                    d += .0001;
                }
                double ddd = d * d * d;

                dE_dxm +=
                        k_mi * (1 - l_mi / d) * dx;
                dE_dym +=
                        k_mi * (1 - l_mi / d) * dy;
                d2E_d2xm +=
                        k_mi * (1 - l_mi * dy * dy / ddd);
                d2E_dxmdym +=
                        k_mi * l_mi * dx * dy / ddd;
                d2E_d2ym +=
                        k_mi * (1 - l_mi * dx * dx / ddd);
            }
        }
        // d2E_dymdxm equals to d2E_dxmdym.
        d2E_dymdxm = d2E_dxmdym;

        double denomi = d2E_d2xm * d2E_d2ym - d2E_dxmdym * d2E_dymdxm;
        double deltaX = (d2E_dxmdym * dE_dym - d2E_d2ym * dE_dxm) / denomi;
        double deltaY = (d2E_dymdxm * dE_dxm - d2E_d2xm * dE_dym) / denomi;
        return new double[]
                {
                    deltaX, deltaY
                };
    }

    /**
     * KKLayout - Calculates the gradient of energy function at the vertex m.
     */
    private double calcDeltaM(int m, Point2D[] xydata)
    {
        double dEdxm = 0;
        double dEdym = 0;
        for (int i = 0; i <
                vertices.length; i++)
        {
            if (i != m)
            {
                double dist = dm[m][i];
                double l_mi = L * dist;
                double k_mi = K / (dist * dist);

                double dx = xydata[m].getX() - xydata[i].getX();
                if (dx == 0)
                {
                    dx += .0001;
                }
                double dy = xydata[m].getY() - xydata[i].getY();
                if (dy == 0)
                {
                    dy += .0001;
                }
                double d = Math.sqrt(dx * dx + dy * dy);
                if (d == 0)
                {
                    d += .0001;
                }

                double common = k_mi * (1 - l_mi / d);
                dEdxm +=
                        common * dx;
                dEdym +=
                        common * dy;
            }
        }
        return Math.sqrt(dEdxm * dEdxm + dEdym * dEdym);
    }

    /**
     * KKLayout - Calculates the energy function E.
     */
    private double calcEnergy(Point2D[] xydata)
    {
        double energy = 0;
        for (int i = 0; i <
                vertices.length - 1; i++)
        {
            for (int j = i + 1; j <
                    vertices.length; j++)
            {
                double dist = dm[i][j];
                double l_ij = L * dist;
                double k_ij = K / (dist * dist);
                double dx = xydata[i].getX() - xydata[j].getX();
                double dy = xydata[i].getY() - xydata[j].getY();
                double d = Math.sqrt(dx * dx + dy * dy);


                energy +=
                        k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
                        2 * l_ij * d);
            }
        }
        return energy;
    }

    /**
     * KKLayout - Calculates the energy function E as if positions of the
     * specified vertices are exchanged.
     */
    private double calcEnergyIfExchanged(int p, int q, Point2D[] xydata)
    {
        if (p >= q)
        {
            throw new RuntimeException("p should be < q");
        }
        double energy = 0;		// < 0
        for (int i = 0; i <
                vertices.length - 1; i++)
        {
            for (int j = i + 1; j <
                    vertices.length; j++)
            {
                int ii = i;
                int jj = j;
                if (i == p)
                {
                    ii = q;
                }
                if (j == q)
                {
                    jj = p;
                }
                double dist = dm[i][j];
                double l_ij = L * dist;
                double k_ij = K / (dist * dist);
                double dx = xydata[ii].getX() - xydata[jj].getX();
                double dy = xydata[ii].getY() - xydata[jj].getY();
                double d = Math.sqrt(dx * dx + dy * dy);

                energy +=
                        k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
                        2 * l_ij * d);
            }
        }
        return energy;
    }

    /**
     * Sets the size and resets the iteration counter so that a complete
     * update is make with the new size of panel
     */
    public void refreshSize(Dimension d)
    {
        currentIteration = 0;
        this.size.height = d.height;
        this.size.width = d.width;
    }

    /**
     * Gene Layout - Places the genes on the screen so that the 10 biggest are forefront,
     * with smaller genes behind. 
     */
    private int setUpGeneLocations(ThreeWayGraphNode v, ThreeWayGeneGroupNode[] order, int index2)
    {
        double s1 = size.getHeight() / 14.0;
        double s1p = size.getWidth() / 16.0;
        double s2 = size.getHeight() / 10.0;
        double s2p = size.getWidth() / 12.0;
        double w = size.getWidth() / 2.0 + 35.0;
        double h = size.getHeight() / 2.0 - 50.0;
        boolean isFound = false;
        for (int i = 0; i < 10; i++)
        {
            if (v == order[i] ||
                    (v instanceof ThreeWayGeneNode && ((ThreeWayGeneNode) v).getGroup() == order[i]))
            {
                isFound = true;
                double x = w;
                double y = h;
                switch (i)
                {
                    case 0:
                        break;
                    case 2:
                        x += s2p;
                        y += 2.5 * s2;
                        break;
                    case 1:
                        x += s2p;
                        y -= 2.5 * s2;
                        break;
                    case 3:
                        x += 2 * s2p;
                        y -= 3.25 * s2;
                        break;
                    case 4:
                        x += 2 * s2p;
                        y += 3.25 * s2;
                        break;
                    case 5:
                        x += 3 * s2p;
                        y += 3.8 * s2;
                        break;
                    case 6:
                        x += 3 * s2p;
                        y -= 3.8 * s2;
                        break;
                    case 7:
                        x += 4 * s2p;
                        y -= 4 * s2;
                        break;
                    case 8:
                        x += 3.5 * s2p;
                        y += 4 * s2;
                        break;
                    case 9:
                        x += 4 * s2p;
                        y += 4.25 * s2;
                        break;
                }
                this.setLocation(v, x, y);
                return index2;
            }
        }
        if (!isFound)
        {
            if (index2 + h - 4 * s2 < this.getSize().height - 100)
            {
                this.setLocation(v, w + 5 * s2p - 35, index2 + h - 4 * s2);
            }
            else
            {
                if ((index2 - this.getSize().height) + h - 3 - 4 * s2 < this.getSize().height - 100)
                {
                    this.setLocation(v, w + 5 * s2p - 20, (index2 - this.getSize().height + 120) + h - 3 - 4 * s2);
                }
                else
                {
                    this.setLocation(v, w + 5 * s2p - 5, (index2 - 2 * this.getSize().height + 120) + h - 4 * s2);
                }
            }
        }
        index2 += 15;
        return index2;
    }

    /**
     * Spring layout method
     */
    protected void relaxEdges()
    {
        try
        {
            for (ThreeWayGraphEdge e : getGraph().getEdges())
            {
                double force_multiplier;
                if (!(e instanceof ThreeWayAssociationEdge))
                {
                    force_multiplier = this.association_force_multiplier;
                }
                else
                {
                    force_multiplier = this.correlation_force_multiplier;
                }
                Pair<ThreeWayGraphNode> endpoints = getGraph().getEndpoints(e);
                ThreeWayGraphNode v1 = endpoints.getFirst();
                ThreeWayGraphNode v2 = endpoints.getSecond();

                Point2D p1 = transform(v1);
                if (p1.getX() == Double.NaN)
                {
                    p1.setLocation(0, 0);
                }
                Point2D p2 = transform(v2);
                if (p2.getX() == Double.NaN)
                {
                    p2.setLocation(0, 0);
                }
                if (p1 == null || p2 == null)
                {
                    continue;
                }
                double vx = p1.getX() - p2.getX();
                double vy = p1.getY() - p2.getY();
                double len = Math.sqrt(vx * vx + vy * vy);

                double desiredLen = 5.0 * distance.getDistance(v2, v1).doubleValue();//lengthFunction.transform(e);

                // round from zero, if needed [zero would be Bad.].
                len = (len == 0) ? .0001 : len;

                double f = force_multiplier * (desiredLen - len) / len;

                f = f * Math.pow(stretch, (getGraph().degree(v1) + getGraph().degree(v2) - 2));

                // the actual movement distance 'dx' is the force multiplied by the
                // distance to go.
                double dx = f * vx;
                double dy = f * vy;
                SpringVertexData v1D, v2D;
                v1D = springVertexData.get(v1);
                v2D = springVertexData.get(v2);

                v1D.edgedx += dx;
                v1D.edgedy += dy;
                v2D.edgedx += -dx;
                v2D.edgedy += -dy;
            }
        }
        catch (ConcurrentModificationException cme)
        {
            relaxEdges();
        }
    }

    /**
     * Spring layout method
     */
    protected void calculateRepulsion()
    {
        try
        {
            for (ThreeWayGraphNode v : getGraph().getVertices())
            {
                if (isLocked(v))
                {
                    continue;
                }

                SpringVertexData svd = springVertexData.get(v);
                if (svd == null)
                {
                    continue;
                }
                double dx = 0, dy = 0;

                for (ThreeWayGraphNode v2 : getGraph().getVertices())
                {
                    if (v == v2)
                    {
                        continue;
                    }
                    Point2D p = transform(v);
                    Point2D p2 = transform(v2);
                    if (p == null || p2 == null)
                    {
                        continue;
                    }
                    double vx = p.getX() - p2.getX();
                    double vy = p.getY() - p2.getY();
                    double distanceSq = p.distanceSq(p2);
                    double factor = trait_repulsion_factor;
                    if (v2 instanceof ThreeWayGeneGroupNode && v instanceof ThreeWayGeneGroupNode)
                    {
                        factor = gene_repulsion_factor;
                    }
                    if (distanceSq == 0)
                    {
                        dx += Math.random();
                        dy += Math.random();
                    }
                    else if (distanceSq < repulsion_range_sq)
                    {
                        dx += factor * vx / distanceSq;
                        dy += factor * vy / distanceSq;
                    }
                }
                //my code for center repulsion
                Dimension d = getSize();
                Point2D p = transform(v);
                int width = (int) (d.width * .9);
                int height = (int) (d.height * .9);
                double vx = p.getX() - width / 2.0;
                double vy = p.getY() - height / 2.0;
                if (!v.hasAssociation())
                {
                    if (vx > vy)
                    {
                        dx += vx;
                    }
                    else
                    {
                        dy += vy;
                    }
                }
                double dlen = dx * dx + dy * dy;
                if (dlen > 0)
                {
                    dlen = Math.sqrt(dlen) / 2;
                    svd.repulsiondx += dx / dlen;
                    svd.repulsiondy += dy / dlen;
                }
            }
        }
        catch (ConcurrentModificationException cme)
        {
            calculateRepulsion();
        }
    }

    /**
     * Spring layout method
     */
    protected void moveNodes()
    {
        synchronized (getSize())
        {
            try
            {
                for (ThreeWayGraphNode v : getGraph().getVertices())
                {
                    if (isLocked(v))
                    {
                        continue;
                    }
                    SpringVertexData vd = springVertexData.get(v);
                    if (vd == null)
                    {
                        continue;
                    }
                    Point2D xyd = transform(v);

                    vd.dx += vd.repulsiondx + vd.edgedx;
                    vd.dy += vd.repulsiondy + vd.edgedy;

                    // keeps nodes from moving any faster than 5 per time unit
                    xyd.setLocation(xyd.getX() + Math.max(-5, Math.min(5, vd.dx)),
                            xyd.getY() + Math.max(-5, Math.min(5, vd.dy)));

                    Dimension d = getSize();
                    int width = (int) (d.width);
                    int height = (int) (d.height);

                    if (xyd.getX() < 30)
                    {
                        xyd.setLocation(30, xyd.getY());
                    }
                    else if (xyd.getX() > width - 80)
                    {
                        xyd.setLocation(width - 80, xyd.getY());
                    }
                    if (xyd.getY() < 40)
                    {
                        xyd.setLocation(xyd.getX(), 40);
                    }
                    else if (xyd.getY() > height - 80)
                    {
                        xyd.setLocation(xyd.getX(), height - 80);
                    }

                }
            }
            catch (ConcurrentModificationException cme)
            {
                moveNodes();
            }
        }
    }

    /**
     * Updates the layout's parameters to be in line with the user settings.
     */
    public void update(Observable o, Object arg)
    {
        boolean isChanged = false;
        if (settings.getIsKKLayout() != this.isKKLayout)
        {
            isChanged = true;
            this.isKKLayout = settings.getIsKKLayout();
        }
        if (settings.getNumberLayoutUpdateIterations() != this.maxIterations)
        {
            this.maxIterations = settings.getNumberLayoutUpdateIterations();
            isChanged = true;
        }
        if (settings.getIsKKLayout() && settings.getKKLayoutDisconnectedMultiplier() != this.disconnected_multiplier)
        {
            this.disconnected_multiplier = settings.getKKLayoutDisconnectedMultiplier();
            isChanged = true;
        }
        if (settings.getIsKKLayout() && settings.getKKLayoutLengthFactor() != this.length_factor)
        {
            isChanged = true;
            this.length_factor = settings.getKKLayoutLengthFactor();
        }
        if (!settings.getIsKKLayout() && settings.getSpringLayoutAssociationForceMultiplier() != this.association_force_multiplier)
        {
            this.association_force_multiplier = settings.getSpringLayoutAssociationForceMultiplier();
            isChanged = true;
        }
        if (!settings.getIsKKLayout() && settings.getSpringLayoutCorrelationForceMultiplier() != this.correlation_force_multiplier)
        {
            this.correlation_force_multiplier = settings.getSpringLayoutCorrelationForceMultiplier();
            isChanged = true;
        }
        if (!settings.getIsKKLayout() && settings.getSpringLayoutGeneRepulsionFactor() != this.gene_repulsion_factor)
        {
            this.gene_repulsion_factor = settings.getSpringLayoutGeneRepulsionFactor();
            isChanged = true;
        }
        if (!settings.getIsKKLayout() && settings.getSpringLayoutStretch() != this.stretch)
        {
            this.stretch = settings.getSpringLayoutStretch();
            isChanged = true;
        }
        if (!settings.getIsKKLayout() && settings.getSpringLayoutTraitRepulsionFactor() != this.trait_repulsion_factor)
        {
            this.trait_repulsion_factor = settings.getSpringLayoutTraitRepulsionFactor();
            isChanged = true;
        }
        if (!settings.getIsKKLayout() && settings.getSpringLayoutRepulsionRange() *
                settings.getSpringLayoutRepulsionRange() != this.repulsion_range_sq)
        {
            isChanged = true;
            this.repulsion_range_sq = settings.getSpringLayoutRepulsionRange() * settings.getSpringLayoutRepulsionRange();
        }

        if (isChanged)
        {
            this.isInited = false;
            this.init();
        }
    }
}
