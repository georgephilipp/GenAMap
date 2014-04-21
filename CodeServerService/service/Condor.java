package service;

import java.io.*;
import java.util.ArrayList;

public class Condor
{
	public static String makeCondorRunFiles(
		String universe, String exe, ArrayList<String> args, int step, String homeDir, String prio)
	{
		String s = (homeDir + "/condor_runme_" + step + ".sh");
		int idx = 0;
		try
		{
			FileWriter ostream = new FileWriter(homeDir + "/condor_runme_" + step + ".sh");
			BufferedWriter outo = new BufferedWriter(ostream);
			for(int i = 0; i < args.size(); i ++)
			{
				if(i % 100 == 0 && i > 0)
				{
					outo.close();
					ostream = new FileWriter(homeDir + "/condor_runme_" + step + ".sh" + 
							idx++);
					outo = new BufferedWriter(ostream);
				}
				String loc = (homeDir + "/condor_runme_" + step + "_" + i + ".txt");
				FileWriter fstream = new FileWriter(loc);
				outo.write("condor_submit " + loc + "\n");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("Universe = " + universe + "\n");
				out.write("Log = " + homeDir + "/log_" + step + ".txt\n");
				out.write("Output = " + homeDir + "/out_" + step + "_" + i + ".txt\n");
				out.write("Error = " + homeDir + "/err_" + step + ".txt\n");
				out.write("Executable = " + exe + "\n");
				out.write("Arguments = " + args.get(i) + "\n");
				out.write("initialdir = " + homeDir + "\n");
				out.write("Notification = Error\n");
				out.write("priority = " + prio + "\n");
				out.write("Queue\n");
				out.close();
			}
			outo.close();
		}
		catch(Exception e)
		{
			Printer.inst.println(e.getMessage());
			return null;
		}
		return s;
	}

	public static void stopJobs(ArrayList<CondorJob> jobs)
	{
		waitTillCondorRuns();
		for(int i = 0; i < jobs.size(); i ++)
		{
			try
			{
				//if(jobs.get(i).condorid != -2)
				//{
					Process p = Runtime.getRuntime().exec("condor_rm " + jobs.get(i).condorid);
					p.getInputStream().close();
					p.getErrorStream().close();
					p.getOutputStream().close();
				//}
			}
			catch(Exception e){}
		}
	}

	public static void releaseJob(int job)
	{
		waitTillCondorRuns();
		try
		{
			//if(job != -2)
			//{
				Process p = Runtime.getRuntime().exec("condor_release " + job);
        	                p.getInputStream().close();
                	        p.getErrorStream().close();
                        	p.getOutputStream().close();
			//}

		}
		catch(Exception e){}
	}

	public static /*ArrayList<CondorJob>*/void runCondorSubmitFile(String submitFile, int jobID, int step)
	{
		runCondorSubmitFile(submitFile, jobID, step, -1/*, null*/);
	}

	public static /*ArrayList<CondorJob>*/void runCondorSubmitFile(String submitFile, int jobID, int step, int call/*, ArrayList<CondorJob> ids*/)
	{
		if(call == -1)
		{
			int i = 0; 
			while(i++ < 5)
			{
				try
				{
					makeExeRunnable(submitFile);
					/*ids = */runExe(submitFile, jobID, step);
					//int idx = 0;
					//File f = new File(submitFile + idx++);
					//while(f.exists())
					//{
					//	for(int q = 0; q < 10; q ++)
					//	{
					//		ids.add(-2);
					//	}
					//	f = new File(submitFile + idx++);
					//}
					return /*ids*/;
				}
				catch(Exception e) {}
			}
			//return null;
		}
		else
		{
			int i = 0;
			while(i++ < 5)
			{
				try
				{
					makeExeRunnable(submitFile + call);
					/*ArrayList<CondorJob> temp = */runExe(submitFile+call, jobID, step);
					/*int begin = 0;
					for(int j = 0; j < ids.size(); j ++)
					{
						if(ids.get(j).condorid == -2 && begin == 0)
						{
							begin = j;
							break;
						}
					}
					for(int j = 0; j < temp.size();j ++)
					{
						ids.set(j+begin, temp.get(j));
					}
					return null;*/
					//ids.addAll(temp);
					return /*ids*/;					
				}
				catch(Exception e) {}
			}
		}
		//return null;
	}

	public static int[] getStatus(ArrayList<CondorJob> ids, String submitFile,int jobid, int step, int totalJobs) throws Exception
	{
		int[] toRet = new int[3];
		toRet[0] = 0;
		toRet[1] = 0;
		toRet[2] = 0;
		for(int i = 0; i < ids.size(); i ++)
		{
			/*if(ids.get(i).condorid == -2)
			{
				toRet[0] ++;
				System.out.println("??");
			}*/
			char st = getState(ids.get(i));
			if(st == 'H')
			{
				releaseJob(ids.get(i).condorid);
				toRet[1] ++;
			}
			else if(st == 'R' || st == 'I')
			{
				toRet[0] ++;
			}
			else
				toRet[2]++;
		}
		//int noJobs = (getNumJobs(submitFile, ids.size()));
		//System.out.println(noJobs);
		int reallyRunning = toRet[0];
		/*if(noJobs != ids.size())
		{
			int sz = noJobs - ids.size();
			for(int i = 0; i < sz; i ++)
			{
				ids.add(new CondorJob(-2,0));
				toRet[0]++;
			}
		}
		if(reallyRunning < 20)
		{
			int cmpt = 0;
			for(int j = 0; j < ids.size(); j ++)
			{
				if(ids.get(j).condorid != -2)
				{
					cmpt ++;
				}
			}
			if(cmpt != ids.size())//we need to spawn some more jobs.
			{
				int no = (cmpt / 100)-1;
				runCondorSubmitFile(submitFile, jobid, step, no, ids);
			}
		}*/
		int unspawned = totalJobs - ids.size();
		if( reallyRunning < 20 && unspawned > 0 )
		{
			Printer.inst.println("\tRespawning begins");
 			runCondorSubmitFile(submitFile, jobid, step, (ids.size() / 100)-1/*, ids*/);
			ArrayList<CondorJob> newIds = SQLCommander.getInstance().getCondorIDs(jobid, step);
			if(newIds.size() <= ids.size())
			    throw new RuntimeException("no new ids generated");
			Printer.inst.println("\tRespawning ends with old and new job size: " + ids.size() + newIds.size());			
			return getStatus(newIds, submitFile, jobid, step, totalJobs);
		}

		return toRet;
	}

	/*public static int getNumJobs(String submitFile, int init)
	{
		int nojobs = init < 100 ? init : 100;
		int idx = 0;
		File f = new File(submitFile + idx++);
		while(f.exists())
		{
			try
			{
				LineNumberReader lnr = new LineNumberReader(new FileReader(f));
				lnr.skip(f.length());
				nojobs += lnr.getLineNumber();
				lnr.close();
			}
			catch(Exception e)
			{
				nojobs += 100;
				System.err.println(e.getMessage());
			}
			f = new File(submitFile + idx++);
		}

		//int baselinejobs = nojobs;
		//while(init > nojobs)//implies that this is a repeat of the job
		//{
		//	nojobs += baselinejobs;
		//}
		return nojobs;
	}*/

	private static char getState(CondorJob id) throws Exception
	{
		waitTillCondorRuns();
		if(id.finished)
			return 'C';

		String s = "" + id.condorid;
		String line;
		Process p = Runtime.getRuntime().exec("condor_q " + s);
		p.waitFor();
            	p.getErrorStream().close();
                p.getOutputStream().close();

		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while((line = input.readLine()) != null)
		{
			if(line.startsWith(s))
			{
				String[] splitted = line.split("\\s+");
				input.close();
				return splitted[5].charAt(0);
			}
			else if(line.toLowerCase().contains("fetch") || line.toLowerCase().contains("error:"))
			{
				return 'I';
			}
		}
		input.close();
		p.getInputStream().close();
		SQLCommander.getInstance().finishCondorJob(id.condorid);
		return 'C';
	}

	private static void makeExeRunnable(String file) throws Exception
	{
		Runtime r = Runtime.getRuntime();
		Process p = r.exec("chmod u+x " + file);
		p.waitFor();
                p.getInputStream().close();
                p.getErrorStream().close();
                p.getOutputStream().close();

	}

	private static /*ArrayList<CondorJob>*/ void runExe(String exe, int jobID, int step) throws Exception
	{
		waitTillCondorRuns();
		String line;
		ArrayList<CondorJob> toRet = new ArrayList<CondorJob>();
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(exe);
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while((line = input.readLine()) != null)
		{
			//System.out.println(line);
			if(line.startsWith("1"))
			{
				line = line.replace("1 job(s) submitted to cluster ", "");
				int value = Integer.parseInt(line.replace(".", ""));
				Printer.inst.println("\t\t" + value);
				toRet.add(new CondorJob(value,0));
			}
		}
		input.close();
                p.getInputStream().close();
                p.getErrorStream().close();
                p.getOutputStream().close();

		SQLCommander.getInstance().insertCondorIDs(jobID, step, toRet);

		//return toRet;
	}

	private static boolean isCondorRunning()
	{
		try
		{
			String line;
			Process p = Runtime.getRuntime().exec("condor_q");
			p.waitFor();
		    	p.getErrorStream().close();
		        p.getOutputStream().close();

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			boolean res = true;
			while((line = input.readLine()) != null)
			{
				if(line.toLowerCase().contains("error") || line.toLowerCase().contains("not found") || line.toLowerCase().contains("cannot run"))
					res = false;
			}
			input.close();
			p.getInputStream().close();
			return res;
		}
		catch(IOException e)
		{
			return false;
		}
		catch(InterruptedException e2)
		{
			return false;
		}
	}

	private static void waitTillCondorRuns()
	{
	        boolean result = false;
		while(!result)
		{
			result = isCondorRunning();
			if(!result)
			{
				Printer.inst.println("Condor not found to be running. Waiting 5 seconds.");
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
				}
			}
		}
	}
}
