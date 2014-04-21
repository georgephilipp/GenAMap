package service;

import java.util.ArrayList;
import java.util.Date;

public class Service
{
	private ArrayList<Job> jobs;

	public Service()
	{
		jobs = new ArrayList<Job>();
	}

	public void run()
	{
		try
		{
			while(true)
			{
				//System.out.println("Hello World! The Service is awake!\n");
				Printer.inst.println("" + new Date());
				//System.out.println("Checking for new jobs to track ... ");
				ArrayList<Integer> ids = SQLCommander.getInstance().getRunningJobs();

				for(int i = 0; i < ids.size(); i ++)
				{
					boolean found = false;
					for(int j = 0; j < jobs.size(); j ++)
					{
						if(jobs.get(j).jobID == ids.get(i))
							found = true;
					}

					if(!found)
					{
						Printer.inst.println("The service is now tracking job: " + 
							ids.get(i));
						jobs.add(new Job(ids.get(i)));
					}
				}

				Printer.inst.println("");

				for(int i = jobs.size()-1; i >= 0; i --)
				{
					Printer.inst.println("Updating job: " + jobs.get(i).jobID);
					if(!jobs.get(i).update())
					{
						Printer.inst.println("\tNo longer tracking job " + jobs.get(i).jobID);
						jobs.remove(jobs.get(i));
					}
				}

				//System.out.println();
				//System.out.println("All tasks completed; good night.");
				Thread.sleep(5000);
			}
		}
		catch(Exception e)
		{
			Printer.inst.println("Oh no! We've got problems ! !");
			Printer.inst.println(e.getMessage());
		}
	}
}
