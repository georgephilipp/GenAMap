package service;

public class Driver
{
        public static int steptorun;
	public static void main(String[] args)
	{
		int argsize = args.length;
		if( argsize < 1 || argsize > 3 )
		{
			System.out.print("Incorrect number of input args");
			throw new RuntimeException();
		}

		String db = args[0];
 
		if( argsize > 1 )
                    steptorun = Integer.parseInt(args[1]);  
		else 
                    steptorun = -1;           

		if( argsize > 2 )
			Printer.inst = new Printer(args[2]);
		else
			Printer.inst = new Printer("");   

		//System.out.println("Hello wolrd!");
		SQLCommander.setInstance(db);
		//we could check for a valid connection here if we wanted to.
		Service s = new Service();
		s.run();
		SQLCommander.getInstance().closeConnection();
	}
}
