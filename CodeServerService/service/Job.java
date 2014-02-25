package service;

import java.util.ArrayList;
import java.io.*;

public class Job
{
	public int jobID;
	private String jobName;
	private String status;
	private String homedir;
	private int step = 0;
	private int maxStep = -5;
	private int currentNumCondorJobs;
	private ArrayList<String> condorIDs;

	public Job(int i)
	{
		this.jobID = i;
		jobName = null;
	}

	public boolean update()
	{
		if(jobName == null || status.equals("INI"))
		{
			if(!SQLCommander.getInstance().setupDBForNewJob(jobID))
                        {
                                sendJobToError("Could not unpause this job.");
                                System.out.println("\tCould not manage to unpause this job.");
                                return false;
                        }

			ArrayList<String> jobInfo = SQLCommander.getInstance().getJobNameAndStatus(jobID);
			jobName = jobInfo.get(0);
			status = jobInfo.get(1);
			homedir = jobInfo.get(2);
			step = Integer.parseInt(jobInfo.get(3));
			//System.out.println(homedir);
			System.out.println("\tReceived info for " + this.jobID + ": " + jobName + ", " + status);
		}
		if(status.equals("INI"))
		{
			return startJob();
		}
		else
		{
			ArrayList<CondorJob> condorIDs = SQLCommander.getInstance().getCondorIDs(jobID, step);

			switch(SQLCommander.getInstance().getKillStatus(jobID))
			{
				case 0: this.status = "PSN";
					SQLCommander.getInstance().pauseJob(jobID);
					System.out.println("\tThis job will be paused at the first opportunity");
					break;
				case 1: System.out.println("\tJob " + jobID + " will now be KILLED!!!");
					Condor.stopJobs(condorIDs);
					SQLCommander.getInstance().setJobAsKilled(jobID);
					endJob("CNL");
					return false;
				default: 
					break;
			}

			try
			{
				String exeloc = (homedir + "/condor_runme_" + step + ".sh");
				int[] stat = Condor.getStatus(condorIDs, exeloc, jobID, step, currentNumCondorJobs);
				System.out.print("\t" + stat[0] + " R, " + stat[1] + " H, " + (condorIDs.size() - stat[0] - stat[1]) + " C.  ");
				updatePerCmpt(stat[0]+stat[1], condorIDs.size()-stat[0]-stat[1]);
				if(stat[1] == 0 && stat[0] > 0)
					return true;
				if(stat[1] > 0)
				{
					//Condor.releaseJobs();
					//sendJobToError("Condor processes relating to this job were held.");
					System.out.println("\tJob contains holds on condor jobs.");
					return true;
				}
				String s = getErrorString();
				if(s.equals(""))
				{
					SQLCommander.getInstance().clearCondorIds(jobID, step);
					step ++;
					SQLCommander.getInstance().updateStep(jobID, step);
					if(step == maxStep) //complete
					{
						System.out.println("\tJob complete");
						SQLCommander.getInstance().updatePerComplete(jobID, 100);
						endJob("CPT");
						return false;
					}
					if(SQLCommander.getInstance().shouldJobCont(jobID))
					{
						System.out.println("\tMoving job to next step.");
						return startJob();
					}
					else
					{
						System.out.println("\tJob going into hybernation mode.");
						endJob("PSD");
						return false;
					}	
				}
				else if (s.contains("loadUnable"))
				{
					step --;
					if(step < 0)
					{
						sendJobToError(s);
						return false;
					}
					SQLCommander.getInstance().updateStep(jobID, step);
					System.out.println("\tJob going back a step.");
					return startJob();
				}
				else
				{
					sendJobToError(s);
					return false;
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}

		return true;
	}

	private void updatePerCmpt(int run, int cmpt)
	{
		if(this.maxStep < 0)
		{
			maxStep = SQLCommander.getInstance().getMaxSteps(jobID);
		}

		double stepSz = 99.0 / (maxStep + 1);
		double stepDun = stepSz * step;
		double subDun = (double)(cmpt) / (double)(run + cmpt) * stepSz;
		//System.out.println(subDun);
		int perCpt = (int)(1.0 + stepDun + subDun);
		System.out.println(perCpt+"%");
		SQLCommander.getInstance().updatePerComplete(jobID, perCpt);
	}

	private boolean startJob()
	{

		if(Driver.steptorun != -1 && step != Driver.steptorun)
		{
			System.out.print("Will not run this step");
			throw new RuntimeException();
		}

		System.out.println("\tStarting job : " + jobID);
		//get parms and universe
		String parmsloc = homedir + "/parms";
		parmsloc += (step>0?"" + step +".txt":".txt");
		boolean first = step == 0;
		ArrayList<String> parms = new ArrayList<String>();
		String universe = null;
		boolean go = true;
		boolean tried = false;		
		while(go)
		{
			try
			{
				FileInputStream fstream = new FileInputStream(parmsloc);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				if(first)
					parms.add(br.readLine() + " " + SQLCommander.getInstance().getDB());
				else parms.add(br.readLine());
				go = false;
				universe = br.readLine();
				while((strLine = br.readLine()) != null)
				{
					if(first)
						parms.add(strLine + " " + SQLCommander.getInstance().getDB());
					else parms.add(strLine);
				}

				in.close();
			}
			catch(Exception e)
			{
				if(tried)
				{
					sendJobToError("Error in reading parameter file: " + e.getMessage());
					return false;
				}
				else
				{
					tried = true;
					try
					{
						Thread.sleep(5000);//file just isn't copied in condor yet
					}
					catch(Exception ex){}
				}
			}
		}

		String exeLoc = SQLCommander.getInstance().getExeLoc(jobID, step);
		String prio = SQLCommander.getInstance().getJobPrio(jobID, step);
		//System.out.println("jobid: " + jobID);
		//System.out.println("step: " + step);
		//System.out.println("exeLoc: " + exeLoc);
		currentNumCondorJobs = parms.size();
		String shLoc = Condor.makeCondorRunFiles(universe, homedir + "/.." + exeLoc, parms, step, homedir, prio);
		ArrayList<CondorJob> jobs = Condor.runCondorSubmitFile(shLoc, jobID, step);
		//SQLCommander.getInstance().insertCondorIDs(jobID, step, jobs);
		//SQLCommander.getInstance().updatePerComplete(jobID, 1.0);
		SQLCommander.getInstance().updateStatus("RUN", jobID);
		this.status = "RUN";

		return true;
	}

	private void sendJobToError(String errorText)
	{
		if(errorText.length() > 150)
			errorText = errorText.substring(0, 140) + "\n...";	

		System.out.println("\tJob will be stopped with the following error: " + errorText);

		SQLCommander.getInstance().insertErrorText(jobID, errorText);
		endJob("ERR");
	}

	private void endJob(String status)
	{
		SQLCommander.getInstance().updateStatus(status, jobID);
		SQLCommander.getInstance().setEndTime(jobID);
	}

	private String getErrorString()
	{
		String errors = "";
		try
		{
			File f = new File(homedir + "/error.txt");
			if(f.exists())
			{
				FileInputStream fstream = new FileInputStream(homedir + "/error.txt");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				while((strLine = br.readLine()) != null)
				{
					errors += strLine;
				}
				in.close();
			}

			//Thread.sleep(1000);
			FileInputStream fis = new FileInputStream(homedir + "/err_" + step + ".txt");
			DataInputStream in2 = new DataInputStream(fis);
			BufferedReader  buf = new BufferedReader(new InputStreamReader(in2));
			String lin;
			//System.out.println(homedir + "/err_" + step + ".txt");
			while((lin = buf.readLine())!= null)
			{
				System.out.println("!" + lin);
				if(lin.length() >= 6)
				{
					if(!lin.substring(0, 6).equals("Condor"))
						errors += lin;
				}
			}
			in2.close();
			System.out.println(errors);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return e.getMessage();
		}	
		return errors;
	}
}
