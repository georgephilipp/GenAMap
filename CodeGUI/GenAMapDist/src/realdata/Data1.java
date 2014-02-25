package realdata;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Remote processing and data storage are done on cogito, which the user must
 * have access to remotely. This is done through URL posts and gets, and so
 * the user must have access to this website. In order to hide this information
 * from the user and to not expose the cluster, we put this information in this class.
 * The user is also required to have a valid login to the access the database
 * in order to get data and run jobs. This is stored in this class as well. 
 *
 * Primarily a data holder, which is serialized and reloaded when the program
 * exits and starts.
 * @author rcurtis
 */
public class Data1 implements Serializable
{
    /**
     * The name of the user that matches in the user's table in the mysql database,
     * giving the user access to the database on cogito.
     */
    public String mysqlusername;
    /**
     * The password of the user that matches in the user's table in the mysql database,
     * giving the user access to the database on cogito.
     */
    public String mysqlpassword;
    /**
     * The URL to post to in order to run mysql select, update, delete, and insert statements.
     */
    private String website;
    /**
     * The URL to post to in order to send files over to cogito.
     */
    private String transfer;
    /**
     * This should always be cogito.ml.cmu.edu
     */
    private String site="cogito.ml.cmu.edu";

    public boolean checkTeamCreationCode(String s)
    {
        if(s .equals("X76;;PP!@zxiw"))
        {
            return true;
        }
        return false;
    }

    /**
     * This class is implemented as a singleton, where any class needing the information
     * in the different projects can all access the same instance, avoid confusion and multiple
     * acess.
     */
    private static Data1 instance;

    /**
     * Returns the URL that the user should use to post information and queries to mysql. 
     * @return the url that acts as an interface for data input into the mysql db.
     */
    public String getWebsiteAddress()
    {
        return website;
    }

    /**
     * Returns the URL used to transfer files to the remote server.
     * @return the url that acts as an interface for the GUI to the server.
     */
    public String getTransferWebsiteAddress()
    {
        return transfer;
    }

    /**
     * REturns the address of the remote machine
     * @return cogito.ml.cmu.edu
     */
    public String getSiteAddress()
    {
        return site;
    }

    /**
     * When the user sets up their remote settings, they  have the option to control
     * which machine they will access. At this point we leave this here for flexibility,
     * although there is no support beyond cogito.ml.cmu.edu
     * @param site the address of the remote machine to access.
     */
    public void setWebsiteAddress(String site)
    {
        this.site = site;
        website = "http://" + site + "/test/genamap_beta/phps_genamap/genamapconn.php";
        transfer= "http://" + site + "/test/genamap_beta/phps_genamap/genamaptrans.php";
    }

    /**
     * Gets the semi-exact address of where the php scripts are so that
     * the user can run user-creation commands. 
     * @return
     */
    public String getWebsiteFolder()
    {
        return "http://" + site + "/test/genamap_beta/phps_genamap/";
    }

    /**
     * Returns the static instance of this class, allowing the user to retrieve and
     * set information for the entire project.
     * @return the static SQLSettings instance. This is the only instance that should be
     * used for the entire application.
     */
    public static Data1 getInstance()
    {
        if(instance == null) instance = new Data1();
        return instance;
    }

    /**
     * This method is called after deserializing the SQLSettings instance. The user
     * can then set the instance to what has been read in from serialization
     * @param s the SQLSettings instance for this project.
     */
    public static void setInstance(Data1 s)
    {
        instance = s;
    }

    /**
     * Queries the database to find the id of the currently logged in user.
     * @return
     */
    public String getUserId()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("uid=\"" + this.mysqlusername + "\"");
        return DataManager.runSelectQuery("id", "user", true, where, null).get(0);
    }

    /**
     * A private constructor to prohibit any part of the program other than this
     * class of making a second instance of SQLSettings. 
     */
    private Data1()
    {
    }
}
