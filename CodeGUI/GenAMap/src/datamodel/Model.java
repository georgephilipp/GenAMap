package datamodel;

import control.DataAddRemoveHandler;
import control.DatabaseSetup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import realdata.Tester;
import realdata.DataManager;
import realdata.Data1;
import java.awt.Color;
import java.util.Collections;

/**
 * This is the data model.  It is to be a reflection of the database; All classes
 * contained in here are just pointers to the db.  They also contain information
 * so that we don't have to query the db each time unless we have a refresh called.
 *
 * @author rcurtis
 */
public class Model implements Serializable
{
     /*
     * temporary variable for switching from one code version to the next
     */
    public static boolean isNewCode = false;
    

    /**
     * The colors that will be used to display the different populations, GO categories, etc.
     */
    public static Color[] colors =
    {
        new Color(0.0f, 0.0f, 1.0f), //1
        new Color(1.0f, 0.0f, 0.0f), //2
        new Color(0.0f, 1.0f, 0.0f), //3
        new Color(141, 211, 199),
        new Color(190, 186, 218),
        new Color(251, 128, 114),
        new Color(128, 177, 211),
        new Color(253, 180, 98),
        new Color(179, 222, 105),
        new Color(252, 205, 229),
        new Color(188, 128, 189),
        new Color(204, 235, 197),
        new Color(255, 237, 111),
        new Color(1.0f, 0.5f, 0.0f), //4
        new Color(0.5f, 0.75f, 1.0f), //5
        new Color(0.0f, 1.0f, 1.0f), //6
        new Color(1.0f, 0.0f, 1.0f), //7
        new Color(0.63f, 0.22f, 0.18f), //8
        new Color(1.0f, 1.0f, 0.0f), //10
        new Color(.39f, 0.58f, .93f), //12
        new Color(0.5f, 1.0f, 0.83f), //11
        new Color(.78f, 0.12f, 0.30f), //13
        new Color(255, 255, 179)
    //new Color(1.0f, .07f, .50f),  //14
    //new Color(.68f, 1.0f, .20f),  //15
    //new Color(.48f, .41f, .93f),  //16
    //new Color(1.0f, 0.5f, .44f), //17
    //new Color(0f, 1.0f, .5f), //18
    //new Color(.60f, .80f, .196f), //19
    //new Color(.88f, .75f, .88f), //20
    //new Color(.54f, .27f, .08f), //21
    //new Color(.56f, .74f, .56f), //22
    //new Color(.29f, 0.0f, .51f) //23
    };
    /**
     * Upon exit, the DataAddRemoveHandler stores the current open tab
     * in the data display here so that it can be accessed when the program
     * restarts.
     */
    private int tab = 0;
    /**
     * Upon exit, the DataAddRemoveHandler stores whether or not it is in JUNG
     * or matrix view in the visualization view here so that it can be accessed
     * when the program restarts.
     */
    private int viewertype = 0;
    /**
     * Database settings are serialized on exit in this file called sets.ser
     */
    private static final String filename_sets = "sets.ser";

    /**
     * Finds out what the user's team id is so that only projects in his
     * team show up in the data model. 
     * @return the team id for the logged in user. 
     */
    public String getTeamId()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("uid=\'" + Data1.getInstance().mysqlusername + "\'");
        return (String) DataManager.runSelectQuery("teamid", "user", true, where, null).get(0);
    }
    /**
     * ParameterSet is a dummy object that just passes a bunch of parameters to
     * different methods without having to pass each item individually. This is
     * use when we create objects from the database.
     */
    public class ParameterSet
    {

        public String value_string;
        public int value_int;
        public double value_double;
        public int value_int_2;
        public int value_int_3;
        public int value_int_4;
        public int value_int_5;
        public int value_int_6;
        public String value_string_2;
        public boolean value_bool1;

        /** 
         * Create a ParmeterSet ojbect with a string and an int.
         * @param v1 a string
         * @param v2 an int
         */
        public ParameterSet(String v1, int v2)
        {
            value_string = v1;
            value_int = v2;
        }

        /**
         * Creates a ParameterSet object with two strings, 4 ints, a boolean and
         * a double
         * @param v1 a string
         * @param v2 an int
         * @param v3 a double
         * @param v4 an int
         * @param v5 an int
         * @param v6 an int
         * @param v7 a string
         * @param v8 a boolean
         */
        public ParameterSet(String v1, int v2, double v3, int v4, int v5,
                int v6, String v7, boolean v8, int v9, int v10)
        {
            this.value_string = v1;
            this.value_int = v2;
            this.value_double = v3;
            this.value_int_2 = v4;
            this.value_int_3 = v5;
            this.value_int_4 = v6;
            this.value_string_2 = v7;
            this.value_bool1 = v8;
            this.value_int_5 = v9;
            this.value_int_6 = v10;
        }
    }
    /**
     * The instance object of this Model. User cannot create other instances;
     * all references to the data and the data db come through this instance.
     */
    private static Model instance;
    /**
     * The list of projects belonging to this model that are currently stored
     * in memory.
     */
    private ArrayList<Project> projects;
    /**
     * GenAMap stores the file path that the user last used so that they don't
     * always have to browse to the same folder.
     */
    private String lastFilePath = "";
    /**
     * Extra-curricular data folder that GenAMap will use to store different
     * local-files it uses.
     */
    public String directory = "data";
    /**
     * The model will be serialized at the end of the application's run in
     * order to save values. Most db info will not be stored.
     */
    private static final String filename = "model.ser";
    /**
     * We want to serialize the association object tabs, where the tabs are
     * and what projects are selected when the application closes. Unfortunately
     * this would require another serialization step. Therefore, we pass those
     * objects down to this level and serialize them here.
     */
    private ArrayList<Object> displaySettings;
    /**
     * We don't want too many threads all refreshing the model at the same
     * time
     */
    private boolean isRefreshing = false;

    /**
     * Returns the list of all projects associated with this model. These
     * should have been initialized according to the database in another step.
     * @return
     */
    public ArrayList<Project> getProjects()
    {
        return projects;
    }

    /**
     * Returns a list of objects representing the saved data trees from the last
     * run of GenAMap. DataAddRemoveHandler uses this object and this method
     * to save the state of the trees upon exit.
     * @return an object of trees.
     */
    public ArrayList<Object> getDisplaySettings()
    {
        return displaySettings;
    }

    /**
     * Returns an int representing whether the last run of GenAMap closed
     * in JUNG or matrix view.DataAddRemoveHandler uses this to know what
     * to display in the new view
     * @return whether GenAMap closed in matrix or jung view.
     */
    public int getViewerType()
    {
        return this.viewertype;
    }

    /**
     * Returns an int representing the open tab from the last
     * run of GenAMap. DataAddRemoveHandler uses this to know what tab was
     * last open.
     * @return the open tab when GenAMap closed
     */
    public int getTab()
    {
        return this.tab;
    }

    /**
     * Called whenever a user selects a file containing data to import. This
     * is saved to be the starting directory on the next import.
     * @param filePath
     */
    public void AccountForLastFilePath(String filePath)
    {
        this.lastFilePath = filePath;
    }

    /**
     * This is called in order to retrieve the last directory that the user
     * browsed to in order to select data in any import in GenAMap. 
     * @return
     */
    public String GetLastFilePath()
    {
        return this.lastFilePath;
    }

    /**
     * Creates a model from the serialization file and refreshes it with the
     * database.
     * @return a pointer to the current data model.
     */
    public static Model getInstance()
    {
        if (instance == null)
        {
            createNewModel();
        }
        return instance;
    }

    /**
     * Deserializes the settings needed to connect to the SQL database.
     */
    private static void deserializeSettings()
    {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try
        {
            fis = new FileInputStream(filename_sets);
            in = new ObjectInputStream(fis);
            Data1 s = (Data1) in.readObject();
            if (s != null)
            {
                Data1.setInstance(s);
            }

            in.close();
        }
        catch (IOException ex)
        {
        }
        catch (ClassNotFoundException ex)
        {
        }
        catch (ClassCastException ex)
        {
        }
    }
    static class dummyobject implements Comparable<dummyobject>
    {

        public int markerid;
        public int count;

        public int compareTo(dummyobject o)
        {
            if (count > o.count)
            {
                return -1;
            }
            else if (count < o.count)
            {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Deserializes the sql settings. If the settings are invalid, GenAMap will
     * allow the user to register or login to the application. If the user is
     * unable to do so, the application returns and sets up for an error.
     *
     * If the user does log in appropriately, the datamodel is populated and
     * the application will be able to run normally. 
     */
    private static void createNewModel()
    {
        if(!isNewCode)
        {
            deserializeSettings();
            if (!Tester.testConnection())
            {
                DatabaseSetup ds = new DatabaseSetup(null, true);
                ds.setVisible(true);
            }
            if (!Tester.testConnection())
            {
                System.err.println("Application cannot run without valid db settings.");
                return;
            }
        }

        /////////////////////////////////////garbage
        /*HashSet<String> set = new HashSet<String>();
        //HashMap<String, String> tidToname = new HashMap<String, String>();
        //HashMap<String, String> midToname = new HashMap<String, String>();

        ArrayList<String> where = new ArrayList<String>();
        where.add("gtassocsetid = 40");//brain
        where.add("abs(value) > 1e-2");

        ArrayList<String> cols = new ArrayList<String>();
        cols.add("geneid");
        cols.add("traitid");

        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "genetraitassociation", true, where, null);

        for (HashMap<String, String> r : res)
        {
            String gid = r.get("geneid");
            String tid = r.get("traitid");

            where.clear();
            where.add("id = " + gid);

            String geneName = DataManager.runSelectQuery("name", "trait", true, where, null).get(0);

            where.clear();
            where.add("id = " + tid);
            String traitName = DataManager.runSelectQuery("name", "trait", true, where, null).get(0);

            if (!traitName.contains("Date"))
            {
                set.add(geneName + "**" + traitName);
            }
        }

        System.out.println(set.size());

        for (String s : set)
        {
            System.out.println(s);
        }

        String hi = "hello";*/

        /*HashSet<String> set = new HashSet<String>();
        //HashMap<String, String> tidToname = new HashMap<String, String>();
        //HashMap<String, String> midToname = new HashMap<String, String>();

        ArrayList<String> where = new ArrayList<String>();
        where.add("assocsetid = 664");//PLINK

        ArrayList<String> cols = new ArrayList<String>();
        cols.add("markerid");
        cols.add("traitid");
        where.add("value < 8e-5");

        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "association", true, where, null);

        for (HashMap<String, String> r : res)
        {
            String mid = r.get("markerid");
            String tid = r.get("traitid");

            where.clear();
            where.add("id = " + tid);

            String geneName = DataManager.runSelectQuery("name", "trait", true, where, null).get(0);

            where.clear();
            where.add("id = " + mid);
            cols.clear();
            cols.add("name");
            cols.add("locus");
            cols.add("chr");
            HashMap<String, String> mdat = DataManager.runMultiColSelectQuery(cols, "marker", true, where, null).get(0);

            String mName = mdat.get("name");
            String mChr = mdat.get("chr");
            String mLoc = mdat.get("locus");

            if (!geneName.contains("Date"))
            {
                set.add(mName + "**" + mChr + "**" + mLoc + "**" + geneName);
            }
        }

        System.out.println(set.size());

        for (String s : set)
        {
            System.out.println(s);
        }

        String hi = "hello";*/

        /*HashSet<String> set = new HashSet<String>();
        //HashMap<String, String> tidToname = new HashMap<String, String>();
        //HashMap<String, String> midToname = new HashMap<String, String>();

        ArrayList<String> where = new ArrayList<String>();
        where.add("assocsetid = 667");//liver

        ArrayList<String> cols = new ArrayList<String>();
        cols.add("markerid");
        cols.add("traitid");

        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "association", true, where, null);

        for(HashMap<String, String> r : res)
        {
        String mid = r.get("markerid");
        String tid = r.get("traitid");

        where.clear();
        where.add("id = " + tid);

        String geneName = DataManager.runSelectQuery("name", "trait", true, where, null).get(0);

        where.clear();
        where.add("id = " + mid);
        cols.clear();
        cols.add("name");
        cols.add("locus");
        cols.add("chr");
        HashMap<String, String> mdat = DataManager.runMultiColSelectQuery(cols, "marker", true, where, null).get(0);

        String mName = mdat.get("name");
        String mChr = mdat.get("chr");
        String mLoc = mdat.get("locus");

        set.add(mName + "**" + mChr + "**" + mLoc + "**" + geneName);
        }

        System.out.println(set.size());

        for(String s : set)
        {
        System.out.println(s);
        }

        String hi = "hello";*/

        /*ArrayList<netanalEdge> edges = new ArrayList<netanalEdge>();
        HashSet<String> set = new HashSet<String>();
        HashMap<String, String> idToname = new HashMap<String, String>();
        ArrayList<String> where = new ArrayList<String>();
        where.add("netid = 382");//brain
        int numEdges = Integer.parseInt(DataManager.runSelectQuery("count(*)", "networkval", true, where, null).get(0));

        for (int i = 0; i < numEdges + 1000; i += 1000)
        {
        System.out.println(i + "\t" + set.size());
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("trait1");
        cols.add("trait2");
        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "networkval", true, where, " limit " + i + "," + 1000);

        for (HashMap<String, String> r : res)
        {
        netanalEdge toadd = new netanalEdge();
        toadd.s1 = r.get("trait1");
        toadd.s2 = r.get("trait2");

        ArrayList<String> where2 = new ArrayList<String>();
        if (idToname.get(toadd.s1) != null)
        {
        toadd.s1 = idToname.get(toadd.s1);
        }
        else
        {
        String id = toadd.s1;
        where2.add("id=" + toadd.s1);
        toadd.s1 = DataManager.runSelectQuery("name", "trait", true, where2, null).get(0);
        idToname.put(id, toadd.s1);
        }
        if (idToname.get(toadd.s2) != null)
        {
        toadd.s2 = idToname.get(toadd.s2);
        }
        else
        {
        String id = toadd.s2;
        where2.clear();
        where2.add("id=" + toadd.s2);
        toadd.s2 = DataManager.runSelectQuery("name", "trait", true, where2, null).get(0);
        idToname.put(id, toadd.s2);
        }



        if (toadd.s1.compareTo(toadd.s2) < 0)
        {
        String temp = toadd.s1;
        toadd.s1 = toadd.s2;
        toadd.s2 = temp;
        }

        String s = toadd.s1 + "***" + toadd.s2;

        if (!toadd.s1.equals(toadd.s2))
        {
        set.add(s);
        }
        }
        }

        System.out.println(set.size());

        for(String s : set)
        {
        System.out.println(s);
        }

        String hi = "hello";*/

        /*int cnt = 0;
        for (int i = 88789; i < 88809; i++)
        {
        ArrayList<String> where = new ArrayList<String>();
        where.add("id = " + i);

        String s = DataManager.runSelectQuery("list", "traitlist", true, where, null).get(0);

        String[] traits = s.split(",");
        HashSet<String> set = new HashSet<String>();
        for (int j = 0; j < traits.length; j++)
        {
        if (traits[j].trim().length() > 0)
        {
        ArrayList<String> where2 = new ArrayList<String>();
        where2.add("id=" + traits[j]);
        String nm = DataManager.runSelectQuery("name", "trait", true, where2, null).get(0);

        set.add(nm);
        }
        }

        System.out.println("Module #" + ++cnt);
        for (String st : set)
        {
        System.out.println(st);
        }
        }

        String hi = "hello";*/

        /*ArrayList<String> cols = new ArrayList<String>();
        cols.add("markerid");
        cols.add("traitid");
        ArrayList<String> where = new ArrayList<String>();
        where.add("assocsetid = 611");
        where.add("value < 1e-4");
        ArrayList<HashMap<String, String>> plinkvals = DataManager.runMultiColSelectQuery(cols, "association", true, where, null);
        String s = DataManager.getLastError();

        where.clear();
        where.add("assocsetid = 386");
        ArrayList<HashMap<String, String>> genevals = DataManager.runMultiColSelectQuery(cols, "association", true, where, null);

        cols.clear();
        cols.add("geneid");
        cols.add("traitid");
        where.clear();
        where.add("gtassocsetid = 13");
        ArrayList<HashMap<String, String>> genetraits = DataManager.runMultiColSelectQuery(cols, "genetraitassociation", true, where, null);


        HashMap<Integer, Integer> dualPG = new HashMap<Integer, Integer>();

        for (HashMap<String, String> hm : plinkvals)
        {
            Integer mid = Integer.parseInt(hm.get("markerid"));
            boolean canadd = true;

            for (int i = -10; i < 10; i++)
            {
                if (dualPG.get(mid + i) != null)
                {
                    canadd = false;
                }
            }
            if (canadd)
            {
                dualPG.put(mid, 0);
            }
        }
        }
        if(!found)
        {
        dummyobject dob = new dummyobject();
        dob.markerid = id;
        dob.count = 1;
        counts.add(dob);
        }
        }

        HashMap<Integer, Integer> nun = new HashMap<Integer, Integer>();
        for (HashMap<String, String> hm : genevals)
        {
            Integer mid = Integer.parseInt(hm.get("markerid"));
            boolean isadded = false;
            for (int ix = -10; ix < 10; ix++)
            {
                Integer i = dualPG.get(mid + ix);
                if (i != null)
                {
                    isadded = true;
                    i++;
                    dualPG.put(mid, i);
                }
            }
            if (!isadded)
            {
                nun.put(mid, 0);
            }

        }

        int num = 0;
        int tot = 0;
        int badeggs = 0;
        HashMap<String, Integer> finalres = new HashMap<String, Integer>();
        int numpairs = 0;
        int totalgenes = 0;
        for (Integer qs : dualPG.keySet())
        {
            Integer i = dualPG.get(qs);
            if (i != 0)
            {
                String mid = i.toString();
                HashSet<String> traits = new HashSet<String>();

                for (HashMap<String, String> hm : plinkvals)
                {
                    Integer m1 = Integer.parseInt(hm.get("markerid"));
                    if (m1 + 10 > qs && m1 - 10 < qs)
                    {
                        traits.add(hm.get("traitid"));
                    }
                }

                HashSet<String> genes = new HashSet<String>();
                for (HashMap<String, String> hm : genevals)
                {
                    Integer m1 = Integer.parseInt(hm.get("markerid"));
                    if (m1 + 10 > qs && m1 - 10 < qs)
                    {
                        genes.add(hm.get("traitid"));
                    }
                }


                for (String gene : genes)
                {
                    where.clear();
                    where.add("gtassocsetid = 13");
                    where.add("geneid=" + gene);
                    ArrayList<String> res = DataManager.runSelectQuery("traitid", "genetraitassociation", true, where, null);

                    for(String st : res)
                    {
                        for(String tr : traits)
                        {
                            if(st.equals(tr))
                            {
                                System.out.println(qs + " " + gene + " " + tr + " " + st);
                                String key = qs + "_" + tr;
                                Integer inty = finalres.get(key);
                                if(inty != null)
                                {
                                    finalres.put(key, inty+1);
                                    numpairs++;
                                    totalgenes++;
                                }
                                else
                                {
                                    finalres.put(key, 1);
                                    totalgenes++;
                                }
                            }
                        }
                    }
                }



                num++;
                tot += i;
            }
            else
            {
                badeggs++;
            }
        }



        System.out.println("Total number of SNPs with gene/plink assocs: " + num);
        System.out.println("Average number of genes: " + (double) tot / (double) num);
        System.out.println("Number of SNPs with plink but not gene: " + badeggs);
        System.out.println("Number of M-T pairs with genes now: " + numpairs);
        System.out.println("Avg num of connecting genes:" + (double)totalgenes / (double)numpairs);
        System.out.println("Number of SNPs with gene but not plink: " + nun.size());*/

        /*ArrayList<dummyobject> counts = new ArrayList<dummyobject>();
        for(int i = 0; i < vals.size(); i ++)
        {
        int id = Integer.parseInt(vals.get(i).get("markerid"));
        boolean found = false;
        for(dummyobject dob : counts)
        {
        if(dob.markerid == id)
        {
        found = true;
        dob.count++;
        }
        }
        if(!found)
        {
        dummyobject dob = new dummyobject();
        dob.markerid = id;
        dob.count = 1;
        counts.add(dob);
        }
        }

        Collections.sort(counts);

        for(int i = 0; i <= 2400; i ++)
        {
        dummyobject dob = counts.get(i);
        cols.clear();
        cols.add("chr");
        cols.add("name");
        cols.add("locus");

        where.clear();
        where.add("id=" + dob.markerid);

        HashMap<String,String> m  = (HashMap<String, String>) DataManager.runMultiColSelectQuery(cols, "marker", true, where, null).get(0);
        System.out.println(m.get("chr") + "\t" + m.get("name") + "\t" + m.get("locus"));
        }

        for(int i = 0; i <= 2400; i ++)
        {
        dummyobject dob = counts.get(i);
        where.clear();
        where.add("markerid = " + dob.markerid);

        ArrayList<String> mv = DataManager.runSelectQuery("value", "markerval", true, where, "sampleid");
        for(int j = 0; j < mv.size(); j ++)
        {
        System.out.print(mv.get(j) + "\t");
        }
        System.out.print("\n");
        }

         */
        //////////////////////////////////////////////

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try
        {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            instance = (Model) in.readObject();
            instance.isRefreshing = false;
            in.close();
            instance.refreshModel();
        }
        catch (IOException ex)
        {
            instance = new Model();
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
            instance = new Model();
        }

        if (!new File(instance.directory).exists())
        {
            (new File(instance.directory)).mkdir();
        }
    }

    /**
     * A private constructor that refreshes the model according to the database
     * and sets up for first GenAMap run. 
     */
    private Model()
    {
        projects = new ArrayList<Project>();

        refreshModel();
    }

    /**
     * Iterate through all the projects to remove objects unnecessary to
     * serialize and the serialize the model with the current data that is
     * important.
     */
    public static void serialize()
    {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try
        {
            instance.displaySettings = DataAddRemoveHandler.getInstance().getTrees();
            instance.tab = DataAddRemoveHandler.getInstance().getTab();
            instance.viewertype = DataAddRemoveHandler.getInstance().getViewType();
            for (Project p : instance.projects)
            {
                p.dropByNeed();
            }
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(instance);
            out.close();
        }
        catch (IOException ex)
        {
            //ex.printStackTrace();
        }
    }

    /**
     * Causes the model to be updated - checking against the database.
     * This method should only be called from the DataAddRemoveHandler in order
     * to preserve the integrity of the trees as well.  
     */
    public void refreshModel()
    {
        if (this.isRefreshing)
        {
            return;
        }
        this.isRefreshing = true;
        ArrayList<String> whereArgs = new ArrayList<String>();

        /*whereArgs.add("list IS NULL");
        SQLController.runUpdateQuery("traitlist", "list", ",", whereArgs);
        whereArgs.clear();
        whereArgs.add("list IS NULL");
        SQLController.runUpdateQuery("golist", "list", ",", whereArgs);
        whereArgs.clear();*/

        refreshProjectsWithDB();

        //now update the markers, traits, and associations.
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("name");
        cols.add("projectid");
        cols.add("speciesid");
        cols.add("goannos");
        whereArgs.add("loadcmpt=1");
        ArrayList<HashMap<String, String>> res_t = DataManager.runMultiColSelectQuery(cols, "traitset", true, whereArgs, null);
        cols.clear();
        cols.add("id");
        cols.add("name");
        cols.add("projectid");
        ArrayList<HashMap<String, String>> res_m = DataManager.runMultiColSelectQuery(cols, "markerset", true, whereArgs, null);
        cols.add("thresh");
        cols.add("netid");
        cols.add("tsid");
        cols.add("msid");
        cols.add("ispval");
        cols.add("popid");
        cols.add("popnum");
        ArrayList<HashMap<String, String>> res_a = DataManager.runMultiColSelectQuery(cols, "assocset", true, whereArgs, null);
        cols.clear();
        cols.add("id");
        cols.add("type");
        cols.add("name");
        cols.add("ts");
        ArrayList<HashMap<String, String>> res_n = DataManager.runMultiColSelectQuery(cols, "network", true, whereArgs, null);
        cols.clear();
        cols.add("id");
        cols.add("name");
        cols.add("tsid");
        cols.add("updated");
        ArrayList<HashMap<String, String>> res_tt = DataManager.runMultiColSelectQuery(cols, "traittree", true, whereArgs, null);

        cols.clear();
        cols.add("id");
        cols.add("name");
        cols.add("markersetid");
        ArrayList<HashMap<String, String>> res_popstr = DataManager.runMultiColSelectQuery(cols, "popstruct", true, whereArgs, null);

        for (Project p : projects)
        {
            ArrayList<ParameterSet> traits = new ArrayList<ParameterSet>();
            ArrayList<ParameterSet> markers = new ArrayList<ParameterSet>();
            for (HashMap<String, String> pp : res_t)
            {
                if (Integer.parseInt(pp.get("projectid")) == p.getId())
                {
                    ParameterSet q = new ParameterSet(pp.get("name"), Integer.parseInt(pp.get("id")),
                            0, Integer.parseInt(pp.get("speciesid")), 0, 0, pp.get("goannos"), false, 0, 0);
                    traits.add(q);
                }
            }
            for (HashMap<String, String> pp : res_m)
            {
                if (Integer.parseInt(pp.get("projectid")) == p.getId())
                {
                    ParameterSet q = new ParameterSet(pp.get("name"), Integer.parseInt(pp.get("id")));
                    markers.add(q);
                }
            }

            p.refreshTraits(traits);
            p.refreshMarkers(markers);

        }

        for (Project p : projects)
        {
            for (TraitSet t : p.getTraits())
            {
                ArrayList<ParameterSet> nets = new ArrayList<ParameterSet>();
                for (HashMap<String, String> nn : res_n)
                {
                    if (Integer.parseInt(nn.get("ts")) == t.getId())
                    {
                        nets.add(new ParameterSet(nn.get("type"), Integer.parseInt(nn.get("id")),
                                0.0, 0, 0, 0, nn.get("name"), false, 0, 0));
                    }
                }
                t.refreshNets(nets);

                ArrayList<ParameterSet> trees = new ArrayList<ParameterSet>();
                for (HashMap<String, String> tt : res_tt)
                {
                    if (Integer.parseInt(tt.get("tsid")) == t.getId())
                    {
                        trees.add(new ParameterSet(tt.get("name"),
                                Integer.parseInt(tt.get("id")), 0.0, 0, 0, 0,
                                "", tt.get("updated").equals("1"), 0, 0));
                    }
                }
                t.refreshTrees(trees);
            }

            //addded
            for (MarkerSet ms : p.getMarkers())
            {
                ArrayList<ParameterSet> pops = new ArrayList<ParameterSet>();
                for (HashMap<String, String> pp : res_popstr)
                {
                    if (Integer.parseInt(pp.get("markersetid")) == ms.getId())
                    {
                        pops.add(new ParameterSet(pp.get("name"),
                                Integer.parseInt(pp.get("id")), 0.0, 0, 0,
                                0, "", false, 0, 0));
                    }
                }
                ms.refreshPops(pops);
            }

            ArrayList<ParameterSet> assocs = new ArrayList<ParameterSet>();
            for (HashMap<String, String> pp : res_a)
            {
                if (Integer.parseInt(pp.get("projectid")) == p.getId())
                {
                    ParameterSet q;
                    if (pp.get("netid") == null)
                    {
                        q = new ParameterSet(pp.get("name"), Integer.parseInt(pp.get("id")),
                                Double.parseDouble(pp.get("thresh")), -1,
                                Integer.parseInt(pp.get("tsid")), Integer.parseInt(pp.get("msid")),
                                null, pp.get("ispval").equals("1"), Integer.parseInt(pp.get("popid")),
                                Integer.parseInt(pp.get("popnum")));
                    }
                    else
                    {
                        if (pp.get("netid").equals(""))
                        {
                            pp.put("netid", "-1");
                        }
                        q = new ParameterSet(pp.get("name"), Integer.parseInt(pp.get("id")),
                                Double.parseDouble(pp.get("thresh")), Integer.parseInt(pp.get("netid")),
                                Integer.parseInt(pp.get("tsid")), Integer.parseInt(pp.get("msid")), null,
                                pp.get("ispval").equals("1"), Integer.parseInt(pp.get("popid")),
                                Integer.parseInt(pp.get("popnum")));
                    }
                    assocs.add(q);
                }
            }
            p.refreshAssocs(assocs);
        }
        this.isRefreshing = false;
    }

    /**
     * Goes through the list of projects to ensure that they in sync
     * with the database.
     */
    private void refreshProjectsWithDB()
    {
        String id = getTeamId();
        ArrayList<String> cols = new ArrayList<String>();
        cols.add("id");
        cols.add("name");
        ArrayList<String> where = new ArrayList<String>();
        where.add("deleted=0");
        where.add("teamid=" + id);
        ArrayList<HashMap<String, String>> res = DataManager.runMultiColSelectQuery(cols, "project", true, where, null);
        ArrayList<Boolean> projectOK = new ArrayList<Boolean>();
        for (Project p : this.projects)
        {
            projectOK.add(false);
        }
        for (HashMap<String, String> h : res)
        {
            if (!iHaveThisProject(h.get("id"), h.get("name")))
            {
                this.addProject(h.get("name"));
                projectOK.add(true);
            }
            else
            {
                for (int i = 0; i < projects.size(); i++)
                {
                    Project p = this.projects.get(i);
                    if (p.getId() == Integer.parseInt(h.get("id")))
                    {
                        projectOK.set(i, Boolean.TRUE);
                    }
                }
            }
        }
        for (int i = projects.size() - 1; i >= 0; i--)
        {
            if (!projectOK.get(i))
            {
                projects.remove(i);
                projectOK.remove(i);
            }
        }
    }

    /**
     * This method should be called in order to add a project to the database.
     * The model is updated accordingly. 
     * @param projectName the name of the new project.
     * @return a populated object from the model representing the new project.
     */
    public Project addProject(String projectName)
    {
        for (int i = 0; i < projects.size(); i++)
        {
            if (projects.get(i).getName().equals(projectName))
            {
                return null;
            }
        }

        int id = addProjectToDB(projectName);
        if (id < 0)
        {
            return null;
        }

        Project proj = new Project(id, projectName);

        projects.add(proj);
        return proj;
    }

    /**
     * Given the name of a new project, this method will add the new project
     * to the database.
     * @param projectName
     * @return
     */
    private int addProjectToDB(String projectName)
    {
        HashSet<Integer> projectIds = new HashSet<Integer>();
        ArrayList<String> whereArgs = new ArrayList<String>();
        whereArgs.add("name='" + projectName + "'");
        ArrayList<String> ids = DataManager.runSelectQuery("name", "project", true, whereArgs, null);
        if (ids.size() == 0)
        {
            ArrayList<String> cols = new ArrayList<String>();
            cols.add("name");
            cols.add("teamid");
            ArrayList<String> values = new ArrayList<String>();
            values.add(projectName);
            values.add(this.getTeamId());
            DataManager.runInsertQuery(cols, values, "project");
        }
        ids = DataManager.runSelectQuery("id", "project", true, whereArgs, null);
        if (ids.size() != 0)
        {
            int projectId = Integer.parseInt(ids.get(0));
            if (!projectIds.contains(projectId))
            {
                projectIds.add(projectId);
            }
            return projectId;
        }
        return -1;
    }

    /**
     * Given a project id, this method will return the name of the project. 
     * @param projectId
     * @return
     */
    public String getProjectName(int projectId)
    {
        for (Project p : this.projects)
        {
            if (p.getId() == projectId)
            {
                return p.getName();
            }
        }
        return null;
    }

    /**
     * Give the name of the project, this method will return the populated
     * object representing said project. 
     * @param name
     * @return
     */
    public Project getProject(String name)
    {
        for (int i = 0; i < projects.size(); i++)
        {
            if (projects.get(i).getName().equals(name))
            {
                return projects.get(i);
            }
        }
        return null;
    }

    /**
     * Given the id of a project, this method will return the populated object
     * for that project. 
     * @param id
     * @return
     */
    public Project getProject(int id)
    {
        for (int i = 0; i < projects.size(); i++)
        {
            if (projects.get(i).getId() == id)
            {
                return projects.get(i);
            }
        }
        return null;
    }

    /**
     * Considers a project to see if it is currently in the model's list
     * of projects.
     * @param id
     * @param name
     * @return
     */
    private boolean iHaveThisProject(String id, String name)
    {
        for (Project p : this.projects)
        {
            if (p.getName().equals(name) && p.getId() == Integer.parseInt(id))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of strings containing all project names in this team's
     * repository. 
     * @return
     */
    public ArrayList<String> getProjectNames()
    {
        ArrayList<String> toRet = new ArrayList<String>();

        for (Project p : this.projects)
        {
            toRet.add(p.getName());
        }

        ArrayList<String> where = new ArrayList<String>();
        where.add("deleted = 1");
        ArrayList<String> dels = DataManager.runSelectQuery("name", "project", true, where, "id");

        for (String s : dels)
        {
            toRet.add(s);
        }

        return toRet;
    }
}
