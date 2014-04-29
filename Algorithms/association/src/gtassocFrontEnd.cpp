#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <math.h>
#include <sys/stat.h>
#include <cstdlib>
using namespace std;
using namespace mysqlpp;

void createParmFiles(int tsid, int netid, string mask, string name, int gsid, int netid1, int netid2, int associd);
string toString(int i);
int getK(int tsid);
int gettsid(string mask);
int writeTraitFile(int id, string filename);
int writeNetFile(string appID, int netID, bool hardThresh);
int writeAssocFile(int associd, string filename);
string team;

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

int main(int argc, char* argv[])
{
	if(argc != 9)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string file;
	string mask;
	string name;
	int netid;
	int k;
	int l;
	int netid2;
	int tsid;
	int associd;
	int gsid;
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
			case 4: 
				netid2 = atoi(argv[i]);
				break;
			case 5: 
				tsid = atoi(argv[i]);
				break;
			case 6: 
				associd = atoi(argv[i]);
				break;
			case 7: team = argv[i]; break;
			case 8: db = argv[i];
				break;	
		}
	}
	std::system("./../exe/copyFilesGGFLasso.sh");
	gsid = gettsid(mask);
	k = getK(gsid);
	l = getK(tsid);
	cout << tsid << " " << gsid << " " << k << " " << l << endl;
	writeTraitFile(gsid, mask + "_g");
	writeTraitFile(tsid, mask + "_t");
	writeAssocFile(associd, mask+"_assoc");
	createParmFiles(tsid, netid, mask, name, gsid, netid, netid2, associd);
	writeNetFile(mask + "G", netid, false);
	writeNetFile(mask + "T", netid2, false);
	return 0;
}

int writeAssocFile(int associd, string filename)
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
		query << "SELECT idx, value, markerid FROM association" << team << ", trait WHERE assocsetid = " << associd << " AND (trait.id = traitid)";
		StoreQueryResult ares = query.store();
		if(!ares)
		{
			cerr << query.error() << endl;
			return -1;
		}
		ofstream myfile;
		myfile.open(filename.c_str());
		for(size_t i = 0; i < ares.num_rows(); i ++)
		{
			myfile << (ares[i]["idx"]+1) << "\t";
			double d = ares[i]["value"];
			myfile << d << "\t" << ares[i]["markerid"] << endl;		
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
				myfile << (ares[i]["idx"]+1) << "\t";
			}
			else
			{
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

int writeTraitFile(int id, string filename)
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

                StoreQueryResult ares;

                ofstream myfile;
                myfile.open(filename.c_str());
                query.reset();

                query << "SELECT id FROM trait WHERE traitsetid = " << id << " ORDER BY idx";
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

void createParmFiles(int tsid, int netid, string mask, string name, int gsid, int netid1, int netid2, int associd)
{
        bool isNotWritten = true;
	int mid = 0;
	int tid = 0;
	int meth = 0;
	string markerFileName;
	string traitFileName;
	int numTfile = 0;
	int numMfile = 0;
	int noPops = -1;

	ofstream myfile;
	myfile.open("parms1.txt");
	myfile << "gtaprep.m mask='" << mask << "';gtaprep" << endl;
	myfile << "vanilla" << endl;
	myfile.close();

	ofstream parms6;
	parms6.open("parms18.txt");
	parms6 << name << " " << gsid << " " << tsid << " " << netid1 << " " << netid2 << " " << associd << " " << team << endl;
	parms6 << "vanilla" << endl;
}

/*void makeGFLParms(string mask, int subsz, int j, int k, int msid, int tsid, int netid, string name, int N, int method)
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
	parms15 << k << " " << tsid << " " << mask << " thresh.txt " << name << " " << j << " " << netid << " " << msid << " " << db << endl;
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

}*/

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
