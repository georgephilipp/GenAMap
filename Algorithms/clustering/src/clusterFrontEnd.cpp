#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <math.h>
#include <cstdlib>


using namespace std;
using namespace mysqlpp;

void createParmFiles(int k, string mask, string name, int x, int tsid);
string toString(int i);
int getK(int tsid);
int gettsid(string mask);
int writeNetFile(string appID, int netID, int k);
string db;
string team;

int main(int argc, char* argv[])
{
	if(argc != 6)
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
			case 4: team = argv[i]; break;
			case 5: db = argv[i];
				break;
		}
	}

	string meth = mask.substr(0,3);
	int tsid = gettsid(mask);
	k = getK(tsid);
	createParmFiles(k, mask, name, netid, tsid);
	writeNetFile(mask, netid, k);
	return 0;
}

int writeNetFile(string appID, int netID, int k)
{
	double network[k][k];
	for(int i = 0; i < k; i ++)
		for(int j = 0; j < k; j ++)
			network[i][j] = 0.0;
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
		query << "SELECT idx, weight FROM networkval" << team << ", trait where netid  = " << netID << " AND (trait.id = trait1 OR trait.id = trait2)";
		StoreQueryResult ares = query.store();
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}

		int idx1 = 0;
		for(size_t i = 0; i < ares.num_rows(); i++)
		{
			if(i%2 == 0)
			{
				idx1 = ares[i]["idx"];
			}
			else
			{
				int idx2 = ares[i]["idx"];
				double val = ares[i]["weight"];
				network[idx1][idx2] = val;
				network[idx2][idx1] = val;
			}
		}
		ofstream myfile;
		myfile.open((appID + "_net.txt").c_str());

		for(int i = 0; i < k; i ++)
		{
			for(int j = 0; j < k ; j++)
			{
				myfile << network[i][j];
				if(j != k -1)
					myfile << "\t";
			}
			myfile << endl;
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

void createParmFiles(int k, string mask, string name, int netid, int tsid)
{
        bool isNotWritten = true;
	ofstream parms1;
	ofstream parms2;
	parms1.open("parms1.txt");
	parms2.open("parms2.txt");

	parms1 << mask << "_net.txt " << k << endl;
	parms1 << "standard" << endl;
	parms1.close();

	parms2 << mask << " " << netid << " " << tsid  << " " << name << " " << db << endl;
	parms2 << "vanilla" << endl;
	parms2.close();
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
