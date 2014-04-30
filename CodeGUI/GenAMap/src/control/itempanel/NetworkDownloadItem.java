package control.itempanel;

import datamodel.Trait;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import realdata.DataManager;

/**
 * For large networks, we need to download them from the db.
 * We are going to create several files at a different resolutions so that
 * we only have to download all the data once.
 *
 * The data will be stored in the file system and will then be accessible
 * to the application at each resolution the file will be loaded down to
 * a certain level where the database will take over.
 *
 * @author rcurtis
 * @author fgrosan
 */
public class NetworkDownloadItem extends ThreadItem
{

    /**
     * The form that holds this item. A repaint will update the GUI
     */
    private JFrame form;
    /**
     * The database id of the network we are working with
     */
    private int netid;
    /**
     * An ordered list of the traits that we will be getting resolution data for
     */
    private ArrayList<Trait> traits;
    /**
     * The directory where we are going to save all of the information to. 
     */
    private String dir;
    /**
     * The error text of the last error that happened
     */
    private String errorText;
    /**
     * The current status of the network download
     */
    private String status = "In queue...";

    /**
     * Creates a new NetworkDownloadItem and prepares it to run.
     * @param form The form that holds this item
     * @param traits an ordered list of the traits to read in
     * @param netid the network id
     * @param dir the directory to save the data to.
     */
    public NetworkDownloadItem(JFrame form, ArrayList<Trait> traits, int netid, String dir)
    {
        this.form = form;
        this.netid = netid;
        this.dir = dir;
        this.traits = new ArrayList<Trait>();

        for(int i = 0; i < traits.size(); i ++)
        {
            this.traits.add(new Trait(traits.get(i)));
        }

        setValue(0);
    }

    @Override
    public String getName()
    {
        return "Network for " + traits.get(0).getTraitSet().getName();
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
        return "Network download finished";
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }

    class Task extends Thread
    {

        @Override
        public void run()
        {
            setValue(0);
            form.repaint();

            ArrayList<ArrayList<BufferedWriter>> outputStreams = null;

            try
            {
                outputStreams = createOutputStreams(traits, dir);
                status = "Downloading network ...";
                form.repaint();
                runQueryTraitByTrait(outputStreams, traits, netid);
            }
            catch (Exception e)
            {
                if(errorText == null)
                {
                    errorText = e.getMessage();
                }
                setIsError(true);
                return;
            }

            closeOutputStreams(outputStreams);
            try
            {
                FileWriter fstream = new FileWriter(dir + "//complete.txt");
                fstream.close();
            }
            catch (IOException e)
            {
                setIsError(true);
                errorText = e.getMessage();
                return;
            }
            setValue(100);
            form.repaint();
        }

        /**
         * Creates the output streams for each resolution. This way we don't
         * have to have all of the data in memory, we just write out to each
         * stream appropriately. 
         * @param traits
         * @param dir
         * @return
         */
        private ArrayList<ArrayList<BufferedWriter>> createOutputStreams(ArrayList<Trait> traits, String dir)
        {
            int size = traits.size();
            int max = 5;
            ArrayList<ArrayList<BufferedWriter>> toRet = new ArrayList<ArrayList<BufferedWriter>>();
            FileWriter fstream = null;
            try
            {
                ArrayList<BufferedWriter> toAdd = new ArrayList<BufferedWriter>();
                fstream = new FileWriter(dir + "//" + "net1_1.txt");
                BufferedWriter out = new BufferedWriter(fstream);
                toAdd.add(out);
                toRet.add(toAdd);

                int idx = 1;
                int cur = 1;
                size = (int) (Math.ceil(size / 2));
                while (idx < max && size > 250)
                {
                    cur = cur * 4 - (idx - 1) * 2;
                    if (cur == 4)
                    {
                        cur = 3;
                    }
                    toAdd = new ArrayList<BufferedWriter>();
                    for (int i = 1; i <= cur; i++)
                    {
                        fstream = new FileWriter(dir + "//" + "net" + (idx + 1) + "_" + i + ".txt");
                        out = new BufferedWriter(fstream);
                        toAdd.add(out);
                    }
                    toRet.add(toAdd);

                    idx++;
                    size = size / 2;
                }

            }
            catch (IOException ex)
            {
            }
            return toRet;
        }

        /**
         * Closes the outpust streams. This doesn't throw any exceptions. 
         * @param outputStreams
         */
        private void closeOutputStreams(ArrayList<ArrayList<BufferedWriter>> outputStreams)
        {
            for (ArrayList<BufferedWriter> a : outputStreams)
            {
                for (BufferedWriter bw : a)
                {
                    try
                    {
                        bw.close();
                    }
                    catch (IOException ex)
                    {
                    }
                }
            }
        }

        /**
         * Goes trait by trait and creates the resolution file in a smart way.
         * @param outputStreams
         * @param traits
         * @param netid
         * @throws IOException
         */
        private void runQueryTraitByTrait(ArrayList<ArrayList<BufferedWriter>> outputStreams, ArrayList<Trait> traits, int netid) throws IOException
        {
            int tsize = traits.size();
            ArrayList<Integer> noTraitsPerFile = new ArrayList<Integer>();
            ArrayList<Integer> noTraitsPerCell = new ArrayList<Integer>();

            int i = 1;
            for (ArrayList<BufferedWriter> a : outputStreams)
            {
                noTraitsPerFile.add((int) Math.ceil((double) tsize / (double) i));
                i *= 2;
            }

            for (i = 0; i < noTraitsPerFile.size(); i++)
            {
                noTraitsPerCell.add((int) Math.ceil((double) noTraitsPerFile.get(i) / 200.0));
            }

            ArrayList<String> cols = new ArrayList<String>();
            cols.add("trait2");
            cols.add("weight");
            ArrayList<String> cols2 = new ArrayList<String>();
            cols2.add("trait1");
            cols2.add("weight");

            ArrayList<Integer> curLine = new ArrayList<Integer>();
            int startID = 0;//traits.get(0).getId();
            for (int j : noTraitsPerCell)
            {
                curLine.add(0);
            }

            ArrayList<ArrayList<Double>> vals = new ArrayList<ArrayList<Double>>();
            ArrayList<ArrayList<Integer>> starts = new ArrayList<ArrayList<Integer>>();
            for (int j = 0; j < noTraitsPerFile.size(); j++)
            {
                vals.add(new ArrayList<Double>());
                starts.add(new ArrayList<Integer>());
                int lim = (int) (Math.pow(2, j) * (Math.ceil(noTraitsPerFile.get(j) / noTraitsPerCell.get(j)))) + 1;
                lim += (noTraitsPerFile.get(j) % noTraitsPerCell.get(j) == 0) ? 0 : 1;
                //lim++;
                for (int k = 0; k < lim; k++)
                {
                    if (k < lim - 1)
                    {
                        vals.get(j).add(0.0);
                    }
                    int s = startID + k * noTraitsPerCell.get(j);
                    starts.get(j).add(s);
                }
                starts.get(j).set(starts.get(j).size() - 1, startID + traits.size() + 10);
            }

            HashMap<Integer, Integer> idxMap = new HashMap<Integer, Integer>();

            for (i = 0; i < traits.size(); i++)
            {
                idxMap.put(traits.get(i).getId(), traits.get(i).getSortIdx());
            }

            for (i = 0; i < traits.size(); i++)
            {
                ArrayList<String> where = new ArrayList<String>();

                where.add("netid=" + netid);
                where.add("trait1=" + traits.get(i).getId());
                ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "networkval", true, where, "trait2");
                where.clear();
                where.add("netid=" + netid);
                where.add("trait2=" + traits.get(i).getId());
                ArrayList<HashMap<String, String>> res2 = DataManager.runMultiColSelectQuery(cols2, "networkval", true, where, "trait1");
                int prog = (int) ((double) i / (double) traits.size() * 99.0);
                setValue(prog);
                form.repaint();

                for (int p = 0; p < vals.size(); p++)
                {
                    ArrayList<Double> v = vals.get(p);
                    ArrayList<Integer> divisions = starts.get(p);

                    int curCell = -1;
                    for (int n = 0; n < starts.get(p).size(); n++)
                    {
                        if (starts.get(p).get(n) > startID + i)
                        {
                            break;
                        }
                        curCell++;
                    }

                    for (HashMap<String, String> r : res)
                    {
                        int ix = Integer.parseInt(r.get("trait2"));
                        /*for(Trait t:traits)
                        {
                        if(t.getId() == ix)
                        {
                        ix = t.getSortIdx();
                        break;
                        }
                        }*/
                        ix = idxMap.get(ix);
                        double weight = Double.parseDouble(r.get("weight"));
                        int cell;
                        try
                        {
                            cell = 0;
                            while (cell + 1 < divisions.size() && divisions.get(cell + 1) < ix)
                            {
                                cell++;
                            }
                            if (cell >= v.size())
                            {
                                cell = v.size() - 1;
                            }
                            if (v.get(cell) < Math.abs(weight))
                            {
                                v.set(cell, Math.abs(weight));
                            }
                        }
                        catch (Exception e)
                        {
                            setIsError(true);
                            errorText = e.getMessage();
                            return;
                        }
                    }

                    for (HashMap<String, String> r : res2)
                    {
                        int ix = Integer.parseInt(r.get("trait1"));

                        ix = idxMap.get(ix);
                        double weight = Double.parseDouble(r.get("weight"));
                        int cell;
                        try
                        {
                            cell = 0;
                            while (cell + 1 < divisions.size() && divisions.get(cell + 1) < ix)
                            {
                                cell++;
                            }
                            if (cell >= v.size())
                            {
                                cell = v.size() - 1;
                            }
                            if (v.get(cell) < Math.abs(weight))
                            {
                                v.set(cell, Math.abs(weight));
                            }
                        }
                        catch (Exception e)
                        {
                            setIsError(true);
                            errorText = e.getMessage();
                            return;
                        }
                    }

                    if ((curLine.get(p) + 1) >= divisions.size() || startID + i + 1 - divisions.get(curLine.get(p) + 1) == 0
                            || i + 1 == traits.size())
                    {
                        if (curLine.get(p) + 1 != divisions.size())
                        {
                            curLine.set(p, curLine.get(p) + 1);
                        }
                        else
                        {
                            System.err.println("Why am I here???");
                        }

                        //we need to figure out what file we are writing to.
                        int row = (int) Math.ceil((double) curLine.get(p) / 200.0);
                        int s = 0;
                        int scell = 0;
                        if (row > 1)
                        {
                            scell = 200 * (row - 1);
                            if (p == 1)
                            {
                                s = 2;
                            }
                            else if (p == 2)
                            {
                                s = 4;
                                if (row == 3)
                                {
                                    s = 7;
                                }
                                else if (row == 4)
                                {
                                    s = 9;
                                }
                            }
                            else if (p == 3)
                            {
                                s = 8;
                                int toAdd = 7;
                                while (--row > 1)
                                {
                                    s += toAdd;
                                    toAdd -= 1;
                                }
                            }
                            else
                            {
                                s = 16;
                                int toAdd = 15;
                                while (--row > 1)
                                {
                                    s += toAdd;
                                    toAdd -= 1;
                                }
                            }
                        }


                        BufferedWriter bw = null;
                        for (int x = scell; x < v.size(); x++)
                        {
                            if (x % 200 == 0)
                            {
                                s++;
                                try
                                {
                                    bw = outputStreams.get(p).get(s - 1);
                                }
                                catch (Exception e)
                                {
                                    System.out.println(e.getMessage());
                                }

                            }

                            double w = v.get(x);
                            if (w > 0 && curLine.get(p) - 1 <= x)
                            {
                                bw.write((curLine.get(p) - 1) + " " + x + " " + w + "\n\r");
                            }
                        }

                        for (int x = 0; x < v.size(); x++)
                        {
                            v.set(x, 0.0);
                        }
                    }
                }
            }
        }
    }
}
