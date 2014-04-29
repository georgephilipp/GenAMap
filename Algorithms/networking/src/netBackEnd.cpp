#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>

using namespace std;
using namespace mysqlpp;

//argv1 = notraits
//argv2 = ts id
//argv3 = project appID

string toString(int i);
int getTraitIds(int tsid, vector<int>* arr);
void insertNetIntoDB(int tsid, string appID, int k, string type, int subsz);
int getNetId(int tsid, string type);
int insertNetworkValue(int netid, int t1id, int t2id, float val, Query* query, string type, bool needscomma);
double abs(double d);
string db;
string team;

int main(int argc, char* argv[])
{
	int subsz = 1000;//must match netFrontEnd!

	if(argc != 6)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string mask;
	int tsid;
	int k;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1:
				k = atoi(argv[i]);
				cout << k << endl;
				break;
			case 2:
				tsid = atoi(argv[i]);
				cout << tsid << endl;
				break;
			case 3: 
				mask = argv[i];
				cout << mask << endl;
				break;
			case 4:
				team = argv[i];
				cout << team; 
				break;
			case 5:
				db = argv[i];
				cout << db << endl;
				break;
		}
	}

	string meth = mask.substr(0,3);

	insertNetIntoDB(tsid, mask, k, meth, subsz);

	return 0;
}

void insertNetIntoDB(int tsid, string appID, int k, string type, int subsz)
{
	int netID = getNetId(tsid, type);
	cout << "I have this id ... " << netID << endl;
	vector<int> arr;
	if(getTraitIds(tsid,&arr) == 1)
	{
		cout << "And some of these trait ids ... " << endl;
		vector<int>::const_iterator cii;
		for(cii = arr.begin(); cii!=arr.end(); cii++)
		{
			cout << *cii << endl;
		}
	}
	else
	{
		cerr << "Something has gone seriously wrong reading the database ids." << endl;
		return;
	}
	
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
		bool needscomma = false;

		for(int i = 1; i <= k; i+= subsz)
		{
			for(int j=i; j <= k; j += subsz)
			{
				string file = appID + "_" + toString(i) + "_" + toString(j) + "res.txt";
				cout << "reading: " << file << endl;

				ifstream myfile(file.c_str());

		                query.reset();
                	        query << "INSERT INTO networkval" << team << " (trait1, trait2, weight, netid) VALUES";

				int size = 0;
				if(myfile.is_open())
				{
					int idx1 = i;
					while(! myfile.eof())
					{
						string line;
						getline(myfile, line);
						string s;
						int idx2 = j;
						for(int q = 0; q < line.length(); q ++)
						{
	                                	        if(needscomma && idx2 > idx1)
        	                                	{
								if(size > 1000)
								{
									query << ";";
					                                cout << query.str() << endl;
					                                query.execute();
									size = 0;
					                                cout << query.error() << endl;
									query.reset();
					                                query << "INSERT INTO networkval" << team << " (trait1, trait2, weight, netid) VALUES";
									needscomma = false;
								}
								/*else
								{
	                	                                	query << ", ";
								}*/
	                        	                       // needscomma = false;
        	                        	        }

							s += line[q];
							if(line[q] == ' ' || line[q] == '\t' || q + 1 == line.length())
							{
								if(s.length() > 0)
								{
									float f = atof(s.c_str());
									s = "";
									if(idx2 > idx1)	
									{
										int oldsize = size;
										size += insertNetworkValue(netID, arr[idx1-1], arr[idx2-1],  f, &query, type, needscomma);
										if(oldsize != size)
											needscomma = true;
									}
									idx2 ++;
								}
							}
						}
						idx1 ++;
					}
				}
				else
				{
					cerr << "Was not able to create a file! " << file << endl;
					return;	
				}
				if(size > 0)
				{
		                        query << ";";
        	        	        cout << query.str() << endl;
	        	                query.execute();
					size = 0;
        	        	        cout << query.error() << endl;
					needscomma = false;
				}
				else
					cout << "size is bad." << endl;
			}
		}
		query.reset();
		query << "UPDATE network SET loadcmpt = 1 WHERE id = " << netID << ";" << endl;
		query.execute();
		
	}
        catch(BadQuery er)
        {
                cerr << "Error: " << er.what() << endl;
        }
        catch(const BadConversion& er)
        {
                cerr << "Conversion error: " << er.what() << endl;
        }
        catch(const Exception& er)
        {
                cerr << "Error: " << er.what() << endl;
        }

}

int insertNetworkValue(int netid, int t1id, int t2id, float val, Query* query, string type, bool needscomma)
{
	bool toinsert = false;

	if((type == "CR1" && abs(val) > .50) ||
	   (type == "CR2" && abs(val) > .50) ||
	   (type == "SFN" && abs(val) > .15) ||
	   (type == "TOM" && abs(val) > .05))
	{
		//cout <<" it should work:" << endl;
		toinsert = true;
	}
	else
	{
		//cout << abs(val) << endl;
		//cout << type << endl;
	}

	if(toinsert)
	{
		//query.reset();
		if(needscomma)
			*query << ", ";

        	*query << "(" ;
		//*query << "INSERT INTO networkval (trait1, trait2, weight, netid) VALUES(";

		*query << t1id << "," << t2id << "," << val << "," << netid << ")";
	        //query.execute();
//		cout << query.error() << endl;
		return 1;
	}
	return 0;
}

int getNetId(int tsid, string type)
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

                query << "SELECT createNetwork(" << tsid << ",'" << type << "') as id";
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

int getTraitIds(int tsid, vector<int> * arr)
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

                query << "SELECT id FROM trait WHERE traitsetid = " << tsid << " ORDER BY idx";
                StoreQueryResult ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
			for(size_t i = 0; i < ares.num_rows(); i ++)
			{
				arr->push_back(ares[i]["id"]);
			}
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
	return 1;
}


string toString(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();
	return s;
}

double abs(double d)
{
	if(d < 0)
		return -1 * d;
	return d;
}
