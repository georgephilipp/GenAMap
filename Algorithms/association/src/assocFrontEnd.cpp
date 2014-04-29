#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <math.h>
#include <sys/stat.h>
#include <string>
#include <vector>
#include <algorithm>
#include <cstdlib>
using namespace std;
using namespace mysqlpp;
//argv1 = project appID

void combineFiles(int subsz, int k, string mask);
void createParmFiles(int subsz, int k, int tsid, int method, string mask, int mCount, int msid, string name, int x, int popno);
string toString(int i);
int getK(int tsid);
int getmsid(string mask);
int getmCount(int msid);
int gettsid(string mask);
int writeTraitFile(int start, int end, string mask, string filename, int method);
int writeMarkerFile(int start, int end, string mask, string filename, int method);
int writeNetFile(string appID, int netID, bool hardThresh);
void makeWSRParms(string mask, int subsz, int mCount, int k, int numTfiles, int numMfiles,
	int tsid, int msid, int netid, string name);
void makeGFLParms(string mask, int subsz, int mCount, int k, int tsid, int msid, int netid, string name, int N,int method);
void makeLASParms(string mask, int subsz, int mCount, int k, int tsid, int msid, int netid, string name, int N);
void makePNKParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N);
void makePAAParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N, int noPops);
int writeStructureFile(int popid, int popnum);
int writeFeatureFile(string mask, string filename);
void makeADLParms(string mask, int subsz, int mCount, int k, int tsid, int msid, int netid, string name, int N,int method);
//int writeADLMarkerFile(string mask, string filename);
string toStrings(int i)
{
        std::string s;
        std::stringstream out;
        out << i;
        s = out.str();

        return s;


}


double abs(double a)
{
	if(a < 0)
		return -1 * a;
	return a;
}
string db;
string team;

//int maini(int argc, char* argv[])
//{
	int CR1 = 0;
	int CR2 = 1;
	int TOM = 4;
	int SFN = 6;
	int GLO = 5;
	int WSR = 7;
	int GFL = 8;
	int LAS = 9;
	int GCL = 10;
	int GF2 = 11;
	int GC2 = 12;
	int PNK = 13;
	int TLS = 20;
	int PAA = 21;
	int subsz = 250;
	int ADL = 23;

int main(int argc, char* argv[])
{
	cout << "assocFrontEnd begins\n";

	if(argc != 6 && argc != 7)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string file;
	string mask;
	string name;
	int netid;
	int id;
	int k;
	int j;
	int nopops = -1;
	int method=0;
	std::string copyFileName = "";
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				mask = argv[i];
				cout << file << endl<< endl;
				break;
	  	        case 2: 
			        name = argv[i];
			        break;
		        case 3:
				  netid = atoi(argv[i]);
				  break;
			case 4: team = argv[i];
			case 5: db = argv[i];
				break;
			case 6: db = argv[i];
				team = argv[i-1];
				nopops = atoi(argv[i-2]);
				break;	
		}
	}

	string meth = mask.substr(0,3);
	if (strcmp(meth.c_str(), "WSR") == 0)
	{
	    	method = WSR;
	} 
	else if(strcmp(meth.c_str(), "GFL") == 0)
	{
		method = GFL;
	}
	else if(strcmp(meth.c_str(), "LAS") == 0)
	{
		method = LAS;
		copyFileName = "copyFilesLasso.sh";
	}
	else if(strcmp(meth.c_str(), "GCL") == 0)
	{
		method = GCL;
	}
	else if(strcmp(meth.c_str(), "GF2") == 0)
	{
		method = GF2;
		copyFileName = "copyFilesGFLasso.sh";
	}
	else if(strcmp(meth.c_str(), "GC2") == 0)
	{
		method = GC2;
		copyFileName = "copyFilesGFLasso.sh";
	}
	else if (strcmp(meth.c_str(), "PNK") == 0)
	{
		method = PNK;
	}
	else if (strcmp(meth.c_str(), "TLS") == 0)
	{
		method = TLS;
		copyFileName = "copyFilesTreeLasso.sh";
	}
	else if (strcmp(meth.c_str(), "PAA") == 0)
	{
		method = PAA;
		copyFileName = "copyFilesPopAnal.sh";
	}
	else if (strcmp(meth.c_str(), "ADL") == 0)
	{
		method = ADL;
		copyFileName = "copyFilesADL.sh";
	}
	if(copyFileName != "")
	{
		std::string fullCopyFileName = "./../exe/" + copyFileName;
		std::system(fullCopyFileName.c_str());
	}
	int msid = getmsid(mask);
	int tsid = gettsid(mask);
	k = getK(tsid);
	j = getmCount(msid);
	createParmFiles(subsz, k, tsid, method, mask, j, msid, name, netid, nopops);
	if(netid > 0 && nopops == -1)
		writeNetFile(mask, netid, method == GCL || method == GC2);
	return 0;
}

int writeNetFile(string appID, int netID, bool hardThresh)
{
	try
	{
		ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
		Query query = conn.query();
		query << "SELECT idx, weight FROM networkval" << team << ", trait where netid  = " << netID << " AND (trait.id = trait1 OR trait.id = trait2) AND abs(weight) > .1";
		StoreQueryResult ares = query.store();
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}

		ofstream myfile;
		myfile.open((appID + "_net.txt").c_str());

		for(size_t i = 0; i < ares.num_rows(); i++)
		{
			if(i%2 == 0)
			{
				if(!hardThresh)
					myfile << (ares[i]["idx"]+1) << "\t";
				else
				{
					double d = ares[i]["weight"];
					if(abs(d)>.15)
						myfile << (ares[i]["idx"]+1) << "\t";
				}
			}
			else
			{
				if(hardThresh)
				{
					double d = ares[i]["weight"];
					if(d > .15)
						myfile << (ares[i]["idx"]+1) << "\t1" << endl;
					else if(d < -0.15)
						myfile << (ares[i]["idx"]+1) << "\t-1" << endl;
				}
				else
					myfile << (ares[i]["idx"]+1) << "\t" << ares[i]["weight"] << endl;
			}
		}
		myfile.close();
	}

	catch(BadQuery er)
	  {
	    cerr << "Error: " << er.what() << endl;
	    return -1;
	  }
	catch(const BadConversion& er)
	  {
	    cerr << "Conversion error " << er.what() << endl;
	    return -1;
	  }
	catch(const Exception& er)
	  {
		cerr << "Error: "<< er.what() << endl;
		return -1;
	  }
}


int writeFeatureFile(string mask, string filename)
{
	cout<<"writing feature file...."<<endl;
	
	try
	{
	ifstream sqlparmfile("../exe/SQLparms.txt");
	string servername, dbname, username, password;
	getline(sqlparmfile,servername);
	getline(sqlparmfile,dbname);
	getline(sqlparmfile,username);
	getline(sqlparmfile,password);
	Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
        Query query = conn.query();
	query << "Select get_ms_id('"<<mask<<"') as id";
	StoreQueryResult ares = query.store();
	
	int id;
	if(!ares)
    {
	  cerr<<query.error()<<endl;
	  return -1;
	}
	for(size_t i = 0; i<ares.num_rows(); i++)
	 {
	   id = ares[i]["id"];
	   cout << id << endl;
	 }
	query.reset();
	//cout << "ID" << id << endl;
	query << "SELECT * FROM marker WHERE markersetid= " << id << " ORDER BY idx";
	//cout << query.str() << endl;
	ares = query.store();
	if(!ares)
	  {
	    cerr << "Right here. " << endl;
	    cerr << query.error()<<endl;
	    return -1;
	  }  
	else
	{
	
		ofstream myfile;
		
	  myfile.open(filename.c_str());
	  for (size_t i = 0; i <ares.num_rows(); i++)
	    {
		
	      Query q2 = conn.query();
	      q2 << "SELECT value FROM featureval WHERE markerid = " << ares [i]["id"] << " ORDER BY featureid";
		cout << q2.str() << endl;
	      StoreQueryResult ares2 = q2.store();
	    
		//sample features
		/*for(int sf=0; sf < 10; sf++)
		{
			myfile << (((float)sf+1.0)/10.0) << "\t";
		}*/
				for(size_t j = 0; j < ares2.num_rows(); j ++)
				{
					
						myfile << ares2[j]["value"] << "\t";
				}
				myfile << endl;
	      q2.reset();
	    }
	
	  myfile.close();
	
	}
	}
	catch(BadQuery er)
	  {
	    cerr << "Error: " << er.what() << endl;
	    return -1;
	  }
	catch(const BadConversion& er)
	  {
	    cerr << "Conversion error " << er.what() << endl;
	    return -1;
	  }
	catch(const Exception& er)
	  {
	    cerr << "Error :" << er.what() << endl;
	    return -1;
	  }
	
	return 1;
}

/*int writeADLMarkerFile(string mask, string filename)
{
	int noidis = 0;
	try
	{
	ifstream sqlparmfile("../exe/SQLparms.txt");
	string servername, dbname, username, password;
	getline(sqlparmfile,servername);
	getline(sqlparmfile,dbname);
	getline(sqlparmfile,username);
	getline(sqlparmfile,password);
	Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
	Query query = conn.query();
	query << "Select get_ms_id('"<<mask<<"') as id";
	StoreQueryResult ares = query.store();
	
	int id;
	if(!ares)
        {
	  cerr<<query.error()<<endl;
	  return -1;
	}
	for(size_t i = 0; i<ares.num_rows(); i++)
	 {
	   id = ares[i]["id"];
	   //cout << id << endl;
	 }
	query.reset();
	//cout << "ID" << id << endl;
	query << "SELECT id FROM marker WHERE markersetid= " << id << " ORDER BY idx";
	//cout << query.str() << endl;
	ares = query.store();
	if(!ares)
	  {
	    cerr << "Right here. " << endl;
	    cerr << query.error()<<endl;
	    return -1;
	  }  
	else
	{
	
		//run here query once to get size of samples and than store it in a 2d int array
		Query q3 = conn.query();
	    q3 << "SELECT value FROM markerval"<<team<<", marker WHERE markerid = marker.id AND markerid = " << ares [0]["id"] << " ORDER BY idx";
		StoreQueryResult ares3 = q3.store();
		int totalind= ares3.num_rows();
		int totalmark = ares.num_rows();
		cout<<"writing marker"<<endl;
		cout<<"totalind: "<<totalind<<endl;
		cout<<"totalmarker: "<<totalmark<<endl;
		q3.reset();
		int towrite[totalind][totalmark];
	  ofstream myfile;
	  myfile.open(filename.c_str());
	  
	  ofstream myfile11;
	  string fn = "data.vocab";
	  myfile11.open(fn.c_str());
	  noidis = ares.num_rows();
	  for (size_t i = 0; i <ares.num_rows(); i++)
	    {
		
			myfile11 << ares[i]["id"] << "\n";
	      Query q2 = conn.query();
	      q2 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [i]["id"] << " ORDER BY sampleid";
		//cout << q2.str() << endl;
	      StoreQueryResult ares2 = q2.store();
	     // noidis = ares2.num_rows();
	      //cout << noidis << endl;
		for(size_t j = 0; j <ares2.num_rows(); j ++)
		{
			towrite[j][i] = (int)ares2[j]["value"] ;
		  //myfile << ares2[j]["value"] << "\t";
		}
	      //myfile << endl;
	      q2.reset();
	    }
		myfile11.close();
		for(int ind=0; ind <totalind ; ind++ )
		{
			for(int mark=0; mark<totalmark; mark++)
			{
				myfile << towrite[ind][mark] << "\t";
			}
			myfile << endl;
		}
	  myfile.close();

	
	}

	}
	catch(BadQuery er)
	  {
	    cerr << "Error: " << er.what() << endl;
	    return -1;
	  }
	catch(const BadConversion& er)
	  {
	    cerr << "Conversion error " << er.what() << endl;
	    return -1;
	  }
	catch(const Exception& er)
	  {
	    cerr << "Error :" << er.what() << endl;
	    return -1;
	  }
	//cout << noidis;
	
	return noidis;
}*/

int writeMarkerFile(int start, int end, string mask, string filename, int method)
{
	int noidis = 0;
	try
	{
	ifstream sqlparmfile("../exe/SQLparms.txt");
	string servername, dbname, username, password;
	getline(sqlparmfile,servername);
	getline(sqlparmfile,dbname);
	getline(sqlparmfile,username);
	getline(sqlparmfile,password);
	Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  

        Query query = conn.query();
	query << "Select get_ms_id('"<<mask<<"') as id";
	StoreQueryResult ares = query.store();
	
	int id;
	if(!ares)
        {
	  cerr<<query.error()<<endl;
	  return -1;
	}
	for(size_t i = 0; i<ares.num_rows(); i++)
	 {
	   id = ares[i]["id"];
	   cout << id << endl;
	 }
	query.reset();
	//cout << "ID" << id << endl;
	query << "SELECT * FROM marker WHERE markersetid= " << id << " AND idx >= " << start << " AND idx < " << end << " ORDER BY chr, locus";
	//cout << query.str() << endl;
	ares = query.store();
	if(!ares)
	  {
	    cerr << "Right here. " << endl;
	    cerr << query.error()<<endl;
	    return -1;
	  }  
	else
	{
	  ofstream myfile;
		ofstream mapfile;
		bool isPrintIds = false;
		if(method == PNK)
		{
			string mapfi = mask + ".map";
			mapfile.open(mapfi.c_str());
			isPrintIds = true;

			for(size_t i=0; i < ares.num_rows(); i ++)
			{
				mapfile << ares[i]["chr"] << "\t" << ares[i]["name"] << "\t0\t" << ares[i]["locus"] << endl;
			}
		}
	  myfile.open(filename.c_str());
	  for (size_t i = 0; i <ares.num_rows(); i++)
	    {
		
	      Query q2 = conn.query();
		cout << ares[i]["id"] << endl;
	      q2 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = " << ares [i]["id"] << " AND markerid = marker.id ORDER BY sampleid";
		//cout << q2.str() << endl;
	      StoreQueryResult ares2 = q2.store();
	      noidis = ares2.num_rows();
	      cout << noidis << endl;
		if(isPrintIds)
		{
			for(int c = 0; c < 6; c ++)
			{
				for(size_t j = 0; j < ares2.num_rows(); j ++)
				{
					if(c < 2)
					{
						myfile << j << "\t";
					}
					else if(c < 4)
					{
						myfile << "0\t";
					}
					else
					{
						myfile << "1\t";
					}
				}
				myfile << endl;
			}
			isPrintIds = false;
		}
		for(int k = 0; (k < 2 && method == PNK) || (k < 1 && method != PNK); k ++)
		{
			for(size_t j = 0; j <ares2.num_rows(); j ++)
			{
				if(method == 13)
				{
					double value = ares2[j]["value"];
					if(value== 0)
					{
						myfile << "2" << "\t";
					}
					else if(value == 1)
					{
						string s =  k==1?"2\t":"1\t";
						myfile << s;
					}
					else if(value == 2)
					{
						myfile << "1\t";
					}
					else
					{
						cerr << "I'm sorry. Algorithms that use PLINK cannot be used if SNP values are not exactly 0,1 or 2\n";
						return -1;
					}
				}
			
				else
				{
		 	 		myfile << ares2[j]["value"] << "\t";
				}
			}
	      		myfile << endl;
		}
	      q2.reset();
	    }
	
	  myfile.close();
	}

	}
	catch(BadQuery er)
	  {
	    cerr << "Error: " << er.what() << endl;
	    return -1;
	  }
	catch(const BadConversion& er)
	  {
	    cerr << "Conversion error " << er.what() << endl;
	    return -1;
	  }
	catch(const Exception& er)
	  {
	    cerr << "Error :" << er.what() << endl;
	    return -1;
	  }
	//cout << noidis;
	return noidis;
}
int writeTraitFile(int start, int end, string mask, string filename, int method)
{
	int noidis = 0;
        try
        {
                ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
                Query query = conn.query();

                query << "SELECT get_ts_id('" << mask << "') as id";
                StoreQueryResult ares = query.store();

                int id;
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                for(size_t i=0; i < ares.num_rows(); i ++)
                {
                        id = ares[i]["id"];
                }
		
                ofstream myfile;
                myfile.open(filename.c_str());
		bool isWriteId = false;
                if(method == PNK)
                {
			isWriteId = true;
                }
                query.reset();

                query << "SELECT id FROM trait WHERE traitsetid = " << id << " AND idx >= " << start << " AND idx < " << end << " ORDER BY idx";
                ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
			for(size_t i = 0; i < ares.num_rows(); i ++)
			{
				Query q2 = conn.query();
				query << "SELECT value FROM traitval" << team << ",trait WHERE traitid = " << ares[i]["id"] << " AND traitid=trait.id ORDER BY sampleid";
				StoreQueryResult ares2 = query.store();
				noidis = ares2.num_rows();
				
                		if(isWriteId)
                		{
					isWriteId = false;
	                	        for(int a = 0; a < 2; a ++)
        		                {
        	        	               for(size_t j = 0; j < ares2.num_rows(); j ++)
	                        	       {	
                                		       myfile << j << "\t";
	                        	       }
        	        	               myfile << endl;
                		        }
		                }

				for(size_t j = 0; j < ares2.num_rows(); j ++)
				{
					myfile << ares2[j]["value"] << "\t";
				}
				myfile << endl;

				q2.reset();
			}
			myfile.close();
                }
        }
        catch(BadQuery er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
        catch(const BadConversion& er)
        {
                cerr << "Conversion error: " << er.what() << endl;
                return -1;
        }
        catch(const Exception& er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
	return noidis;
}
int getmsid(string mask)
{
  try
    {
      ifstream sqlparmfile("../exe/SQLparms.txt");
	string servername, dbname, username, password;
	getline(sqlparmfile,servername);
	getline(sqlparmfile,dbname);
	getline(sqlparmfile,username);
	getline(sqlparmfile,password);
	Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
      //cout << conn.error() << endl;
      Query query = conn.query();
      query << "SELECT get_ms_id('" << mask << "') as id";
      StoreQueryResult ares = query.store();

      if(!ares)
	{
	  cerr << query.error() << endl;
	  return -1;

	}
      int id;
      for(size_t i = 0; i < ares.num_rows(); i++)
	{
	  id = ares[i]["id"];
	  return id;
	}  


    }
  catch(BadQuery er)
    {
      cerr<< "Error: " <<er.what() <<endl;
      return -1;
    }
  catch(const BadConversion& er)
    {
      cerr <<"Conversion error: " << er.what() << endl;
      return -1;
    }
  catch (const Exception& er)
    {
      cerr << "Error: " <<er.what() << endl;
      return -1;
    }

}
int gettsid(string mask)
{
	try
	{
		ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
		//	cout << conn.error() << endl;
		Query query = conn.query();

		query << "SELECT get_ts_id('" << mask << "') as id";
		StoreQueryResult ares = query.store();

		int id;
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}
		for(size_t i=0; i < ares.num_rows(); i ++)
		{
			id = ares[i]["id"];
			return id;
		}
	}
	catch(BadQuery er)
	{
		cerr << "Error: " << er.what() << endl;
		return -1;
	}
	catch(const BadConversion& er)
	{
		cerr << "Conversion error: " << er.what() << endl;
		return -1;
	}
	catch(const Exception& er)
	{
		cerr << "Error: " << er.what() << endl;
		return -1;
	}
}
int getmCount(int msid)
{
        try
	  {
	    ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
	    Query query = conn.query();
	    query << "SELECT marker_count (" <<msid<<") as cnt";
	    StoreQueryResult ares = query.store();

	    if(!ares)
	      {
		cerr<< query.error() << endl;
		return -1;
	      }
	    else
	      {
		return ares[0]["cnt"];
	      }
	  }


	catch(BadQuery er)
	  {
	    cerr << "Error: " << er.what() << endl;
	    return -1;
	  }
	catch(const BadConversion& er)
	  {
	    cerr << "Conversion error: " << er.what() << endl;
	    return -1;
	  }
	catch(const Exception& er)
	  {
	    cerr << "Error: " << er.what() << endl;
	    return -1;
	  }
       

}

int getK(int tsid)
{
        try
        {
                ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
                Query query = conn.query();
		query << "SELECT trait_count(" << tsid << ") as cnt";
                StoreQueryResult ares = query.store();
                
		if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
                        return ares[0]["cnt"];
                }


        }
        catch(BadQuery er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
        catch(const BadConversion& er)
        {
                cerr << "Conversion error: " << er.what() << endl;
                return -1;
        }
        catch(const Exception& er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
}

int writeStructureFile(int popid, int popnum)
{
        int noidis = 0;
        int tts=-1;
        try
        {
                ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str());  
                Query query = conn.query();
                                string pp = "pop"+toStrings(popnum);
                                query << "SELECT "<<pp<<" FROM structure WHERE popstructid = " << popid << " ORDER BY sampleid";
                StoreQueryResult  ares = query.store();


                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
                                                ofstream myfile;
                                                string fname = "pops.txt";
                                                myfile.open(fname.c_str());
                                                tts  = ares.num_rows();

                                        for(size_t i = 0; i < ares.num_rows(); i ++)
                                        {

                                                myfile << i << "\t" << i << "\t" << ares[i][pp.c_str()] << "\n";

                                        }
                                        myfile.close();

                }
		query = conn.query();
		query << "SELECT count(distinct(" + pp + ")) AS a FROM structure WHERE popstructid = " << popid;
		ares = query.store();
		tts = ares[0]["a"];
        }
        catch(BadQuery er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
        catch(const BadConversion& er)
        {
                cerr << "Conversion error: " << er.what() << endl;
                return -1;
        }
        catch(const Exception& er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
        return tts;
}





void createParmFiles(int subsz, int k, int tsid, int method, string mask, int mCount, int msid, string name, int netid, int popno)
{
        bool isNotWritten = true;
	int mid = 0;
	int tid = 0;
	int meth = 0;
	string markerFileName;
	string traitFileName;
	string featureFileName;
	int numTfile = 0;
	int numMfile = 0;
	int noPops = -1;
	for(int i = 1; i <= k && method != PNK && method != PAA && method != ADL; i += subsz)
	{
		
	        traitFileName = mask + "_t_" + toString(i);
		int end = i + subsz - 1;
		if(end > k)
			end = k;
		
		if((tid=writeTraitFile(i-1, end, mask, traitFileName,method))==-1)
		{
			cerr << "Error in creating files from the database" << endl;
			return;
		}
		numTfile++;

	}
	if(method==ADL || method == 8 || method == 9 || method == 10 || method == 11 || method == 12 || method == PNK || method == 20 || method == PAA)
	{
		markerFileName = mask + "_m";
		//if(method==ADL)
			//writeADLMarkerFile(mask, markerFileName);
		//else
			mid = writeMarkerFile(0, mCount, mask, markerFileName, method);
		
		if(method == PAA)
		{
			markerFileName = mask + "_m2";
			writeMarkerFile(0, mCount, mask, markerFileName, PNK);
			traitFileName = mask + "_t2";
			writeTraitFile(0, k, mask, traitFileName, PNK);
			noPops = writeStructureFile(netid, popno);
		}
		numMfile++;
		traitFileName = mask + "_t";
		tid = writeTraitFile(0, k, mask, traitFileName,method);
		
		if(method == ADL)
		{
			//write feature file
			featureFileName = mask + "_f";
			int success = writeFeatureFile(mask, featureFileName);
		}
	}
	else
	{
		for(int i = 1; i <=mCount; i += subsz)
	  	{
	    		int end = i + subsz-1;
	    		if(end >mCount)
	      			end = mCount;
		        markerFileName = mask + "_m_" + toString(i);
	    		if((mid = writeMarkerFile(i-1, end, mask, markerFileName, method))== -1)
	    		{
	      			cerr << "Error in creating files from database " <<endl;
	      			return;
	    		}
	    		numMfile++;
	  	}
	}

	if(method == 7)
		makeWSRParms(mask, subsz, mCount, k, numTfile, numMfile, msid, tsid, netid, name);
	if(method == 8 || method == 10 || method == 11 || method == 12 || method == 20)
		makeGFLParms(mask, subsz, mCount, k, msid, tsid, netid, name, mid, method);
	if(method == 9)
		makeLASParms(mask, subsz, mCount, k, msid, tsid, netid, name, mid);
	if(method == PNK)
		makePNKParms(mask, subsz, mCount, k, msid, tsid, netid, name, mid);
	if(method == PAA)
		makePAAParms(mask, subsz, mCount, k, msid, tsid, netid, name, mid, noPops);
	if(method == ADL)
		makeADLParms(mask, subsz, mCount, k, msid, tsid, netid, name, mid, method);
}

//string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N, int method
void makeADLParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N, int method)
{

		//connected component
		ofstream secondfile113;
        secondfile113.open("parms1.txt");
		secondfile113 << mask<<"_net.txt "<<.15<<" "<<mask<<endl;//change here
		secondfile113 << "vanilla" << endl;
		secondfile113.close(); 
		
		ofstream scprepparam;
        scprepparam.open("parms2.txt");		
		scprepparam << "scprep.m mask='" << mask << "';scprep" << endl;
		scprepparam << "vanilla" << endl;
		scprepparam.close();
		
		ofstream backEndParam;
        backEndParam.open("parms11.txt");	
		backEndParam << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << j << " " << netid << " " << msid << " " << team << " " <<  db << endl;
		backEndParam << "vanilla" << endl;
		backEndParam.close();
	//ofstream myfile1;
   // myfile1.open("parms1.txt");
	//myfile1<<"run_aml.m trait_filename='"<<mask<<"_t',marker_filename='"<<mask<<"_m',feature_filename='"<<mask<<"_f'" <<";run_aml"<<"\n"<<"vanilla\n";
    /*for(int i=2; i <=totaltrait ; i++)
	{
		myfile1<<"main_function_parallelRunning.m x_fileName='data.test.X',y_fileName='"<< "tr"<<toStrings(i)<<"',z_fileName='data.test.Z',v_filename='data.vocab',out_file_f='outputf"<<toStrings(i)<<"';main_function_parallelRunning"<<"\n";
	}*/
	//myfile1.close();
	//
	//ofstream myfile;
      //  myfile.open("parms2.txt");
		//myfile <<mask<<" "<<name_str<< " " <<team<<" "<< db<<endl;
		//myfile<<"vanilla"<<endl;
      //  myfile.close();
}

void makeGFLParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N, int method)
{
	ofstream myfile;
	myfile.open("parms1.txt");

	string markerFileName = mask+"_m";
	int subsetsz = (int)ceil(N*.9);
	for(int i = 0; i < k; i +=subsz)
	{
		string traitFileName = mask + "_t_" + toString(i+1);
		int noTs = subsz;
		if(i + subsz > k)
			noTs = k - i;
		myfile << "0 0 " << N << " " << subsetsz << " " << j << " " << noTs << " " << markerFileName;
		myfile << " " << traitFileName << " " << mask << "_" << toString(i+1) << endl;

		if(i == 0)
			myfile << "vanilla" << endl;

		mkdir(toString(i+1).c_str(),0777);
	}
	myfile.close();

	ofstream parms15;
	if(method == 20)
		parms15.open("parms14.txt");
	else
		parms15.open("parms16.txt");
	parms15 << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << j << " " << netid << " " << msid << " " << team << " " <<  db << endl;
	parms15 << "vanilla" << endl;
	parms15.close();

	ofstream parms2;
	ofstream parms5;
	ofstream parms3;
	parms2.open("parms2.txt");
	parms5.open("parms5.txt");
	parms3.open("parms3.txt");

	if(method > 10)
	{
		parms2 << "ve2.m mask='";
		parms5 << "ve2.m mask='";
	}
	else
	{
		parms2 << "ve.m mask='";
		parms5 << "ve.m mask='";
	}
	parms2 << mask << "';subsz=" << subsz << ";vss=" << subsetsz;
	parms5 << mask << "';subsz=" << subsz << ";vss=" << subsetsz;
	if(method > 10)
	{
		parms2 << ";ve2" << endl;
		parms5 << ";ve2" << endl;
	}
	else
	{
		parms2 << ";ve" << endl;
		parms5 << ";ve" << endl;
	}

	parms3 << mask << "_net.txt " << 0.15 << " " << mask << endl;
	parms2 << "vanilla" << endl;
	parms5 << "vanilla" << endl;
	parms3 << "vanilla" << endl;

	ofstream parms6;
	ofstream parms7;

	parms6.open("parms6.txt");
	parms7.open("parms7.txt");

	if(method > 10)
	{
		parms6 << "markerprocessing2.m ";
	}
	else
	{
		parms6 << "markerprocessing.m ";
	}
	parms6 << "mask='" << mask << "';";
	if(method > 10)
	{
		parms6 << "markerprocessing2" << endl;
	}
	else
	{
		parms6 << "markerprocessing" << endl;
	}
	parms6 << "vanilla" << endl;

	parms7 << "scprep.m mask='" << mask << "';scprep" << endl;
	parms7 << "vanilla" << endl;

	parms2.close();
	parms3.close();
	parms5.close();
	parms6.close();
	parms7.close();

}

void makePNKParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N)
{
	ofstream myfile;
	myfile.open("parms3.txt");

	ofstream parms1;
	ofstream parms2;

	parms1.open("parms1.txt");
	parms2.open("parms2.txt");

	parms1 << mask << "_m " << mask << ".ped" << endl;
	parms2 << mask << "_t " << mask << ".txt" << endl;

	parms1 << "vanilla" << endl;
	parms2 << "vanilla" << endl;

	parms1.close();
	parms2.close();

	myfile << "--file " << mask << " --assoc --pheno " << mask << ".txt --all-pheno --missing-phenotype -99"<< endl;
	myfile << "standard" << endl;
	myfile.close();
	
        ofstream thirdfile;
	thirdfile.open("parms4.txt");
        thirdfile << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << j << " " << netid << " " << msid << " " << team << " " << db << endl;
        thirdfile << "vanilla" << endl;
        thirdfile.close();
}

void makePAAParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N, int noPops)
{
	ofstream parms1;
	ofstream parms2;

	parms1.open("parms1.txt");
	parms2.open("parms2.txt");
	
	parms1 << mask << "_m2 " << mask << ".ped" << endl;
	parms1 << "vanilla" << endl;
	parms1 << mask << "_t2 " << mask << ".txt" << endl;


	parms1.close();

	parms2 << "--file " << mask << " --assoc --pheno " << mask << ".txt --all-pheno --out pop1 --filter pops.txt 1 --missing-phenotype -99" << endl;
	parms2 << "standard" << endl;
	for(int i = 2; i <= noPops; i ++)
	{
		parms2 << "--file " << mask << " --assoc --pheno " << mask << ".txt --all-pheno --out pop" << i << " --missing-phenotype -99 --filter pops.txt " << i << endl;
	}

	parms2.close();

	ofstream parms3;
	parms3.open("parms3.txt");
	for(int i = 0; i < k; i ++)
	{
		parms3 << "popanalysis.m mask='"  << mask << "';tidx=" << (i+1) << ";popanalysis" << endl;

		if(i == 0)
		{
			parms3 << "vanilla" << endl;
		}
	}
	parms3.close();

	ofstream thirdfile;
	thirdfile.open("parms4.txt");
	thirdfile << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << j << " " << netid << " " << msid << " " << noPops << " " << team << " " << db << endl;
	thirdfile << "vanilla" << endl;
	thirdfile.close();


}

void makeLASParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N)
{
	std::vector<int> folds;
	for(int i=0;i<N;i++)
		folds.push_back(i % 10 + 1);
	std::random_shuffle(folds.begin(), folds.end());

        ofstream myfile;
        myfile.open("parms1.txt");

	cout << "Parameter writing begins...\n";

        string markerFileName = mask+"_m";
        int subsetsz = (int)ceil(N*.2);
        for(int i = 0; i < k; i +=subsz)
        {
		cout << "Starting batch " << i << endl;
		ofstream foldfile;
		std::string foldfileloc = mask+"_"+toString(i+1)+"_"+"folds";
		foldfile.open(foldfileloc.c_str());
		for(int n=0;n<N;n++)
			foldfile << folds[n] << "\n";
		foldfile.close();

                string traitFileName = mask + "_t_" + toString(i+1);
                int noTs = subsz;
                if(i + subsz > k)
                        noTs = k - i;
                myfile << "0 0 " << N << " " << subsetsz << " " << j << " " << noTs << " " << markerFileName;
                myfile << " " << traitFileName << " " << mask << "_" << toString(i+1) << " 1 " << toString(i+1) << " " << endl;

                if(i == 0)
                        myfile << "vanilla" << endl;

		for(int ii=1;ii<noTs;ii++)
		{
			myfile << "0 0 " << N << " " << subsetsz << " " << j << " " << noTs << " " << markerFileName;
                	myfile << " " << traitFileName << " " << mask << "_" << toString(i+1) << " " << (ii+1) << " " << toString(i+1) << " " << endl;
                }

		mkdir(toString(i+1).c_str(),0777);

        }
        myfile.close();

        ofstream parms15;
        parms15.open("parms5.txt");
        parms15 << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << j << " " << netid << " " << msid << " " << team << " " << db << endl;
        parms15 << "vanilla" << endl;
        parms15.close();

        ofstream parms2;
        ofstream parms5;
        parms2.open("parms2.txt");
        parms5.open("parms4.txt");

        parms2 << "velas.m mask='" << mask << "';subsz=" << subsz << ";vss=" << subsetsz << ";writeb=0;velas" << endl;
        parms5 << "velas.m mask='" << mask << "';subsz=" << subsz << ";vss=" << subsetsz << ";writeb=1;velas" << endl;
        parms2 << "vanilla" << endl;
        parms5 << "vanilla" << endl;

        parms2.close();
        parms5.close();
}

void makeWSRParms(string mask, int subsz, int mCount, int k, int numTfile, int numMfile, int msid, int tsid, int netid, string name)
{
	ofstream myfile;
	myfile.open("parms1.txt");

	for(int i = 0; i <numTfile; i++)
	{
	    for(int j = 0; j<numMfile; j++)
	    {
		string traitFileName = mask + "_t_" + toString(subsz*i+1);
		string markerFileName = mask+ "_m_" + toString(subsz*j+1);
		myfile << traitFileName << " " << markerFileName << " "; 
		int msub = 0;
		int tsub = 0;
		if(i*subsz + subsz > k)
			tsub = k - i*subsz ;  //myfile << k-i+1 << " ";
	 	else
			tsub = subsz;	///	myfile << subsz << " ";
		if(j*subsz + subsz > mCount)
		  	msub = mCount - j*subsz ;//myfile << mCount-j+1 << " ";
		else
		  	msub = subsz;//myfile << subsz << " ";
		myfile << tsub << " " << msub << " ";
		myfile << "1 "; 
		myfile << msub << " 1 " << tsub << " " << mask << "_" << (j*subsz+1) << "_" << (i*subsz+1) << " 10 0" << endl;
		if(i == 0 && j == 0)
			  myfile << "standard" << endl;
			
	      }
	  } 
		  
      
      
	
        myfile.close();

	
        ofstream secondfile;
       	secondfile.open("parms2.txt");
        secondfile << mask << " " << mCount << " " << k << " " << subsz << " "  << subsz << endl;
	secondfile << "vanilla" << endl;		
       	secondfile.close();
	ofstream thirdfile;

	thirdfile.open("parms3.txt");
	thirdfile << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << mCount << " " << netid << " " << msid << " " << team << " " << db << endl;
	thirdfile << "vanilla" << endl;
	thirdfile.close();
}

string toString(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();

	while(s.length() < 4)
	{
		s = "0" + s;
	}

	return s;
}
