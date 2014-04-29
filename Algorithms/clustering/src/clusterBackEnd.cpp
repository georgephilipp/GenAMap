#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>
#include <sys/stat.h>

using namespace std;
using namespace mysqlpp;

void insertCluster(int netid, int tid, string s, string name);
void insertTree(int netid, int tid, vector<vector<int> > tree, string name);
string db;

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

bool FileExists(string strFilename) { 
  struct stat stFileInfo; 
  bool blnReturn; 
  int intStat; 

  // Attempt to get the file attributes 
  intStat = stat(strFilename.c_str(),&stFileInfo); 
  if(intStat == 0) { 
    // We were able to get the file attributes 
    // so the file obviously exists. 
    blnReturn = true; 
  } else { 
    // We were not able to get the file attributes. 
    // This may mean that we don't have permission to 
    // access the folder which contains this file. If you 
    // need to do that level of checking, lookup the 
    // return values of stat which will give you 
    // more details on why stat failed. 
    blnReturn = false; 
  } 
   
  return(blnReturn); 
}

vector<int> &split(const std::string &s, char delim, std::vector<int> &elems) 
{
    stringstream ss(s);
    string item;
    while(getline(ss, item, delim)) 
    {
        elems.push_back(atoi(item.c_str()));
    }
    return elems;
}


vector<int> split(const string &s, char delim) 
{
    vector<int> elems;
    return split(s, delim, elems);
}

int main(int argc, char* argv[])
{
	if(argc != 6)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string mask;
	int tsid, netid;
	string name;
	int netID;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1:
				mask = (argv[i]);
				break;
			case 2:
				netid = atoi(argv[i]);
				break;
			case 3: 
			        tsid = atoi(argv[i]);
				break;
	   		case 4:
		  		name = argv[i];
		  		cout << name << endl;
			  	break;
			case 5: db = argv[i];
				cout << db << endl;
				break;
		}
	}
	if(FileExists("cluster.txt"))
	{
		ifstream myfile("cluster.txt");
		string s;
		string temp;
		while(!myfile.eof())
		{
			getline(myfile, temp);
			if(temp.length() > 0)
				s = s + temp + ",";
		}
		myfile.close();

		insertCluster(netid, tsid, s, name);
	}
	else
	{
		ifstream myfile("tree.txt");
		string s;
		string temp;
		vector<vector<int> > tree;
		while(!myfile.eof())
		{
			getline(myfile, temp);
			if(temp.length() > 0)
				tree.push_back(split(temp, ' '));
		}
		myfile.close();
		insertTree(netid, tsid, tree, name);
	}
	return 0;
}

void insertTree(int netid, int tsid, vector<vector<int> > tree, string name)
{
	vector<int> ids;

//	for(int i=0; i<tree.size(); i ++)
//	{
//		for(int j=0; j < tree.at(i).size(); j++)
//		{
//			cout << tree.at(i).at(j);
//		}
//		cout << endl;
//	}

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

		query << "SELECT createTree(" << tsid << ",\"" << name << "\") as id;";
		StoreQueryResult ares = query.store();

                int tid;
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return ;
                }
                for(size_t i=0; i < ares.num_rows(); i ++)
                {
                        tid = ares[i]["id"];
                        cout << tid;
                }

		vector<int> traitids;
		if(getTraitIds(tsid,&traitids) == 1)
	        {
        	        //cout << "And some of these trait ids ... " << endl;
	                //vector<int>::const_iterator cii;
        	        //for(cii = traitids.begin(); cii!=traitids.end(); cii++)
	                //{
        	        //        cout << *cii << endl;
	                //}
	        }
	        else
	        {
	                cerr << "Something has gone seriously wrong reading the database ids." << endl;
	                return;
	        }

		for(int i=0;i<tree.size();i++)
		{
			query = conn.query();
			if(tree.at(i).at(3) == -1)//this is da rute!
			{
				query << "SELECT insertIntoTree(" << tid << ",1,1) as id;";
			}
			else if(tree.at(i).at(1) == -1)
			{
				//cout << ids.size();
				query << "SELECT insertIntoTree(" << tid << "," << tree.at(i).at(2) << "," <<
					ids.at(tree.at(i).at(3)-1) << ") as id;";
			}
			else
			{
				query << "SELECT insertTraitIdIntoTree(" << tid << "," << tree.at(i).at(2) << "," <<
					ids.at(tree.at(i).at(3)-1) << "," 
					<< traitids.at(tree.at(i).at(1)) << ") as id;";
			}
			ares = query.store();

			int trid;
			if(!ares)
			{
				cerr << query.error() << endl;
				return;
			}	
			for(size_t i=0; i<ares.num_rows(); i++)
			{
				trid = ares[i]["id"];
			}
			ids.push_back(trid);
		}
                query.reset();
		query << "UPDATE traittree SET loadcmpt = 1 WHERE id = " << tid << ";" << endl;
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

void insertCluster(int netid, int tsid, string s, string name)
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

                query << "INSERT INTO cluster (netid, traitid, name, value) VALUES (" << netid << "," << tsid << ",'" << name << "','" << s << "');";
                query.execute();
       		cout << query.str() << endl;
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
