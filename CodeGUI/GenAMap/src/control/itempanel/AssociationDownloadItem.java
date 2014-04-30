package control.itempanel;

import datamodel.Marker;
import datamodel.Trait;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import realdata.DataManager;

/**
 * This download item will download the large association sets into several
 * resolutions of matrix data so that we can see the overall structure in a 
 * matrix format. 
 *
 * @author flaviagrosan
 * @author rcurtis
 */
public class AssociationDownloadItem extends ThreadItem
{
    /**
     * The frame that represents this task. We can call the repaint method
     * to re-sync the visualization with the display
     */
    private JFrame form;
    /**
     * The id of the associationset that we are downloading
     */
    private int associd;
    /**
     * The directory to which we will write all of the information. 
     */
    private String dir;
    /**
     * An array list of traits that will be used (in order) to find the resolution
     * data for this association set.
     */
    private ArrayList<Trait> traits;
    /**
     * An array list of markers taht will be used to calculate the resolution
     * data for this association set.
     */
    private ArrayList<Marker> markers;
    /**
     * A string representing the current status of this association download
     */
    private String status;
    /**
     * A string representing the error text of the last error that occured
     * during downloading.
     */
    private String errorText="";

    /**
     * Creates a new AssociationDownloadItem
     * @param form the form that displays the ItemPanel to represent the
     * display
     * @param id the id of the association set to download
     * @param dir the directory to store the resolution data
     * @param traits the traits that are in this assocset
     * @param markers the markers that are in this assocset
     */
    public AssociationDownloadItem(JFrame form, int id, String dir, ArrayList<Trait> traits,
            ArrayList<Marker> markers)
    {
        this.form = form;
        this.associd = id;
        this.dir = dir;
        this.traits = new ArrayList<Trait>();

        for(int i = 0; i < traits.size(); i ++)
        {
            this.traits.add(new Trait(traits.get(i)));
        }
        this.markers = markers;

        this.status = "In queue ...";
        this.setValue(0);
    }

    @Override
    public String getName()
    {
        return "association dir " + this.dir;
    }

    @Override
    public String getErrorText()
    {
        return this.errorText;
    }

    @Override
    public String getStatus()
    {
        return this.status;
    }

    @Override
    public String getSuccessMessage()
    {
        return "Successfully downloaded to: " + this.dir;
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
            status = "Downloading data... ";
            form.repaint();

            ArrayList<ArrayList<BufferedWriter>> outputStreams = createOutputStreams(traits, markers, dir);

            try
            {
                ArrayList<String> cols = new ArrayList<String>();
                cols.add("thresh");
                cols.add("ispval");
                ArrayList<String> where = new ArrayList<String>();
                where.add("id=" + associd);
                HashMap<String, String> res = (HashMap<String,String>)DataManager.runMultiColSelectQuery(cols, "assocset", true, where, null).get(0);
                double thresh = Double.parseDouble(res.get("thresh"));
                boolean ispval = res.get("ispval").equals("1");
                runQueryTraitByTrait(outputStreams, traits, associd, ispval, thresh);
            }
            catch (Exception e)
            {
                closeOutputStreams(outputStreams);
                errorText = e.getMessage();
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
            }
            setValue(100);
        }

        /**
         * Create the output streams for each of the resolutions. These will
         * be written to as the data is ready. This way we don't have to
         * hold all the data in memory. 
         * @param traits
         * @param markers
         * @param dir
         * @return
         */
        private ArrayList<ArrayList<BufferedWriter>> createOutputStreams(ArrayList<Trait> traits,
                ArrayList<Marker> markers, String dir)
        {
            int tsize = traits.size();
            int msize = markers.size();
            int size = Math.max(tsize, msize);
            int max = 5;
            ArrayList<ArrayList<BufferedWriter>> toRet = new ArrayList<ArrayList<BufferedWriter>>();
            FileWriter fstream = null;
            try
            {
                ArrayList<BufferedWriter> toAdd = new ArrayList<BufferedWriter>();
                fstream = new FileWriter(dir + "//" + "assoc1_1.txt");
                BufferedWriter out = new BufferedWriter(fstream);
                toAdd.add(out);
                toRet.add(toAdd);

                int idx = 1;
                int cur = 1;
                size = (int) (Math.ceil(size / 2));
                while (idx < max && size > 250)
                {
                    cur = cur * 4;
                    toAdd = new ArrayList<BufferedWriter>();
                    for (int i = 1; i <= cur; i++)
                    {
                        fstream = new FileWriter(dir + "//" + "assoc" + (idx + 1) + "_" + i + ".txt");
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
                setIsError(true);
                errorText = ex.getMessage();
            }
            return toRet;
        }

        /**
         * Close all the output streams.
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
                        setIsError(true);
                        errorText = ex.getMessage();
                    }
                }
            }
        }

        /**
         * Go trait by trait through the association set and create the output
         * resolution data. Save to each file. 
         * @param outputStreams
         * @param traits
         * @param associd
         * @param isPval
         * @param thresh
         * @throws IOException
         */
        private void runQueryTraitByTrait(ArrayList<ArrayList<BufferedWriter>> outputStreams, ArrayList<Trait> traits, int associd, boolean isPval, double thresh) throws IOException
        {
            int tsize = traits.size();
            int msize = markers.size();
            ArrayList<Integer> noTraitsPerFile = new ArrayList<Integer>();
            ArrayList<Integer> noTraitsPerCell = new ArrayList<Integer>();
            ArrayList<Integer> noMarkersPerFile = new ArrayList<Integer>();
            ArrayList<Integer> noMarkersPerCell = new ArrayList<Integer>();

            int i = 1;
            for (ArrayList<BufferedWriter> a : outputStreams)
            {
                noTraitsPerFile.add((int) Math.ceil((double) tsize / (double) i));
                i *= 2;
            }
            i = 1;
            for (ArrayList<BufferedWriter> a : outputStreams)
            {
                noMarkersPerFile.add((int) Math.ceil((double) msize / (double) i));
                i *= 2;
            }

            for (i = 0; i < noTraitsPerFile.size(); i++)
            {
                noTraitsPerCell.add((int) Math.ceil((double) noTraitsPerFile.get(i) / 200.0));
            }
            for (i = 0; i < noMarkersPerFile.size(); i++)
            {
                noMarkersPerCell.add((int) Math.ceil((double) noMarkersPerFile.get(i) / 200.0));
            }

            ArrayList<String> cols = new ArrayList<String>();
            cols.add("traitid");
            cols.add("value");

            ArrayList<Integer> curLine = new ArrayList<Integer>();
            int startID = 0;//traits.get(0).getId();
            for (int j : noMarkersPerCell)
            {
                curLine.add(0);
            }

            ArrayList<ArrayList<Double>> vals = new ArrayList<ArrayList<Double>>();
            ArrayList<ArrayList<Integer>> startsForTraits = new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<Integer>> startsForMarker = new ArrayList<ArrayList<Integer>>();

            for (int j = 0; j < noTraitsPerFile.size(); j++)
            {
                vals.add(new ArrayList<Double>());
                startsForTraits.add(new ArrayList<Integer>());
                startsForMarker.add(new ArrayList<Integer>());
                int limT = (int) (Math.pow(2, j) * (Math.ceil(noTraitsPerFile.get(j) / noTraitsPerCell.get(j)))) + 1;
                int limM = (int) (Math.pow(2, j) * (Math.ceil(noMarkersPerFile.get(j) / noMarkersPerCell.get(j)))) + 1;
                limT += (noTraitsPerFile.get(j) % noTraitsPerCell.get(j) == 0) ? 0 : 1;
                limM += (noMarkersPerFile.get(j) % noMarkersPerCell.get(j) == 0) ? 0 : 1;

                for (int k = 0; k < limM; k++)
                {
                    int s = startID + k * noMarkersPerCell.get(j);
                    startsForMarker.get(j).add(s);
                }
                startsForMarker.get(j).set(startsForMarker.get(j).size() - 1, startID + markers.size() + 10);

                for (int k = 0; k < limT; k++)
                {
                    if (k < limT - 1)
                    {
                        vals.get(j).add(0.0);
                    }
                    int s = startID + k * noTraitsPerCell.get(j);
                    startsForTraits.get(j).add(s);
                }
                startsForTraits.get(j).set(startsForTraits.get(j).size() - 1, startID + traits.size() + 10);
            }

            HashMap<Integer, Integer> idxMap = new HashMap<Integer, Integer>();

            for (i = 0; i < traits.size(); i++)
            {
                idxMap.put(traits.get(i).getId(), traits.get(i).getSortIdx());
            }

            for (i = 0; i < markers.size(); i++)
            {
                ArrayList<String> where = new ArrayList<String>();

                where.add("assocsetid=" + associd);
                where.add("markerid=" + markers.get(i).getId());
                if (isPval)
                {
                    where.add("value < " + thresh);
                }
                ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "association", true, where, "traitid");
                int prog = (int) ((double) i / (double) markers.size() * 98.0);
                setValue(prog);
                form.repaint();
                //setProgress(prog);

                for (int p = 0; p < vals.size(); p++)
                {
                    ArrayList<Double> v = vals.get(p);
                    ArrayList<Integer> divisions = startsForTraits.get(p);

                    int curCell = -1;
                    for (int n = 0; n < startsForTraits.get(p).size(); n++)
                    {
                        if (startsForTraits.get(p).get(n) > startID + i)
                        {
                            break;
                        }
                        curCell++;
                    }

                    for (HashMap<String, String> r : res)
                    {
                        int ix = Integer.parseInt(r.get("traitid"));
                        ix = idxMap.get(ix);
                        double weight = Double.parseDouble(r.get("value"));
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
                            //if(v.get(cell) < Math.abs(weight))
                            if (!isPval)
                            {
                                v.set(cell, v.get(cell)
                                        + Math.abs(weight) / Math.min(10.0, (double) noTraitsPerCell.get(p)));
                            }
                            else
                            {
                                if(weight == 0)
                                    weight = 1e-50;
                                if (v.get(cell) == 0)
                                {
                                    v.set(cell, -Math.log10(weight));
                                }
                                else
                                {
                                    v.set(cell, Math.max(-Math.log10(weight), v.get(cell)));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            setIsError(true);
                            errorText = e.getMessage();
                            return;
                        }
                    }

                    divisions = startsForMarker.get(p);
                    if ((curLine.get(p) + 1) >= divisions.size() || startID + i + 1 - divisions.get(curLine.get(p) + 1) == 0
                            || i + 1 == markers.size())
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
                        int s = row - 1;
                        int scell = 0;
                        if (row > 1)
                        {
                            //scell = 200 * (row-1);
                            if (p == 1)
                            {
                                s = 2;
                            }
                            else if (p == 2)
                            {
                                s = 4;
                                if (row == 3)
                                {
                                    s = 8;
                                }
                                else if (row == 4)
                                {
                                    s = 12;
                                }
                            }
                            else if (p == 3)
                            {
                                s = 8;
                                int toAdd = 8;
                                while (--row > 1)
                                {
                                    s += toAdd;
                                }
                            }
                            else
                            {
                                s = 16;
                                int toAdd = 16;
                                while (--row > 1)
                                {
                                    s += toAdd;
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
                            if (w > 0)// && curLine.get(p)-1 <= x)
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
