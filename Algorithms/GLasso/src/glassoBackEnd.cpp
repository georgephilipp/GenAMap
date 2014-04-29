#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>
#include <math.h>


using namespace std;
using namespace mysqlpp;

string toString(int i);
//int getOtherDim(int tsid);
string toStringf(float i);
void Tokenize(const string& str,vector<string>& tokens,const string& delimiters);
int getTraitIds(int tsid, vector<int>* arr);
int getNetId(int tsid, string type);
string db;
string team;

int main(int argc, char* argv[])
{
	int subsz = 1000;

	if(argc != 7)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string mask;
	int tsid;
	int totaltraits;
	float finallambda = 0.0;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1:
				finallambda = atof(argv[i]);
				cout << finallambda << endl;
				break;
			case 3:
				tsid = atoi(argv[i]);
				cout << tsid << endl;
				break;
			case 4: 
				mask = argv[i];
				cout << mask << endl;
				break;
			case 2: 
				totaltraits = atoi(argv[i]);
				//cout << mask << endl;
				break;
			case 5:
				team = argv[i]; break;
			case 6:
				db = argv[i];
				cout << db << endl;
				break;
		}
	}

	//read number of total files
	int totalclusts = 0; 
	string filenns_nosc = mask+ "_" +"finalSelectClust";
	ifstream myfile_nosc(filenns_nosc.c_str());
	if(myfile_nosc.is_open())
	{
		while(! myfile_nosc.eof())
		{
			string ll2;
			getline(myfile_nosc, ll2);
			if(ll2.length()<=0)
					continue;
			cout<<"read line from file "<<ll2<<endl;
			totalclusts = atoi(ll2.c_str());
		}
	}
	
	cout << " total clusters: "<<totalclusts<<endl;
	
	//read each glasso results for all clusters and make the matrix
	vector<int> traitsinclust[totalclusts];
	for(int tc=0; tc < totalclusts; tc++)
	{
		string traitinclust = toString(tc)+ "_" +"final_num";
		
		ifstream myfile1(traitinclust.c_str());
		if(myfile1.is_open())
		{
			while(! myfile1.eof())
			{
				string ll2;
				getline(myfile1, ll2);
				if(ll2.length()<=0)
					continue;
				int trnum = atoi(ll2.c_str());
				cout<<"pushing "<<trnum<<endl;
				traitsinclust[tc].push_back(trnum);
			}
		}
	}
	
	
	float lambda = finallambda;
	
		float finalmatrix[totaltraits][totaltraits]; 
		for(int t1=0; t1 < totaltraits; t1++ )
		{
			for(int t2=0; t2 < totaltraits ; t2++)
			{
					finalmatrix[t1][t2] = 0.0;
			}
		}
		for(int tc=0; tc < totalclusts; tc++)
		{
			string glassout = toString(tc)+ "_" +"final_"+toStringf(lambda); //glasso output file
			cout<<"reading file "<<glassout<<endl;
			ifstream myfile1(glassout.c_str());
			if(myfile1.is_open())
			{
			cout<<"opened file "<<glassout<<endl;
				string ll2;
				//ignore first line having columns name
				getline(myfile1, ll2);
				
				int ii=0;
				while(!myfile1.eof())
				{
					getline(myfile1, ll2);
					if(ll2.length()<=0)
						continue;
					vector<string> tokens;
					Tokenize(ll2, tokens," ");
					int totaltok = (int) tokens.size();
					cout<<"total token: "<<totaltok<<endl;
					for(int q=1; q <= traitsinclust[tc].size(); q++)
					{
						finalmatrix[traitsinclust[tc][ii]][traitsinclust[tc][q-1]] = atof(tokens[traitsinclust[tc].size()+q].data());
					}
					ii++;
				}
			}
			
		}
		
		cout<<"final matrix calculated"<<endl;
		
		//here we get the complete matrix for each lambda from glasso results
		//generate sum and write param files for 
	
		int netID = getNetId(tsid, "GLO");
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
			return -1;
		}
	
		//////chnage here
			int k = totaltraits;
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
				for(int i = 0; i < k; i++)
				{
					cout<<"in loop"<<endl;
					for(int j=i; j < k; j++)
					{
						if(j==i)
							continue;
						if(finalmatrix[i][j] == 0.0)
							continue;
						query.reset();
						
						
						query << "INSERT INTO networkval" << team << " (trait1, trait2, weight, netid) VALUES (" << arr[i] <<"," << arr[j] << "," <<finalmatrix[i][j]<<","<<netID<<");";
						if(i%100==0)
							cout<<"INSERT INTO networkval" << team << " (trait1, trait2, weight, netid) VALUES (" << arr[i] <<"," << arr[j] << "," <<finalmatrix[i][j]<<","<<netID<<");"<<endl;
						query.execute();
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

string toString(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();
	return s;
}
string toStringf(float i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();
	return s;
}

void Tokenize(const string& str,vector<string>& tokens,const string& delimiters = " ")
{
    // Skip delimiters at beginning.
    string::size_type lastPos = str.find_first_not_of(delimiters, 0);
    // Find first "non-delimiter".
    string::size_type pos     = str.find_first_of(delimiters, lastPos);

    while (string::npos != pos || string::npos != lastPos)
    {
        // Found a token, add it to the vector.
        tokens.push_back(str.substr(lastPos, pos - lastPos));
        // Skip delimiters.  Note the "not_of"
        lastPos = str.find_first_not_of(delimiters, pos);
        // Find next "non-delimiter"
        pos = str.find_first_of(delimiters, lastPos);
    }
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


