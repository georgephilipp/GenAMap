#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <string>
#include <stdlib.h>
#include <cstdlib>

using namespace std;
using namespace mysqlpp;

//argv1 = project appID

void combineFiles(int subsz, int k, string mask);
void createParmFiles(int subsz, int k, int tsid, string mask);
string toString(int i);
int getK(int tsid);
int gettsid(string mask);
int writeTraitFile(int start, int end, string mask, string filename);
string db;
string team;

int main(int argc, char* argv[])
{
	/*int CR1 = 0;
	int CR2 = 1;
	int TOM = 4;
	int SFN = 6;
	int GLO = 5;*/
	int subsz = 1000;

	if(argc != 4)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	std::system("./../exe/copyFilesGlasso.sh");
	string file;
	string mask;
	int id;
	int k;
	int method=0;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				mask = argv[i];
				cout << file << endl;
				break;
			case 2: team = argv[i]; break;
			case 3:
				db = argv[i];
				cout << db << endl;
		}
	}

	int tsid = gettsid(mask);
	cout << tsid << endl;
	k = getK(tsid);	
	cout << k << endl;
	createParmFiles(subsz, k, tsid, mask);
	return 0;
}

int writeTraitFile(int start, int end, string mask, string filename)
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
			ofstream myfile;
			myfile.open(filename.c_str());
			for(size_t i = 0; i < ares.num_rows(); i ++)
			{
				Query q2 = conn.query();
				query << "SELECT value FROM traitval" << team << ",sample WHERE traitid = " << ares[i]["id"] << " AND sampleid = sample.id ORDER BY sample.id";
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

		cout << conn.error() << endl;
		Query query = conn.query();
                string qtext = "SELECT get_ts_id('" + mask + "') as id";
		query << qtext;
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

void createParmFiles(int subsz, int k, int tsid, string mask)
{
        ofstream myfile;
        myfile.open("parms1.txt");
        bool isNotWritten = true;
	int id = 0;
	//int meth = method;

	for(int i = 1; i <= k; i += subsz)
	{
		string filename = mask + "_" + toString(i);
		int end = i + subsz - 1;
		if(end > k)
			end = k;

		if((id=writeTraitFile(i-1, end, mask, filename))==-1)
		{
			cerr << "Error in creating files from the database" << endl;
			return;
		}
	}

        for(int i = 1; i <= k; i += subsz)
        {
                for(int j = i; j <= k; j += subsz)
                {
			myfile << mask << "_" << i << " " << mask << "_" << j << " 0 " ;
                        myfile << mask << "_" << i << "_" << j << " ";
			myfile << id << " " ;
			if(i + subsz > k)
				myfile << k-i+1 << " ";
			else
				myfile << subsz << " ";
			if(j + subsz > k)
				myfile << k-j+1;
			else
				myfile << subsz;
			myfile << endl;
                        if(i==1 && isNotWritten)
                        {
                                myfile << "standard" << endl;
                                isNotWritten = false;
                        }
                }
        }

        myfile.close();

		        ofstream secondfile;
        	secondfile.open("parms2.txt");
	        secondfile << k << " " << tsid << " " << mask << " " << db << endl;
        	secondfile << "vanilla" << endl;
	        secondfile.close();
			
			ofstream secondfile113;
        	secondfile113.open("parms3.txt");
			secondfile113 << mask<<"_net.txt "<<.75<<" "<<mask<<endl;//change here
			secondfile113 << "vanilla" << endl;
			secondfile113.close(); 
			
			ofstream secondfile11;
        	secondfile11.open("parms4.txt");
			secondfile11 <<  tsid << " " << mask << " "<<k << " "<< db << endl;
			secondfile11 << "vanilla" << endl;
			secondfile11.close(); 
	        //secondfile1 << k << " " << tsid << " " << mask << " " << db << endl;//change here
        	
			//int minclust= (k / 3000)+1;
			//int maxclust = minclust+10;
			//string toclustfilen = mask + "_" +"finalcor.txt";
			//secondfile11 << "runSC.m addpath('../exe/');runSC('"<<toclustfilen<<"',"<<minclust<<",'"<<minclust<<"_sc');" <<endl;//change here
			//secondfile11 << "vanilla" << endl;
			//for(int c =minclust+1; c <= maxclust; c++  )
			//{
			//	secondfile11 << "runSC.m addpath('../exe/');runSC('"<<toclustfilen<<"',"<<c<<",'"<<c<<"_sc');" <<endl;
			//}
			
			
			
			ofstream secondfile1;
        	secondfile1.open("parms6.txt");
	       secondfile1 << k << " " << tsid << " " << mask << " "<< team << " " << db << endl;//change here
        	secondfile1 << "vanilla" << endl;
	       secondfile1.close();
		   
		   
		   ofstream secondfile18;
        	secondfile18.open("parms8.txt");
	       secondfile18 << k << " " << tsid << " " << mask << " "<< team << " " << db << endl;//change here
        	secondfile18 << "vanilla" << endl;
	       secondfile18.close();

}

void combineFiles(int subsz, int k, string mask)
{
	ofstream results;
	string filename = mask + "results.txt";
	results.open(filename.c_str());

	results << "00";
	int numfis = (int)(k / subsz);
	if(k % subsz > 0)
		numfis ++;

	ifstream infile[numfis];

        for(int i = 1; i <= k; i += subsz)
        {
		int p = 0;
                for(int j = 1; j <= k; j += subsz)
                {
			string s = mask + "_" + toString(i) + "_" + toString(j) + "res.txt";
			infile[p++].open(s.c_str());
                }

		for(int j = 0; j < subsz; j ++)
		{
			string s;
			for(int k = 0; k < numfis; k ++)
			{
				getline(infile[k], s);
				results << s;
				//cout << s;
			}
			results << endl;
			//cout << endl;
		}

		for(int j = 0; j < numfis; j ++)
		{
			infile[j].close();
		}
        }
	results.close();

}

string toString(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();
	return s;
}