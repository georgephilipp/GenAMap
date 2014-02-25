package control.itempanel;

import BiNGO.BiNGOresults2GenAMap;
import control.GOFrame;
import datamodel.Trait;
import datamodel.TraitTree;
import datamodel.TraitTreeValIntPointers;
import javax.swing.JFrame;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This item will start the process that interacts with the database
 * to make interactions with the tree just require simple queries instead
 * of scrolling down the entire tree each time to find out which traits
 * are associated with a node.
 *
 * @author rcurtis
 */
public class TraitTreeGoUpdateItem extends ThreadItem
{
    /**
     * The form that will display the status of this item. form.repaint will
     * update the GUI
     */
    private JFrame form;
    /**
     * the trait tree that this update item is interacting with
     */
    private final TraitTree tree;
    /**
     * The last error that occured
     */
    private String errorText = "";
    /**
     * The current status
     */
    private String status = "In queue ...";
    /**
     * Determines whether or not we should get the parameters from the BiNGO form
     * before running the analysis...
     */
    private boolean isShowForm = false;

    /**
     * Creates a new TraitTreeUpdateItem.
     * @param form
     * @param tt
     */
    public TraitTreeGoUpdateItem(JFrame form, TraitTree tt, boolean isShowForm)
    {
        this.form = form;
        this.tree = tt;
        this.isShowForm = isShowForm;
        setValue(0);
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }

    @Override
    public String getName()
    {
        return "GO Update " + tree.getName();
    }

    @Override
    public String getErrorText()
    {
        return this.errorText;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public String getSuccessMessage()
    {
        return "Tree is updated and ready to view!";
    }
    class Task extends Thread
    {
        @Override
        public void run()
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("id=" + tree.getId());
            //SQLController.runUpdateQuery(
            //        "traittree", "updated", "0", where);
            status = "Updating ...";
            setValue(5);
            form.repaint();

            //get non traits.
            ArrayList<String> cols = new ArrayList<String>();
            cols.add("id");
            cols.add("parentid");
            cols.add("level");
            cols.add("traitlist");
            cols.add("golist");
            where.clear();
            where.add("ttid=" + tree.getId());
            where.add("traitid = -1");
            ArrayList<HashMap<String, String>> results =
                    DataManager.runMultiColSelectQuery(cols, "traittreeval",
                    true, where, null);
            int i = 0;

            status = "Traversing tree ...";
            form.repaint();

            //initialize the GO frame.
            ArrayList<Trait> traits = tree.getTraitSet().getTraits();
            HashSet reference = new HashSet<String>();
            for (Trait t : traits)
            {
                reference.add(t.getName().toUpperCase());
            }
            GOFrame go;
            try
            {

                go = new GOFrame(reference, reference, tree.getTraitSet().getSpecies(),
                        tree.getTraitSet());
            }
            catch (Exception e)
            {
                setIsError(true);
                errorText = "No go information.";
                form.repaint();
                return;

            }

            if (!isShowForm)
            {
                go.setParameters(.05 + "", "FDR", "Hyper", "full");
            }
            else
            {
                go.getAndSetSettings();
            }


            for (HashMap<String, String> h : results)
            {
                i++;

                //get traits
                String listid = h.get("traitlist");
                where.clear();
                where.add("id =" + listid);
                ArrayList<String> traitname = DataManager.runSelectQuery("list", "traitlist", true, where, null);
                if (traitname.size() == 0)
                {
                    continue;
                }
                //System.out.println(traitname.get(0)+"\n\n\n");
                String[] traitnames = traitname.get(0).split(",");

                //run go enrichments
                HashSet<String> names = new HashSet<String>();
                for (String s : traitnames)
                {
                    try
                    {
                        names.add(tree.getTraitSet().getTrait(Integer.parseInt(s)).getName().toUpperCase());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        String hi = "hllo";
                    }
                }
                go.autoPerformAnalysis(names);
                //insert results into database.
                BiNGOresults2GenAMap res = go.getResults();
                if (res != null)
                {
                    if (i == 1)
                    {
                        where.clear();
                        where.add("id=" + tree.getId());
                        DataManager.runUpdateQuery("traittree", "gomethod", res.getStringRepresentationOfMethod(), where);
                    }
                    where.clear();
                    int goid = Integer.parseInt(h.get("golist"));
                    if (goid == -1)
                    {
                        ArrayList<String> args = new ArrayList<String>();
                        args.add(h.get("id"));
                        goid = Integer.parseInt(DataManager.runFunction("insert_go_list_ttv" + DataManager.getTeamCode(), args));
                    }
                    where.add("id=" + goid);
                    DataManager.runUpdateQuery("golist", "list", res.getStringRepresentationOfGoResults(), where);

                    setValue(5 + (int) ((double) i / (double) results.size() * 92.0));
                    form.repaint();
                }
                //Date d1 = new Date();
                //System.out.println((d1.getTime() - d.getTime()));
            }

            where.clear();

            where.add("id=" + tree.getId());
            //SQLController.runUpdateQuery(
            //        "traittree", "updated", "1", where);

            setValue(100);
            form.repaint();
            go.dispose();
        }
    }
}
