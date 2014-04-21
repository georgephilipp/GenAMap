package control;

import control.itempanel.AssociationUploadItem;
import control.itempanel.CytoNet;
import control.itempanel.NetworkUploadItem;
import control.itempanel.PopulationItem;
import control.itempanel.TraitTreeUpdateItem;
import control.itempanel.ThreadingItemFrame;
import control.itempanel.TraitTreeItem;
import datamodel.Population;
import datamodel.Association;
import javax.swing.tree.*;
import datamodel.Model;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import datamodel.Project;
import datamodel.AssociationSet;
import datamodel.GeneTraitAssociation;
import datamodel.Marker;
import datamodel.MarkerSet;
import datamodel.Network;
import datamodel.TraitSet;
import datamodel.TraitTree;
import datamodel.TraitSubset;
import datamodel.TraitTreeVal;
import realdata.DataManager;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * Controls the addition and removal of all data objects.
 * Mediator between the model as well as the AssociationObjectTabs
 * This is a little awkward of an implementation because we have to keep
 * track of the trees for both the AOT and the Model, so it seems to be
 * quite redundant with the model. It is nice to have them separate, however.
 * @author ross
 */
public class DataAddRemoveHandler
{
    /**
     * Pointer to the AOT's marker tree in the marker tab.
     */
    private JTree markerDisplay;
    /**
     * pointer to the AOT's trait tree in the trait tab.
     */
    private JTree traitDisplay;
    /**
     * pointer to the AOT's associaition tree in the assoc tab.
     */
    private JTree assocDisplay;
    /**
     * The instance of this class that all outside sources wishing to call methods
     * to add or remove data can access to call.
     */
    private static DataAddRemoveHandler instance;
    /**
     * The current tab I am on.
     */
    private int tab = -1;
    /**
     * Whether I am using JUNG or matrix view - this should updated by
     * The AssociationViewer
     */
    private int viewerType = -1;

    /**
     * Returns a list that has the instance's three trees stored as objects
     * in a set order. Called by the Model during serialization.
     * @return
     */
    public ArrayList<Object> getTrees()
    {
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(markerDisplay);
        temp.add(traitDisplay);
        temp.add(assocDisplay);
        return temp;
    }

    /**
     * Returns the one and only instance of this class.
     * @return
     */
    public static DataAddRemoveHandler getInstance()
    {
        if (instance == null)
        {
            instance = new DataAddRemoveHandler();
        }
        return instance;
    }

    /**
     * Constructor for the instance of this class. The importance of this
     * method is to start the management of the data trees from the model.
     * This is important to track what the users have selected to keep
     * GenAMap accurate between runs. 
     */
    private DataAddRemoveHandler()
    {
        try
        {
            ArrayList<Object> trees = Model.getInstance().getDisplaySettings();
            markerDisplay = (JTree) trees.get(0);
            traitDisplay = (JTree) trees.get(1);
            assocDisplay = (JTree) trees.get(2);
        }
        catch (Exception e)
        {
            markerDisplay = new JTree();
            traitDisplay = new JTree();
            assocDisplay = new JTree();

            DefaultMutableTreeNode markerRoot = new DefaultMutableTreeNode("Projects");
            DefaultMutableTreeNode traitRoot = new DefaultMutableTreeNode("Projects");
            DefaultMutableTreeNode assocRoot = new DefaultMutableTreeNode("Projects");

            markerDisplay.setModel(new DefaultTreeModel(markerRoot));
            traitDisplay.setModel(new DefaultTreeModel(traitRoot));
            assocDisplay.setModel(new DefaultTreeModel(assocRoot));
        }
    }

    /**
     * This method is called by the AOT in order to ensure that the
     * trees that the DARH controls are the same as those displayed by the AOT.
     * We also have to worry about stored values from the serialization files,
     * which are synced through this method.
     * @param markers
     * @param traits
     * @param assoc
     */
    public void setup(JTree markers, JTree traits, JTree assoc)
    {
        syncDisplay(markers, markerDisplay);
        syncDisplay(traits, traitDisplay);
        syncDisplay(assoc, assocDisplay);

        this.markerDisplay = markers;
        this.traitDisplay = traits;
        this.assocDisplay = assoc;

        syncWithModel();
    }

    /**
     * Calls the Model to add a project to the database and then syncs the
     * display with the updated model. 
     *
     * @param name
     * @return
     */
    public boolean addProject(String name)
    {
        Project proj = Model.getInstance().addProject(name);
        if (proj == null || proj.getTeamId() != DataManager.getTeamId() || proj.isDeleted())
        {
            return false;
        }

        DefaultMutableTreeNode child = new DefaultMutableTreeNode(proj.getName());
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (markerDisplay.getModel().getRoot());
        int count = parent.getChildCount();
        DefaultTreeModel model = (DefaultTreeModel) markerDisplay.getModel();
        model.insertNodeInto(child, parent, count);
        TreePath path = new TreePath(parent);
        path = path.pathByAddingChild(child);
        markerDisplay.makeVisible(path);

        child = new DefaultMutableTreeNode(proj.getName());
        parent = (DefaultMutableTreeNode) (traitDisplay.getModel().getRoot());
        count = parent.getChildCount();
        model = (DefaultTreeModel) traitDisplay.getModel();
        model.insertNodeInto(child, parent, count);
        path = new TreePath(parent);
        path = path.pathByAddingChild(child);
        traitDisplay.makeVisible(path);

        child = new DefaultMutableTreeNode(proj.getName());
        parent = (DefaultMutableTreeNode) (assocDisplay.getModel().getRoot());
        count = parent.getChildCount();
        model = (DefaultTreeModel) assocDisplay.getModel();
        model.insertNodeInto(child, parent, count);
        path = new TreePath(parent);
        path = path.pathByAddingChild(child);
        assocDisplay.makeVisible(path);

        return true;
    }

    private void removeProjectFromTree(String projectName)
    {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (markerDisplay.getModel().getRoot());
        DefaultTreeModel model = (DefaultTreeModel) markerDisplay.getModel();
        TreePath path = new TreePath(parent);
        DefaultMutableTreeNode project = getChildNode(parent, projectName);
        model.removeNodeFromParent(project);
        path = path.pathByAddingChild(project);
        markerDisplay.collapsePath(path);
        path = new TreePath(parent);
        markerDisplay.makeVisible(path);

        parent = (DefaultMutableTreeNode) (traitDisplay.getModel().getRoot());
        path = new TreePath(parent);
        model = (DefaultTreeModel) traitDisplay.getModel();
        project = getChildNode(parent, projectName);
        model.removeNodeFromParent(project);
        path = path.pathByAddingChild(project);
        traitDisplay.collapsePath(path);
        path = new TreePath(parent);
        traitDisplay.makeVisible(path);

        parent = (DefaultMutableTreeNode) (assocDisplay.getModel().getRoot());
        model = (DefaultTreeModel) assocDisplay.getModel();
        path = new TreePath(parent);
        project = getChildNode(parent, projectName);
        model.removeNodeFromParent(project);
        path = path.pathByAddingChild(project);
        assocDisplay.collapsePath(path);
        path = new TreePath(parent);
        assocDisplay.makeVisible(path);
    }

    /**
     * Removes the specified project from the database and the visualization
     * after checking with the user first.
     * @param projectName
     * @param jf
     */
    public void removeProject(String projectName, JFrame jf)
    {
        int i = JOptionPane.showConfirmDialog(jf, "Do you really want to delete the project (and all associations, markers, traits, and networks): " + projectName + "?", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
        if (i > 0)
        {
            return;//right away!
        }
        Project p = Model.getInstance().getProject(projectName);
        p.delete();
        Model.getInstance().getProjects().remove(p);
        this.refreshDisplay();
    }

    /**
     * Removes the specified markerset from the database and the visualization
     * after checking with the user first.
     * @param project
     * @param markerName
     * @param j
     */
    public void removeMarker(String project, String markerName, JFrame j)
    {
        int i = JOptionPane.showConfirmDialog(j, "Do you really want to delete the markerset (and all associations): " + markerName + "?", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
        if (i > 0)
        {
            return;//right away!
        }
        Model.getInstance().getProject(project).removeMarker(markerName);
        this.refreshDisplay();
    }

    /**
     * Removes the specified traitset from the database and the visualization 
     * after checking with the user first. 
     * @param project
     * @param traitName
     * @param jf
     */
    public void removeTrait(String project, String traitName, JFrame jf)
    {
        int i = JOptionPane.showConfirmDialog(jf, "Do you really want to delete the traitset (and all associations): " + traitName + "?", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
        if (i > 0)
        {
            return;//right away!
        }
        Model.getInstance().getProject(project).removeTrait(traitName);
        this.refreshDisplay();
    }

    /**
     * Removes the specified subset from the model and updates the visualization
     * @param project
     * @param traitName
     * @param subset
     * @return
     */
    public boolean removeSubset(String project, String traitName, String subset)
    {

        Model.getInstance().getProject(project).getTrait(traitName).removeSubset(subset);
        this.syncWithModel();

        return true;
    }

    /**
     * Removes the specified subset from the tree and from the data model.
     * @param project
     * @param traitName
     * @param subset
     * @return
     */
    public boolean removeSubsetfromAssoc(String project, String traitName, String subset)
    {
        Model.getInstance().getProject(project).getAssociation(traitName).getTraitSet().removeSubset(subset);
        this.syncWithModel();

        return true;
    }

    /**
     * Retrieves the child node with the said name from the parent.
     * @param parent the parent
     * @param name the name of the child to get
     * @return the node in the tree with the called-for name.
     */
    public DefaultMutableTreeNode getChildNode(DefaultMutableTreeNode parent, String name)
    {
        int numKids = parent.getChildCount();
        for (int i = 0; i < numKids; i++)
        {
            if (((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject().equals(name))
            {
                return ((DefaultMutableTreeNode) parent.getChildAt(i));
            }
        }
        return null;
    }

    private ArrayList<DefaultMutableTreeNode> getChildrenNodes(DefaultMutableTreeNode projNode)
    {
        ArrayList<DefaultMutableTreeNode> chillins = new ArrayList<DefaultMutableTreeNode>();
        for (int j = 0; j < projNode.getChildCount(); j++)
        {
            chillins.add((DefaultMutableTreeNode) projNode.getChildAt(j));
        }
        return chillins;
    }

    private boolean isProjectInTree(Project p)
    {
        boolean isNodeThere = false;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (markerDisplay.getModel().getRoot());
        for (Enumeration<DefaultMutableTreeNode> e = parent.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode n = e.nextElement();
            DefaultMutableTreeNode test = getChildNode(parent, p.getName());
            if (test != null)
            {
                return true;
            }
        }
        return isNodeThere;
    }

    /**
     * Refreshes the display to match with the model.
     */
    public void refreshDisplay()
    {
        syncWithModel();
    }

    private void syncDisplay(JTree markers, JTree markerDisplay)
    {
        markers.setModel(markerDisplay.getModel());
        Enumeration t = markerDisplay.getExpandedDescendants(new TreePath(markerDisplay.getModel().getRoot()));
        if (t != null)
        {
            TreePath[] selects = markerDisplay.getSelectionPaths();
            for (; t.hasMoreElements();)
            {
                TreePath s = (TreePath) t.nextElement();
                markers.expandPath(s);
            }
            markers.setSelectionPaths(selects);
        }
    }

    private void syncWithModel()
    {
        ArrayList<Project> projects = Model.getInstance().getProjects();

        for (int i = 0; i < ((DefaultMutableTreeNode) markerDisplay.getModel().getRoot()).getChildCount(); i++)
        {
            DefaultMutableTreeNode projNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) markerDisplay.getModel().getRoot()).getChildAt(i);
            if (!contains(projects, projNode.toString()))
            {
                this.removeProjectFromTree(projNode.toString());
                i--;
            }
            else
            {
                ArrayList<DefaultMutableTreeNode> chillins;

                for (int j = 0; j < getChildrenNodes(projNode).size(); j++)
                {
                    chillins = getChildrenNodes(projNode);
                    DefaultMutableTreeNode mNode = chillins.get(j);
                    if (Model.getInstance().getProject(projNode.toString()).getMarker(mNode.toString()) == null)
                    {
                        ((DefaultTreeModel) markerDisplay.getModel()).removeNodeFromParent(mNode);
                        j--;
                    }
                }
                projNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) traitDisplay.getModel().getRoot()).getChildAt(i);

                for (int j = 0; j < getChildrenNodes(projNode).size(); j++)
                {
                    chillins = getChildrenNodes(projNode);
                    DefaultMutableTreeNode tNode = chillins.get(j);
                    if (Model.getInstance().getProject(projNode.toString()).getTrait(tNode.toString()) == null)
                    {
                        ((DefaultTreeModel) traitDisplay.getModel()).removeNodeFromParent(tNode);
                        j--;
                    }
                }
                projNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) assocDisplay.getModel().getRoot()).getChildAt(i);

                for (int j = 0; j < projNode.getChildCount(); j++)
                {
                    chillins = getChildrenNodes(projNode);
                    DefaultMutableTreeNode aNode = chillins.get(j);
                    if (Model.getInstance().getProject(projNode.toString()).getAssociation(aNode.toString()) == null)
                    {
                        ((DefaultTreeModel) assocDisplay.getModel()).removeNodeFromParent(aNode);
                        j--;
                    }
                }
            }
        }

        for (Project p : projects)
        {
            if (!isProjectInTree(p))
            {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(p.getName());
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (markerDisplay.getModel().getRoot());
                int count = parent.getChildCount();
                DefaultTreeModel model = (DefaultTreeModel) markerDisplay.getModel();
                model.insertNodeInto(child, parent, count);
                TreePath path = new TreePath(parent);
                path = path.pathByAddingChild(child);
                markerDisplay.makeVisible(path);

                child = new DefaultMutableTreeNode(p.getName());
                parent = (DefaultMutableTreeNode) (traitDisplay.getModel().getRoot());
                count = parent.getChildCount();
                model = (DefaultTreeModel) traitDisplay.getModel();
                model.insertNodeInto(child, parent, count);
                path = new TreePath(parent);
                path = path.pathByAddingChild(child);
                traitDisplay.makeVisible(path);

                child = new DefaultMutableTreeNode(p.getName());
                parent = (DefaultMutableTreeNode) (assocDisplay.getModel().getRoot());
                count = parent.getChildCount();
                model = (DefaultTreeModel) assocDisplay.getModel();
                model.insertNodeInto(child, parent, count);
                path = new TreePath(parent);
                path = path.pathByAddingChild(child);
                assocDisplay.makeVisible(path);
            }

            for (TraitSet t : p.getTraits())
            {
                if (!isTraitInTree(p, t))
                {
                    DefaultMutableTreeNode child = new DefaultMutableTreeNode(t.getName());
                    DefaultMutableTreeNode parent = getChildNode((DefaultMutableTreeNode) (traitDisplay.getModel().getRoot()),
                            p.getName());
                    ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(child, parent, parent.getChildCount());
                    for (Network n : t.getNetworks())
                    {
                        DefaultMutableTreeNode trait = child;
                        DefaultMutableTreeNode network = new DefaultMutableTreeNode(n.getName());
                        ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(network, trait, trait.getChildCount());
                    }
                    for (TraitTree tt : t.getTraitTrees())
                    {
                        DefaultMutableTreeNode trait = child;
                        DefaultMutableTreeNode tree = new DefaultMutableTreeNode(tt.getName());
                        ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(tree, trait, trait.getChildCount());
                    }
                    //if(t.getSubsets().size() > 0)
                    {
                        DefaultMutableTreeNode trait = child;
                        DefaultMutableTreeNode subby = new DefaultMutableTreeNode("subsets");
                        ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(subby, trait, trait.getChildCount());
                        for (TraitSubset tsd : t.getSubsets())
                        {
                            DefaultMutableTreeNode sub = new DefaultMutableTreeNode(tsd.getName());
                            ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(sub, subby, subby.getChildCount());
                        }
                    }
                }
                else
                {
                    DefaultMutableTreeNode tINtree = getTrait(p, t);
                    for (int i = 0; i < tINtree.getChildCount(); i++)
                    {
                        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) tINtree.getChildAt(i);
                        if (!temp.toString().equals("subsets"))
                        {
                            if (!t.isNetwork(temp.toString()))
                            {
                                ((DefaultTreeModel) traitDisplay.getModel()).removeNodeFromParent(temp);
                                i--;
                            }
                        }
                    }
                    for (Network n : t.getNetworks())
                    {
                        if (!isChild(tINtree, n.getName()))
                        {
                            ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(n.getName()), tINtree, tINtree.getChildCount() - 1);
                        }
                    }
                    for (TraitTree tt : t.getTraitTrees())
                    {
                        if (!isChild(tINtree, tt.getName()))
                        {
                            ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(tt.getName()), tINtree, tINtree.getChildCount() - 1);
                        }
                    }

                    DefaultMutableTreeNode subsets = (DefaultMutableTreeNode) tINtree.getLastChild();
                    for (int i = 0; i < subsets.getChildCount(); i++)
                    {
                        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) subsets.getChildAt(i);
                        if (!t.isSubset(temp.toString()))
                        {
                            ((DefaultTreeModel) traitDisplay.getModel()).removeNodeFromParent(temp);
                            i--;
                        }
                    }

                    for (TraitSubset s : t.getSubsets())
                    {
                        if (!isChild(subsets, s.getName()))
                        {
                            ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(s.getName()), subsets, subsets.getChildCount());
                        }
                    }
                }
            }

            for (MarkerSet m : p.getMarkers())
            {
                if (!isMarkerInTree(p, m))
                {
                    DefaultMutableTreeNode child = new DefaultMutableTreeNode(m.getName());
                    DefaultMutableTreeNode parent = getChildNode((DefaultMutableTreeNode) (markerDisplay.getModel().getRoot()),
                            p.getName());
                    ((DefaultTreeModel) markerDisplay.getModel()).insertNodeInto(child, parent, parent.getChildCount());

                    for (Population n : m.getPopulations())
                    {
                        DefaultMutableTreeNode marker = child;
                        DefaultMutableTreeNode population = new DefaultMutableTreeNode(n.getName());
                        ((DefaultTreeModel) markerDisplay.getModel()).insertNodeInto(population, marker, marker.getChildCount());
                    }
                }
                else
                {
                    DefaultMutableTreeNode tINtree = getMarker(p, m);
                    for (int i = 0; i < tINtree.getChildCount(); i++)
                    {
                        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) tINtree.getChildAt(i);
                        if (m.getPopulation(temp.toString()) == null)
                        {
                            ((DefaultTreeModel) markerDisplay.getModel()).removeNodeFromParent(temp);
                            i--;
                        }
                    }

                    for (Population n : m.getPopulations())
                    {
                        if (!isChild(tINtree, n.getName()))
                        {
                            ((DefaultTreeModel) markerDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(n.getName()), tINtree, tINtree.getChildCount());
                        }
                    }
                }
            }

            for (AssociationSet a : p.getAssocs())
            {
                if (!isAssocInTree(p, a))
                {
                    DefaultMutableTreeNode child = new DefaultMutableTreeNode(a.getName());
                    DefaultMutableTreeNode parent = getChildNode((DefaultMutableTreeNode) (assocDisplay.getModel().getRoot()),
                            p.getName());
                    ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(child, parent, parent.getChildCount());
                    DefaultMutableTreeNode subst = new DefaultMutableTreeNode("subsets");
                    ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(subst, child, child.getChildCount());
                    for (GeneTraitAssociation gta : a.getGeneTraitAssocs())
                    {
                        DefaultMutableTreeNode assoc = child;
                        DefaultMutableTreeNode gtao = new DefaultMutableTreeNode(gta.getName());
                        ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(gtao, assoc, assoc.getChildCount() - 1);
                    }
                    for (Network n : a.getTraitSet().getNetworks())
                    {
                        DefaultMutableTreeNode assoc = child;
                        DefaultMutableTreeNode network = new DefaultMutableTreeNode(n.getName());
                        ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(network, assoc, assoc.getChildCount() - 1);
                    }
                    for (TraitTree tt : a.getTraitSet().getTraitTrees())
                    {
                        DefaultMutableTreeNode assoc = child;
                        DefaultMutableTreeNode tree = new DefaultMutableTreeNode(tt.getName());
                        ((DefaultTreeModel) traitDisplay.getModel()).insertNodeInto(tree, assoc, assoc.getChildCount() - 1);
                    }
                    if (a.getTraitSet().getSubsets().size() > 0)
                    {
                        DefaultMutableTreeNode trait = child;
                        //DefaultMutableTreeNode subby = new DefaultMutableTreeNode("subsets");
                        //((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(subby, trait, trait.getChildCount());
                        for (TraitSubset tsd : a.getTraitSet().getSubsets())
                        {
                            DefaultMutableTreeNode sub = new DefaultMutableTreeNode(tsd.getName());
                            ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(sub, subst, subst.getChildCount());
                        }
                    }
                }
                else
                {
                    DefaultMutableTreeNode tINtree = getAssoc(p, a);
                    for (int i = 0; i < tINtree.getChildCount(); i++)
                    {
                        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) tINtree.getChildAt(i);
                        if (!temp.toString().equals("subsets"))
                        {
                            if (!a.getTraitSet().isNetwork(temp.toString()) && a.getGeneTraitAssoc(temp.toString()) == null)
                            {
                                ((DefaultTreeModel) assocDisplay.getModel()).removeNodeFromParent(temp);
                                i--;
                            }
                        }
                    }
                    for (GeneTraitAssociation gta : a.getGeneTraitAssocs())
                    {
                        if (!isChild(tINtree, gta.getName()))
                        {
                            ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(gta.getName()), tINtree, tINtree.getChildCount() - 1);
                        }
                    }
                    for (Network n : a.getTraitSet().getNetworks())
                    {
                        if (!isChild(tINtree, n.getName()))
                        {
                            ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(n.getName()), tINtree, tINtree.getChildCount() - 1);
                        }
                    }
                    for (TraitTree tt : a.getTraitSet().getTraitTrees())
                    {
                        if (!isChild(tINtree, tt.getName()))
                        {
                            ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(tt.getName()), tINtree, tINtree.getChildCount() - 1);
                        }
                    }

                    DefaultMutableTreeNode subsets = (DefaultMutableTreeNode) tINtree.getLastChild();
                    for (int i = 0; i < subsets.getChildCount(); i++)
                    {
                        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) subsets.getChildAt(i);
                        if (!a.getTraitSet().isSubset(temp.toString()))
                        {
                            ((DefaultTreeModel) assocDisplay.getModel()).removeNodeFromParent(temp);
                            i--;
                        }
                    }

                    for (TraitSubset s : a.getTraitSet().getSubsets())
                    {
                        if (!isChild(subsets, s.getName()))
                        {
                            ((DefaultTreeModel) assocDisplay.getModel()).insertNodeInto(new DefaultMutableTreeNode(s.getName()), subsets, subsets.getChildCount());
                        }
                    }
                }
            }
        }

    }

    private boolean isChild(DefaultMutableTreeNode parent, String childname)
    {
        for (int i = 0; i < parent.getChildCount(); i++)
        {
            if (childname.equals(parent.getChildAt(i).toString()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isTraitInTree(Project p, TraitSet t)
    {
        return getTrait(p, t) != null;
    }

    private boolean contains(ArrayList<Project> projects, String toString)
    {
        for (Project p : projects)
        {
            if (p.getName().equals(toString))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isMarkerInTree(Project p, MarkerSet m)
    {
        return getMarker(p, m) != null;
    }

    private DefaultMutableTreeNode getTrait(Project p, TraitSet t)
    {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (traitDisplay.getModel().getRoot());
        for (Enumeration<DefaultMutableTreeNode> e = parent.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode n = e.nextElement();
            DefaultMutableTreeNode test = getChildNode(parent, p.getName());
            if (test != null)
            {
                for (Enumeration<DefaultMutableTreeNode> q = test.children(); q.hasMoreElements();)
                {
                    DefaultMutableTreeNode r = q.nextElement();
                    DefaultMutableTreeNode t2 = getChildNode(test, t.getName());
                    if (t2 != null)
                    {
                        return t2;
                    }
                }
            }
        }
        return null;
    }

    private DefaultMutableTreeNode getNetwork(Project p, TraitSet t, Network net)
    {
        DefaultMutableTreeNode t2 = getTrait(p, t);

        if (t2 != null)
        {
            for (Enumeration<DefaultMutableTreeNode> q = t2.children(); q.hasMoreElements();)
            {
                DefaultMutableTreeNode r = q.nextElement();
                DefaultMutableTreeNode t3 = getChildNode(t2, net.getName());
                if (t3 != null)
                {
                    return t3;
                }
            }
        }

        return null;
    }

    private DefaultMutableTreeNode getSubset(Project p, TraitSet t, TraitSubset s)
    {
        DefaultMutableTreeNode t2 = getTrait(p, t);

        if (t2 != null)
        {
            for (Enumeration<DefaultMutableTreeNode> q = t2.children(); q.hasMoreElements();)
            {
                DefaultMutableTreeNode r = q.nextElement();
                DefaultMutableTreeNode t3 = getChildNode(t2, "subsets");
                if (t3 != null)
                {
                    for (Enumeration<DefaultMutableTreeNode> x = t3.children(); x.hasMoreElements();)
                    {
                        DefaultMutableTreeNode r2 = x.nextElement();
                        DefaultMutableTreeNode t4 = getChildNode(t3, s.getName());
                        if (t4 != null)
                        {
                            return t4;
                        }
                    }
                }
            }
        }

        return null;
    }

    private DefaultMutableTreeNode getProject(Project p, JTree tree)
    {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (tree.getModel().getRoot());
        for (Enumeration<DefaultMutableTreeNode> e = parent.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode n = e.nextElement();
            DefaultMutableTreeNode test = getChildNode(parent, p.getName());
            if (test != null)
            {
                return test;
            }
        }
        return null;
    }

    private DefaultMutableTreeNode getAssoc(Project p, AssociationSet a)
    {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (assocDisplay.getModel().getRoot());
        for (Enumeration<DefaultMutableTreeNode> e = parent.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode n = e.nextElement();
            DefaultMutableTreeNode test = getChildNode(parent, p.getName());
            if (test != null)
            {
                for (Enumeration<DefaultMutableTreeNode> q = test.children(); q.hasMoreElements();)
                {
                    DefaultMutableTreeNode r = q.nextElement();
                    DefaultMutableTreeNode t2 = getChildNode(test, a.getName());
                    if (t2 != null)
                    {
                        return t2;
                    }
                }
            }
        }
        return null;
    }

    private DefaultMutableTreeNode getMarker(Project p, MarkerSet m)
    {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (markerDisplay.getModel().getRoot());
        for (Enumeration<DefaultMutableTreeNode> e = parent.children(); e.hasMoreElements();)
        {
            DefaultMutableTreeNode n = e.nextElement();
            DefaultMutableTreeNode test = getChildNode(parent, p.getName());
            if (test != null)
            {
                for (Enumeration<DefaultMutableTreeNode> q = test.children(); q.hasMoreElements();)
                {
                    DefaultMutableTreeNode r = q.nextElement();
                    DefaultMutableTreeNode t2 = getChildNode(test, m.getName());
                    if (t2 != null)
                    {
                        return t2;
                    }
                }
            }
        }
        return null;
    }

    private boolean isAssocInTree(Project p, AssociationSet a)
    {
        return getAssoc(p, a) != null;
    }

    public void addMarkers(JFrame frame, boolean b, ArrayList<Marker> markers, String text, String project, boolean insertSamples, int offset)
    {
        Project p = Model.getInstance().getProject(project);

        System.out.println("In AddMarkers: markers.size = " + markers.size());

        ThreadingItemFrame form = ThreadingItemFrame.getInstance();
        System.out.println("Addmarkers: text = " + text);
        System.out.println("Addmarkers: projectId = " + Integer.toString(p.getId()));
    }

    /**
     * Given the root of a read in tree, this method will call the appropriate
     * places to add the tree to the database. 
     * @param projid the project id of the tree
     * @param tsid the traitid of the tree
     * @param name the name of the tree
     * @param root the root of the tree. 
     */
    public void addTraitTree(int projid, int tsid, String name, String fileName, boolean format)
    {
        ThreadingItemFrame form = ThreadingItemFrame.getInstance();
        TraitTreeItem item = new TraitTreeItem(form, name, fileName, tsid, projid, format);

        form.addToThreadList(item);
        form.setVisible(true);
        form.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public void addAssociation(int projid, TraitSet ts, MarkerSet ms, String name,
            String filename, Network net)
    {
        ThreadingItemFrame form = ThreadingItemFrame.getInstance();
        AssociationUploadItem item = new AssociationUploadItem(
                form, name, ts, ms, projid, filename, net);

        form.addToThreadList(item);
        form.setVisible(true);
        form.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    /**
     * Calls the threader to add a network item to the database. 
     * @param tsid traitset id
     * @param name name of the network
     * @param isEdgeFormat Whether the network is an edge-by-edge format
     */
    public void addNetwork(int projID, int tsid, String name, String fileName, boolean isEdgeFormat)
    {
        ThreadingItemFrame form = ThreadingItemFrame.getInstance();
        NetworkUploadItem item = new NetworkUploadItem(
                form, 
                projID,
                tsid,
                name,
                fileName,
                isEdgeFormat
                );

        form.addToThreadList(item);
        form.setVisible(true);
        form.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    /**
     * Adds a population set to the database by calling the thread worker.
     * @param filename the name of the file
     * @param projID the project's db id
     * @param markerID the marker's db id
     * @param name the name of the population set
     */
    public void addPopulation(String filename, int projID, int markerID, String name)
    {
        ThreadingItemFrame form = ThreadingItemFrame.getInstance();
        PopulationItem item = new PopulationItem(markerID, projID, filename, name, form);

        form.addToThreadList(item);
        form.setVisible(true);
        form.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    /**
     * When a tree is created, it needs one iteration through in order to
     * assigned traits to each node. We do this on this side of the server,
     * through a thread. This method starts that thread.
     * @param tt The traittree master object which we will use to descend.
     */
    public static void updateTraitTree(TraitTree tt)
    {
        ThreadingItemFrame form = ThreadingItemFrame.getInstance();

        TraitTreeUpdateItem item = new TraitTreeUpdateItem(ThreadingItemFrame.getInstance(), tt);
        form.addToThreadList(item);
        form.setVisible(true);
        form.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    /**
     * Removes the association project from the db and updates the visualization
     * tabs. 
     * @param j the frame to base location on
     * @param project the project
     * @param assoc the assoc set to remove. 
     */
    public void removeAssociation(JFrame j, String project, String assoc)
    {
        int i = JOptionPane.showConfirmDialog(j, "Do you really want to delete the association: " + assoc + "?", "Confirm Delete", JOptionPane.WARNING_MESSAGE);

        if (i > 0)
        {
            return;//right away!
        }
        Model.getInstance().getProject(project).removeAssociation(assoc);
        this.syncWithModel();
    }

    /**
     * Removes the requested network from the db after checking with the user
     * @param j The frame to base locatio on
     * @param project the project this network belongs to
     * @param traits the traitset that owns the network
     * @param network the name of the network. 
     */
    public void removeNetwork(JFrame j, String project, String traits, String network, String assocName)
    {
        int i = JOptionPane.showConfirmDialog(j, "Do you really want to delete: " + network + "?", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
        if (i > 0)
        {
            return;//right away!
        }
        boolean isdeleted = Model.getInstance().getProject(project).getTrait(traits).removeNetwork(network, j);

        if(!isdeleted) //this is a three-way dataset
        {
            Model.getInstance().getProject(project).getAssociation(assocName).getGeneTraitAssoc(network).delete();

            Model.getInstance().getProject(project).getAssociation(assocName).removeGTA(network);
        }

        this.syncWithModel();
    }

    /**
     * Remove the requested population structure from the database after checking
     * witht he user
     * @param projectName the name of the project
     * @param traitName the name of the markerset
     * @param netName the name of the popstruct
     */
    public void removePopStruct(JFrame j, String projectName, String markerName, String popName)
    {
        int i = JOptionPane.showConfirmDialog(j, "Do you really want to delete: " + popName + "?", "Confirm Delete", JOptionPane.WARNING_MESSAGE);
        if (i > 0)
        {
            return;//right away!
        }
        Model.getInstance().getProject(projectName).getMarker(markerName).removePop(popName);
        refreshDisplay();
    }

    /**
     * Renames the project in the db and updates the visualization. 
     * @param owner
     * @param projectName
     */
    public void renameProject(JFrame owner, String projectName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true, "Enter this project's new name:", Model.getInstance().getProjectNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            p.rename(ndng.newName);
            this.syncWithModel();
        }

    }

    /**
     * Renames the traitset in the tree and then updates the database. 
     * @param owner
     * @param projectName
     * @param traitName
     */
    public void renameTraitset(JFrame owner, String projectName, String traitName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true,
                "Enter this traitset's new name:", Model.getInstance().getProject(projectName).getTraitNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            TraitSet t = p.getTrait(traitName);
            t.rename(ndng.newName);
            this.syncWithModel();
        }
    }

    /**
     * Renames the network in the tree and then updates the database. 
     * @param owner
     * @param projectName
     * @param traitName
     * @param netName
     */
    public void renameNetwork(JFrame owner, String projectName, String traitName, String netName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true,
                "Enter the new name:", Model.getInstance().getProject(projectName).getTrait(traitName).getNetworkNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            TraitSet t = p.getTrait(traitName);
            Object n = t.getTraitStructure(netName);
            if (n instanceof Network)
            {
                ((Network) n).rename(ndng.newName);

            }
            else
            {
                ((TraitTree) n).rename(ndng.newName);

            }
            this.syncWithModel();
        }
    }

    /**
     * Renames this population object
     * @param owner frame to base dialog off of
     * @param projectName the project name
     * @param markerName the marker name
     * @param popName the population name. 
     */
    public void renamePopStruct(JFrame owner, String projectName, String markerName, String popName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true,
                "Enter new pop name:", Model.getInstance().getProject(projectName).getMarker(markerName).getPopulationNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            MarkerSet m = p.getMarker(markerName);
            Population s = m.getPopulation(popName);
            s.rename(ndng.newName);
            this.refreshDisplay();
        }
    }

    /**
     * Renames the markerset in the tree and then updates the database. 
     * @param owner
     * @param projectName
     * @param markerName
     */
    public void renameMarkerset(JFrame owner, String projectName, String markerName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true,
                "Enter this markerset's new name:", Model.getInstance().getProject(projectName).getMarkerNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            MarkerSet m = p.getMarker(markerName);
            m.rename(ndng.newName);
            this.syncWithModel();
        }
    }

    public void renameAssocSet(JFrame owner, String projectName, String assocName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true,
                "Enter the AssocSet's new name:", Model.getInstance().getProject(projectName).getAssocNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            AssociationSet as = p.getAssociation(assocName);
            as.rename(ndng.newName);
            syncWithModel();
        }
    }

    /**
     * Renames the subset in the tree and then updates the database. 
     * @param owner
     * @param projectName
     * @param traitName
     * @param subName
     */
    public void renameSubset(JFrame owner, String projectName, String traitName, String subName)
    {
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true,
                "Enter this subset's new name:", Model.getInstance().getProject(projectName).getTrait(traitName).getSubsetNames());
        ndng.setVisible(true);
        if (ndng.SUCCESS)
        {
            Project p = Model.getInstance().getProject(projectName);
            TraitSet t = p.getTrait(traitName);
            TraitSubset s = t.getSubset(subName);
            s.rename(ndng.newName);
            this.syncWithModel();
        }
    }

    /**
     * Forces a select in the tree on the subset with the specified name. 
     * @param name
     */
    public void selectSubsetInTraitTree(String name)
    {
        selectSubsetInTree(name, this.traitDisplay);
    }

    /**
     * Forces a select in the association tree on the subset with the
     * specified name. 
     * @param name
     */
    public void selectSubsetInAssocTree(String name)
    {
        selectSubsetInTree(name, this.assocDisplay);
    }

    private void selectSubsetInTree(String name, JTree display)
    {
        TreePath[] selects = display.getSelectionPaths();

        if (selects.length == 2)
        {
            TreePath[] temp = new TreePath[3];
            temp[0] = selects[0];
            temp[1] = selects[1];
            temp[2] = selects[1];
            Object o = this.getChildNode((DefaultMutableTreeNode) temp[2].getLastPathComponent(), "subsets");
            if (o == null)
            {
                temp[0] = selects[1];
                temp[1] = selects[0];
                o = this.getChildNode((DefaultMutableTreeNode) temp[1].getLastPathComponent(), "subsets");
            }
            temp[2] = temp[2].pathByAddingChild(o);
            temp[2] = temp[2].pathByAddingChild(this.getChildNode((DefaultMutableTreeNode) temp[2].getLastPathComponent(), name));
            selects = temp;
        }

        display.setSelectionPaths(selects);
    }

    /**
     * Returns the tab that the AOT is displaying.
     * @return
     */
    public int getTab()
    {
        if (tab < 0)
        {
            tab = Model.getInstance().getTab();
        }
        return tab;
    }

    /**
     * Reutrns whether or not it is JUNG view or matrix view. 
     * @return
     */
    public int getViewType()
    {
        if (viewerType < 0)
        {
            viewerType = Model.getInstance().getViewerType();
        }
        return 1;
    }

    /**
     * Sets which tab the AOT is currently showing. 
     * @param tab
     */
    public void setTab(int tab)
    {
        this.tab = tab;
    }

    /**
     * Sets whether or not GenAMap is showing matrix or JUNG view. 
     * @param type
     */
    public void setViewerType(int type)
    {
        this.viewerType = type;
    }

    /**
     * Creates a trait subset by looking at all traits that are associated with a
     * given set of markers. 
     * @param ac
     * @param markers
     * @param subset
     * @param owner
     */
    public void addSubsetFromAssociation(AssociationSet ac, ArrayList<Marker> markers, TraitSubset subset, JFrame owner)
    {
        ArrayList<Integer> markerID = new ArrayList<Integer>();
        for (Marker m : markers)
        {
            markerID.add(m.getId());
        }
        ArrayList<Integer> indxs;
        indxs = ac.getTraitSet().getIds();
        Collection<Association> assocs = ac.findAssociations(markerID, indxs, -1);
        NewDataNameGetter ndng = new NewDataNameGetter(owner, true, "Choose a name for this subset:", ac.getTraitSet().getSubsetNames());
        ndng.setVisible(true);
        if (!ndng.SUCCESS)
        {
            return;
        }
        String newName = ndng.newName;

        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (Association a : assocs)
        {
            if (!indices.contains(a.getTraitId()))
            {
                indices.add(a.getTraitId());
            }
        }

        TraitSubset s = new TraitSubset(ac.getTraitSet(), indices, newName);
        ac.getTraitSet().addSubset(s);
        refreshDisplay();
    }
}
