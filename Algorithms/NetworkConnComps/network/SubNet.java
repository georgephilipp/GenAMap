package network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SubNet
{
	ArrayList<Integer> comps = new ArrayList<Integer>();
	
	public SubNet(int a, int b)
	{
		comps.add(a);
		comps.add(b);
	}
	
	public void merge(SubNet s)
	{
		for(Integer i : s.comps)
		{
			comps.add(i);
		}
	}
	
	public boolean contains(int i)
	{
		return comps.contains(i);
	}
	
	public void add(Integer i)
	{
		if(!this.contains(i))
		{
			comps.add(i);
		}
		else
		{
			throw new IllegalArgumentException("Code is not correct");
		}
	}
	
	public void print(String filename) throws IOException
	{
		Collections.sort(comps);
		//System.out.println("Subnetwork!");
		
		//System.out.println(comps.size());
		
		FileWriter fstream = new FileWriter(filename);
		BufferedWriter out = new BufferedWriter(fstream);
				
		for(Integer i:comps)
		{
			out.write(i + "\n");
		}
		
		out.close();
		
	}
}
