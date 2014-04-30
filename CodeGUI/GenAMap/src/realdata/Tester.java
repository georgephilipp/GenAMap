package realdata;

import java.util.ArrayList;

/**
 * Tests the connection to see if the db is online and will accept queries
 * from this IP.
 * @author rcurtis
 */
public class Tester
{
    public static boolean testConnection()
    {
        ArrayList<String> jobTypes =  DataManager.runSelectQuery("jobtype", "jobrun", false, null,null);
        boolean success  = jobTypes != null;// && jobTypes.size() != 0;
        return success;
    }

    public static String getMessage()
    {
        return DataManager.getLastError();
    }
}
