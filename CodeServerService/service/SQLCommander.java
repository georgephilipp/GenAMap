package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

// http://www.java2s.com/Code/Java/Database-SQL-JDBC/LoadMYSQLJDBCDriverandrunthequery.htm
public class SQLCommander
{
	Connection connection;
	private static SQLCommander instance;
	private String db;

	public static SQLCommander setInstance(String db)
	{
		if(instance == null)
			instance = new SQLCommander(db);
		return instance;
	}

	public static SQLCommander getInstance()
	{
		return instance;
	}

	public void closeConnection()
	{
		try
		{
			connection.close();
		}
		catch(Exception e){}
	}

	public String getDB()
	{
		return db;
	}

	private SQLCommander(String db)
	{
		this.db = db;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch(Exception e)
		{
			Printer.inst.println("Unable to find and load driver");
		}

		try
		{
			connection = DriverManager.getConnection("jdbc:mysql://localhost:4306/" + db + "?user=assocmap&password=Thisisadumbpassword*");
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
	}

	private void displaySQLErrors(SQLException e)
	{
		Printer.inst.println("SQLException: " + e.getMessage());
		Printer.inst.println("SQLState: " + e.getMessage());
		Printer.inst.println("VendorError: " + e.getErrorCode());
	}

	public String getExeLoc(int jobID, int step)
	{
		Statement st = null;
		ResultSet rs = null;
		String toRet = null;
                try
                {
                        st = connection.createStatement();
                        rs = st.executeQuery("Select exe FROM jobrun, step WHERE jobrun.jobid = " + jobID + " AND jobrun.step = step.pos AND step.pos = " + step + " AND step.jobtypeid = jobrun.jobtype;");
                        while(rs.next())
                        {
                                toRet = rs.getString("exe");
                        }
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                if(st != null)
                                        st.close();
                                if(rs != null)
                                        rs.close();
                        }
                        catch(Exception e){}
                }

		return toRet;
	}

        public String getJobPrio(int jobID, int step)
        {
                Statement st = null;
                ResultSet rs = null;
                String toRet = null;
                try
                {
                        st = connection.createStatement();
                        rs = st.executeQuery("Select priority FROM jobrun, step WHERE jobrun.jobid = " + jobID + " AND jobrun.step = step.pos AND step.pos = " + step + " AND step.jobtypeid = jobrun.jobtype;");
                        while(rs.next())
                        {
                                toRet = rs.getString("priority");
                        }
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                if(st != null)
                                        st.close();
                                if(rs != null)
                                        rs.close();
                        }
                        catch(Exception e){}
                }

                return toRet;
        }

	public void clearCondorIds(int jobid, int step)
	{
                Statement st2 = null;
                boolean success=false;
                try
                {
                	st2 = connection.createStatement();
                        st2.executeUpdate("DELETE FROM condorid WHERE jobid = " + jobid + " AND step = " + step + ";");
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                st2.close();
                        }
                        catch(Exception e) {}
                }

	}
	public ArrayList<CondorJob> getCondorIDs(int jobid, int step)
	{
		Statement st = null;
		ResultSet rs = null;
		ArrayList<CondorJob> ids = new ArrayList<CondorJob>();
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT condorid, finished FROM condorid WHERE jobid = " + jobid + " AND step = " + step + ";");
		
			while(rs.next())
			{
				ids.add(new CondorJob(rs.getInt("condorid"), rs.getInt("finished")));
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				rs.close();
				st.close();
			}
			catch(Exception e) {}
		}
		return ids;
	}

	public boolean shouldJobCont(int id)
	{
		Statement st = null;
		ResultSet rs = null;
		boolean cont = false;
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT cont FROM jobrun WHERE jobid = " + id + ";");
			while(rs.next())
			{
				if(rs.getInt("cont") == 1)
					cont = true;	
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				rs.close();
				st.close();
			}
			catch(Exception eq){}
		}
		return cont;
	}
		

	public int getMaxSteps(int id)
	{
		Statement st = null;
		ResultSet rs = null;
		int noSteps = -1;
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT nosteps from jobrun,jobtype WHERE jobrun.jobtype = jobtype.id AND jobrun.jobid = " + id + ";");
			while(rs.next())
			{
				noSteps = rs.getInt("nosteps");
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				rs.close();
				st.close();
			}
			catch(Exception e){}
		}
		return noSteps;
	}

	public int getKillStatus(int id)
	{
		Statement st = null;
		ResultSet rs = null;
		int stat = -1;
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT killit FROM pause WHERE jobid = " + id + " AND done = 0;");
			while(rs.next())
			{
				stat = rs.getInt("killit");
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				rs.close();
				st.close();
			}
			catch(Exception e){}
		}		
		return stat;
	}

	public boolean setupDBForNewJob(int id)
	{
		Statement st = null;
		Statement st2 = null;
		ResultSet rs = null;
		boolean success=false;
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM jobrun, jobstatus WHERE jobrun.jobid = jobstatus.id AND jobrun.jobid = " + id + ";");
			while(rs.next())
			{
				success = true;
			}
			if(!success)
			{
				//so, it looks like this job was here before, but was paused.
				rs = st.executeQuery("SELECT appID from jobstatus WHERE id = " + id + ";");
				String appID = null;
				while(rs.next())
				{
					appID = rs.getString("appID");
				}

				rs = st.executeQuery("SELECT id FROM jobstatus WHERE appID = \"" + appID + "\";");
				//we have a bunch of ids, and we want to use them to update the jobrun table
				st2 = connection.createStatement();
				while(rs.next())
				{
					int key = rs.getInt("id");
					if(key < id)
					{
						st2.executeUpdate("UPDATE jobrun SET jobid = " + id + " WHERE jobid = " + key + ";");
						st2.executeUpdate("UPDATE jobrun SET cont = 1 WHERE jobid = " + id + ";");
						success = true;
					}
				}
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				rs.close();
				st.close();
				st2.close();
			}
			catch(Exception e) {}
		}
		return success;
	}

	public ArrayList<String> getJobNameAndStatus(int id)
	{
		Statement st = null;
		ResultSet rs = null;
		ArrayList<String> info = new ArrayList<String>();
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("Select appID, status, loc, step FROM jobstatus,jobrun where jobstatus.id = " + id + " AND jobstatus.id = jobrun.jobid;");
			while(rs.next())
			{
				info.add(rs.getString("appID"));
				info.add(rs.getString("status"));
				info.add(rs.getString("loc"));
				info.add(rs.getString("step"));
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
                finally
                {
                        try
                        {
                                if(st != null)
                                        st.close();
                                if(rs != null)
                                        rs.close();
                        }
                        catch(Exception e){}
                }
                return info;
	}

	public void insertCondorIDs(int jobid, int step, ArrayList<CondorJob> jobs)
	{
		Statement st = null;
		try
		{
			st = connection.createStatement();
			for(int i = 0; i < jobs.size(); i ++)
			{
				st.executeUpdate("INSERT INTO condorid (jobid, step, condorid) VALUES (" + jobid + ", " + step + ", " + jobs.get(i).condorid + ");");
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				st.close();
			}
			catch(Exception e){}
		}
	}

        public void finishCondorJob(int id)
        {
                Statement st = null;
                try
                {
                        st = connection.createStatement();
                        st.executeUpdate("UPDATE condorid SET finished = 1 WHERE condorid = " + id + ";");
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                st.close();
                        }
                        catch(Exception e){}
                }
        }


	public void updatePerComplete(int jobid, double val)
	{
		Statement st = null;
		try
		{
			st = connection.createStatement();
			st.executeUpdate("UPDATE jobstatus SET percmpt = " + val + " WHERE id = " + jobid + ";");
			//System.out.println("percent completed updated successfully");
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				st.close();
			}
			catch(Exception e){}
		}		
	}

	public void setJobAsKilled(int jobid)
	{
		Statement st = null;
		try
		{
			st = connection.createStatement();
			st.executeUpdate("UPDATE pause SET done = 1 WHERE jobid = " + jobid  + ";");
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				st.close();
			}
			catch(Exception e){}
		}
	}

	public void updateStep(int jobid, int step)
	{
		Statement st = null;
		try
		{
			st = connection.createStatement();
			st.executeUpdate("UPDATE jobrun SET step = " + step + " WHERE jobid = " + jobid + ";");
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				st.close();
			}
			catch(Exception e){}
		}
	}

	public void pauseJob(int jobid)
	{
		Statement st = null;
		try
		{
			st = connection.createStatement();
			st.executeUpdate("UPDATE jobstatus SET status = \"PSN\" WHERE id = " + jobid + ";");
			st.executeUpdate("UPDATE jobrun SET cont = 0 WHERE jobid = " + jobid + ";");
			st.executeUpdate("UPDATE pause SET done = 1 WHERE jobid = " + jobid + ";");
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				st.close();
			}
			catch(Exception e){}
		}
	}

        public void insertErrorText(int jobid, String error)
	{
		Statement st = null;
		try
                {
                        st = connection.createStatement();
                        st.executeUpdate("INSERT INTO error (jobid, error) VALUES (" + jobid + ",\"" + error + "\");");
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                if(st != null)
                                        st.close();
                        }
                        catch(Exception e){}
                }

	}

        public void updateStatus(String status, int jobID)
	{
                Statement st = null;
                try
                {
                        st = connection.createStatement();
                        st.executeUpdate("UPDATE jobstatus SET status = \"" + status + "\" WHERE id = " + jobID + ";");
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                if(st != null)
                                        st.close();
                        }
                        catch(Exception e){}
                }
		
	}

	public void setEndTime(int jobID)
	{
                Statement st = null;
                try
                {
                        st = connection.createStatement();
                        st.executeUpdate("UPDATE jobstatus SET end = NOW() WHERE id = " + jobID + ";");
                }
                catch(SQLException e)
                {
                        displaySQLErrors(e);
                }
                finally
                {
                        try
                        {
                                if(st != null)
                                        st.close();
                        }
                        catch(Exception e){}
                }
	}


	public ArrayList<Integer> getRunningJobs()
	{
		Statement st = null;
		ResultSet rs = null;
		ArrayList<Integer> jobIDs = new ArrayList<Integer>();
		try
		{
			st = connection.createStatement();
			rs = st.executeQuery("Select id from jobstatus WHERE status = \'INI\' OR " +
					"status = \'PSN\' or status = \'RUN\';");

			while(rs.next())
			{
				jobIDs.add(rs.getInt("id"));
			}
		}
		catch(SQLException e)
		{
			displaySQLErrors(e);
		}
		finally
		{
			try
			{
				if(st != null)
					st.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		return jobIDs;
	}
}
