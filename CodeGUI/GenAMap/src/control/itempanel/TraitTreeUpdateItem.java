package control.itempanel;

import datamodel.TraitTree;
import datamodel.TraitTreeValIntPointers;
import javax.swing.JFrame;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This item will start the process that interacts with the database
 * to make interactions with the tree just require simple queries instead
 * of scrolling down the entire tree each time to find out which traits
 * are associated with a node.
 *
 * @author rcurtis
 */
public class TraitTreeUpdateItem extends ThreadItem
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
    private String errorText="";
    /**
     * The current status
     */
    private String status = "In queue ...";

    /**
     * Creates a new TraitTreeUpdateItem.
     * @param form
     * @param tt
     */
    public TraitTreeUpdateItem(JFrame form, TraitTree tt)
    {
        this.form = form;
        this.tree = tt;
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
        return "Update " + tree.getName();
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
        return "Tree is updated and almost ready to view!";
    }

    class Task extends Thread
    {

        @Override
        public void run()
        {
            ArrayList<String> where = new ArrayList<String>();
            where.add("id="
                    + tree.getId());
            if(DataManager.runSelectQuery("updated", "traittree", true, where, null).get(0).equals("1"))
            {
                status = "Complete";
                setValue(100);
                form.repaint();
                return;
            }

            status = "Updating ...";
            setValue(5);
            form.repaint();

            //get traits.
            ArrayList<String> cols = new ArrayList<String>();
            cols.add("traitid");
            cols.add("parentid");
            cols.add("level");
            where = new ArrayList<String>();
            where.add("ttid=" + tree.getId());
            where.add("traitid > -1");
            ArrayList<HashMap<String, String>> results =
                    DataManager.runMultiColSelectQuery(cols, "traittreeval",
                    true, where, null);
            int i = 0;

            status = "Traversing tree ...";
            form.repaint();
            HashMap<Integer, TraitTreeValIntPointers> map =
                    new HashMap<Integer, TraitTreeValIntPointers>();

            for (HashMap<String, String> h : results)
            {
                i++;
                int traitid = Integer.parseInt(h.get("traitid"));

                int parentid = Integer.parseInt(h.get("parentid"));

                ArrayList<ArrayList<String>> allVals = new ArrayList<ArrayList<String>>();
                Date d = new Date();

                while (parentid != 1)
                {
                    //trace up the tree one trait by one trait, adding to the list.
                    cols.clear();
                    cols.add("parentid");
                    cols.add("traitlist");
                    where.clear();
                    where.add("ttid=" + tree.getId());
                    where.add("id=" + parentid);
                    int id = parentid;

                    TraitTreeValIntPointers pointers = map.get(id);
                    int listid = 0;
                    if (pointers == null)
                    {
                        ArrayList<HashMap<String, String>> subres =
                                DataManager.runMultiColSelectQuery(cols, "traittreeval",
                                true, where, null);
                        for (HashMap<String, String> hh : subres)
                        {
                            parentid = Integer.parseInt(hh.get("parentid"));
                            if (hh.get("traitlist").equals("1"))
                            {
                                cols.clear();
                                cols.add(id + "");
                                String r = DataManager.runFunction("insert_list", cols);
                                //SQLController.runUpdateQuery("traittreeval", "traitlist", r, where);
                                listid = Integer.parseInt(r);
                            }
                            else
                            {
                                listid = Integer.parseInt(hh.get("traitlist"));
                            }
                        }
                        pointers = new TraitTreeValIntPointers(parentid, listid);
                        map.put(id, pointers);
                    }
                    else
                    {
                        parentid = pointers.parentid;
                        listid = pointers.listid;
                    }
                    pointers.addTrait(traitid+"");

//                    ArrayList<String> vals = new ArrayList<String>();
//                    vals.add(listid + "");
//                    vals.add(traitid + "");
//                    //SQLController.runInsertQuery(cols, vals, "listval");
//                    allVals.add(vals);
                }
//                cols.clear();
//                cols.add("listid");
//                cols.add("refid");
//                SQLController.runMultipleInsertQuery(cols, allVals, "listval");
//
//                if(i % 500 == 0)
//                {
//                    map.clear();
//                }

                setValue(5 + (int) ((double) i / (double) results.size() * 82.0));
                //Date d1 = new Date();
                form.repaint();;
                //System.out.println((d1.getTime() - d.getTime()));
            }

            status = ("Loading table ...");
            form.repaint();
            int numb = results.size() -1;
            int cnt = 0;
            Iterator it = map.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry pairs = (Map.Entry)it.next();
                TraitTreeValIntPointers temp = (TraitTreeValIntPointers) pairs.getValue();
                where.clear();
                where.add("id=" + temp.listid);
                DataManager.runUpdateQuery("traitlist", "list", temp.toString(), where);
                setValue(89 + (int)((double)cnt++ / (double)numb*10));
                form.repaint();
            }

            where.clear();

            where.add("id="
                    + tree.getId());
            DataManager.runUpdateQuery(
                    "traittree", "updated", "1", where);
            ThreadingItemFrame.getInstance().addToThreadList(
                    new TraitTreeGoUpdateItem(form, tree, false));

            setValue(100);
            form.repaint();
        }
    }
}
