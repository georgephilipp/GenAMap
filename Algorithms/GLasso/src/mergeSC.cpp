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
void Tokenize(const string& str,vector<string>& tokens,const string& delimiters);
string team;
int main(int argc, char* argv[])
{
	int subsz = 1000;

	if(argc != 6)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string db;
	string mask;
	int tsid;
	int k;
	//int minclust;
	//int maxclust;
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
			case 5:
				db = argv[i];
				cout << db << endl;
				break;
			case 4: team = argv[i];
				cout << team << endl;
				break;
		}
	}


	ofstream myfileee;
	myfileee.open("parms7.txt");


	int finalfilenum=0;
	string line;
	int writevanilla = 1;
	
	//read the no_sc file
	string filenns_nosc = mask+"_noSC";
	ifstream myfile_nosc(filenns_nosc.c_str());
	if(myfile_nosc.is_open())
	{
		while(! myfile_nosc.eof())
		{
			string ll2;
			getline(myfile_nosc, ll2);
			if(ll2.length()<=0)
					continue;
			string filennamee =ll2;
			ifstream myfilen(filennamee.c_str());
			if(myfilen.is_open())
			{
				ofstream myfile;
				string toop = toString(finalfilenum)+ "_" +"final";
				myfile.open(toop.c_str());
				cout << "writing " << toop <<endl;
				ofstream myfile1;
				string toop1 = toString(finalfilenum)+ "_" +"final_num";
				myfile1.open(toop1.c_str());
				
				for(float lambda=.1 ; lambda <=1; lambda=lambda+.1)
				{
					string toopr = toString(finalfilenum)+ "_" +"final_"+toStringf(lambda);
					myfileee<<toop<<" "<<lambda<<" "<<toopr<<endl;
					if(writevanilla==1)
					{
						writevanilla=-1;
						myfileee<<"vanilla" << endl;
					}
				}
				while(! myfilen.eof())
				{
					string l1;
					getline(myfilen, l1);
					if(l1.length()<=0)
					continue;
					int currno = atoi(l1.c_str());
					
			
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
					
					//for(int sk=0; sk < (int)tr_list[cnum].size(); sk++)
					//{
					
						//int currno = (tr_list[cnum])[sk];
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
								query << "SELECT value FROM traitval" << team << ",sample WHERE traitid = " << ares[i]["id"] << " AND sampleid = sample.id ORDER BY sample.id";
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
				finalfilenum++;
				//}
			}
		}
	}
	
	//read the SC files
	string filenns = mask+"_SC";
	ifstream myfile(filenns.c_str());
	if(myfile.is_open())
	{
		while(! myfile.eof())
		{
			cout<< "Reading SC file" << endl;
			getline(myfile, line);
			if(line.length()<=0)
					continue;
			vector<string> tokens;
			Tokenize(line, tokens," ");
			int num = atoi(tokens[0].data());
			int minclust = atoi(tokens[1].data());
			int maxclust = atoi(tokens[2].data());
			vector<int> org_tr_list; 
			//file having trait ids
			string filennamee = mask+"_SC_"+toString(num);
			ifstream myfilen(filennamee.c_str());
			if(myfilen.is_open())
			{
				while(! myfilen.eof())
				{
					string l1;
					getline(myfilen, l1);
					if(l1.length()<=0)
					continue;
					int to_num = atoi(l1.c_str());
					org_tr_list.push_back(to_num);
				}
			}
			//now check each cluster and pick the minimum and write the files
			for(int c = minclust; c<= maxclust; c++)
			{
				cout << "Cluster " << c <<endl;

				int counts[c];
				for(int te=0; te<c ; te++)
					counts[te]=0;
				
				vector<int> tr_list[c];
				
				cout << "Initialized " <<endl;
				//ofstream myfile;
				string toclustfilen = toString(num)+"_"+toString(c)+"_sc";
				ifstream myfilet(toclustfilen.c_str());
				int trno=0;
				int tocont=0;
				cout << "reading file " << toclustfilen <<endl;
				if(myfilet.is_open())
				{			
					while(! myfilet.eof())
					{
					
						string line;
						getline(myfilet, line);
						if(line.length()<=0)
							continue;
						int cno = atoi(line.c_str());
						cout << "cno " << cno << endl;
						tr_list[cno-1].push_back(org_tr_list[trno]);
						trno++;
						counts[cno-1]++;
						if(counts[cno-1] > 3000)
						{
							cout << "Cluster has more than 3000" << cno <<endl;

							tocont = 1;
							break;
						}
					}
				}
				if(tocont == 0)
				{
					//so now we got the which clustering result to use - save that in the file 
					cout << "Cluster selected " << c <<endl;

	
						
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
						string toop = toString(finalfilenum)+ "_" +"final";
						myfile.open(toop.c_str());
						cout << "writing " << toop <<endl;
						ofstream myfile1;
						string toop1 = toString(finalfilenum)+ "_" +"final_num";
						myfile1.open(toop1.c_str());
						
						for(float lambda=.1 ; lambda <=1; lambda=lambda+.1)
						{
							string toopr = toString(finalfilenum)+ "_" +"final_"+toStringf(lambda);
							myfileee<<toop<<" "<<lambda<<" "<<toopr<<endl;
							if(writevanilla==1)
							{
								writevanilla=-1;
								myfileee<<"vanilla" << endl;
							}
						}
						
						for(int sk=0; sk < (int)tr_list[cnum-1].size(); sk++)
						{
						
							int currno = (tr_list[cnum-1])[sk];
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
									query << "SELECT value FROM traitval" << team << ",sample WHERE traitid = " << ares[i]["id"] << " AND sampleid = sample.id ORDER BY sample.id";
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
						finalfilenum++;
					}
					
					
					
					//break
					break;
				}
			}
			
		}
		
	}
	
	
	
	//write final number of clusters
	//save number of cluster in file
	ofstream myfilef;
	string toop = mask+ "_" +"finalSelectClust";
	myfilef.open(toop.c_str());
	myfilef<<finalfilenum<<endl;
	myfilef.close();
	
	myfileee.close();

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
