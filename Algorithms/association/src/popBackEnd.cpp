#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>

using namespace std;
using namespace mysqlpp;

string toString(int i);
string toStrings(int i);
int getTraitIds(int tsid, vector<int>* arr);
int getMarkerIds(int msid, vector<int>* arr);
void insertAssocsIntoDB(int tsid, string appID, int k, string name, int msid, int J, int popID, int noPops);
int getAssocId(int tsid, int msid, float thresh, string name, int netid, int noPops, bool pval);
double abs(double d);
string db;
string team;

int main(int argc, char* argv[])
{
	if(argc != 12)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string mask;
	int tsid, msid;
	int k;
	string thresh;
	float threshVal;
	string name;
	int mCount;
	int netID;
	int noPops;
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
			  thresh = argv[i];
			  cout << thresh << endl;
			  break;
	   	case 5:
		  name = argv[i];
		  cout << name << endl;
		  break;
		case 6:
		  mCount = atoi(argv[i]);
		  cout << mCount << endl;
		  break;
		case 7:
		  netID = atoi(argv[i]);
		  cout << netID << endl;
		  break;
		case 8:
			msid = atoi(argv[i]);
			cout << msid << endl;
			break;
		case 9:
			noPops = atoi(argv[i]);
			cout << noPops << endl;
			break;
		case 10: team = argv[i]; break;
		case 11:
			db = argv[i];
			cout << db << endl;
			break;
		}
	}

	string meth = mask.substr(0,3);

	insertAssocsIntoDB(tsid, mask, k, name, msid, mCount,netID,noPops);

	return 0;
}

void insertAssocsIntoDB(int tsid, string appID, int k, string name, int msid, int J, int popID, int noPops)
{
        int assocIDcv = getAssocId(tsid, msid, 0, name + "_crossValidation", popID, noPops, false);
	int assocIDpl = getAssocId(tsid, msid, 1, name + "_PLINK", popID, noPops, true);
	int assocIDx2 = getAssocId(tsid, msid, 1, name + "_likelihoodTest", popID, noPops, true);
	int assocIDtt = getAssocId(tsid, msid, 1, name + "_tTest", popID, noPops, true);

	cout << "I have this id ... " << assocIDcv << endl;	
	vector<int> arr;
	vector<int> arr_m;
	if(getTraitIds(tsid,&arr) == 1 && getMarkerIds(msid,&arr_m) == 1)
	{
		cout << "And some of these trait ids ... " << endl;
		vector<int>::const_iterator cii;
		//for(cii = arr.begin(); cii!=arr.end(); cii++)
		//{
		//	cout << *cii << endl;
		//}

		cout << "And some marker ids ... " << endl;
		//for(cii = arr_m.begin(); cii != arr_m.end(); cii++)
		//{
		//	cout << *cii << endl;
		//}
		cout << arr_m.size();
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
                Query querycv = conn.query();
		Query querypl = conn.query();
		Query querytt = conn.query();
		Query queryx2 = conn.query();
		bool needscomma = false;
		int nocols = noPops*4;
		for(int i = 1; i <= k; i ++)
		{
			string file = "results" + toStrings(i) + ".txt";
			cout << "reading: " << file << endl;
	
			ifstream myfile(file.c_str());
	
			int tid = arr[i-1];
			querycv.reset();
			querypl.reset();
			querytt.reset();
			queryx2.reset();
                	querycv << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid, popref) VALUES";
                        querypl << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid, popref) VALUES";
                        querytt << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid, popref) VALUES";
                        queryx2 << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid, popref) VALUES";
			bool needscommacv = false;
			bool needscommapl = false;
			bool needscommatt = false;
			bool needscommax2 = false;
			bool isXTcv = false;
			bool isXTpl = false;
			bool isXTtt = false;
			bool isXTx2 = false;

			if(myfile.is_open())
			{
				int idx1 = 1;
				while(! myfile.eof())
				{
					int markerid = arr_m[idx1-1];
					string line;
					getline(myfile, line);
					string s;
					int idx2 = 0;
					for(int q = 0; q < line.length(); q ++)
					{
						if(q+1 == line.length())
							s += line[q];
						if(line[q] == ' ' || line[q] == '\t' || q + 1 == line.length())
						{
							if(s.length() > 0)
							{
								float f = atof(s.c_str());
								int popul = idx2 + 1;
								if(popul <= noPops && f > .2)
								{
									if(needscommacv)
										querycv << ",";
									querycv << "(" << markerid << "," << tid << "," << f << "," << assocIDcv << "," << popul << ")";
									needscommacv = true;
									isXTcv = true;
								}
								popul = popul - noPops;
								if(popul > 0 && popul <= noPops && f < 1e-4)
								{
									if(needscommatt)
										querytt << ",";
									querytt << "(" << markerid << "," << tid << "," << f << "," << assocIDtt << "," << popul << ")";
									needscommatt = true;
									isXTtt = true;
								}
								popul = popul - noPops;
								if(popul > 0 && popul <= noPops && f < 1e-4)
								{
									if(needscommax2)
                                                                        	queryx2 << ",";
                                                                        queryx2 << "(" << markerid << "," << tid << "," << f << "," << assocIDx2 << "," << popul << ")";
                                                                        needscommax2 = true;
									isXTx2 = true;
                                                                 }
								 popul = popul - noPops;
								if(popul > 0 && popul <= noPops && f < 1e-4)
								{
									if(needscommapl)
										querypl << ",";
									querypl << "(" << markerid << "," << tid << "," << f << "," << assocIDpl << "," << popul << ")";
									needscommapl = true;
									isXTpl = true;
								}
								idx2 ++;
								s = "";
							}
						}
						else s+= line[q];
					}
					idx1 ++;	
				}
			}
			else
			{
				cerr << "Was not able to create a file! " << file << endl;
				return;		
			}
		      	querycv << ";";
			if(isXTcv)
			{
	       	      		cout << querycv.str() << endl;
	      	        	querycv.execute();
        			cerr << querycv.error() << endl;
			}
			if(isXTx2)
			{
                        	queryx2 << ";";
                        	cout << queryx2.str() << endl;
                       	 	queryx2.execute();
                        	cerr << queryx2.error() << endl;
			}
			if(isXTtt)
			{
                        	querytt << ";";
                        	cout << querytt.str() << endl;
                        	querytt.execute();
                        	cerr << querytt.error() << endl;
			}
			if(isXTpl)
			{
                        	querypl << ";";
                        	cout << querypl.str() << endl;
                        	querypl.execute();
                        	cerr << querypl.error() << endl;
			}
		}
      	        querycv.reset();
		querypl.reset();
		queryx2.reset();
		querytt.reset();
               	querycv << "UPDATE assocset SET loadcmpt = 1 WHERE id = " << assocIDcv << ";" << endl;
		querypl << "UPDATE assocset SET loadcmpt = 1 WHERE id = " << assocIDpl << ";" << endl;
		queryx2 << "UPDATE assocset SET loadcmpt = 1 WHERE id = " << assocIDtt << ";" << endl;
		querytt << "UPDATE assocset SET loadcmpt = 1 WHERE id = " << assocIDx2 << ";" << endl;
                querycv.execute();
		querypl.execute();
		querytt.execute();
		queryx2.execute();
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

int getAssocId(int tsid, int msid, float thresh, string name, int netID, int noPops, bool pval)
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
		
		query << "SELECT createAssoc_nonet(" << tsid <<"," <<msid<<"," << thresh << ",'" << name << "', " << pval << ") as id";
		cout << query << endl;
		                
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

		query.reset();
		query << "UPDATE assocset SET popid="<<netID<<", popnum="<<noPops<<" where id=" << id;
		cout << query.str() << endl;
		query.execute();
		cerr << query.error() << endl;
		query.reset();
		return id;
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
		//conn.connect("geneassoc", "localhost", "assocmap", "Thisisadumbpassword*");
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

int getMarkerIds(int msid, vector<int> * arr)
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
		//conn.connect("geneassoc", "localhost", "assocmap", "Thisisadumbpassword*");
                Query query = conn.query();

                query << "SELECT id FROM marker WHERE markersetid = " << msid << " ORDER BY idx";
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

	while(s.length() < 4)
	{
		s = "0" + s;
	}

	return s;
}

string toStrings(int i)
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
