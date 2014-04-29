#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include <mysql++.h>
#include <map>

using namespace std;
using namespace mysqlpp;

void getclustering(int clusterid, vector<int>* v);
string db="geneassoc";
string toString(int i);
string team;
class module
{
	public:
		int start, stop, length;
		module(int st, int sp, int len)
		{
			start = st;
			stop = sp;
			length = len;
		}
};

void getmods(vector<module>* v);
void geteqtls(vector<string>* v);
void gettraitlists(vector<string>* v, vector<int> cluster, vector<module> mods, int netid);
void insertlists(vector<string> traits, vector<string> eqtls, vector<int>* ids, string anno, int associd, int netid);
void insertDBentry(vector<int> ids, int netid, int associd, string goanno, int clusterid);


int main(int argc, char* argv[])
{
	if(argc != 7)
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
		//	case 6:
		//		db = argv[i];
		//		cout << db << endl;
		}
	}

	vector<int> clustering;
	getclustering(clusterid, &clustering);
	if(clustering.size() == 0)
	{
		cerr << "Cannot parse clustering for : " << clusterid << endl;
		return 0;
	}
	//read in module file
	vector<module> mods;
	getmods(&mods);
	if(mods.size() == 0)
	{
		cerr << "Module generation failed!" << endl;
		return 0;
	}
	//read in eQTL files
	vector<string> eqtls;
	geteqtls(&eqtls);
	if(eqtls.size() == 0)
	{
		cerr << "eQTL analysis failed!!" << endl;
		return 0;
	}
	//create traitlists
	vector<string> traitlists;
	gettraitlists(&traitlists, clustering, mods, netid);
	if(traitlists.size() == 0)
	{
		cerr << "Could not read from database! " << endl;
		return 0;
	}
	//insert traitlists, eqtllists
	vector<int> ids;
	insertlists(traitlists, eqtls, &ids, goanno, associd, netid);
	if(ids.size() == 0)
	{
		cerr << "Could not write to database!" << endl;
		return 0;
	}
	
	//insert db entry
	insertDBentry(ids, netid, associd, goanno, clusterid);

	return 0;
}

void insertDBentry(vector<int> ids, int netid, int associd, string goanno, int clusterid)
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
                for(int i = 0; i < 20; i ++)
                {
                        query.reset();
                        query << "INSERT INTO netmodule (netid, idx, traitlistid, assocsetid, golistid, eQTLlistid, goanno, clusterid) VALUES (" << netid << ", " << i << ", " << ids[i] << ", " << associd << ", -1, " << ids[i+20] << ", \'" << goanno << "\', " << clusterid << ")"; 
			cout << query.str() << endl;
	                if(!query.execute())
			{
				cerr << query.error() << endl;
			}       
                }
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

void insertlists(vector<string> traits, vector<string> eqtls, vector<int>* ids, string anno, int associd, int netid)
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
		for(int i = 0; i < 20; i ++)
		{
			query.reset();
			query << "INSERT INTO traitlist (list) VALUES (\'" << traits[i] << "\')";
			query.execute();
			ids->push_back(query.insert_id());
		}
		for(int i = 0; i < 20; i ++)
		{
			query.reset();
			query << "INSERT INTO eQTLlist (list) VALUES (\'" << eqtls[i] << "\')";
			query.execute();
			ids->push_back(query.insert_id());
		}

		query.reset();
		StoreQueryResult ares;
		query << "SELECT ts FROM network WHERE id = " << netid;
                ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return ;
                }
                int tsid = 0;
                for(size_t i = 0; i < ares.num_rows(); i ++)
                {
                        tsid = ares[i]["ts"];
                }

		for(int i = 0; i < 20; i ++)
		{
			query.reset();
			string name = "module" + toString(i) + "id" + toString(associd) + anno;
			query << "INSERT INTO traitsubset (name, tsid, traitlist) VALUES (\'" << name 
				<< "\'," << tsid << "," << (*ids)[i] << ")";
			query.execute();
		}
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

void geteqtls(vector<string>* v)
{
	for(int i = 1; i < 21; i ++)
	{
		ifstream myeqtl (("eQTLres" + toString(i)).c_str());
		string res = ",";
		while(myeqtl.good())
		{
			string line;
			getline(myeqtl, line);
			for(int i = 0; i < line.length(); i ++)
			{
				if(line[i] == ',')
				{
					res += '*';
				}
				else
				{
					res += line[i];
				}
			}
			if(line.length() > 1)
			{
				res += ',';
			}
		}
		v->push_back(res);
	}
}

void getmods(vector<module>* v)
{
	ifstream mymods ("modules.txt");
	if(mymods.is_open())
	{
		while(mymods.good())
		{
			string line;
			getline(mymods, line);

			string d1 = "";
			string d2 = "";
			string d3 = "";
			int idx = 0;
			bool isgoing = false;
			for(int i = 0; i < line.length(); i ++)
			{
				if(line[i] != ' ')
				{
					isgoing = true;
					switch(idx)
					{
						case 0: d1 += line[i]; break;
						case 1: d2 += line[i]; break;
						case 2: d3 += line[i]; break;
					}
				}
				else
				{
					if(isgoing)
					{
						idx++;
						isgoing = false;
					}
				}
			}
			if(!d1.empty() && !d2.empty() && !d3.empty())
			{
				int i1 = (int)atof(d1.c_str());
				int i2 = (int)atof(d2.c_str());
				int i3 = (int)atof(d3.c_str());
				module* m = new module(i1, i2, i3);
				v->push_back(*m);
			}
		}
	}
	mymods.close();
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

void gettraitlists(vector<string>* v, vector<int> clustering, vector<module> mods, int netid)
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
			return ;
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

			traitids[idx] = tid;
		}
		query.reset();

		for(int i = 0; i < 20; i ++)
		{
			int start = mods[i].start;
			int end = mods[i].stop;
			string list=",";

			for(int j = start; j <= end; j ++)
			{
				int idx = clustering[j];
				idx = traitids.find(idx-1)->second;
				list += toString(idx) + ",";
			}
			v->push_back(list);
		}
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

string toString(int i)
{
        std::string s;
        std::stringstream out;
        out << i;
        s = out.str();
        return s;
}

