package algorithm;

import realdata.DataManager;
import realdata.Data1;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * AlgorithmData is the class that communicates with the database in order
 * to populate the list of running algorithms that are seen on the screen
 * at any one time. It also controls which algorithms the user has chosen
 * to ignore. 
 * @author rcurtis
 */
public class AlgorithmData implements Serializable
{
    /**
     * The file name of where the list of algorithms to ignore from the
     * database is stored.
     */
    private static final String filename = "algos.ser";
    /**
     * The filename of the SQLSettings that are stored upon close. This
     * happens through this class, for some strange reason.
     */
    private static final String filename_sets = "sets.ser";
    /**
     * The list of algorithms is only read from the database just once - in
     * order to avoid frequent repetition, we store it in this variable. 
     */
    private static ArrayList<Algorithm> allAlgos;
    /**
     * This is the list of algorithms that we want to ignore. The user
     * can delete errored or completed algorithms. Although this has
     * no effect in the database, it is populated in this list and the user
     * no longer has to worry about them. 
     */
    private static ArrayList<Algorithm> ignoredAlgos;

    /**
     * This method will deserialize the ignored Algorithm file in order
     * to find out which DB algorithms to not add to the list. It will then
     * check to see if it needs to query for more algorithms from the database.
     *
     * Once the algorithms are started, it no longer touches the database
     * until after the program shuts down and restarts. The lsit management
     * happens directly through the GUI after that. 
     * @return
     */
    public static ArrayList<Algorithm> getAllAlgos()
    {
        if (ignoredAlgos == null)
        {
            FileInputStream fis = null;
            ObjectInputStream in = null;
            try
            {
                fis = new FileInputStream(filename);
                in = new ObjectInputStream(fis);
                ignoredAlgos = (ArrayList<Algorithm>) in.readObject();

                /*for(int i = 0; i < allAlgos.size(); i ++)
                allAlgos.get(i).wakeUp();*/

                in.close();
                System.out.println("Success! " + ignoredAlgos.size());
            }
            catch (IOException ex)
            {
                System.out.println("Serialization file not found, creating new algorithm data.");
                ignoredAlgos = new ArrayList<Algorithm>();
            }
            catch (ClassNotFoundException ex)
            {
                ex.printStackTrace();
                ignoredAlgos = new ArrayList<Algorithm>();
            }
        }

        if (allAlgos == null)
        {
            ArrayList<String> cols = new ArrayList<String>();
            cols.add("jobtype");
            cols.add("jobid");
            cols.add("projectid");
            cols.add("traitid");
            cols.add("markerid");
            cols.add("appid");
            cols.add("start");
            cols.add("status");

            ArrayList<String> whereargs = new ArrayList<String>();
            whereargs.add("jobid = jobstatus.id");
            whereargs.add("userid = user.id");
            whereargs.add("uid = \'" + Data1.getInstance().mysqlusername + "\'");
            ArrayList<HashMap<String, String>> vals = DataManager.runMultiColSelectQuery(cols, "jobrun, jobstatus, user", true, whereargs, null);
            allAlgos = new ArrayList<Algorithm>();
            for (HashMap<String, String> val : vals)
            {
                if (!val.get("status").equals("CNL"))
                {
                    Algorithm a = new Algorithm(Integer.parseInt(val.get("jobtype")),
                            Integer.parseInt(val.get("jobid")), Integer.parseInt(val.get("projectid")),
                            Integer.parseInt(val.get("traitid")), Integer.parseInt(val.get("markerid")),
                            val.get("appid"), val.get("start"), val.get("status"));

                    if (!isIgnoreAlgorithm(a))
                    {
                        a.algoUpdate();
                        allAlgos.add(a);
                    }
                }
            }
        }
        return allAlgos;
    }

    /**
     * Serializes the ignored algorithms and the SQLSettings so they can be
     * access on a relaunch. It may be nice to someday move the SQL serialization
     * somewhere else. We've had to do a lot of clunky things because this was
     * in this class, as an artifact!
     */
    public static void serialize()
    {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try
        {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(ignoredAlgos);
            out.close();
            fos = new FileOutputStream(filename_sets);
            out = new ObjectOutputStream(fos);
            out.writeObject(Data1.getInstance());
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Adds the algorithm to the ignore list so that it will not be
     * repopulated into the list of algorithms when the application starts
     * again. 
     * @param a
     */
    public static void ignoreAlgorithm(Algorithm a)
    {
        ignoredAlgos.add(a);
    }

    /**
     * Looks at the given algorithm against the list to determine whether
     * this algorithm should be ignored or added to the GUI list.
     * @param a the algorithm to query against.
     * @return whether or not to ignore the algorithm. 
     */
    private static boolean isIgnoreAlgorithm(Algorithm a)
    {
        for (Algorithm m : ignoredAlgos)
        {
            if (a.getName().equals(m.getName()))
            {
                return true;
            }
        }
        return false;
    }
}
