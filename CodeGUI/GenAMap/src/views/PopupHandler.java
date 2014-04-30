package views;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.tree.*;
import control.NewProjectCreator;
import control.DataAddRemoveHandler;
import datamodel.Model;
import javax.swing.JFrame;
import control.importing.TraitImporter;
import control.importing.MarkerImporter;
import algorithm.AlgorithmView;
import control.algodialog.AssociationAlgorithmDialog;
import control.algodialog.GeneTraitAssociationAlgorithmDialog;
import control.importing.BedImporter;
import control.algodialog.NetworkAlgorithmDialog;
import control.importing.PedImporter;
import control.algodialog.PopulationStructDialog;
import control.importing.SubsetDialog;
import control.algodialog.TreeAlgorithmDialog;
import control.importing.MarkerFeatureImporter;

/**
 * The tree view in the AssociationObjectTabs shows us all the data in the database.
 * Interaction with this tree to rename, unload, load, add projects, etc all happens
 * through a right-click menu.
 *
 * The PopupHandler controlls the right click menu and so the user can interact and make changes to
 * the datamodel.
 *
 * http://www.java-forums.org/awt-swing/6404-how-create-popup-menu-sub-menu-while-right-clicking-jtree-node.html
 * @author ross
 */
public class PopupHandler implements ActionListener
{
    /**
     * The JFrame that owns this popup handler, a pointer to the AssociationGUI
     * This is for
     */
    JFrame owner;
    /**
     * The parent that owns this popup handler - the association object tabs, this
     * allows us to get the pointer to the AssociationGUI.
     */
    JPanel parent;
    /**
     * The current tree that is listening for the popup handler. When the tab
     * is switched, the popup handler will be called to change its tree to
     * association, traits, or markers.
     */
    JTree tree;
    /**
     * The popup menu that will show then an action is performed. This will
     * know what menu items to add for the user to select form.
     */
    JPopupMenu popup;
    /**
     * The location of the right-click action where the popup menu will be drawn.
     */
    Point loc;
    /**
     * A pointer to myself that is visible in the JPopupMenu context.
     */
    PopupHandler me = this;
    /**
     * Is the popup handler currently following the trait data tree?
     */
    boolean isTraitTree;
    /**
     * Is the popup hanlder currently following the marker data tree? Note if
     * it is following neither the trait nor the marker trees, then it is
     * following the association tree.
     */
    boolean isMarkerTree;

    /**
     * Constructor for the popup hanlder that creates the instance that will
     * be used to follow the AssociationObjectTabs.
     * @param tree the current visible tree in the AssociationObjectTabs
     * @param popup a pointer to the data tree's popup handler
     * @param isTraitTree a boolean representing whether or not the trait tab is shown
     * @param isMarkerTree a boolean representing whether or not the marker tab is shown
     * @param aot a pointer to the AssociationObjectTabs object.
     */
    public PopupHandler(JTree tree, JPopupMenu popup, boolean isTraitTree, boolean isMarkerTree,
            JPanel aot)
    {
        super();

        parent = aot;

        this.tree = tree;
        this.popup = popup;
        this.tree.addMouseListener(ma);
        this.isTraitTree = isTraitTree;
        this.isMarkerTree = isMarkerTree;
    }

    /**
     * This method is called on a change tab in associationObjectTabs so that the
     * popup handler can follow the correct tree.
     * @param tree the new tree to follow on the new tab.
     * @param isTraitTree is the new tab now a trait data tree?
     * @param isMarkerTree is the new tab now a marker data tree?
     */
    public void updateHandler(JTree tree, boolean isTraitTree, boolean isMarkerTree)
    {
        tree.removeMouseListener(ma);
        this.tree = tree;
        this.isTraitTree = isTraitTree;
        this.isMarkerTree = isMarkerTree;
        tree.addMouseListener(ma);
    }

    /**
     * This method is called when the user selects an action from the list
     * of possible actions in the popup menu. This is where all the delegation
     * of this class is performed. 
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        Container temp = parent;
        while (owner == null && temp != null)
        {
            temp = temp.getParent();
            if (temp instanceof JFrame)
            {
                owner = (JFrame) temp;
            }
        }

        String ac = e.getActionCommand();
        TreePath path = tree.getPathForLocation(loc.x, loc.y);
        //System.out.println("path = " + path);
        //System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
        if (ac.equals("ADD NEW PROJECT"))
        {
            NewProjectCreator creator = new NewProjectCreator(tree.getParent(), true);
            creator.setVisible(true);
        }
        else if (ac.equals("PED FORMAT"))
        {
            PedImporter pi = new PedImporter(owner, true);
            pi.setSeletedProject(path.getLastPathComponent().toString());
            pi.show();

        }
        else if (ac.equals("BED FORMAT"))
        {
            BedImporter pi = new BedImporter(owner, true);
            pi.setSeletedProject(path.getLastPathComponent().toString());
            pi.show();
        }
        else if (ac.equals("ADD TRAIT DATA"))
        {
            TraitImporter ti = new TraitImporter(owner, true);
            ti.setSeletedProject(path.getLastPathComponent().toString());
            ti.show();
        }
        else if (ac.equals("ADD MARKER DATA"))
        {
            MarkerImporter mi = new MarkerImporter(owner, true);
            mi.setSeletedProject(path.getLastPathComponent().toString());
            mi.show();
        }
        else if (ac.equals("REMOVE PROJECT"))
        {
            DataAddRemoveHandler.getInstance().removeProject(path.getLastPathComponent().toString(), owner);

        }
        else if (ac.equals("REMOVE MARKER DATA"))
        {
            String markerName = path.getLastPathComponent().toString();
            String projectName = path.getParentPath().getLastPathComponent().toString();

            DataAddRemoveHandler.getInstance().removeMarker(projectName, markerName, owner);
        }
        else if (ac.equals("REMOVE TRAIT DATA"))
        {
            String projectName = path.getParentPath().getLastPathComponent().toString();
            String traitName = path.getLastPathComponent().toString();
            DataAddRemoveHandler.getInstance().removeTrait(projectName, traitName, owner);
        }
        else if (ac.equals("REMOVE SUBSET"))
        {
            if (this.isTraitTree)
            {
                DataAddRemoveHandler.getInstance().removeSubset(path.getParentPath().getParentPath().getParentPath().getLastPathComponent().toString(),
                        path.getParentPath().getParentPath().getLastPathComponent().toString(),
                        path.getLastPathComponent().toString());
            }
            else
            {
                DataAddRemoveHandler.getInstance().removeSubsetfromAssoc(path.getParentPath().getParentPath().getParentPath().getLastPathComponent().toString(),
                        path.getParentPath().getParentPath().getLastPathComponent().toString(),
                        path.getLastPathComponent().toString());
            }
        }
        else if (ac.equals("ADD POPULATION STRUCTURE"))
        {
            PopulationStructDialog netDialog = new PopulationStructDialog(owner, true,
                    AlgorithmView.getInstance(), true, path.getLastPathComponent().toString(),
                    path.getParentPath().getLastPathComponent().toString());
            netDialog.setVisible(true);
        }
        else if (ac.equals("ADD FEATURE DATA"))
        {
             MarkerFeatureImporter netDialog = new MarkerFeatureImporter(owner, true,
                    AlgorithmView.getInstance(), true, path.getLastPathComponent().toString(),
                    path.getParentPath().getLastPathComponent().toString());
            netDialog.setVisible(true);

        }
        else if (ac.equals("ADD NETWORK DATA"))
        {
            NetworkAlgorithmDialog netDialog = new NetworkAlgorithmDialog(owner, true,
                    AlgorithmView.getInstance(), true, path.getLastPathComponent().toString(),
                    path.getParentPath().getLastPathComponent().toString());
            netDialog.setVisible(true);
        }
        else if (ac.equals("ADD TREE DATA"))
        {
            TreeAlgorithmDialog treeDialog = new TreeAlgorithmDialog(owner, true,
                    AlgorithmView.getInstance(), true, path.getLastPathComponent().toString(),
                    path.getParentPath().getLastPathComponent().toString());
            treeDialog.setVisible(true);
        }
        else if (ac.equals("REMOVE STRUCTURE"))
        {
            String netName = path.getLastPathComponent().toString();
            String projectName = path.getParentPath().getParentPath().getLastPathComponent().toString();
            String traitName;
            String assocName = null;
            if (this.isTraitTree)
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
            }
            else if (this.isMarkerTree)
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
                DataAddRemoveHandler.getInstance().removePopStruct(owner, projectName, traitName, netName);
                return;
            }
            else
            {
                assocName = path.getParentPath().getLastPathComponent().toString();
                traitName = Model.getInstance().getProject(projectName).
                        getAssociation(assocName).getTraitSet().getName();
            }

            DataAddRemoveHandler.getInstance().removeNetwork(owner, projectName,
                    traitName, netName, assocName);
        }
        else if (ac.equals("ADD ASSOCIATION DATA"))
        {
            AssociationAlgorithmDialog aad = new AssociationAlgorithmDialog(owner, true, AlgorithmView.getInstance(), true,
                    path.getLastPathComponent().toString(), null, null);
            aad.show();
        }
        else if (ac.equals("REMOVE ASSOCIATION DATA"))
        {
            DataAddRemoveHandler.getInstance().removeAssociation(owner, path.getParentPath().getLastPathComponent().toString(), path.getLastPathComponent().toString());
        }
        else if (ac.equals("REFRESH REPOSITORY"))
        {
            Model.getInstance().refreshModel();
            DataAddRemoveHandler.getInstance().refreshDisplay();
        }
        else if (ac.equals("RENAME PROJECT"))
        {
            DataAddRemoveHandler.getInstance().renameProject(owner, path.getLastPathComponent().toString());
        }
        else if (ac.equals("RENAME TRAITSET"))
        {
            DataAddRemoveHandler.getInstance().renameTraitset(owner, path.getParentPath().getLastPathComponent().toString(),
                    path.getLastPathComponent().toString());
        }
        else if (ac.equals("RENAME MARKERSET"))
        {
            DataAddRemoveHandler.getInstance().renameMarkerset(owner, path.getParentPath().getLastPathComponent().toString(),
                    path.getLastPathComponent().toString());
        }
        else if (ac.equals("RENAME STRUCTURE"))
        {
            String netName = path.getLastPathComponent().toString();
            String projectName = path.getParentPath().getParentPath().getLastPathComponent().toString();
            String traitName;
            if (this.isTraitTree)
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
            }
            else if (this.isMarkerTree)
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
                DataAddRemoveHandler.getInstance().renamePopStruct(owner, projectName, traitName, netName);
                return;
            }
            else
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
                traitName = Model.getInstance().getProject(projectName).
                        getAssociation(traitName).getTraitSet().getName();
            }


            DataAddRemoveHandler.getInstance().renameNetwork(owner, projectName,
                    traitName, netName);
        }
        else if (ac.equals("RENAME SUBSET"))
        {
            String subName = path.getLastPathComponent().toString();
            String projectName = path.getParentPath().getParentPath().getParentPath().getLastPathComponent().toString();
            String traitName;
            if (this.isTraitTree)
            {
                traitName = path.getParentPath().getParentPath().getLastPathComponent().toString();
            }
            else
            {
                traitName = path.getParentPath().getParentPath().getLastPathComponent().toString();
                traitName = Model.getInstance().getProject(projectName).
                        getAssociation(traitName).getTraitSet().getName();
            }


            DataAddRemoveHandler.getInstance().renameSubset(owner, projectName,
                    traitName, subName);
        }
        else if (ac.equals("RENAME ASSOCIATIONSET"))
        {
            DataAddRemoveHandler.getInstance().renameAssocSet(owner, path.getParentPath().getLastPathComponent().toString(),
                    path.getLastPathComponent().toString());
        }
        else if (ac.equals("ADD TRAIT SUBSET"))
        {
            String projectName = path.getParentPath().getParentPath().getLastPathComponent().toString();
            String traitName;
            if (this.isTraitTree)
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
            }
            else
            {
                traitName = path.getParentPath().getLastPathComponent().toString();
                traitName = Model.getInstance().getProject(projectName).
                        getAssociation(traitName).getTraitSet().getName();
            }
            SubsetDialog sd = new SubsetDialog(owner, true, AlgorithmView.getInstance(), true, traitName, projectName);
            sd.setVisible(true);
        }
        else if( ac.equals("ADD GENE-TRAIT ASSOCIATION"))
        {
            GeneTraitAssociationAlgorithmDialog gtaad = new GeneTraitAssociationAlgorithmDialog(owner, true, AlgorithmView.getInstance(), true,
                    path.getLastPathComponent().toString());
            gtaad.show();
        }
    }

    /**
     * Creates a menu item to display in the popup. This assigns the action
     * listener to be the delegating method in this class.
     * @param s the string to display
     * @param al this, meaning this class.
     * @return the newly created menu item. 
     */
    private JMenuItem getMenuItem(String s, ActionListener al)
    {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s.toUpperCase());
        menuItem.addActionListener(al);
        return menuItem;
    }
    /**
     * The mouseListener will check for an intercept the popup to create
     * its own menu. 
     */
    private MouseListener ma = new MouseAdapter()
    {
        /**
         * checkForPopup will determine the point that the user clicked,
         * and then look at the tree to determine which options should
         * be displayed in the popup menu.
         */
        private void checkForPopup(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                loc = e.getPoint();
                TreePath path = tree.getPathForLocation(loc.x, loc.y);
                if (path != null)
                {
                    popup.removeAll();
                    popup.add(getMenuItem("Refresh repository", me));
                    if (path.getPathCount() == 1)
                    {
                        popup.add(getMenuItem("Add new Project", me));
                    }
                    else if (path.getPathCount() == 2)
                    {
                        popup.add(getMenuItem("Rename Project", me));
                        popup.add(getMenuItem("Add Marker Data", me));
                        //JMenu m = new JMenu("Add Marker Data");
                        //m.add(getMenuItem("Matrix Format", me));
                        //m.add(getMenuItem("PED Format", me));
                        //m.add(getMenuItem("BED Format", me));
                        //popup.add(m);
                        popup.add(getMenuItem("Add Trait Data", me));
                        popup.add(getMenuItem("Add Association Data", me));
                        popup.add(getMenuItem("Add Gene-Trait Association", me));
                        popup.add(getMenuItem("Remove Project", me));

                    }
                    else if (path.getPathCount() == 3 && isTraitTree)
                    {
                        popup.add(getMenuItem("Rename TraitSet", me));
                        popup.add(getMenuItem("Add Network Data", me));
                        popup.add(getMenuItem("Add Tree Data", me));
                        popup.add(getMenuItem("Remove Trait Data", me));
                    }
                    else if (path.getPathCount() == 3 && isMarkerTree)
                    {
                        popup.add(getMenuItem("Add Population Structure", me));
                        popup.add(getMenuItem("Add Feature Data", me));
                        popup.add(getMenuItem("Rename MarkerSet", me));
                        popup.add(getMenuItem("Remove Marker Data", me));
                    }
                    else if (path.getPathCount() == 3 && !isMarkerTree && !isTraitTree)
                    {
                        popup.add(getMenuItem("Rename AssociationSet", me));
                        popup.add(getMenuItem("Remove Association Data", me));
                    }
                    else if (path.getPathCount() == 4)
                    {
                        if (path.getLastPathComponent().toString().equals("subsets"))
                        {
                            popup.add(getMenuItem("Add Trait Subset", me));
                        }
                        else
                        {
                            popup.add(getMenuItem("Rename Structure", me));
                            popup.add(getMenuItem("Remove Structure", me));

                        }
                    }
                    else if (path.getPathCount() == 5)
                    {
                        popup.add(getMenuItem("Rename Subset", me));
                        popup.add(getMenuItem("Remove Subset", me));
                    }
                    else
                    {
                        return;
                    }

                    popup.show(tree, loc.x, loc.y);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            checkForPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            checkForPopup(e);
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            checkForPopup(e);
        }
    };
}
