package network;

import java.io.*;
import java.util.ArrayList;

public class NetConCompsDriver 
{

	public static void main(String[] args) 
	{
		String netfile = args[0];
		double thresh = Double.parseDouble(args[1]);
		String mask = args[2];

		ArrayList<SubNet> networks = new ArrayList<SubNet>();
		
		try
		{
			FileInputStream fstream = new FileInputStream(netfile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
			String strLine;
			
			while((strLine = br.readLine()) != null)
			{
				String[] vals = strLine.split("\t");
				
				if(vals.length != 3)
					continue;
				
				if(Double.parseDouble(vals[2]) >= thresh)
				{
					int i = Integer.parseInt(vals[0]);
					int j = Integer.parseInt(vals[1]);
					boolean hasi = false;
					boolean hasj = false;
					SubNet si = null;
					SubNet sj = null;
					
					for(SubNet s : networks)
					{
						if(s.contains(i))
						{
							hasi = true;
							si = s;
						}
						if(s.contains(j))
						{
							hasj = true;
							sj = s;
						}
					}
					
					if(!hasi && !hasj)
					{
						networks.add(new SubNet(i,j));
					}
					else if(hasi && hasj)
					{
						if(sj != si)
						{
							sj.merge(si);
							networks.remove(si);
						}
					}
					else if(hasi)
					{
						si.add(j);
					}
					else if(hasj)
					{
						sj.add(i);
					}
				}
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
System.out.println(networks.size());
		int i = 0;
		for(SubNet s : networks)
		{
			String name = mask + "_subnet_" + (i++) + ".txt";
			try {
				s.print(name);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		System.out.println("I am done!");
		
	}

}
