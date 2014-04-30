package datamodel;

import control.itempanel.DeletionItem;
import control.itempanel.ThreadingItemFrame;
import datamodel.Model.ParameterSet;
import java.io.Serializable;
import java.util.ArrayList;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;
import realdata.DataManager;

/**
 * A Project is the main class for storing data. Each project belongs to a team,
 * and all members of that team can access data, upload markers, traits, and
 * find associations. The project cannot be accessed by other teams. It is
 * really nothing more than a data holder, but it is the main data holder that
 * all other data types live under. Here, we represent the project as in the
 * database.
 *
 * @author Shirdoo
 * @author rcurtis
 */
public class Project implements Serializable
{

    /**
     * A list of all markersets in the project
     */
    private ArrayList<MarkerSet> markers;
    /**
     * A list of all traitsets in the project
     */
    private ArrayList<TraitSet> traits;
    /**
     * A list of all association sets in the project
     */
    private ArrayList<AssociationSet> associations;
    /**
     * The database id of the project
     */
    private int id;
    /**
     * The user-defined name of the project
     */
    private String name;

    /**
     * Creates a project object that points to the corresponding
     * project in the database. Because id and  name are all we need
     * about the project, we don't really have to query for anything
     * @param id db id
     * @param name name of project
     */
    public Project(int id, String name)
    {
        this.name = name;
        markers = new ArrayList<MarkerSet>();
        traits = new ArrayList<TraitSet>();
        associations = new ArrayList<AssociationSet>();
        this.id = id;
    }

    /**
     * Returns all markersets in this project
     * @return
     */
    public ArrayList<MarkerSet> getMarkers()
    {
        return markers;
    }

    /**
     * Returns all traitsets in this project
     * @return
     */
    public ArrayList<TraitSet> getTraits()
    {
        return traits;
    }

    /**
     * Returns the names of all traitsets in this project as Strings
     * @return
     */
    public ArrayList<String> getTraitNames()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("projectid=" + this.id);
        return DataManager.runSelectQuery("name", "traitset", true, where, null);
    }

    /**
     * Returns the names of all markersets in this project as Strigns
     * @return
     */
    public ArrayList<String> getMarkerNames()
    {
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < markers.size(); i++)
        {
            names.add(this.markers.get(i).getName());
        }
        return names;
    }

    /**
     * Returns the name of this project.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Looks through all traitsets in this project and returns the one
     * with the matching name.
     * @param traitname the name of the traitset to look for
     * @return the corresponding traitset or null.
     */
    public TraitSet getTrait(String traitname)
    {
        for (int i = 0; i < this.traits.size(); i++)
        {
            if (this.traits.get(i).getName().equals(traitname))
            {
                return this.traits.get(i);
            }
        }

        ArrayList<String> where = new ArrayList<String>();
        where.add("loadcmpt = 0");
        ArrayList<String> noncmptMarkers =
                DataManager.runSelectQuery("name", "traitset", true, where, null);

        for(String s: noncmptMarkers)
        {
            if(s.equals(traitname))
                return new TraitSet(s); 
        }

        return null;
    }

    /**
     * Returns the traitset with the specified id
     * @param id the id of the traitset to find
     * @return the traitset in this prject with the matching id
     */
    public TraitSet getTrait(int id)
    {
        for (int i = 0; i < this.traits.size(); i++)
        {
            if (this.traits.get(i).getId() == id)
            {
                return this.traits.get(i);
            }
        }
        return null;
    }

    /**
     * Looks through all markersets in this project and returns the markerset
     * with the corresponding name.
     * @param markername The name of the markerset to return
     * @return the markerset with the name
     */
    public MarkerSet getMarker(String markername)
    {
        for (int i = 0; i < this.markers.size(); i++)
        {
            if (this.markers.get(i).getName().equals(markername))
            {
                return this.markers.get(i);
            }
        }

        ArrayList<String> where = new ArrayList<String>();
        where.add("loadcmpt = 0");
        where.add("projectid=" + this.getId());
        ArrayList<String> noncmptMarkers =
                DataManager.runSelectQuery("name", "markerset", true, where, null);

        for(String s: noncmptMarkers)
        {
            if(s.equals(markername))
                return new MarkerSet(s); 
        }

        return null;
    }

    /**
     * Looks through all markersets and returns the markerset with the
     * id passed in.
     * @param id the id to look for
     * @return the markerset with the corresponding id.
     */
    public MarkerSet getMarker(int id)
    {
        for (int i = 0; i < this.markers.size(); i++)
        {
            if (this.markers.get(i).getId() == id)
            {
                return this.markers.get(i);
            }
        }
        return null;
    }

    /**
     * Removes the markerset with the name passed in
     * @param name the name of the markerset to remove
     * @param jf the frame that will own the deletion dialogs.
     */
    public void removeMarker(String name)
    {
        for (int i = 0; i < markers.size(); i++)
        {
            if (markers.get(i).getName().equals(name))
            {
                for (int j=0; j < associations.size(); j ++)
                {
                    AssociationSet a = associations.get(j);
                    if (a.getMarkerSet() == markers.get(i))
                    {
                        associations.remove(a);
                        break;
                    }
                }
                markers.get(i).delete();
                markers.remove(i);
                return;
            }
        }
    }

    /**
     * Removes the traitset with the name passed in
     * @param name the name of the traitset to remove
     * @param jf the owner of all deletion dialogs.
     */
    public void removeTrait(String name)
    {
        for (int i = 0; i < traits.size(); i++)
        {
            if (traits.get(i).getName().equals(name))
            {
                for (int j=0; j < associations.size(); j ++)
                {
                    AssociationSet a = associations.get(j);
                    if (a.getTraitSet() == traits.get(i))
                    {
                        associations.remove(a);
                        break;
                    }
                }

                traits.get(i).delete();
                traits.remove(i);
                return;
            }
        }
    }

    /**
     * Removes the associationset with the passed in name
     * @param name the associationset to remove
     * @param owner the dialog owner
     */
    public void removeAssociation(String name)
    {
        for (int i = 0; i < associations.size(); i++)
        {
            if (associations.get(i).getName().equals(name))
            {
                associations.get(i).delete(this.id);
                associations.remove(i);
                return;
            }
        }
    }

    /**
     * Doesn't do anything
     */
    public void prepForDeletion()
    {
    }

    /**
     * Returns all associationsets in this project
     * @return
     */
    public ArrayList<AssociationSet> getAssocs()
    {
        return this.associations;
    }

    /**
     * Returns the database id of this project.
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the associationset object with the given name
     * @param assocName the name of the associationset in this project to find.
     * @return
     */
    public AssociationSet getAssociation(String assocName)
    {
        for (AssociationSet assoc : this.associations)
        {
            if (assoc.getName().equals(assocName))
            {
                return assoc;
            }
        }
        return null;
    }

    /**
     * Returns true if there is an association set, loaded or not, present
     * int the project with the name
     * @return
     */
    public boolean isAssocNamePresentInProject(String name)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("projectid = " + this.id);
        where.add("name=\'" + name + "\'");
        return DataManager.runSelectQuery("id", "assocset", true, where, null).size() > 0;
    }

    /**
     * Returns the names of all associations associated with this project
     * @return
     */
    public ArrayList<String> getAssocNames()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("projectid = " + this.id);
        return DataManager.runSelectQuery("id", "assocset", true, where, null);
    }

    /**
     * Returns the associationset with the given id in this project
     * @param id the id of the associationset to find.
     * @return
     */
    public AssociationSet getAssociation(int id)
    {
        for (AssociationSet assoc : this.associations)
        {
            if (assoc.getId() == id) 
            {
                return assoc;
            }
        }
        return null;
    }

    /**
     * Delete this project
     * @param jf the owner of all deletion dialogs. 
     * @return
     */
    public boolean delete()
    {

        for (int i = 0; i < associations.size(); i++)
        {
            associations.get(i).delete(this.id);
        }

        for (int i = 0; i < traits.size(); i++)
        {
            traits.get(i).delete();
        }

        for (int i = 0; i < markers.size(); i++)
        {
            markers.get(i).delete();
        }

        ThreadingItemFrame tif = ThreadingItemFrame.getInstance();
        DeletionItem di = new DeletionItem(tif, id,
                DeletionItem.DELETE_PROJECT, this.name);
        tif.addToThreadList(di);
        tif.setVisible(true);
        tif.setDefaultCloseOperation(HIDE_ON_CLOSE);

        return true;
    }

    /**
     * Refresh the traitlist in the project to be 100% completely agreeable
     * to what is in the database. 
     * @param traits the db queried results of traits
     */
    void refreshTraits(ArrayList<ParameterSet> traits)
    {
        ArrayList<TraitSet> validTrait = new ArrayList<TraitSet>();

        for (ParameterSet p : traits)
        {
            boolean found = false;
            for (TraitSet t : this.traits)
            {
                if (p.value_int == t.getId())
                {
                    validTrait.add(t);
                    t.updateTraitSubsets();
                    found = true;
                }
            }

            if (!found)
            {
                TraitSet t = new TraitSet(p.value_int, this, p.value_string, p.value_int_2, p.value_string_2);
                this.traits.add(t);
                validTrait.add(t);
            }
        }

        ArrayList<TraitSet> toRemove = new ArrayList<TraitSet>();
        for (TraitSet t : this.traits)
        {
            if (!validTrait.contains(t))
            {
                toRemove.add(t);
            }
        }

        for (TraitSet t : toRemove)
        {
            this.traits.remove(t);
        }
    }

    /**
     * Refresh the Project's marker list to be the same as the databse
     * @param markers the db queried list of markers. 
     */
    void refreshMarkers(ArrayList<ParameterSet> markers)
    {
        ArrayList<MarkerSet> validMarker = new ArrayList<MarkerSet>();

        for (ParameterSet p : markers)
        {
            boolean found = false;
            for (MarkerSet m : this.markers)
            {
                if (p.value_int == m.getId())
                {
                    validMarker.add(m);
                    found = true;
                }
            }

            if (!found)
            {
                MarkerSet m = new MarkerSet(p.value_int, this, p.value_string);
                this.markers.add(m);
                validMarker.add(m);
            }
        }

        ArrayList<MarkerSet> toRemove = new ArrayList<MarkerSet>();
        for (MarkerSet m : this.markers)
        {
            if (!validMarker.contains(m))
            {
                toRemove.add(m);
            }
        }

        for (MarkerSet m : toRemove)
        {
            this.markers.remove(m);
        }
    }

    /**
     * Refresh the list of associationsets in this project to be consistent
     * with the database
     * @param assocs the db queried list of associationsets.
     */
    void refreshAssocs(ArrayList<ParameterSet> assocs)
    {
        ArrayList<AssociationSet> validAssoc = new ArrayList<AssociationSet>();

        for (ParameterSet p : assocs)
        {
            boolean found = false;
            for (AssociationSet a : this.associations)
            {
                if (p.value_int == a.getId())
                {
                    validAssoc.add(a);
                    a.checkForUpdate(a.getTraitSet());
                    found = true;
                }
            }

            if (!found)
            {
                AssociationSet a = new AssociationSet(p.value_int, this,
                        p.value_string, p.value_double, p.value_int_2, 
                        p.value_int_3, p.value_int_4, p.value_bool1, p.value_int_5,
                        p.value_int_6);
                this.associations.add(a);
                validAssoc.add(a);
            }
        }

        ArrayList<AssociationSet> toRemove = new ArrayList<AssociationSet>();
        for (AssociationSet a : this.associations)
        {
            if (!validAssoc.contains(a))
            {
                toRemove.add(a);
            }
        }

        for (AssociationSet a : toRemove)
        {
            this.associations.remove(a);
        }
    }

    /**
     * Call all datasets in this project to remove data that we don't want to
     * serialize. 
     */
    void dropByNeed()
    {
        for (AssociationSet as : this.associations)
        {
            as.dropByNeed();
        }
        for (MarkerSet ms : this.markers)
        {
            ms.dropByNeed();
        }
        for (TraitSet ts : this.traits)
        {
            ts.dropByNeed();
        }
    }

    /**
     * Rename this project in the database. 
     * @param newName
     */
    public void rename(String newName)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        DataManager.runUpdateQuery("project", "name", newName, where);
        this.name = newName;
    }

    /**
     * Returns true if this project is deleted in the database
     * @return
     */
    public boolean isDeleted()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        return DataManager.runSelectQuery("deleted", "project", true, where, null).get(0).equals("1");
    }

    /**
     * Returns the team id associated with this project
     */
    public int getTeamId()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + id);
        return Integer.parseInt((String)DataManager.runSelectQuery("teamid", "project", true, where, null).get(0));
    }
}
