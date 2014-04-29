#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>

using namespace std;
using namespace mysqlpp;

string toString(int i);
//int getOtherDim(int tsid);
string toStringf(float i);

int main(int argc, char* argv[])
{
	int subsz = 1000;

	if(argc != 7)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string db;
	string mask;
	int tsid;
	int k;
	int minclust;
	int maxclust;
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
				minclust = atoi(argv[i]);
				//cout << mask << endl;
				break;
			case 5: 
				maxclust = atoi(argv[i]);
				//cout << mask << endl;
				break;
			case 6:
				db = argv[i];
				cout << db << endl;
				break;
		}
	}

	int writevanilla = 1;
	//int otherdim = getOtherDim(tsid);
	//read results from spectral clustering
	for(int c = minclust; c<= maxclust; c++)
	{
		cout << "Cluster " << c <<endl;

		int counts[c];
		for(int te=0; te<c ; te++)
			counts[te]=0;
		
		vector<int> tr_list[c];
		//ofstream myfile;
		string toclustfilen = toString(c)+ "_" +"sc";
		ifstream myfile(toclustfilen.c_str());
		int trno=0;
		int tocont=0;
		if(myfile.is_open())
		{			
			while(! myfile.eof())
			{
			
				string line;
				getline(myfile, line);
				trno++;
				int cno = atoi(line.c_str());
				tr_list[cno-1].push_back(trno);
				counts[cno-1]++;
				if(counts[cno-1] > 3000)
				{
					cout << "Cluster has more than 3000" << cno <<endl;

					tocont = 1;
					continue;
				}
			}
		}
		if(tocont == 0)
		{
			//so now we got the which clustering result to use - save that in the file 
			cout << "Cluster selected " << c <<endl;

			ofstream myfileee;
				myfileee.open("parms5.txt");
				
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

                cout << "id " << id <<endl;

				
			for(int cnum =1; cnum <= c; cnum++)
			{
				ofstream myfile;
				string toop = toString(cnum)+ "_" +"final";
				myfile.open(toop.c_str());
				cout << "writing " << toop <<endl;
				ofstream myfile1;
				string toop1 = toString(cnum)+ "_" +"final_num";
				myfile1.open(toop1.c_str());
				
				for(float lambda=.1 ; lambda <=1; lambda=lambda+.1)
				{
					string toopr = toString(cnum)+ "_" +"final_"+toStringf(lambda);
					myfileee<<toop<<" "<<lambda<<" "<<toopr<<endl;
					if(writevanilla==1)
					{
						writevanilla=-1;
						myfileee<<"standard" << endl;
					}
				}
				
				for(int sk=0; sk < (int)tr_list[cnum].size(); sk++)
				{
				
					int currno = (tr_list[cnum])[sk];
					myfile1<<currno<<endl;
					query << "SELECT id FROM trait WHERE traitsetid = " << id << " AND idx = " << currno;
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
							query << "SELECT value FROM traitval,sample WHERE traitid = " << ares[i]["id"] << " AND sampleid = sample.id ORDER BY sample.idx";
							StoreQueryResult ares2 = query.store();
							//noidis = ares2.num_rows();
							for(size_t j = 0; j < ares2.num_rows(); j ++)
							{
								myfile << ares2[j]["value"] << "\t";
							}
							myfile << endl;

							q2.reset();
						}
				
					}
       		
				}
				myfile.close();
				myfile1.close();
			}
			
			//write params for R
			
			
			
			//save number of cluster in file
			ofstream myfile;
			string toop = mask+ "_" +"finalSelectClust";
			myfile.open(toop.c_str());
			myfile<<c<<endl;
			myfile.close();
			
			myfileee.close();
			//break
			break;
		}
	}

	return 0;
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

/*int getOtherDim(int tsid)
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

                query << "SELECT id FROM trait WHERE traitsetid = " << id ;
                ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
					Query q2 = conn.query();
					query << "SELECT value FROM traitval,sample WHERE traitid = " << ares[i]["id"] << " AND sampleid = sample.id ORDER BY sample.idx";
					StoreQueryResult ares2 = query.store();
					noidis = ares2.num_rows();
					return ares2.num_rows();
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
}*/
