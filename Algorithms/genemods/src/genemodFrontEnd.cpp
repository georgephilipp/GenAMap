#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cstdlib>
#include <sstream>
#include <mysql++.h>
#include <map>

using namespace std;
using namespace mysqlpp;

void createParmFiles(string mask, int netid, int associd, string goanno, int clusterid);
string toString(int i);
int writeNetworkFile(int netid, string mask, vector<int> cluster);
int writeAssocFile(int associd, string mask, vector<int> cluster);
void getclustering(int clusterid, vector<int>* v);
string db;
string name;
string team;
int main(int argc, char* argv[])
{
	if(argc != 8)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string mask;
	int netid;
	int associd;
	int clusterid;
	string goanno;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				mask = argv[i];
				cout << mask << endl;
				break;
			case 2:
				netid = atoi(argv[i]);
				break;
			case 3:
				associd = atoi(argv[i]);
				break;
			case 4:
				clusterid = atoi(argv[i]);
				break;
			case 5:
				goanno = argv[i];
				break;
			case 6: team = argv[i]; break;
			case 7:
				db = argv[i];
				cout << db << endl;
		}
	}

	std::string copyFileName = "./../exe/copyFilesGeneMod.sh";
	std::system(copyFileName.c_str());

	vector<int> clustering;
	getclustering(clusterid, &clustering);
	if(clustering.size() == 0)
	{
		cerr << "Cannot parse clustering for : " << clusterid << endl;
		return 0;
	}
	//for(int i = 0; i < clustering.size(); i ++)
	//{
	//	cout << clustering[i] << endl;
	//}
	writeNetworkFile(netid, mask, clustering);
	writeAssocFile(associd, mask, clustering);
	createParmFiles(mask, netid, associd, goanno, clusterid);

	return 0;
}

void createParmFiles(string mask, int netid, int associd, string goanno, int clusterid)
{
	ofstream parms1;
	ofstream parms2;
	ofstream parms3;

	parms1.open("parms1.txt");
	parms2.open("parms2.txt");
	parms3.open("parms3.txt");

	parms1 << "find20mods.m net=load(\'" << mask << "_net\');find20mods" << endl;
	parms1 << "vanilla" << endl;

	parms2 << 0 << " " << mask << "_assoc " << mask << "_mkey" << endl;
	parms2 << "vanilla" << endl;
	for(int i = 1; i < 20; i ++)
	{
		parms2 << i << " " << mask << "_assoc " << mask << "_mkey" << endl;
	}

	parms3 << mask << " " << netid << " " << associd << " " << clusterid << " " << goanno << " " << team << endl;
	parms3 << "vanilla" << endl;

	parms1.close();
	parms2.close();
	parms3.close();
}

int writeAssocFile(int associd, string mask, vector<int> clustering)
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
                StoreQueryResult ares;

                query << "SELECT tsid FROM assocset WHERE id = " << associd;
                ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                int tsid = 0;
                for(size_t i = 0; i < ares.num_rows(); i ++)
                {
                        tsid = ares[i]["tsid"];
                }

                query.reset();

                query << "SELECT id, idx FROM trait WHERE traitsetid = " << tsid;
                ares = query.store();
                map<int, int> traitids;
                for(size_t i = 0; i < ares.num_rows(); i ++)
                {
                        int tid = ares[i]["id"];
                        int idx = ares[i]["idx"];

                        traitids[tid] = idx;
                }
                query.reset();

		query << "SELECT msid, ispval, thresh FROM assocset WHERE id = " << associd;
		ares = query.store();
		bool ispval;
		float thresh;
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}
		int msid = 0;
		for(size_t i = 0; i < ares.num_rows(); i ++)
		{
			msid = ares[i]["msid"];
			ispval = ares[i]["ispval"];
			thresh = ares[i]["thresh"];
		}
		query.reset();
		query << "SELECT id FROM marker WHERE markersetid = " << msid << " order by idx";
		ares = query.store();
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}
		vector<int> msids;
		for(size_t i = 0; i < ares.num_rows(); i ++)
		{
			msids.push_back(ares[i]["id"]);
		}
		
		query.reset();

                query << "SELECT markerid, traitid, value FROM association" << team << " WHERE assocsetid = " << associd;
                int sz = clustering.size();
		int szx = msids.size();
                double mat[szx][sz];
                for(int i = 0; i < szx; i ++)
                {
                        for(int j = 0; j < sz; j ++)
                        {
                                mat[i][j] = 0;
                        }
                }
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
                                int m1 = ares[i]["markerid"];
                                int t2 = ares[i]["traitid"];
                                float w = ares[i]["value"];

                                int ix = traitids.find(t2)->second;
                                int idx1 = -1;
                                for(int k = 0; k < clustering.size(); k++)
                                {
                                        if(clustering[k] == ix +1)
                                        {
                                                idx1 = k;
                                        }
                                }
				int idx2 = -1;
				for(int k = 0; k < msids.size(); k ++)
				{
					if(msids[k] == m1)
					{
						idx2 = k;
						break;
					}
				}
                                //cout << t1 << " " << idx << " " << clustering[idx2] << " " << idx2 << endl;
                                if(idx1 == -1 || idx2 == -1)
                                {
                                        cerr << "serious problems with this applicatione xist!" << endl;
                                }
				if((ispval && w < thresh) || (!ispval && w > thresh))
				{
					if(w == 0)
						w = 1e-25;
                                	mat[idx2][idx1-1] = w;
				}
                        }
                        ofstream myfile;
                        string name = mask + "_assoc";
                        myfile.open(name.c_str());
                        for(int i = 0; i < szx; i ++)
                        {
                                for(int j = 0; j < sz; j ++)
                                {
                                        myfile << mat[i][j] << " ";
                                }
                                myfile << endl;
                        }
                        myfile.close();
			ofstream myfile2;
			string nmae = mask + "_mkey";
			myfile2.open(nmae.c_str());
			for(int i = 0; i < szx; i ++)
			{
				myfile2 << msids[i] << endl;
			}
			myfile2.close();
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

void StringSplit(string str, string delim, vector<int>* results)
{
	int cutAt;
	while((cutAt = str.find_first_of(delim)) != str.npos)
	{
		if(cutAt > 0)
		{
			results->push_back(atoi((str.substr(0,cutAt).c_str())));
		}
		str = str.substr(cutAt + 1);
		//cout << str << endl;
	}
	if(str.length()>0)
	{
		results->push_back(atoi(str.c_str()));
	}
}

void getclustering(int clusterid, vector<int> * v)
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

		query << "SELECT value FROM cluster WHERE id = " << clusterid << ";";
		StoreQueryResult ares = query.store();

		int id;
		if(!ares)
		{
			cerr << query.error() << endl;
			return;
		}
		string cluster;
		for(size_t i=0; i < ares.num_rows(); i ++)
		{
			//cout << ares[i]["value"];
			ares[i]["value"].to_string(cluster);
			//cout << cluster << endl;
		}

		query.reset();
		StringSplit(cluster, ",", v);

		//for(int i = 0; i < v->size(); i ++)
		//{
		//	cout << v->get(i) << endl;
		//}
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

int writeNetworkFile(int netid, string mask, vector<int> clustering)
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
		StoreQueryResult ares;

		query << "SELECT ts FROM network WHERE id = " << netid;
		ares = query.store();
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}
		int tsid = 0;
		for(size_t i = 0; i < ares.num_rows(); i ++)
		{
			tsid = ares[i]["ts"];
		}

		query.reset();

		query << "SELECT id, idx FROM trait WHERE traitsetid = " << tsid;
		ares = query.store();
		map<int, int> traitids;
		for(size_t i = 0; i < ares.num_rows(); i ++)
		{
			int tid = ares[i]["id"];
			int idx = ares[i]["idx"];

			traitids[tid] = idx;
		}
		query.reset();
                query << "SELECT trait1, trait2, weight FROM networkval" << team << " WHERE netid = " << netid;
		int sz = clustering.size();
		double mat[sz][sz];
		for(int i = 0; i < sz; i ++)
		{
			for(int j = 0; j < sz; j ++)
			{
				mat[i][j] = 0;
			}
		}
                ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
			//ofstream myfile;
			//myfile.open(filename.c_str());
			for(size_t i = 0; i < ares.num_rows(); i ++)
			{
				int t1 = ares[i]["trait1"];
				int t2 = ares[i]["trait2"];
				float w = ares[i]["weight"];

				int idx = traitids.find(t1)->second;
				int ix = traitids.find(t2)->second;
				int idx1 = -1;
				int idx2 = -1;
				for(int k = 0; k < clustering.size(); k++)
				{
					if(clustering[k] == idx+1)
					{
						idx2 = k;
					}
					if(clustering[k] == ix +1)
					{
						idx1 = k;
					}
				}
				//cout << t1 << " " << idx << " " << clustering[idx2] << " " << idx2 << endl;
				if(idx1 == -1 || idx2 == -1)
				{
					cerr << "serious problems with this applicatione xist!" << endl;
				}
				mat[idx1-1][idx2-1] = w;
				mat[idx2-1][idx1-1] = w;
			//		myfile << ares2[j]["value"] << "\t";
			}
			ofstream myfile;
			string name = mask + "_net";
			myfile.open(name.c_str());
			for(int i = 0; i < sz; i ++)
			{
				for(int j = 0; j < sz; j ++)
				{
					myfile << mat[i][j] << " ";
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
