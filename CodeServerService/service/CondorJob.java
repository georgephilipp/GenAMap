package service;

public class CondorJob
{
	public int condorid;
	public boolean finished;

	public CondorJob(int ci, int f)
	{
		this.condorid = ci;
		this.finished = f == 1;
	}
}
