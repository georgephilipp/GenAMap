package control.itempanel;

import datamodel.Marker;
import datamodel.MarkerSet;
import datamodel.Model;
import datamodel.Network;
import datamodel.Population;
import datamodel.Trait;
import datamodel.TraitSet;
import datamodel.TraitTree;
import realdata.DataManager;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * This item in the item panel is responsible for deleting various data
 * structures. We determined to delete everything from this one item
 * and then just call methods. This allows us to have a little more
 * control over what is going on ... since many parts have to delete many
 * other parts. 
 * @author rcurtis
 */
public class DeletionItem extends ThreadItem
{
    /**
     * The name of the stuff we are deleting, trait, assoc, what?
     */
    private String name;
    /**
     * If there are errors, we want to try and specify to the user
     * what went wrong.
     */
    private String errorText = "";
    /**
     * We will update the user on exactly what we are deleting at a time
     * so they have an idea of what is going on.
     */
    private String status = "In queue ...";
    /**
     * the object that performs the deletion task on a separate thread. 
     */
    private Task t;
    /**
     * The type of deletion depends on what the object being deleted is.
     * We pass that in to the Task, this way we only have one object
     * to delete all the different types of objects. 
     */
    private int type = -10;
    public static final int DELETE_ASSOCIATION = 0;
    public static final int DELETE_NETWORK = 1;
    public static final int DELETE_TRAITSET = 5;
    public static final int DELETE_MARKERSET = 59;
    public static final int DELETE_PROJECT = 198;
    public static final int DELETE_TREE = 521;
    public static final int DELETE_POPSTRUCT = 783;
    public static final int DELETE_GTA = 18274262;
    /**
     * The owner of this deletion item. When we have updated the GUI, we
     * call repaint in order to reset the visualization. 
     */
    private JFrame form;
    /**
     * The id of the project that we are deleting from.
     */
    private int projectID;
    /**
     * A traitset if we are deleting such
     */
    private TraitSet ts;
    /**
     * The markerset, if we are deleting such
     */
    private MarkerSet ms;
    /**
     * The name of an association that a GTA belongs to
     */
    private String assocName;

    /**
     * Constructor
     * @param form will call repaint on this form
     * @param projID owner id of the object being deleted
     * @param type type of deletion
     * @param name name of object being deleted
     */
    public DeletionItem(JFrame form, int projID, int type, String name)
    {
        this.form = form;
        this.projectID = projID;
        this.type = type;
        this.name = name;
    }

    /**
     * To delete a traitset, we need the object, and so we pass it in
     * @param form
     * @param projID
     * @param type
     * @param name
     */
    public DeletionItem(JFrame form, int projID, int type, String name, TraitSet ts)
    {
        this.form = form;
        this.projectID = projID;
        this.type = type;
        this.name = name;
        this.ts = ts;
    }

    /**
     * To delete a markerset, we need the object, and so we pass it in. 
     * @param form
     * @param projID
     * @param type
     * @param name
     * @param ms
     */
    public DeletionItem(JFrame form, int projID, int type, String name, MarkerSet ms)
    {
        this.form = form;
        this.projectID = projID;
        this.type = type;
        this.name = name;
        this.ms = ms;
    }

    /**
     * To delete a gta, we need the association set as well
     * @param form
     * @param projID
     * @param type
     * @param name
     * @param assocName
     */
    public DeletionItem(JFrame form, int projID, int type, String name, String assocName)
    {
        this.form = form;
        this.projectID = projID;
        this.type = type;
        this.name = name;
        this.assocName = assocName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getErrorText()
    {
        return errorText;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public String getSuccessMessage()
    {
        return name + " has been deleted.";
    }

    @Override
    public void start()
    {
        t = new Task();
        t.start();
    }
    public class Task extends Thread
    {
        @Override
        public void run()
        {
            setValue(0);
            status = "Initializing ...";
            form.repaint();

            switch (type)
            {
                case DELETE_ASSOCIATION:
                    DeleteAssociations(projectID, name, 1.0, 0, true);
                    break;
                case DELETE_NETWORK:
                    DeleteNetwork(projectID, name, 1.0, 0, true);
                    break;
                case DELETE_TREE:
                    DeleteTree(projectID, name, 1.0, 0, true);
                    break;
                case DELETE_TRAITSET:
                    DeleteTraitSet(projectID, name, 1.0, 0, ts, true);
                    break;
                case DELETE_MARKERSET:
                    DeleteMarkerSet(projectID, name, ms, true);
                    break;
                case DELETE_PROJECT:
                    DeleteProject(projectID, name);
                    break;
                case DELETE_POPSTRUCT:
                    DeletePopStruct(projectID, name, 1.0, 0, true);
                    break;
                case DELETE_GTA:
                    DeleteGTA(projectID, assocName, name, 1.0, 0, true);
            }

            setValue(100);
            form.repaint();
        }

        private void DeleteProject(int projId, String name)
        {
            ArrayList<String> whereArgs = new ArrayList<String>();
            whereArgs.add("id=" + projId);
            DataManager.runUpdateQuery("project", "deleted", "1", whereArgs);

            whereArgs.clear();
            whereArgs.add("projectid=" + projId);
            DataManager.deleteQuery("sample", whereArgs);
            setValue(100);
        }

        private void DeleteAssociations(int projectId, String name, double factor, int offset, boolean callRefresh)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("projectid = " + projectId);
            where.add("name = '" + name + "'");

            int id;
            try
            {
                id = Integer.parseInt((String) DataManager.runSelectQuery("id", "assocset", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            try
            {
                where.clear();
                where.add("snpassocid = " + id);
                int mygtacnt = Integer.parseInt((String) DataManager.runSelectQuery("count(*)", "genetraitassocset", true, where, null).get(0));

                if(mygtacnt >  0)
                {
                    setIsError(true);
                    errorText = "Delete 3-way assocsets first";
                    return;
                }
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            setValue((int) (1.0 / factor + offset));
            status = "Deleteing association values ...";
            form.repaint();

            int count;
            try
            {
                where.clear();
                where.add("assocsetid = " + id);
                count = Integer.parseInt((String) DataManager.runSelectQuery("count(*)", "association", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            setValue((int) (5.0 / factor + offset));
            form.repaint();

            if (count < 100000)
            {
                DataManager.deleteQuery("association", where);
            }
            else
            {
                int times = count / 10000 + 1;

                int min = Integer.parseInt((String) (DataManager.runSelectQuery("min(id)", "association", true, where, null).get(0)));

                for (int i = 0; i < times; i++)
                {
                    where.clear();
                    where.add("assocsetid = " + id);
                    where.add("id < " + (min + 50000 * (i + 1)));
                    DataManager.deleteQuery("association", where);

                    setValue((int) ((5 + (double) (i + 1) / (double) times * 90) / factor + offset));
                    form.repaint();
                }
            }

            setValue((int) (95.0 / factor + offset));
            status = "Finishing up ...";
            form.repaint();

            where.clear();
            where.add("assocsetid=" + id);
            DataManager.deleteQuery("netmodule", where);

            where.clear();
            where.add("projectid = " + projectId);
            where.add("name = '" + name + "'");
            where.add("id = " + id);
            DataManager.deleteQuery("assocset", where);

            setValue((int) (100.0 / factor + offset));
        }

        private void DeleteTraitSet(int projID, String name, double factor, int offset, TraitSet ts, boolean callRefresh)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("projectid = " + projID);
            where.add("name = '" + name + "'");

            //TraitSet ts = Model.getInstance().getProject(projID).getTrait(name);

            setValue((int) (1.0 / factor + offset));
            status = "Deleting Associations";
            form.repaint();

            where.clear();
            where.add("tsid=" + ts.getId());
            ArrayList<String> res = DataManager.runSelectQuery("name", "assocset", true, where, null);

            double count = ts.getNetworks().size() + ts.getTraitTrees().size() + 3 + factor + res.size();
            int idx = 0;

            for (String s : res)
            {
                this.DeleteAssociations(projID, s, count, (int) (idx++ * 100.0 / count), false);
            }

            status = "Deleting Networks ...";
            form.repaint();

            for (Network n : ts.getNetworks())
            {
                this.DeleteNetwork(ts.getId(), n.getType(), count, 1 + (int) (idx++ * 100.0 / count), false);
            }

            status = "Deleting Trees ...";
            form.repaint();

            for (TraitTree t : ts.getTraitTrees())
            {
                this.DeleteTree(ts.getId(), t.getDBName(), count, 1 + (int) (idx++ * 100.0 / count), false);
            }

            int percmpt = 1 + (int) (idx++ * 100.0 / count);
            percmpt = (int) ((double) percmpt / factor + offset);
            int left = 100 - percmpt - 2;
            status = "Deleting TraitSet ...";
            setValue(percmpt);
            form.repaint();

            ArrayList<Trait> traits = ts.getTraits();

            if (traits.size() < 250)
            {
                for (int i = 0; i < traits.size(); i++)
                {
                    Trait t = traits.get(i);

                    t.delete();
                    setValue(percmpt + (int) (left / factor * ((double) (i + 1) / (double) traits.size())));
                    form.repaint();
                }
            }
            else
            {
                ArrayList<String> vals = new ArrayList<String>();
                for (int i = 0; i < traits.size(); i++)
                {
                    Trait t = traits.get(i);

                    if (i % 250 == 0 && vals.size() > 0)
                    {
                        DataManager.deleteMultipleQuery("traitval", "traitid", vals);
                        DataManager.deleteMultipleQuery("trait", "id", vals);
                        vals.clear();
                    }

                    vals.add(t.getId() + "");
                    setValue(percmpt + (int) (left / factor * ((double) (i + 1) / (double) traits.size())));
                    form.repaint();
                }
                if (vals.size() > 0)
                {
                    DataManager.deleteMultipleQuery("traitval", "traitid", vals);
                    DataManager.deleteMultipleQuery("trait", "id", vals);
                    vals.clear();
                }
            }

            where.clear();
            where.add("traitid =" + ts.getId());
            DataManager.runUpdateQuery("jobrun", "traitid", "-1", where);

            status = "Finishing up!";
            setValue((int) (99 / factor + offset));
            form.repaint();

            where.clear();
            where.add("id=" + ts.getId());
            DataManager.deleteQuery("traitset", where);

            setValue((int) (100 / factor + offset));
            form.repaint();

            Model.getInstance().getProject(projID).removeTrait(name);
        }

        /**
         * Deletes the specified network from the database and removes
         * all resolution data corresponding to the network!
         * @param traitsetid The traitset that owns this network
         * @param name The type of network that this is. 
         */
        private void DeleteNetwork(int traitsetid, String name, double factor, int offset, boolean callRefresh)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("ts = " + traitsetid);
            where.add("type = '" + name + "'");
            
            int id;
            try
            {
                id = Integer.parseInt((String) DataManager.runSelectQuery("id", "network", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            setValue((int) (1.0 / factor + offset));
            form.repaint();

            int count;
            try
            {
                where.clear();
                where.add("netid = " + id);
                count = Integer.parseInt((String) DataManager.runSelectQuery("count(*)", "networkval", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            status = "Deleting " + name + " ...";
            setValue((int) (5.0 / factor + offset));
            form.repaint();

            if (count < 40000)
            {
                DataManager.deleteQuery("networkval", where);
            }
            else
            {
                int times = count / 10000 + 1;

                int min = Integer.parseInt((String) (DataManager.runSelectQuery("min(id)", "networkval", true, where, null).get(0)));

                for (int i = 0; i < times; i++)
                {
                    where.clear();
                    where.add("netid = " + id);
                    where.add("id < " + (min + 50000 * (i + 1)));
                    DataManager.deleteQuery("networkval", where);

                    setValue((int) (((5 + (double) (i + 1) / (double) times * 80)) / factor + offset));
                    form.repaint();
                }
            }

            where.clear();
            where.add("netid = " + id);
            DataManager.deleteQuery("networkval", where);

            setValue((int) (86.0 / factor + offset));
            status = "Removing resolution data ...";
            form.repaint();

            File path = new File("data");
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory() && files[i].getName().
                        toLowerCase().contains(name.toLowerCase() + "_" + id))
                {
                    File[] fiToDel = files[i].listFiles();
                    for (File fi : fiToDel)
                    {
                        fi.delete();
                    }
                    files[i].delete();
                }
            }

            where.clear();
            where.add("netid=" + id);
            DataManager.deleteQuery("netmodule", where);

            //DataManager.deleteQuery("cluster", where);

            where.clear();
            where.add("ts = " + traitsetid);
            where.add("type = '" + name + "'");
            where.add("id = " + id);
            DataManager.deleteQuery("network", where);

            setValue((int) (100.0 / factor + offset));
            form.repaint();
            if (callRefresh)
            {
                //DataAddRemoveHandler.getInstance().refreshModel();
            }
        }

        /**
         * Deletes the tree from the database, along with the trait
         * information that we stored in other tables
         * @param traitsetid The traitset that owns this tree
         * @param name The name of the tree.
         */
        private void DeleteTree(int traitsetid, String name, double factor, int offset, boolean callRefresh)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("tsid = " + traitsetid);
            where.add("name = '" + name + "'");

            int ttid;
            try
            {
                ttid = Integer.parseInt((String) DataManager.runSelectQuery("id", "traittree", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            status = "Deleting supporting information ...";
            setValue((int) (1.0 / factor + offset));
            form.repaint();

            where.clear();
            where.add("ttid=" + ttid);
            where.add("traitlist != 1");
            ArrayList<String> listids = DataManager.runSelectQuery("traitlist", "traittreeval", true, where, null);
            ArrayList<String> goids = DataManager.runSelectQuery("golist", "traittreeval", true, where, null);
            int count = listids.size() + goids.size();
            int i = 0;

            for (String s : listids)
            {
                where.clear();
                where.add("id=" + s);
                if (!s.equals("-1"))
                {
                    DataManager.deleteQuery("traitlist", where);
                }

                setValue((int) ((((double) (i++) / (double) count * 44.0) + 1.0) / factor + offset));
                form.repaint();
            }

            for (String s : goids)
            {
                where.clear();
                where.add("id=" + s);
                if (!s.equals("-1"))
                {
                    DataManager.deleteQuery("golist", where);
                }

                setValue((int) ((((double) (i++) / (double) count * 44.0) + 1.0) / factor + offset));
                form.repaint();
            }

            status = "Deleting tree ...";
            form.repaint();

            where.clear();
            where.add("ttid = " + ttid);
            DataManager.deleteQuery("traittreeval", where);

            status = "Finishing up ...";
            setValue((int) (95.0 / factor + offset));
            form.repaint();

            where.clear();
            where.add("tsid = " + traitsetid);
            where.add("name = '" + name + "'");
            where.add("id = " + ttid);
            DataManager.deleteQuery("traittree", where);

            setValue((int) (100.0 / factor + offset));
            form.repaint();
            if (callRefresh)
            {
                //DataAddRemoveHandler.getInstance().refreshModel();
            }
        }

        private void DeleteMarkerSet(int ownerID, String name, MarkerSet ms, boolean callRefresh)
        {
            try
            {
                ArrayList<String> where = new ArrayList<String>();
                where.add("projectid = " + ownerID);
                where.add("name = '" + name + "'");

                int id;
                try
                {
                    id = Integer.parseInt((String) DataManager.runSelectQuery("id", "markerset", true, where, null).get(0));
                }
                catch (Exception e)
                {
                    setIsError(true);
                    errorText = e.getMessage();
                    return;
                }

                setValue(1);
                status = "Deleting associations ...";
                form.repaint();

                where.clear();
                where.add("msid=" + id);
                ArrayList<String> res = DataManager.runSelectQuery("name", "assocset", true, where, null);

                double count = ms.getPopulationNames().size() + res.size() + 3;

                int idx = 0;
                for (String s : res)
                {
                    this.DeleteAssociations(projectID, s, count, (int) (idx++ * 100.0 / count), false);
                }
                status = "Deleting pops ...";
                form.repaint();

                for (Population p : ms.getPopulations())
                {
                    this.DeletePopStruct(ms.getId(), p.getName(), count, 1 + (int) (idx++ * 100.0 / count), false);
                }

                int percmpt = 1 + (int) (idx++ * 100.0 / count);
                int left = 99 - percmpt - 2;

                status = "Deleting MarkerSet ...";
                setValue(percmpt);
                form.repaint();

                ArrayList<Integer> markers = Marker.getMarkerIds(id);

                if (markers.size() < 250)
                {
                    for (int i = 0; i < markers.size(); i++)
                    {
                        Integer m = markers.get(i);

                        Marker.delete(m);
                        setValue(percmpt + (int) (left * ((double) (i + 1) / (double) markers.size())));
                        form.repaint();
                    }
                }
                else
                {
                    int sz = markers.size();
                    while (sz != 0) //make sure that we delete them all!
                    {
                        ArrayList<String> vals = new ArrayList<String>();
                        for (int i = 0; i < markers.size(); i++)
                        {
                            Integer t = markers.get(i);

                            if (i % 25 == 0 && vals.size() > 0)
                            {
                                DataManager.deleteMultipleQuery("markerval", "markerid", vals);
                                DataManager.deleteMultipleQuery("marker", "id", vals);
                                vals.clear();
                            }
                            vals.add(t + "");
                            setValue(percmpt + (int) (left * ((double) (i + 1) / (double) markers.size())));
                            form.repaint();

                        }
                        if (vals.size() > 0)
                        {
                            DataManager.deleteMultipleQuery("markerval", "markerid", vals);
                            DataManager.deleteMultipleQuery("marker", "id", vals);
                            vals.clear();
                        }
                        markers = Marker.getMarkerIds(id);
                        sz = markers.size();
                    }
                }
                where.clear();
                where.add("markerid =" + id);
                DataManager.runUpdateQuery("jobrun", "markerid", "-1", where);

                status = "Finishing up!";
                setValue(99);
                form.repaint();

                where.clear();
                where.add("id=" + id);
                if (!DataManager.deleteQuery("markerset", where))
                {
                    errorText = DataManager.getLastError();
                    setIsError(true);
                    return;
                }

                setValue(100);
            }
            catch (Exception e)
            {
                errorText = e.getMessage();
                setIsError(true);
                return;
            }
        }

        /**
         * Deletes the population structure from the database
         */
        private void DeletePopStruct(int markerSetId, String name, double factor, int offset, boolean callRefresh)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("markersetid = " + markerSetId);
            where.add("name = '" + name + "'");

            int psid;
            try
            {
                psid = Integer.parseInt((String) DataManager.runSelectQuery("id", "popstruct", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            status = "Deleting population structure " + name + " ...";
            setValue((int) (1.0 / factor + offset));
            form.repaint();

            where.clear();
            where.add("popstructid=" + psid);
            DataManager.deleteQuery("structure", where);

            status = "Finishing up ...";
            setValue((int) (95.0 / factor + offset));
            form.repaint();

            where.clear();
            where.add("markersetid = " + markerSetId);
            where.add("name = '" + name + "'");
            where.add("id = " + psid);
            DataManager.deleteQuery("popstruct", where);

            setValue((int) (100.0 / factor + offset));
            form.repaint();
            if (callRefresh)
            {
                //DataAddRemoveHandler.getInstance().refreshModel();
            }
        }

        /**
         * This method brutally remotes a GTA from the database
         * such that it can never be known or heard of again in
         * future times.
         */
        private void DeleteGTA(int projectID, String assocName, 
                String name, double d, int q, boolean b)
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("projectid = " + projectID);
            where.add("name = '" + assocName + "'");

            int id;
            try
            {
                id = Integer.parseInt((String) DataManager.runSelectQuery("id", "assocset", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            try
            {
                where.clear();
                where.add("snpassocid = " + id);
                where.add("name = '" + name + "'");
                id = Integer.parseInt((String) DataManager.runSelectQuery("id", "genetraitassocset", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            setValue((int) 5.0);
            status = "Deleteing three-way association values ...";
            form.repaint();

            int count;
            try
            {
                where.clear();
                where.add("gtassocsetid = " + id);
                count = Integer.parseInt((String) DataManager.runSelectQuery("count(*)", "genetraitassociation", true, where, null).get(0));
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            setValue((int) (10.0 ));
            form.repaint();

            if (count < 100000)
            {
                DataManager.deleteQuery("genetraitassociation", where);
            }
            else
            {
                int times = count / 10000 + 1;

                int min = Integer.parseInt((String) (DataManager.runSelectQuery("min(id)", "genetraitassociation", true, where, null).get(0)));

                for (int i = 0; i < times; i++)
                {
                    where.clear();
                    where.add("assocsetid = " + id);
                    where.add("id < " + (min + 50000 * (i + 1)));
                    DataManager.deleteQuery("association", where);

                    setValue((int) ((10 + (double) (i + 1) / (double) times * 80) ));
                    form.repaint();
                }
            }

            try
            {
                where.clear();
                where.add("gtassocsetid = " + id);
                DataManager.deleteQuery("genegroups", where);
            }
            catch(Exception e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }

            setValue((int) (95.0));
            status = "Finishing up ...";
            form.repaint();

            where.clear();
            where.add("name = '" + name + "'");
            where.add("id = " + id);
            DataManager.deleteQuery("genetraitassocset", where);

            setValue((int) (100.0));
        }
    }
}
