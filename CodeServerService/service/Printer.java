package service;

import java.io.*;
import java.util.Date;
import java.text.*;

public class Printer
{
	String target;
	BufferedWriter writer;
	String dateStrUsed;

	Printer(String _target)
	{
		target = _target;
		dateStrUsed = "";
		if(!target.equals(""))
		{
			String date = getCurrentDateStr();
			setup(date);
		}		
	}

	void setup(String date)
	{
		try
		{
			if(!dateStrUsed.equals(""))
				writer.close();
			FileWriter wr = new FileWriter(target + date);
			writer = new BufferedWriter(wr);
		}
		catch(IOException e)
		{
			System.out.print("Cannot open log file");
			throw new RuntimeException();
		}
		dateStrUsed = date;
	}

	String getCurrentDateStr()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void print(String content)
	{
		System.out.print(content);	
		if(!target.equals(""))
		{
			String date = getCurrentDateStr();
			if(!date.equals(dateStrUsed))
			{
				setup(date);
			}
			try
			{
				writer.write(content);
				writer.flush();
			}
			catch(IOException e)
			{
				System.out.print("Cannot write");
				throw new RuntimeException();
			}
		}
	}

	public void println(String content)
	{
		print(content + "\n");
	}

	public static Printer inst;

}
