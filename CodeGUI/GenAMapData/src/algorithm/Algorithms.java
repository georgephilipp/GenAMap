package algorithm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * GenAMap knows which algorithms to run based on a configuration file,
 * algoConfig.txt. The Algorithms class reads this configuration file
 * to find out what information is needed from ther user to run
 * the algorithm, and what parameters to set, etc.
 * @author ross
 */
public class Algorithms
{
    /**
     * Network algorithms are one type of algorithm where we have several
     * different types. Each of these types of network creating algorithms
     * are read from the config file and parsed using this class.
     */
    public static class NetworkAlgorithms
    {
        private static boolean inited = false;

        private static ArrayList<String> algorithms = new ArrayList<String>();
        private static ArrayList<String> algonames = new ArrayList<String>();
        private static ArrayList<ArrayList<String>> paramNames = new ArrayList<ArrayList<String>>();
        private static ArrayList<ArrayList<Integer>> paramTypes = new ArrayList<ArrayList<Integer>>();
        private static ArrayList<Integer> jobTypeID = new ArrayList<Integer>();

        /**
         * This returns the list of jobs as database ids. This is the one
         * key component that connects the GUI to the database. By knowing the
         * database id, GenAMap can simply insert a record into the database
         * in order to start the process of running the algorithms. 
         * @return
         */
        public static ArrayList<Integer> jobTypeID()
        {
            if(!inited)
            {
                Init();
            }
            return jobTypeID;
        }

        /**
         * Returns the list of algorithm names that are currently in the
         * database that are available for the user to run. This list
         * is often used to populate an option box.
         * @return
         */
        public static ArrayList<String> getalgorithms()
        {
            if(!inited)
            {
                Init();
            }
            return algorithms;
        }

        /**
         * Returns a list of the three letter codes for each of the available
         * algorithms. These three letter codes should be used in order
         * to generate the algorithm name. This is used in the database and
         * in tracking the running algorithm. 
         * @return
         */
        public static ArrayList<String> algonames()
        {
            if(!inited)
            {
                Init();
            }
            return algonames;
        }

        /**
         * Returns the parameter names that are required by the algorithm implementation in
         * order to run the algorithm. Right now this is not used, but we have thought
         * through this as a possible way to implement parameter passing. 
         * @return
         */
        public static ArrayList<ArrayList<String>> paramNames()
        {
            if(!inited)
            {
                Init();
            }
            return paramNames;
        }

        /**
         * Returns the parameter types that are required by the algorithm implementation in
         * order to run this algorithm.  The current implementation, which
         * hasn't been used yet, is that a list of parameters and their types
         * can follow the basic values needed for a network algorithm. 
         * @return
         */
        public static ArrayList<ArrayList<Integer>> paramTypes()
        {
            if(!inited)
            {
                Init();
            }
            return paramTypes;
        }

        /**
         * This method is only called once - it reads the entire configuration
         * file and populates all class variables, which can then be
         * returned to the user. This way we touch the file only once. 
         */
        private static void Init()
        {
            DataInputStream in = null;
            try
            {
                FileInputStream fstream = new FileInputStream("algoConfig.txt");
                in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while((strLine = br.readLine()) != null)
                {
                    if(strLine.equals("Association:"))
                        break;

                    if(!strLine.equals("Network:"))
                    {
                        String[] info = strLine.split("\t");
                        if(info.length >= 3)
                        {
                            algorithms.add(info[0]);
                            algonames.add(info[1]);
                            jobTypeID.add(Integer.parseInt(info[2]));
                            paramNames.add(new ArrayList<String>());
                            paramTypes.add(new ArrayList<Integer>());
                            int l = paramNames.size() - 1;
                            for(int i = 3; i < info.length; i +=2)
                            {
                                paramNames.get(l).add(info[i]);
                                paramTypes.get(l).add(Integer.parseInt(info[i+1]));
                            }
                        }
                    }
                }
                inited = true;
            }
            catch (Exception e)
            {
                System.err.println("Your configuration file is missing!");
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
    }

    /**
     * Association algorithms are one type of algorithm where we have several
     * different types. Each of these types of association creating algorithms
     * are read from the config file and parsed using this class. They differ
     * from network algorithms in that structure is also a required entry in
     * the config file. 
     */
    public static class AssociationAlgorithms
    {
        private static boolean inited = false;

        private static ArrayList<String> algorithms = new ArrayList<String>();
        private static ArrayList<String> algonames = new ArrayList<String>();
        private static ArrayList<ArrayList<String>> paramNames = new ArrayList<ArrayList<String>>();
        private static ArrayList<ArrayList<Integer>> paramTypes = new ArrayList<ArrayList<Integer>>();
        private static ArrayList<Integer> jobTypeID = new ArrayList<Integer>();
        private static ArrayList<Integer> inputStructure = new ArrayList<Integer>();

        /**
         * For association algorithms, structure is often a prerequisit in order
         * to run the algorithm. Association entries are therefore different
         * than network entries in that they have this extra column. 0
         * specifies no input structure needed, 1 implies a network, 2 implies
         * population structure, and 3 implies a tree. This is extendable
         * as more algorithms are added. 
         * @return
         */
        public static ArrayList<Integer> inputStructure()
        {
            if(!inited)
            {
                Init();
            }
            return inputStructure;
        }

        /**
         * This returns the list of jobs as database ids. This is the one
         * key component that connects the GUI to the database. By knowing the
         * database id, GenAMap can simply insert a record into the database
         * in order to start the process of running the algorithms.
         * @return
         */
        public static ArrayList<Integer> jobTypeID()
        {
            if(!inited)
            {
                Init();
            }
            return jobTypeID;
        }

        /**
         * Returns the list of algorithm names that are currently in the
         * database that are available for the user to run. This list
         * is often used to populate an option box.
         * @return
         */
        public static ArrayList<String> getalgorithms()
        {
            if(!inited)
            {
                Init();
            }
            return algorithms;
        }

        /**
         * Returns a list of the three letter codes for each of the available
         * algorithms. These three letter codes should be used in order
         * to generate the algorithm name. This is used in the database and
         * in tracking the running algorithm.
         * @return
         */
        public static ArrayList<String> algonames()
        {
            if(!inited)
            {
                Init();
            }
            return algonames;
        }

        /**
         * Returns the parameter names that are required by the algorithm implementation in
         * order to run the algorithm. Right now this is not used, but we have thought
         * through this as a possible way to implement parameter passing.
         * @return
         */
        public static ArrayList<ArrayList<String>> paramNames()
        {
            if(!inited)
            {
                Init();
            }
            return paramNames;
        }

        /**
         * Returns the parameter types that are required by the algorithm implementation in
         * order to run this algorithm.  The current implementation, which
         * hasn't been used yet, is that a list of parameters and their types
         * can follow the basic values needed for a network algorithm.
         * @return
         */
        public static ArrayList<ArrayList<Integer>> paramTypes()
        {
            if(!inited)
            {
                Init();
            }
            return paramTypes;
        }

        /**
         * This method is only called once - it reads the entire configuration
         * file and populates all class variables, which can then be
         * returned to the user. This way we touch the file only once.
         */
        private static void Init()
        {
            DataInputStream in = null;
            try
            {
                FileInputStream fstream = new FileInputStream("algoConfig.txt");
                in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while((strLine = br.readLine()) != null)
                {
                    if(strLine.equals("Association:"))
                        break;
                }

                while((strLine = br.readLine()) != null)
                {
                    String[] info = strLine.split("\t");
                    if(info.length >= 3)
                    {
                        algorithms.add(info[0]);
                        algonames.add(info[1]);
                        jobTypeID.add(Integer.parseInt(info[2]));
                        inputStructure.add(Integer.parseInt(info[3]));
                        paramNames.add(new ArrayList<String>());
                        paramTypes.add(new ArrayList<Integer>());
                        int l = paramNames.size() - 1;
                        for(int i = 4; i < info.length; i +=2)
                        {
                            paramNames.get(l).add(info[i]);
                            paramTypes.get(l).add(Integer.parseInt(info[i+1]));
                        }
                    }
                }
                inited = true;
            }
            catch (Exception e)
            {
                System.err.println("Your configuration file is missing!");
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch (IOException ex) {}
            }
        }
    }
}
