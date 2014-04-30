package algorithm;

import java.io.Serializable;
import control.DataAddRemoveHandler;
import datamodel.Model;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Time;
import realdata.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import realdata.DataManager;
import realdata.Data1;

/**
 * The Algorithm object is the direct interface between the GUI and the database
 *
 * This is where all the communication happens to start, stop, pause an algorithm,
 * as well as to get information about the algorithm as it runs. 
 * @author ross
 */
public class Algorithm implements Serializable
{
    /**
     * This algorithm has ran to completion
     */
    public static final int STATUS_CPT = 1;
    /**
     * This algorithm has run until it encountered an error; it can be restarted.
     */
    public static final int STATUS_ERR = 0;
    /**
     * This algorithm has just started or been restarted and the server-side
     * service has not picked it up yet.
     */
    public static final int STATUS_INI = 4;
    /**
     * This algorithm has been pasued and will not continue until it is unpaused.
     */
    public static final int STATUS_PSD = 2;
    /**
     * This algorithm has been paused, but will finish running its current jobs
     * before pausing. It will not start any new jobs.
     */
    public static final int STATUS_PSN = 5;
    /**
     * This algorithm is currently running jobs on the cluster.
     */
    public static final int STATUS_RUN = 3;
    /**
     * This algorithm has been canceled and will be killed. 
     */
    public static final int STATUS_KLL = 6;
    /**
     * This is the unique name that the algorithm run is known by in the database,
     * on the server, and as an index. In the db, it is called the appid
     */
    protected String name;
    /**
     * The current status of this running algorithm - cpt, err, etc.
     */
    protected int status;
    /**
     * The time that this algorithm began running - duplicated in the db, but
     * used to calculate the running time of the algorithm. 
     */
    protected Date startTime;
    /**
     * Set to false once an algorithm has completed and can no longer be run. 
     */
    protected boolean isRunning;
    /**
     * The error string retrieved from the database when the algorithm is
     * sent into the error queue.
     */
    public String errorString = "";
    /**
     * How complete the database thinks this algorithm is.
     */
    protected int percentComplete = 0;
    /**
     * The job type id that specifies what steps will be run in order to complete
     * this algorithm.
     */
    protected int jobtypeid;
    /**
     * The project id that this algorithm belongs to.
     */
    protected int projID;
    /**
     * The traitid that this algorithm belongs to.
     */
    protected int traitID;
    /**
     * The markerid that this algorithm belongs to.
     */
    protected int markerID;
    /**
     * The id into the jobstatus table that this algorithm uses to find out the
     * status updates for this algorithm.
     */
    protected String myDBID;
    /**
     * A counter used to ensure that the status is not updated after a pause or
     * replay until the server has had adequate time to notice the change.
     */
    protected int psdwait = 0;
    /**
     * Filled when an algorithm has completed - this way there is no more
     * recalculation. 
     */
    protected String finalRunTime = null;

    /**
     * Creates a new Algorithm object by starting an algorithm on the database
     * and then starting to track the new run. 
     * @param name The appid of this algorithm as figured out by the client;
     * will be inserted in the db.
     * @param parms The parameters to pass to the algorithm's front end.
     * @param jobtypeId The jobtype that will determine which algorithm steps to run.
     * @param projID The project id for this algorithm.
     * @param tID The traitid for this algorithm.
     * @param mID The markerid for this algorithm. 
     */
    public Algorithm(String name, ParameterObject parms, Integer jobtypeId, int projID, int tID, int mID)
    {
        this.name = name;
        this.projID = projID;
        this.traitID = tID;
        this.markerID = mID;
        this.jobtypeid = jobtypeId;

        if (startAlgorithm(parms))
        {
            this.status = STATUS_RUN;
        }
        else
        {
            finalRunTime = getRunningTime();
            this.status = Algorithm.STATUS_ERR;
        }
    }

    /**
     * Instead of starting a new algorithm, this constructor takes the values
     * of an already running algorithm and tracks it from where it is.
     * @param jobtype The type of job that this algorithm is running.
     * @param jobid The id for this algorithm in the database to get info.
     * @param projectid The projec that this algorithm belongs to.
     * @param traitid The trait id that this algorithm belongs to.
     * @param markerid The marker id that this algorithm belongs to.
     * @param appid The name of this algorithm, the appid.
     * @param start When this algorithm started to run.
     * @param status The current run status of this algorithm. 
     */
    public Algorithm(int jobtype, int jobid, int projectid, int traitid, int markerid,
            String appid, String start, String status)
    {
        this.jobtypeid = jobtype;
        this.name = appid;
        this.projID = projectid;
        this.traitID = traitid;
        this.markerID = markerid;

        startTime = parseTime(start);
        if (status.equals("CPT"))
        {
            this.status = STATUS_CPT;
            this.percentComplete = 100;
            finalRunTime = getRunningTime();
        }
        else
        {
            if (status.equals("RUN"))
            {
                this.status = STATUS_RUN;
            }
            else
            {
                if (status.equals("PSD"))
                {
                    this.status = STATUS_PSD;
                }
                else
                {
                    if (status.equals("PSN"))
                    {
                        this.status = STATUS_PSN;
                    }
                    else
                    {
                        if (status.equals("ERR"))
                        {
                            this.status = STATUS_ERR;
                        }
                        else
                        {
                            if (status.equals("INI"))
                            {
                                this.status = STATUS_INI;
                            }
                        }
                    }
                }
            }
        }
        myDBID = jobid + "";
    }

    /**
     * Given a string from the database, figure out what its time value is. 
     * @param s
     * @return
     */
    protected Date parseTime(String s)
    {
        Date d;

        int yr = Integer.parseInt(s.substring(0, 4));
        int mt = Integer.parseInt(s.substring(5, 7));
        int dy = Integer.parseInt(s.substring(8, 10));
        int hr = Integer.parseInt(s.substring(11, 13));
        int mn = Integer.parseInt(s.substring(14, 16));
        int sc = Integer.parseInt(s.substring(17, 19));

        d = new Date(yr - 1900, mt - 1, dy, hr, mn, sc);

        return d;
    }

    /**
     * Determine the running index of this algorithm - should be deprecated.
     * @return
     */
    public int getIdx()
    {
        String num = name.substring(3);
        return Integer.parseInt(num, 16);
    }

    /**
     * Returns the name, or appid, of this algorithm. 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the current error message of this algorithm.
     * @return
     */
    public String getErrorMessage()
    {
        return errorString;
    }

    /**
     * Returns the percent complete value of this algorithm (call only after
     * status update). 
     * @return
     */
    public double getPercentComplete()
    {

        return percentComplete;
    }

    /**
     * This method should be used to update an algorithm's status -
     * without this call there will be no change in state.
     */
    public void algoUpdate()
    {
        if (this.status == Algorithm.STATUS_RUN
                || this.status == Algorithm.STATUS_INI
                || this.status == Algorithm.STATUS_PSN
                || this.status == Algorithm.STATUS_ERR)
        {
            percentComplete = checkStatusOfAlgorithm();
        }
    }

    /**
     * Checks the status of the algorithm by looking at the percent complete,
     * status, and time from the database. 
     * @return
     */
    private int checkStatusOfAlgorithm()
    {
        if (psdwait-- > 0)
        {
            return this.percentComplete;
        }

        ArrayList<String> clause1 = new ArrayList<String>();
        clause1.add("id=" + myDBID);
        ArrayList<String> curstat = DataManager.runSelectQuery("status", "jobstatus", true,
                clause1, null);
        if (curstat.size() == 0)
        {
            return 0;
        }
        String stat = curstat.get(0);

        ArrayList<String> pcmpt = DataManager.runSelectQuery("percmpt", "jobstatus",
                true, clause1, null);
        for (int i = 0; i < pcmpt.size(); i++)
        {
            if (Integer.parseInt(pcmpt.get(i)) > percentComplete)
            {
                percentComplete = Integer.parseInt(pcmpt.get(i));
            }
        }

        if (stat.equals("CPT"))
        {
            Model.getInstance().refreshModel();
            DataAddRemoveHandler.getInstance().refreshDisplay();

            this.status = STATUS_CPT;
            this.percentComplete = 100;
            finalRunTime = getRunningTime();
            return percentComplete;
        }
        if (stat.equals("ERR"))
        {
            //finalRunTime = getRunningTime();
            errorString = "";
            this.status = STATUS_ERR;
            clause1.clear();
            clause1.add("jobid=\'" + this.myDBID + "\'");
            ArrayList<String> error1 = DataManager.runSelectQuery("error", "error", true,
                    clause1, null);
            for (int i = 0; i < error1.size(); i++)
            {
                this.errorString += error1.get(0) + "\n";
            }
            return percentComplete;
        }
        if (stat.equals("PSD"))
        {
            this.status = STATUS_PSD;
        }
        else
        {
            if (stat.equals("PSN"))
            {
                this.status = STATUS_PSN;
            }
            else
            {
                if (stat.equals("RUN"))
                {
                    this.status = STATUS_RUN;
                }
            }
        }
        return percentComplete;
    }

    /**
     * From the parameter object, creates a parameter file that will
     * be passed directly to the webserver through file transfer. 
     * @param p
     * @return
     * @throws Exception
     */
    protected String createParamFile(ParameterObject p) throws Exception
    {
        String filename = "temp_parm.txt";

        FileWriter fstream = new FileWriter(filename);
        BufferedWriter writer = new BufferedWriter(fstream);
        writer.write(this.name + " ");
        if (p != null)
        {
            writer.write(p.getParms());
        }
        writer.write(" " + DataManager.getTeamCode());
        writer.write("\n");
        writer.write("vanilla\n");
        writer.close();

        return System.getProperty("user.dir") + "/" + filename;
    }

    /**
     * Figures out the running time for this algorithm by querying the database
     * and calculating the total - taking into account all pauses. 
     * @return
     */
    public String getRunningTime()
    {
        if (finalRunTime != null)
        {
            return finalRunTime;
        }
        Time tot = queryForRunTime();

        return formatTime(tot);
    }

    /**
     * Queries that database in order to figure out how long this algorithm
     * has run - takes into account all pauses. 
     * @return
     */
    protected Time queryForRunTime()
    {
        ArrayList<String> fil = new ArrayList<String>();
        fil.add("appid=\'" + this.name + "\'");
        ArrayList<String> rss = DataManager.runSelectQuery("start", "jobstatus", true, fil, null);
        ArrayList<String> rse = DataManager.runSelectQuery("end", "jobstatus", true, fil, null);
        Time tot = new Time(0);
        for (int i = 0; rss != null && i < rss.size(); i++)
        {
            Date d1 = parseTime(rss.get(i));
            Date d2;
            if (i >= rse.size())
            {
                d2 = new Date();
            }
            else
            {
                d2 = parseTime(rse.get(i));
            }
            if (new Time(d2.getTime() - d1.getTime()).getTime() > 0)
            {
                tot = new Time(d2.getTime() - d1.getTime() + tot.getTime());
            }
            else
            {
                tot = new Time(new Date().getTime() - d1.getTime() + tot.getTime());
            }
        }
        return tot;
    }

    /**
     * Starts the algorithm running on the database. 
     * @param p
     * @return
     */
    private boolean startAlgorithm(ParameterObject p)
    {
        String workingdir;
        try
        {
            ArrayList<String> files = new ArrayList<String>();
            files.add(createParamFile(p));
            if(p != null && p.getFiles() != null)
            {
                files.addAll(p.getFiles());
            }
            ArrayList<String> destfiles = new ArrayList<String>();
            destfiles.add("parms.txt");
            if(p != null && p.getFiles() != null)
            {
                destfiles.add("input.txt");
                destfiles.add("output.txt");
            }
            workingdir =
                    File.transferFilesToDistributedProcessor(name, files, destfiles);
        }
        catch (Exception e)
        {
            this.errorString = "Cannot transfer files: \n" + e.getMessage();
            return false;
        }

        if (!insertJobIntoDB())
        {
            return false;
        }
        getJobDBId();

        ArrayList<String> val = new ArrayList<String>();
        ArrayList<String> col = new ArrayList<String>();
        col.add("jobtype");
        col.add("loc");
        col.add("jobid");
        col.add("projectid");
        col.add("traitid");
        col.add("markerid");
        col.add("userid");
        val.add(this.jobtypeid + "");
        val.add(workingdir);
        val.add(myDBID);
        val.add(this.projID + "");
        val.add(this.traitID + "");
        val.add(this.markerID + "");
        val.add(Data1.getInstance().getUserId());
        if (!DataManager.runInsertQuery(col, val, "jobrun"))
        {
            return false;
        }

        this.status = STATUS_INI;

        return true;
    }

    /**
     * Kills the algorithm by sending a kill message to the server service. 
     * @return
     */
    public boolean stopAlgorithm()
    {
        ArrayList<String> val = new ArrayList<String>();
        ArrayList<String> col = new ArrayList<String>();
        col.add("jobid");
        col.add("killit");
        val.add(this.myDBID);
        val.add("1");
        DataManager.runInsertQuery(col, val, "pause");
        this.status = STATUS_KLL;
        return true;
    }

    /**
     * Sends a pause message to the server service. 
     * @return
     */
    public boolean pauseAlgorithm()
    {
        psdwait = 3;
        ArrayList<String> val = new ArrayList<String>();
        ArrayList<String> col = new ArrayList<String>();
        col.add("jobid");
        val.add(this.myDBID);
        DataManager.runInsertQuery(col, val, "pause");
        this.status = STATUS_PSN;
        return true;
    }

    /**
     * Restarts the algorithm from where it left off. 
     * @return
     */
    public boolean unpauseAlgorithm()
    {
        psdwait = 3;
        insertJobIntoDB();
        getJobDBId();
        this.status = STATUS_INI;
        return true;
    }

    /**
     * Returns the current status of this algorithm.
     * @return
     */
    public String getStatus()
    {
        switch (this.status)
        {
            case STATUS_ERR:
                return "error";
            case STATUS_CPT:
                return "complete";
            case STATUS_PSD:
                return "paused";
            case STATUS_RUN:
                return "running";
            case STATUS_INI:
                return "init...";
            case STATUS_PSN:
                return "pausing";
            case STATUS_KLL:
                return "killing";
        }
        return "init...";
    }

    /**
     * Formats the current running time to be displayed in a human-readable display.
     * @param t
     * @return
     */
    protected String formatTime(Time t)
    {
        long time = t.getTime();
        String s = "";
        time = time / 1000; //into seconds
        int days = (int) (time / (24 * 60 * 60));
        int hours = (int) ((time - days * 24 * 60 * 60) / (60 * 60));
        int mins = (int) ((time - days * 24 * 60 * 60 - hours * 60 * 60) / (60));
        int secs = (int) (time - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60);
        s = days + " " + hours + ":";
        String min = "" + mins + ":";
        if (min.length() == 2)
        {
            min = "0" + min;
        }
        String sec = "" + secs;
        if (sec.length() == 1)
        {
            sec = "0" + sec;
        }
        s += min + sec;
        return s;
    }

    /**
     * Queries the database to get the id of a job that has just been
     * inserted/started. 
     */
    private void getJobDBId()
    {
        ArrayList<String> fil = new ArrayList<String>();
        fil.add("appid=\'" + this.name + "\'");
        fil.add("status!=" + "\'PSN\'");
        ArrayList<String> res = DataManager.runSelectQuery("id", "jobstatus", true, fil, null);
        int max = 0;
        for (int i = 0; i < res.size(); i++)
        {
            if (Integer.parseInt(res.get(i)) > max)
            {
                this.myDBID = res.get(i);
                max = Integer.parseInt(res.get(i));
            }
        }
    }

    /**
     * Starts a job by inserting its values into the database.
     * @return
     */
    private boolean insertJobIntoDB()
    {
        ArrayList<String> val = new ArrayList<String>();
        ArrayList<String> col = new ArrayList<String>();
        col.add("appid");
        val.add(this.name);
        if (!DataManager.runInsertQuery(col, val, "jobstatus"))
        {
            this.errorString = DataManager.getLastError();
            return false;
        }
        return true;
    }

    /**
     * Restarts a job by inserting its values into the database. 
     * @param isGoBack
     */
    public void restart(boolean isGoBack)
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("jobid = " + myDBID);
        int step = Integer.parseInt((String)DataManager.runSelectQuery("step", "jobrun", true, where, null).get(0));
        if (step != 0 && isGoBack)
        {
            step--;
        }

        DataManager.runUpdateQuery("jobrun", "step", step + "", where);
        insertJobIntoDB();
        getJobDBId();
        this.status = STATUS_INI;
    }
}
