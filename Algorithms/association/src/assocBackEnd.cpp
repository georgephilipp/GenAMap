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
string toStrings(int i);
int getTraitIds(int tsid, vector<int>* arr);
int getMarkerIds(int msid, vector<int>* arr);
void insertNetIntoDB(int tsid, string appID, int k, float thresh, int subsz, string name, string meth, int msid, int mCount, int netid, int szidx, int* lidx);
int getNetId(int tsid, int msid, float thresh, string name, int netid, string type);
int insertNetworkValue(int netid, int tid, int mid, float val, Query* query, bool needscomma, string type);
double abs(double d);
string db;
string team;

int main(int argc, char* argv[])
{
	int subsz = 250;//must match frontEnd!

	if(argc != 11)
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
		case 9: team = argv[i]; break;
		case 10:
			db = argv[i];
			cout << db << endl;
			break;
		}
	}

	string meth = mask.substr(0,3);

	//read in index file
	int idx[100000];
	for(int i=0; i < 100000; i ++)
		idx[i] = -1;

	string fi = mask + "_midx";
	ifstream myfile(fi.c_str());
	if(myfile.is_open())
	{
		string line;
		getline(myfile,line);
		char * pch;
		char * tmp = strdup(line.c_str());
		pch = strtok(tmp," ");
		int l = 0;
		while(pch != NULL)
		{
			idx[l++] = (int) (atof(pch)+.1);
			cout << pch << "\t" << idx[l-1] << endl;
			pch = strtok(NULL, " ");
		}
	}
	int szidx;

	if(idx[0] == -1)
	{
		for(int i = 0; i < mCount; i ++)
		{
			idx[i] = i+1;	
		}
		szidx = mCount;
		//cerr << "Index file contained no values!" << endl;
	}
	else
	{
		for(int i = 0; i < 100000; i ++)
		{
			if(idx[i] == -1)
			{
				cout << "Found " << i << " index values. " << endl;
				szidx = i;
				break;
			}
		}
	}

	FILE* threshFile = fopen(thresh.c_str(), "rt");
	if(threshFile)
	{
		char threshval[100];
		fgets(threshval, 100, threshFile);
		threshVal = atof(threshval);
		cout << threshVal << endl;
	}
	else
	{
		threshVal = .05;
	}
	insertNetIntoDB(tsid, mask, k, threshVal, subsz, name, meth, msid, mCount,netID, szidx, idx);

	return 0;
}

void insertNetIntoDB(int tsid, string appID, int k, float thresh, int subsz, string name, string meth, int msid, int J, int netID, int szidx, int* lidx)
{
        int assocID = getNetId(tsid, msid, thresh, name,netID, meth);
	cout << "I have this id ... " << assocID << endl;	
	vector<int> arr;
	vector<int> arr_m;
	if(getTraitIds(tsid,&arr) == 1 && getMarkerIds(msid,&arr_m) == 1)
	{
		cout << "And some of these trait ids ... " << endl;
		vector<int>::const_iterator cii;
		for(cii = arr.begin(); cii!=arr.end(); cii++)
		{
			cout << *cii << endl;
		}

		cout << "And some marker ids ... " << endl;
		for(cii = arr_m.begin(); cii != arr_m.end(); cii++)
		{
			cout << *cii << endl;
		}	
		cout << arr_m.size();

		vector<int> arr_m1;
		//copy(arr_m.begin(), arr_m.end(), arr_m1.begin()); 
		for(int q = 0; q < szidx && q < arr_m.size(); q ++)
		{
			arr_m1.push_back(arr_m[lidx[q]-1]);
			//cout << arr_m[lidx[q]-1];
		}

		arr_m = arr_m1;
		cout << arr_m.size();
		J = arr_m.size();
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
		if(meth != "PNK")
		{
			for(int j = 1; j <= J; j+= subsz)
			{
				for(int i=1; i <= k; i += subsz)
				{
					string file = appID + "_" + toStrings(j) + "_" + toStrings(i) + "res.txt";
					cout << "reading: " << file << endl;
	
					ifstream myfile(file.c_str());
	
			                query.reset();
                		        query << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid) VALUES";
	
					int size = 0;
					if(myfile.is_open())
					{
						int idx1 = j;
						while(! myfile.eof())
						{
							string line;
							getline(myfile, line);
							string s;
							int idx2 = i;
							for(int q = 0; q < line.length(); q ++)
							{
	                                	        	if(needscomma)
	        	                                	{
									if(size > 1000)
									{
										query << ";";
						                                cout << query.str() << endl;
						                                query.execute();
										size = 0;
					                	                cout << query.error() << endl;
										query.reset();
										   query << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid) VALUES";
										needscomma = false;
									}
        	                        	        	}
	
								s += line[q];
								if(line[q] == ' ' || line[q] == '\t' || q + 1 == line.length())
								{
									if(s.length() > 0)
									{
										float f = atof(s.c_str());
										//s = "";
										int oldsize = size;
										size += insertNetworkValue(assocID, arr[idx2-1], arr_m[idx1-1],  f, &query, needscomma, meth);
										//if(i+250 > k || j + 250 > J)
										//cout << idx1 << " " << idx2 << endl;
										if(oldsize != size)
											needscomma = true;
										
	
										//cout << idx2 << endl;
										if(s.length() > 1) idx2 ++;
										s = "";
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
					{
						cout << "nothing to report." << endl;
						needscomma = false;
					}
				}	
			}
		}
		else
		{
			for(int i=1; i <= k; i ++)
                        {
				bool isassocfile = false;
                                string file = "plink.P" + toStrings(i) + ".qassoc";
                                cout << "reading: " << file << endl;

                                ifstream myfile(file.c_str());

                                query.reset();
                                query << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid) VALUES";

                                int size = 0;
				string line2;
				if(myfile.is_open())
					getline(myfile,line2);
				else
				{
					file = "plink.P" + toStrings(i) + ".assoc";
					myfile.open(file.c_str());
					if(myfile.is_open())
					{
						isassocfile = true;	
						getline(myfile, line2);
					}
				}

                                if(myfile.is_open())
                                {
                                        int idx1 = 1;
                                        while(! myfile.eof())
                                        {
                                                string line;
                                                getline(myfile, line);
						bool ischeck = isassocfile;
						bool canreset = false;
						if(line.length() == 0)
							continue;	
                                                string s;

						for(int q = line.length()-1; q > 0; q--)
						{
							if((line[q] == ' ' || line[q] == '\t') &&
								q < line.length()-4)
							{
								if(ischeck)
								{
									canreset = true;
									s = "";
								}
								else
									q = -1;
							}
							else
							{
								s = line[q] + s;
								if(canreset)
									ischeck = false;
							}
						}

                                                if(needscomma)
                                                {
                                                        if(size > 1000)
                                                        {
                                                               query << ";";
                                                               cout << query.str() << endl;
                                                               query.execute();
                                                               size = 0;
                                                               cout << query.error() << endl;
                                                               query.reset();
                                                               query << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid) VALUES";
                                                               needscomma = false;
                                                         }
                                                }
						size_t found;
						found = s.find("NA");
						if(found != string::npos)
						{
							//cout << "success" << endl;
						}
						else
						{
							float f = atof(s.c_str());
        	                                        int oldsize = size;
                	                                size += insertNetworkValue(assocID, arr[i-1], arr_m[idx1-1],  f, &query, needscomma, meth);
                        	                        if(oldsize != size)
                                	                       needscomma = true;
                                        	        s = "";
						}
                                                idx1++;
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
	                        {
        	                         cout << "nothing to report." << endl;
                	                 needscomma = false;
                        	}
                        }
		}	
      	        query.reset();
               	query << "UPDATE assocset SET loadcmpt = 1 WHERE id = " << assocID << ";" << endl;
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

int insertNetworkValue(int associd, int tid, int mid, float val, Query* query, bool needscomma, string type)
{
	bool toinsert = false;

	if((type == "WSR" || type == "PNK") && abs(val) < 1e-3)
	{
		//cout <<" it should work:" << endl;
		toinsert = true;
	}
	else if((type == "GFL" || type == "LAS" || type == "GCL" || type == "GC2" || type == "GF2" || type == "TLS" || type == "ADL") && abs(val) > 1e-4)
	{
		//cout << abs(val) << endl;
		//cout << type << endl;
		toinsert = true;
	}

	if(toinsert)
	{
		//query.reset();
		if(needscomma)
			*query << ", ";

        	*query << "(" ;
		//*query << "INSERT INTO networkval (trait1, trait2, weight, netid) VALUES(";

		*query << mid << "," << tid << "," << val << "," << associd << ")";
	        //query.execute();
//		cout << query.error() << endl;
		return 1;
	}
	return 0;
}
// Add netID to arguments, change handling of thresh to be read from a file
int getNetId(int tsid, int msid, float thresh, string name, int netID, string type)
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
		
		bool pval = (type == "WSR" || type == "PNK");

		if(netID!= 0)
  		{
			query << "SELECT createAssoc(" << tsid << "," <<msid  <<"," <<thresh << ",'" << name << "'," << netID << ", " << pval << " ) as id";
			cout << query << endl;
		}
		else
		{
			query << "SELECT createAssoc_nonet(" << tsid <<"," <<msid<<"," <<thresh << ",'" << name << "', " << pval << ") as id";
			cout << query << endl;
		}
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

                query << "SELECT id FROM marker WHERE markersetid = " << msid << " ORDER BY chr, locus";
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
