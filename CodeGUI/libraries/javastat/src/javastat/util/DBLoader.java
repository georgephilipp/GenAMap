package javastat.util;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Chen, Guan-Jhih
 * @version 1.4
 */

import java.sql.*;
import java.util.*;

public class DBLoader
{

    /**
     * Univeral resource locator.
     */

    public String url;

    /**
     * The user's name (or account).
     */

    public String user;

    /**
     * The user's password.
     */

    public String password;

    /**
     * SQL statement.
     */

    public String sqlQuery;

    /**
     * The name of the selected table.
     */

    public String selectedTableName;

    /**
     * The names of the selected columns.
     */

    public String[] selectedColumnNames;

    /**
     * The boolean index indicating if the data include titles.
     * true: the data not include titles.
     * false: the data include titles.
     */

    public static boolean hasTitle = false;

    /**
     * The titles.
     */

    protected String[] columnNames;

    /**
     * The data.
     */

    public String[][] data;

    /**
     * The DBMS driver.
     */

    private String[] drivers = {"org.gjt.mm.mysql.Driver",
                                "org.postgresql.Driver",
                                "sun.jdbc.odbc.JdbcOdbcDriver",
                                "com.microsoft.jdbc.sqlserver.SQLServerDriver"};

    /**
     * The status.
     */

    private String status = "DBLoader";

    /**
     * The index indicating if some error occurs.
     */

    private boolean isError = false;

    /**
     * The parameters of the column.
     */

    private String columnparam;

    /**
     * A connection (session) with a specific database.
     */

    private Connection connection;

    /**
     * A statement.
     */

    private Statement statement;

    /**
     * The parameters of the current selected table.
     */

    private String currentTableName = null;

    /**
     * The parameters of the current selected column.
     */

    private String currentColumnName = null;

    /**
     * Default DBLoader constructor.
     */

    public DBLoader()
    {
        for (int i = 0; i < drivers.length; i++)
        {
            try
            {
                Class.forName(drivers[i]);
            }
            catch (ClassNotFoundException cnfe)
            {
                System.err.println(cnfe);
                status = "[DBLoader]Driver load failed: " + cnfe.getMessage();
                isError = true;
            }
        }
    }

    /**
     * Connects to a database and gets the required data.
     * @param url database url, for instance,
     * <br>       jdbc:postgresql://localhost/ or
     * <br>       jdbc:mysql://localhost/ or
     * <br>       jdbc:microsoft:sqlserver://127.0.0.1:1433/.
     * @param user the user's name.
     * @param password the user's password.
     * @param sqlQuery the SQL statement.
     */

    public DBLoader(String url,
                    String user,
                    String password,
                    String sqlQuery)
    {
        this();
        this.url = url;
        this.user = user;
        this.password = password;
        this.sqlQuery = sqlQuery;
        data = getData(url, user, password, sqlQuery);
    }

    /**
     * Connects to a database and gets the required data.
     * @param url database url, for instance,
     * <br>       jdbc:postgresql://localhost/ or
     * <br>       jdbc:mysql://localhost/ or
     * <br>       jdbc:microsoft:sqlserver://127.0.0.1:1433/.
     * @param user the user's name.
     * @param password the user's password.
     * @param selectedTableName the name of the selected table.
     * @param selectedColumnNames the names of the selected columns.
     */

    public DBLoader(String url,
                    String user,
                    String password,
                    String selectedTableName,
                    String[] selectedColumnNames)
    {
        this();
        this.url = url;
        this.user = user;
        this.password = password;
        this.selectedTableName = selectedTableName;
        this.selectedColumnNames = selectedColumnNames;
        data = getData(url, user, password, selectedTableName,
                       selectedColumnNames);
    }

    /**
     * Closes the connection.
     */

    public void close()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException sqle)
            {
                status = sqle.getMessage(); // Display first message
                isError = true;
            }
        }
    }

    /**
     * Opens the connection.
     * @param url database url, for instance,
     * <br>       jdbc:postgresql://localhost/ or
     * <br>       jdbc:mysql://localhost/ or
     * <br>       jdbc:microsoft:sqlserver://127.0.0.1:1433/.
     * @param user the user's name.
     * @param password the user's password.
     */

    private void openConnection(String url,
                                String user,
                                String password)
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
            connection = DriverManager.getConnection(url, user, password);
            status = "[DBLoader] Database connection established";
            isError = false;
            statement = connection.createStatement();
        }
        catch (SQLException sqle)
        {
            status = sqle.getMessage(); // Display first message
            isError = true;
        }
    }


    /**
     * Gets the current table parameters.
     * @return the table parameters.
     */

    public String getCurrentTableName()
    {
        return currentTableName;
    }

    /**
     * Gets the current column parameter.
     * @return the column parameters.
     */

    public String getCurrentColumnName()
    {
        return currentColumnName;
    }

    /**
     * Gets the data from the DBMS.
     * @param url database url, for instance,
     * <br>       jdbc:postgresql://localhost/ or
     * <br>       jdbc:mysql://localhost/ or
     * <br>       jdbc:microsoft:sqlserver://127.0.0.1:1433/.
     * @param user the user's name.
     * @param password the user's password.
     * @param sqlQuery the SQL query.
     * @return the required data.
     */

    public String[][] getData(String url,
                              String user,
                              String password,
                              String sqlQuery)
    {
        openConnection(url, user, password);
        try
        {
            ResultSet res = statement.executeQuery(sqlQuery);
            isError = false;
            try
            {
                ResultSetMetaData metadata = res.getMetaData();
                int columns = metadata.getColumnCount();
                columnNames = new String[columns];
                Vector<String> datas = new Vector<String>();
                for (int i = 0; i < columns; i++)
                {
                    columnNames[i] = metadata.getColumnLabel(i + 1);
                }
                int s = 0;
                while (res.next())
                {
                    s++;
                    for (int i = 0; i < columns; i++)
                    {
                        datas.addElement(res.getString(i + 1));
                    }
                }
                int ri = 0;
                if (hasTitle)
                {
                    data = new String[s + 1][columnNames.length];
                    for (int i = 0; i < columnNames.length; i++)
                    {
                        data[0][i] = columnNames[i];
                    }
                    ri = 1;
                }
                else
                {
                    data = new String[s][columnNames.length];
                }
                int index = 0;
                for (; ri < data.length; ri++)
                {
                    for (int j = 0; j < columnNames.length; j++)
                    {
                        data[ri][j] = datas.elementAt(index);
                        index++;
                    }
                }
                if (!isError)
                {
                    sqlQuery = sqlQuery.toLowerCase();
                    if (sqlQuery.contains("from "))
                    {
                        currentTableName = sqlQuery.substring(sqlQuery.indexOf(
                                "from ") + 5, sqlQuery.length()).trim();
                    }
                    currentColumnName = "";
                    for (int i = 0; i < columnNames.length; i++)
                    {
                        currentColumnName += columnNames[i] + ",";
                    }
                    currentColumnName = currentColumnName.substring(0,
                            currentColumnName.length() - 1);
                    status = currentColumnName + " displayed from table[" +
                             currentTableName + "].";
                }
            }
            catch (SQLException sqle)
            {
                status = "[SQL Exception]" + sqle;
                isError = true;
            }
        }
        catch (SQLException sqle)
        {
            status = "[Select Error]" + sqle.getMessage();
            isError = true;
        }

        return data;
    }

    /**
     * Retrives the data from the DBMS.
     * @param url database url, for instance,
     * <br>       jdbc:postgresql://localhost/ or
     * <br>       jdbc:mysql://localhost/ or
     * <br>       jdbc:microsoft:sqlserver://127.0.0.1:1433/.
     * @param user the user's name.
     * @param password the user's password.
     * @param selectedTableName the table name in a database.
     * @param selectedColumnName the name of the selected column in the table.
     * @return the required data.
     */

    private String[][] getData(String url,
                               String user,
                               String password,
                               String selectedTableName,
                               String selectedColumnName)
    {
        getData(url, user, password,
                "SELECT " + selectedColumnName + " FROM " + selectedTableName);
        if (!isError && selectedColumnName.trim().equals("*"))
        {
            currentColumnName = "*";
            status = "Complete table[" + currentTableName + "] displayed";
        }

        return data;
    }

    /**
     * Retrives data from the DBMS.
     * @param url database url, for instance,
     * <br>       jdbc:postgresql://localhost/ or
     * <br>       jdbc:mysql://localhost/ or
     * <br>       jdbc:microsoft:sqlserver://127.0.0.1:1433/.
     * @param user the user's name.
     * @param password the user's password.
     * @param selectedTableName the name of the selected table.
     * @param selectedColumnNames the names of the selected columns in the table.
     * @return the required data.
     */

    public String[][] getData(String url,
                              String user,
                              String password,
                              String selectedTableName,
                              String[] selectedColumnNames)
    {
        for (int i = 0; i < (selectedColumnNames.length - 1); i++)
        {
            columnparam += (selectedColumnNames[i] + ",");
        }
        columnparam += selectedColumnNames[selectedColumnNames.length - 1];

        return getData(url, user, password, selectedTableName, columnparam);
    }

    /**
     * Retrives data from the DBMS again.
     */

    public void resetData()
    {
        if (currentTableName != null && currentColumnName != null)
        {
            getData(url, user, password, currentTableName, currentColumnName);
        }
    }

}
