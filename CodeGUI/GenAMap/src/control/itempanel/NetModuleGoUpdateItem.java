package control.itempanel;

import BiNGO.BiNGOresults2GenAMap;
import control.GOFrame;
import datamodel.Network;
import datamodel.Trait;
import datamodel.TraitTree;
import javax.swing.JFrame;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * We want to iterate through all 20 modules, calculate their GO enrichments,
 * and then insert that into the database. This step only happens after a
 * module enrichment analysis has completed on the server side. 
 *
 * @author rcurtis
 */
public class NetModuleGoUpdateItem extends ThreadItem
{
    /**
     * The form that will display the status of this item. form.repaint will
     * update the GUI
     */
    private JFrame form;
    /**
     * The last error that occured
     */
    private String errorText = "";
    /**
     * The current status
     */
    private String status = "In queue ...";
    /**
     * The network that this module belongs to
     */
    private Network net;
    /**
     * The name of the association set we're working with
     */
    private String assocname;
    /**
     * The go annotation that we should use
     */
    private String gocode;

    /**
     * Creates a new TraitTreeUpdateItem.
     * @param form
     * @param tt
     */
    public NetModuleGoUpdateItem(JFrame form, Network n, String assocname, String gocode)
    {
        this.form = form;
        this.net = n;
        this.assocname = assocname;
        this.gocode = gocode;
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
        return "GO module update " + net.getName();
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
        return "Network modules are ready to view!";
    }
    class Task extends Thread
    {
        @Override
        public void run()
        {
            status = "Updating ...";
            setValue(5);
            form.repaint();

            int projid = net.getTraitSet().getProjectId();
            ArrayList<String> where = new ArrayList<String>();
            where.add("name=\'" + assocname + "\'");
            where.add("projectid=" + projid);
            int associd = Integer.parseInt((String)
                    DataManager.runSelectQuery("id", "assocset", true, where, null).get(0));

            //initialize the GO frame.
            ArrayList<Trait> traits = net.getTraitSet().getTraits();
            HashSet reference = new HashSet<String>();
            for (Trait t : traits)
            {
                reference.add(t.getName().toUpperCase());
            }
            GOFrame go = new GOFrame(reference, reference, net.getTraitSet().getSpecies(),
                    net.getTraitSet());
            go.setParameters(.05 + "", "FDR", "Hyper", gocode);

            where.clear();
            where.add("netid=" + net.getId());
            where.add("assocsetid=" + associd);
            where.add("goanno=\'" + gocode + "\'");
            ArrayList<String> ids = DataManager.runSelectQuery("id", "netmodule", true, where, null);

            status = "Performing GO analysis ...";
            form.repaint();

            int i = 0;
            for (String entry : ids)
            {
                i++;

                //get traits
                where.clear();
                where.add("id=" + entry);
                String listid = (String)DataManager.runSelectQuery("traitlistid", "netmodule", true, where, null).get(0);
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
                        if (!s.equals(""))
                        {
                            names.add(net.getTraitSet().getTrait(Integer.parseInt(s)).getName().toUpperCase());
                        }
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
                String stringRes = ",";
                if(res != null)
                    stringRes = res.getStringRepresentationOfGoResults();
                where.clear();
                int goid = -1;
                if (goid == -1)
                {
                    ArrayList<String> args = new ArrayList<String>();
                    args.add(entry);
                    args.add(stringRes);
                    goid = Integer.parseInt(DataManager.runFunction("insert_go_list_module", args));
                }

                String subbynm = "module" + (i-1) + "id" + associd + gocode;
                where.clear();
                where.add("name=\'" + subbynm + "\'");
                DataManager.runUpdateQuery("traitsubset", "golist", "" + goid, where);

                setValue(5 + (int) ((double) i / (double) ids.size() * 92.0));
                form.repaint();
            }

            where.clear();
            setValue(100);
            form.repaint();
            go.dispose();
        }
    }
}
