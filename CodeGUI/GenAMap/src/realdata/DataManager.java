package realdata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SQLController is our interface into the database. It will communicate
 * via our host site script. There are two types of methods - those that
 * create queries and then run them through the runQuery method, and
 * then there are other methods that communicate directly with the site
 * though user and team creation scripts. 
 *
 * @author rcurtis
 */
public class DataManager
{
    /**
     * The last error that occured when trying to run a SQL query
     */
    private static String error;
    /**
     * We only really want one connection at a time, so we are going to limit
     * the connections through the isConnected variable. In this way, only
     * one query will happen at a time. 
     */
    private static boolean isConnected;
    /**
     * The current team code for this team
     */
    private static String keycode = null;

    /**
     * Determines the team code for the team currently logged in.
     * @return
     */
    public static String getTeamCode()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("uid=\'" + Data1.getInstance().mysqlusername + "\'");
        where.add("teamid=team.id");
        return DataManager.runSelectQuery("keycode", "user,team", true, where, null).get(0);
    }

    /**
     * Run a selection query on the database
     * @param column The column to select
     * @param table The table to select from
     * @param where Do you have specific where arguments?
     * @param whereArgs A series of statements specifying the where conditions
     * @param orderBy null, or a string specifying a column to order by. 
     * @return
     */
    public static ArrayList<String> runSelectQuery(String column, String table,
            boolean where, ArrayList<String> whereArgs, String orderBy)
    {
        table = checkTable(table);

        String query = "SELECT " + column + " FROM " + table;
        if (where)
        {
            query += " WHERE ";
            for (int i = 0; i < whereArgs.size(); i++)
            {
                query += whereArgs.get(i);
                if (i != whereArgs.size() - 1)
                {
                    query += " AND ";
                }
            }
        }
        if (orderBy != null)
        {
            query += " ORDER BY " + orderBy;
        }
        query += ";";
        ArrayList<String> lines = runQuery(query);
        if (lines == null)
        {
            return null;
        }

        return lines;
    }

    /**
     * Runs a query directly. This is often not used from outside classes,
     * except in the rare case of import. This executes the query
     * given using the settings to connect found in SQLSettings.java.
     * @param query The query to run. 
     * @return
     */
    public static ArrayList<String> runQuery(String query)
    {
        if (query.toLowerCase().contains("insert into markerval "))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            query = query.toLowerCase().replace("insert into markerval ", "insert into markerval" + keycode + " ");
        }

        return runQuery(query, 0);
    }

    private static ArrayList<String> runQuery(String query, int tries)
    {
        String data;
        ArrayList<String> lines = new ArrayList<String>();
        try
        {
            //encode some parms.
            data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(Data1.getInstance().mysqlusername, "UTF-8");
            data += "&" + URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(Data1.getInstance().mysqlpassword, "UTF-8");
            data += "&" + URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(query, "UTF-8");
            //now make the connection and write out the data
            URL url = new URL(Data1.getInstance().getWebsiteAddress());

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.close();
            //finally, get the response ...
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            boolean ok = false;
            while ((line = rd.readLine()) != null)
            {
                line = line.trim();
                if (!ok)
                {
                    if (line.equals("OK"))
                    {
                        ok = true;
                        String temp = query;
                        line = rd.readLine();
                        if (!line.equals(""))
                        {
                            throw new Exception(line);
                        }
                        continue;
                    }
                    else
                    {
                        throw new Exception(line);
                    }
                }

                lines.add(line);
            }
            rd.close();
            //isConnected = false;
        }
        catch (Exception ex)
        {
            //isConnected = false;
            error = ex.getMessage();
            if (error != null && error.toLowerCase().contains("already in use"))
            {
                try
                {
                    Thread.sleep(250);
                }
                catch (InterruptedException ex1)
                {
                }
                if (tries++ < 25)
                {
                    return runQuery(query, tries);
                }
            }
            error = ex.getMessage();
            System.err.println(error);
            return null;
        }
        return lines;
    }

    /**
     * Run a MySQL function that has been defined
     * @param fxn The function to run
     * @param args The arguments to the function (in order)
     * @return
     */
    public static String runFunction(String fxn, ArrayList<String> args)
    {
        if (fxn.contains("go_list_trait"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            fxn = fxn.replace("go_list_trait", "go_list_trait" + keycode);
        }
        else if (fxn.contains("go_list_ttv"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            fxn = fxn.replace("go_list_ttv", "go_list_ttv");
        }
        else if (fxn.contains("go_list_subset"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            fxn = fxn.replace("go_list_subset", "go_list_subset" + keycode);
        }
        else if (fxn.contains("go_list_module"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            fxn = fxn.replace("go_list_module", "go_list_module" +  keycode);
        }

        String query = "SELECT " + fxn + "(";
        for (int i = 0; i < args.size(); i++)
        {
            query += "'" + args.get(i) + "'";

            query += (i == args.size() - 1 ? "" : ",");

            query += " ";

        }
        query += ") as idi;";
        ArrayList<String> lines = runQuery(query);
        if (lines == null)
        {
            return null;
        }
        return lines.get(0);
    }

    /**
     * Run a select query for multiple columns
     * @param columns A Collection of teh columns to query for
     * @param table The table to query
     * @param where Do you want to specify conditions on the query
     * @param whereArgs A collection of those conditions, in order
     * @param orderby The sorting parameter of the query, null if no sorting needed.
     * @return
     */
    public static ArrayList<HashMap<String, String>> runMultiColSelectQuery(ArrayList<String> columns, String table,
            boolean where, ArrayList<String> whereArgs, String orderby)
    {
        table = checkTable(table);
        ArrayList<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();

        String query = "SELECT ";
        for (int i = 0; i < columns.size(); i++)
        {
            query += columns.get(i);
            if (i != columns.size() - 1)
            {
                query += ", ";
            }
        }
        query += " FROM " + table;
        if (where == true)
        {
            query += " WHERE ";
            for (int i = 0; i < whereArgs.size(); i++)
            {
                query += whereArgs.get(i);
                if (i != whereArgs.size() - 1)
                {
                    query += " AND ";
                }
            }
        }
        if(orderby != null && orderby.contains("limit"))
        {
            query += orderby;
        }
        else if (orderby != null)
        {
            query += " ORDER BY " + orderby;
        }
        query += ";";

        ArrayList<String> lines = runQuery(query);

        if (lines == null)
        {
            return null;
        }

        int idx = 0;
        for (String s : lines)
        {
            retList.add(new HashMap<String, String>());
            String[] line = s.split("  ");
            for (int i = 0; i < columns.size(); i++)
            {
                line[i] = line[i].replace('\'', '\'');
                String colName = columns.get(i);
                retList.get(idx).put(colName, line[i]);
            }
            idx++;
        }

        return retList;
    }

    /**
     * Run a deletion query in the database.
     * @param table The table to delete from
     * @param whereArgs The arugments to use in the deletion.
     * @return
     */
    public static boolean deleteQuery(String table, ArrayList<String> whereArgs)
    {
        table = checkTable(table);
        error = "";

        String userQuery = "DELETE FROM " + table + " WHERE ";
        for (int i = 0; i < whereArgs.size(); i++)
        {
            userQuery += whereArgs.get(i);
            if (i != whereArgs.size() - 1)
            {
                userQuery += " AND ";
            }
        }

        userQuery += ";";
        ArrayList<String> lines = runQuery(userQuery);
        if (lines == null)
        {
            return false;
        }
        return true;
    }

    /**
     * Run a deletion query for specific values in the column. DELETE FROM X WHERE COL IN ()
     * @param table The table to delete from
     * @param col The column to specify the deletion
     * @param vals The values of that column to delete
     * @return
     */
    public static boolean deleteMultipleQuery(String table, String col, ArrayList<String> vals)
    {
        table = checkTable(table);
        error = "";

        String userQuery = "DELETE FROM " + table + " WHERE " + col + " IN (";
        for (int i = 0; i < vals.size(); i++)
        {
            userQuery += vals.get(i);
            if (i != vals.size() - 1)
            {
                userQuery += ", ";
            }
        }

        userQuery += ");";
        ArrayList<String> lines = runQuery(userQuery);
        if (lines == null)
        {
            return false;
        }
        return true;
    }

    /**
     * Runs an update query in the database.
     * @param table The table to update
     * @param column The column which will be updated
     * @param value The new value
     * @param whereArgs The conditions on the update.
     * @return
     */
    public static boolean runUpdateQuery(String table, String column, String value, ArrayList<String> whereArgs)
    {
        table = checkTable(table);
        error = "";

        String userQuery = "UPDATE " + table + " SET " + column + "=\'" + value + "\' WHERE ";
        for (int i = 0; i < whereArgs.size(); i++)
        {
            userQuery += whereArgs.get(i);
            if (i != whereArgs.size() - 1)
            {
                userQuery += " AND ";
            }
        }

        userQuery += ";";
        ArrayList<String> lines = runQuery(userQuery);
        if (lines == null)
        {
            return false;
        }
        return true;
    }

    /**
     * Run an insertion query on the database
     * @param columns The columns that are specified for th insert
     * @param vals The values for those columns in the insertion
     * @param table The table to insert into. 
     * @return
     */
    public static boolean runInsertQuery(ArrayList<String> columns, ArrayList<String> vals, String table)
    {
        table = checkTable(table);
        error = "";

        String userQuery = "INSERT INTO " + table + " (";

        for (int i = 0; i < columns.size(); i++)
        {
            userQuery += columns.get(i);
            if (i != columns.size() - 1)
            {
                userQuery += ", ";
            }
        }

        userQuery += ") VALUES (";

        for (int i = 0; i < vals.size(); i++)
        {
            userQuery += "\'" + vals.get(i) + "\'";
            if (i != vals.size() - 1)
            {
                userQuery += ", ";
            }
        }

        userQuery += ");";

        ArrayList<String> lines = runQuery(userQuery);
        if (lines == null)
        {
            return false;
        }
        return true;
    }

    /**
     * Run an insert query with multiple lines
     * @param columns The columns to insert
     * @param vals An ArrayList of ArrayLists of values to insert
     * @param table The table to insert the values into. 
     * @return
     */
    public static boolean runMultipleInsertQuery(ArrayList<String> columns, ArrayList<ArrayList<String>> vals, String table)
    {
        table = checkTable(table);
        error = "";

        String userQuery = "INSERT INTO " + table + " (";

        for (int i = 0; i < columns.size(); i++)
        {
            userQuery += columns.get(i);
            if (i != columns.size() - 1)
            {
                userQuery += ", ";
            }
        }

        userQuery += ") VALUES ";

        for (int i = 0; i < vals.size(); i++)
        {
            userQuery += "(";
            for (int j = 0; j < vals.get(i).size(); j++)
            {
                userQuery += "\'" + vals.get(i).get(j) + "\'";
                if (j != vals.get(i).size() - 1)
                {
                    userQuery += ", ";
                }
            }
            userQuery += (i == vals.size() - 1) ? ");" : "),";
        }

        ArrayList<String> lines = runQuery(userQuery);
        if (lines == null)
        {
            return false;
        }
        return true;
    }

    /**
     * Return the last error that happened on a SQL query. 
     * @return
     */
    public static String getLastError()
    {
        return error;
    }

    /**
     * Given a username, see if that user already exists
     * @param text The name of the new user. 
     * @return
     */
    public static boolean queryForUser(String text)
    {
        String data;
        ArrayList<String> lines = new ArrayList<String>();
        try
        {
            //encode some parms.
            data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");
            URL url = new URL(Data1.getInstance().getWebsiteFolder() + "genamapgetusers.php");

            /*while (isConnected)
            {
            Thread.sleep(50);
            }
            isConnected = true;*/

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.close();
            //finally, get the response ...
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            boolean ok = false;
            while ((line = rd.readLine()) != null)
            {
                line = line.trim();
                if (!ok)
                {
                    if (line.equals("OK"))
                    {
                        ok = true;
                        line = rd.readLine();
                        if (line.equals("OK2"))
                        {
                            rd.close();
                            //isConnected = false;
                            return true;
                        }
                        continue;
                    }
                    else
                    {
                        rd.close();
                        //isConnected = false;
                        return false;
                    }
                }
            }

        }
        catch (Exception ex)
        {
            //isConnected = false;
            error = ex.getMessage();
            System.err.println(error);
            return false;
        }
        //isConnected = false;
        return false;
    }

    /**
     * Get a list of all teams currently in the database
     * @return
     */
    public static ArrayList<String> queryForTeams()
    {
        String data;
        ArrayList<String> lines = new ArrayList<String>();
        try
        {
            //encode some parms.
            URL url = new URL(Data1.getInstance().getWebsiteFolder() + "genamapgetteams.php");

            URLConnection conn = url.openConnection();

            //finally, get the response ...
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            boolean ok = false;
            while ((line = rd.readLine()) != null)
            {
                line = line.trim();
                if (!ok)
                {
                    if (line.equals("OK"))
                    {
                        ok = true;
                        line = rd.readLine();
                        if (!line.equals(""))
                        {
                            rd.close();
                            //isConnected = false;
                            return lines;
                        }
                        continue;
                    }
                }
                else
                {
                    lines.add(line);
                }
            }

        }
        catch (Exception ex)
        {
            //isConnected = false;
            error = ex.getMessage();
            System.err.println(error);
            return lines;
        }
        //isConnected = false;
        return lines;
    }

    /**
     * Given a team and a passcode, check to see if the passcode is correct
     * to allow the user to join the team.
     * @param team
     * @param passcode
     * @return
     */
    public static boolean isPassCodeForTeam(String team, String passcode)
    {
        String data;

        try
        {
            //encode some parms.
            data = URLEncoder.encode("team", "UTF-8") + "=" + URLEncoder.encode(team, "UTF-8") + "&" + URLEncoder.encode("passcode", "UTF-8") + "=" + URLEncoder.encode(passcode, "UTF-8");
            URL url = new URL(Data1.getInstance().getWebsiteFolder() + "genamapcheckteam.php");


            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.close();
            //finally, get the response ...
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            boolean ok = false;
            while ((line = rd.readLine()) != null)
            {
                line = line.trim();
                if (!ok)
                {
                    if (line.equals("OK"))
                    {
                        ok = true;
                        line = rd.readLine();
                        if (line.equals("OK2"))
                        {
                            rd.close();
                            //isConnected = false;
                            return true;
                        }
                        continue;
                    }
                    else
                    {
                        rd.close();
                        //isConnected = false;
                        return false;
                    }
                }
            }

        }
        catch (Exception ex)
        {
            //isConnected = false;
            error = ex.getMessage();
            System.err.println(error);
            return false;
        }
        //isConnected = false;
        return false;
    }

    /**
     * Create a new team with the given name and passcode. 
     * @param team
     * @param passcode
     */
    public static void createTeam(String team, String passcode)
    {
        String data;

        try
        {
            //encode some parms.
            data = URLEncoder.encode("team", "UTF-8") + "=" + URLEncoder.encode(team, "UTF-8") + "&" + URLEncoder.encode("passcode", "UTF-8") + "=" + URLEncoder.encode(passcode, "UTF-8");
            URL url = new URL(Data1.getInstance().getWebsiteFolder() + "genamapcreateteam.php");

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.close();
            //finally, get the response ...
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            boolean ok = false;
            while ((line = rd.readLine()) != null)
            {
                line = line.trim();
                if (!ok)
                {
                    if (line.equals("OK"))
                    {
                        ok = true;
                        continue;
                    }
                    else
                    {
                        rd.close();
                        //isConnected = false;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            //isConnected = false;
            error = ex.getMessage();
            System.err.println(error);
        }
        //isConnected = false;
    }

    /**
     * Since all information has been verified through the form, we can
     * just go ahead and create the new user
     * @param uid The new user id
     * @param pwd The new user password
     * @param org The user's organization
     * @param email The user's email
     * @param name The user's name
     * @param team The team that the user will join. 
     */
    public static void createUser(String uid, String pwd, String org,
            String email, String name, String team)
    {
        String data;

        try
        {
            //encode some parms.
            data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8") + "&" + URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8") + "&" + URLEncoder.encode("org", "UTF-8") + "=" + URLEncoder.encode(org, "UTF-8") + "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" + URLEncoder.encode("team", "UTF-8") + "=" + URLEncoder.encode(team, "UTF-8");
            URL url = new URL(Data1.getInstance().getWebsiteFolder() + "genamapcreateuser.php");

            /*while (isConnected)
            {
            Thread.sleep(50);
            }
            isConnected = true;*/

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.close();
            //finally, get the response ...
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            boolean ok = false;
            while ((line = rd.readLine()) != null)
            {
                line = line.trim();
                if (!ok)
                {
                    if (line.equals("OK"))
                    {
                        ok = true;
                        continue;
                    }
                    else
                    {
                        rd.close();
                        //isConnected = false;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            //isConnected = false;
            error = ex.getMessage();
            System.err.println(error);
        }
        //isConnected = false;
    }

    /**
     * Checks to see if this is a user-specific table.
     * @param table
     * @return
     */
    private static String checkTable(String table)
    {
        if (table.contains("genetraitassociation"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            table = table.replace("genetraitassociation", "genetraitassociation" + keycode);
        }
        else if (table.contains("association"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            table = table.replace("association", "association" + keycode);
        }
        else if (table.contains("markerval"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            table = table.replace("markerval", "markerval" + keycode);
        }
        else if (table.contains("networkval"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            table = table.replace("networkval", "networkval" + keycode);
        }
        else if (table.contains("golist"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            table = table.replace("golist", "golist" + keycode);
        }
        else if (table.contains("traitval"))
        {
            if (keycode == null)
            {
                keycode = getTeamCode();
            }
            table = table.replace("traitval", "traitval" + keycode);
        }
        return table;
    }

    public static int getTeamId()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("keycode='" + DataManager.getTeamCode() + "'");
         ArrayList<String> results = DataManager.runSelectQuery("id", "team", true, where, null);
        return Integer.parseInt(results.get(0));
    }
}
