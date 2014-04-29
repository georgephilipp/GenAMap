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
void insertGTassoc(int gsid, int tsid, int gsnet, int tsnet, int associd, string name, int* lidx, int* genes);
int getNetId(int gsid, int tsid, int net1, int net2, int assoc, string name);
int insertNetworkValue(int netid, int tid, int mid, float val, Query* query, bool needscomma, string type);
double abs(double d);
string db;
string team;

int main(int argc, char* argv[])
{
	if(argc != 8)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string name;//the name of the assocset
	int gsid;//the gene set id
	int tsid;//the trait set id
	int netid1;//the gene network
	int netid2;//the trait network
	int associd;//the genome-gene assoc id
	db = "geneassoc";
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1:
				name = argv[i];
				cout << name << endl;
				break;
			case 2:
				gsid = atoi(argv[i]);
				cout << gsid << endl;
				break;
			case 3: 
			        tsid = atoi(argv[i]);
				cout << tsid << endl;
				break;
	              	case 4:
				netid1 = atoi(argv[i]);
				cout << netid1 << endl;
				break;
	   		case 5:
				netid2 = atoi(argv[i]);
				cout << netid2 << endl;
		  		break;
			case 7: team = argv[i]; break;
			case 6:
		  		associd = atoi(argv[i]);
			  	cout << associd << endl;
		  		break;
		}
	}

	//read in index file
	int idx[100000];
	for(int i=0; i < 100000; i ++)
		idx[i] = -1;

	string fi = "traitidx.txt";
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
	else
	{
		cerr << "This job has been corrupted!!!" << endl;
		return 0;
	}
	int numGenes = 0;
	for(int i = 0; i < 100000; i ++)
	{
		if(idx[i] == -1)
		{
			cout << "Found " << i << " index values. " << endl;
			numGenes = i;
			break;
		}
	}
	fi = "groups.txt";
	int genes[numGenes];
	ifstream mygenes(fi.c_str());
	if(mygenes.is_open())
	{
		string line;
		getline(mygenes, line);
		char * pch;
		char * tmp = strdup(line.c_str());
		pch = strtok(tmp, " ");
		int l = 0;
		while(pch != NULL)
		{
			genes[l++] = (int) (atof(pch)+.1);
			cout << pch << "\t" << genes[l-1] << endl;
			pch = strtok(NULL, " ");
		}
	}
	else
	{
		cerr << "This job has been corrupted!!! No gene file." << endl;
		return 0;
	}
	insertGTassoc(gsid, tsid, netid1, netid2, associd, name, idx, genes);
	return 0;
}

void insertGTassoc(int gsid, int tsid, int gsnet, int tsnet, int associd, string name, int* lidx, int* grps)
{
        int assocID = getNetId(gsid, tsid, gsnet, tsnet, associd, name);
	cout << "I have this id ... " << assocID << endl;	
	vector<int> arr_g;
	vector<int> arr_gsub;
	vector<int> arr_t;
	if(getTraitIds(tsid,&arr_t) == 1 && getTraitIds(gsid,&arr_g) == 1)
	{
		cout << "And some of these trait ids ... " << endl;
		vector<int>::const_iterator cii;
		for(cii = arr_t.begin(); cii!=arr_t.end(); cii++)
		{
			cout << *cii << endl;
		}

		cout << "And some gene ids ... " << endl;
		for(int i = 0; i < 100000 && lidx[i] != -1; i ++)
		{
			arr_gsub.push_back(arr_g[lidx[i]-1]);
		}
		for(cii = arr_gsub.begin(); cii != arr_gsub.end(); cii++)
		{
			cout << *cii << endl;
		}
		cout << arr_gsub.size();
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

		for(int i = 0; i < arr_gsub.size(); i ++)
		{
			query.reset();
			query << "INSERT INTO genegroups (geneid, grp, gtassocsetid) VALUES (" << arr_gsub[i] << "," << grps[i] << "," << assocID << ")";
			query.execute();
		}

		bool needscomma = false;

		ifstream myres("betas.txt");
		int r = -1;
		query.reset();
		query << "INSERT INTO genetraitassociation" << team << " (geneid, traitid, gtassocsetid, value) VALUES ";
        	while(myres.is_open() && !myres.eof())
        	{
                	string line;
			r++;
                	getline(myres, line);
                	char * pch;
                	char * tmp = strdup(line.c_str());
                	pch = strtok(tmp, " ");
                	int l = 0;
                	while(pch != NULL)
                	{
				float val = atof(pch);
				if(abs(val)>1e-5)
				{
					if(needscomma) query << ",";
					needscomma = true;
					query << "(" << arr_gsub[r] << "," << arr_t[l] << "," << assocID << "," << val << ")";
				}
				l++;
                        	pch = strtok(NULL, " ");
                	}
        	}
		cout << query.str() << endl;
		query.execute();
		cerr << query.error();

/*		if(meth != "PNK")
						                                cout << query.str() << endl;
						                                query.execute();
										size = 0;
					                	                cout << query.error() << endl;
										query.reset();
										   query << "INSERT INTO association (markerid, traitid, value, assocsetid) VALUES";
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
                                query << "INSERT INTO association (markerid, traitid, value, assocsetid) VALUES";

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
                                                               query << "INSERT INTO association (markerid, traitid, value, assocsetid) VALUES";
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
*/
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

// Add netID to arguments, change handling of thresh to be read from a file
int getNetId(int gsid, int tsid, int netid1, int netid2, int associd, string name)
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
		
		query << "SELECT createGTassoc(" << gsid << "," << tsid << "," << netid1 << "," << netid2 << "," << associd << ",\'" << name << "\') as id";
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
