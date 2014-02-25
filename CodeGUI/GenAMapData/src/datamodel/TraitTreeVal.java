package datamodel;

import BiNGO.GoItems;
import control.DataAddRemoveHandler;
import control.NewDataNameGetter;
import java.awt.Color;
import java.util.ArrayList;
import realdata.DataManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * TraitTreeVal, like so many objects in GenAMap has two purposes. The first
 * is to help in the loading of the tree. When a tree is read in from a file,
 * it creates the object in memory and then inserts it into the database.
 *
 * When the object is already loaded in the the database, this object acts as
 * an interface between the database and the rest of the GUI. It is the model
 * object access by the control and the view to manage and explore the data. 
 * @author jvendries
 */
public class TraitTreeVal
{
    /**
     * The database id of this TraitTreeVal isntance
     */
    private int id;
    /**
     * This value is null unless the node is a leaf in the tree, then
     * it will be a pointer to the trait that this TTV represents
     */
    private Trait trait;
    /**
     * ID of this node. cannot be null. Root's parent Id = 1
     */
    private TraitTreeVal parent;
    /**
     * The level that this node resides at - representing its depth in the tree
     */
    private int level;
    /**
     * A pointer to the database id of the TT that this TTV belongs to.
     */
    private TraitTree tree;
    /**
     * The name of this TraitTreeVal object - often this is either the
     * number of nodes stored until this TTV object, or it is the name of
     * the trait that it represents.
     */
    private String name;
    /**
     * Each TraitTreeVal, after it has been updated, will have a list in the
     * database of traits that it affects. This is a pointer to that list.
     */
    private int traitlist;
    /**
     * For debugging, each node in the tree is assigned an id. This can be
     * accessed if one is debugging and wants a more definite label than '8 Traits'
     */
    private String codeName;
    /**
     * An array list of the descendants of this node. 
     */
    private ArrayList<TraitTreeVal> children;
    /**
     * The number of traits that are under this node. This is equal to one
     * if the node represents a trait.
     */
    private int noTraits;
    /**
     * the list of Go codes that belong to this node for visualization.
     */
    private int golist = -1;
    /**
     * Holds all go information that has to do with this treeval object.
     */
    private ArrayList<GoItems> myGoItems;
    /**
     * Points to the the parent id if the parent is not stored
     */
    private int parentid;

    /**
     * Constructor that creates a completely empty TraitTreeVal object. 
     */
    public TraitTreeVal()
    {
        //initialized to null because determining whether this array list is null or not
        //is how TreeAlgorithmDialog and TreeUploaderHelper files distinguish between leaf nodes
        //and intermediate notes in a tree
        this.children = null;
    }

    /**
     * Constructor that only takes in a name for the node. This is used
     * in reading in a file of a tree.
     * @param name
     */
    public TraitTreeVal(String name)
    {
        this.name = name;
        this.children = null;
    }

    /**
     * This is the full constructor of the TraitTreeVal. It creates a node that
     * represents full the TraitTreeVal entry in the database.
     * @param id the id of the ttv object in the database
     * @param traitid the id of the trait that it points to
     * @param parentid the id of the parent of this ttv node in the database
     * @param level the level that this ttv object is in the tree
     * @param ttid the id of the tt that this ttv belongs to
     * @param name the code name of this object as assigned by the caller.
     */
    public TraitTreeVal(int id, Trait traitid, TraitTreeVal parent, int level,
            TraitTree ttid, String name, int golist, int parentid)
    {
        this.id = id;
        this.trait = traitid;
        this.parent = parent;
        this.parentid = parentid;
        this.level = level;
        this.tree = ttid;
        this.codeName = name;
        this.golist = golist;
        if (golist != -1)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("id = " + golist);
            String[] gocodes = ((String)DataManager.runSelectQuery("list", "golist", true, where, null).get(0)).split(",");
            ArrayList<GoItems> goitems = new ArrayList<GoItems>();
            for (int i = 0; i < gocodes.length; i++)
            {
                if (gocodes[i].length() > 0)
                {
                    goitems.add(GoItems.parse(gocodes[i]));
                }
            }
            this.myGoItems = goitems;
        }
        else
        {
            this.myGoItems = new ArrayList<GoItems>();
        }

        ArrayList<String> where = new ArrayList<String>();
        where.add("id =" + id);
        traitlist = Integer.parseInt((String)
                DataManager.runSelectQuery("traitlist", "traittreeval",
                true, where, null).get(0));

        if (ttid.isUpdated() && (traitid == null || traitid.getId() == -1)) //we need to get the name for this guy
        {
            String res[] = getTraitsForTraitTreeVal(where);
            int noTraitsToControl = res.length;
            if (name.equals("ROOT") || name.equals("Tree Root"))
            {
                this.name = "Tree Root";
            }
            else
            {
                this.name = "<html><center>";
                if (myGoItems.size() > 0)
                {
                    GoItems go = myGoItems.get(0);
                    this.name += go.descr + "<br>";
                }
                this.name += "(" + noTraitsToControl + " Traits" + ")";
                this.name += "</center></html>";
            }
            this.noTraits = noTraitsToControl;
        }
        else
        {
            this.name = name;
        }

        this.children = null;
    }

    /**
     * Returns the code name of this node as it was called in the constructor
     * @return
     */
    public String getCodeName()
    {
        return this.codeName;
    }

    /**
     * Returns the database code for go categories. Useful when copy objects
     * @return
     */
    public int getGoCode()
    {
        return golist;
    }

    /**
     * Returns all of the GoItems associated with the traittreeval object.
     * @return
     */
    public ArrayList<GoItems> getGoCats()
    {
        return this.myGoItems;
    }

    /**
     * Returns the number of traits underneath this current TTV.
     * @return
     */
    public int getNoTraits()
    {
        return this.noTraits;
    }

    /**
     * Queries the database with the traitlist to find out which nodes
     * belong to this TTV. This will return a complete list of all
     * traits in the same line with the object.
     * @return
     * @throws NumberFormatException
     */
    public ArrayList<Integer> getTraitsUnderNode() throws NumberFormatException
    {
        ArrayList<String> where = new ArrayList<String>();
        String[] res = this.getTraitsForTraitTreeVal(where);
        ArrayList<Integer> res2 = new ArrayList<Integer>();
        for (String s : res)
        {
            res2.add(Integer.parseInt(s));
        }
        return res2;
    }

    /**
     * Returns the database id of this traittreeval object
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the trait object that this TTV represents in the tree.
     * @return
     */
    public Trait getTrait()
    {
        return trait;
    }

    /**
     * Returns the parent TTV object of this TTV object. 
     * @return
     */
    public TraitTreeVal getParent()
    {
        if (parent != null)
        {
            return parent;
        }
        else
        {
            ArrayList<String> where = new ArrayList<String>();
                where.add("id=" + parentid);
            ArrayList<String> cols = new ArrayList<String>();
            cols.add("id");
            cols.add("parentid");
            cols.add("level");
            cols.add("golist");

            ArrayList<HashMap<String,String>> res = DataManager.runMultiColSelectQuery(cols, "traittreeval", true, where, null);

            int pid = Integer.parseInt(res.get(0).get("id"));
            int plev = Integer.parseInt(res.get(0).get("level"));
            int pgolist = Integer.parseInt(res.get(0).get("golist"));
            int ppid = Integer.parseInt(res.get(0).get("parentid"));


            parent = new TraitTreeVal(pid, null, null, plev, this.tree, "NODE", pgolist, ppid);
            return parent;
        }
    }

    /**
     * Returns the depth of the TTV object.
     * @return
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Returns the TraitTree object that holds the collection of TraitTreeVal
     * objects that this object belongs to.
     * @return
     */
    public TraitTree getTraitTree()
    {
        return tree;
    }

    /**
     * Returns a list of all children for thsi TraitTreeVal object. 
     * @return
     */
    public ArrayList<TraitTreeVal> getChildren()
    {
        return children;
    }

    /**
     * Returns the name of this TraitTreeVal object. This is usually
     * the number of traits stored by the object, or the name of the trait
     * the object represents.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the db referencing id of this TTV
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Sets the trait that this TTV points to, can be null.
     * @param traitid
     */
    public void setTrait(Trait traitid)
    {
        this.trait = traitid;
    }

    /**
     * Sets the parent of this TTV - it therefore has a reverse pointer
     * up the tree as well as down.
     * @param parent
     */
    public void setParent(TraitTreeVal parent)
    {
        this.parent = parent;
    }

    /**
     * Sets the level that this TTV belongs to. 
     * @param level
     */
    public void setLevel(int level)
    {
        this.level = level;
    }

    /**
     * Adds a child to this TTV object. 
     * @param child
     */
    public void addChild(TraitTreeVal child)
    {
        if (children == null)
        {
            children = new ArrayList<TraitTreeVal>();
        }

        children.add(child);
    }

    /**
     * Sets the name of this TTV. 
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * The toString method determines what this TTV object should be labeled
     * as. If the trait object is null, then it chooses the number of traits
     * that exist under that object, the name. However, if the trait is not null
     * then it returns the trait's name. 
     * @return
     */
    public
    @Override
    String toString()
    {
        if (trait == null)
        {
            return this.name;
        }
        else
        {
            return trait.getName();
        }
    }

    /**
     * Any TTV object can be called upon to generate a subset. The object
     * consults the database, which has a list of all traits that are in the same line
     * in the tree. All of these traits are then put into this TraitSubSet
     * which is added to the TraitSet that was passed in. 
     * @param ts
     * @param parent
     */
    public void createSubset(TraitSet ts, java.awt.Container parent)
    {
        if (tree.isUpdated())
        {
            if (name.equals("Tree Root"))
            {
                JOptionPane.showMessageDialog(parent, "This is the root - no subset made");
            }
            else
            {
                ArrayList<Integer> res2 = getTraitsUnderNode();

                /*NewDataNameGetter ndng = new NewDataNameGetter(parent, true,
                "Enter a name for this trait subset: ", ts.getSubsetNames());
                ndng.show();*/
                String name = this.tree.getTraitSet().getNextSubsetName();
                ts.addSubset(new TraitSubset(ts, res2, name));
                DataAddRemoveHandler.getInstance().refreshDisplay();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(parent, "Update has not finished running for this tree.");
        }
    }

    /**
     * Returns the parsed list of traits for this traittreeval object
     * @param where
     * @return
     */
    private String[] getTraitsForTraitTreeVal(ArrayList<String> where)
    {
        where.clear();
        where.add("id = " + traitlist);
        String traitsToControl = (String)DataManager.runSelectQuery("list", "traitlist", true, where, null).get(0);
        traitsToControl = traitsToControl.replace(',', '\t');
        traitsToControl = traitsToControl.trim();
        String[] res = traitsToControl.split("\t");
        return res;
    }

    /**
     * Returns an array list of strings of the trait's current GO annotations. 
     * @return
     */
    public ArrayList<String> getGoCatListForTrait(Map<String, Color> map)
    {
        if (trait == null)
        {
            return null;
        }
        return trait.getCurrentGoAnnotation(map);
    }
}
