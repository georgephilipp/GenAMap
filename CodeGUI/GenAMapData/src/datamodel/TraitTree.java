package datamodel;

import control.DataAddRemoveHandler;
import control.itempanel.DeletionItem;
import control.itempanel.ThreadingItemFrame;
import java.io.Serializable;
import java.util.ArrayList;
import realdata.DataManager;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * The TraitTree object holds a collection of TraitTreeVals, which in turn make
 * up the tree. It controls
 * the updating process of a tree once it is created. It can also be used
 * to read in a tree from a file before moving it to the database. 
 * @author jvendries
 */
public class TraitTree implements Serializable
{

    /**
     * The database id of this trait tree object.
     */
    private int id;
    /**
     * The name of this tree
     */
    private String name;
    /**
     * The traitset to which this tree belongs to.
     */
    private TraitSet tsid;
    /**
     * Whether or not this tree has been updated in the database. An update
     * makes a list of all traits associated with each node for easy and quick
     * reference. 
     */
    private boolean updated;

    /**
     * Creates a TraitTree object and checks to see if it has been updated
     * in the database. This mirrors the object in the db.
     * @param id the id of the trait tree
     * @param name the name of the traittree
     * @param ts the traitset to which this tree belongs
     * @param updated whether or not this tree has been updated in teh database.
     */
    public TraitTree(int id, String name, TraitSet ts, boolean updated)
    {
        this.id = id;
        this.name = name;
        this.tsid = ts;
        this.updated = updated;

        checkForUpdate();
    }

    /**
     * Checks to see if a traittree object has been updated in the database.
     * If the tree has been updated then it can be drawn and explored normally
     * in the visualization tool. If it hasn't, then we must wait for its completion.
     * @return
     */
    public boolean isUpdated()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        updated = DataManager.runSelectQuery("updated", "traittree", true, where, null).get(0).equals("1");
        return this.updated;
    }

    /**
     * This method should be called sparingly. Currently it is only called on
     * a refresh of the database. It starts an update if one has not happened yet.
     */
    public void checkForUpdate()
    {
        if (!this.isUpdated())
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("id = " + id);
            updated =
                    DataManager.runSelectQuery("updated", "traittree", true, where, null).get(0).equals("1");
            if (!updated)
            {
                DataAddRemoveHandler.updateTraitTree(this);
            }
        }
    }

    /**
     * Returns the traitset that this traittree belongs to. 
     * @return
     */
    public TraitSet getTraitSet()
    {
        return tsid;
    }

    /**
     * Returns the id of this traittree
     * @return
     */
    public int getId()
    {
        return id;
    }

    public void rename(String newName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id =" +this.id);
        DataManager.runUpdateQuery("traittree", "name", newName, where);
        this.name = newName;
    }

    /**
     * Returns the name of this traittree for display in the data manager.
     * @return
     */
    public String getName()
    {
        return this.name + " (TREE)";
    }

    /**
     * Returns the name of this traittree without the (TREE) attached.
     * @return
     */
    public String getDBName()
    {
        return this.name;
    }

    /**
     * Sets the traitset of this traittree
     * @param ts
     */
    public void setTraitSet(TraitSet ts)
    {
        this.tsid = ts;
    }

    /**
     * Sets the id of this traittree
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Sets the name of this traittree
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Removes this traittree from the database so that it will never
     * be seen again.
     * @return success
     */
    public boolean delete()
    {
        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, tsid.getId(), DeletionItem.DELETE_TREE,
                this.name);
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);

        return true;
    }
}
