#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>

using namespace std;
using namespace mysqlpp;

void insertIntoDB(int msid, string name, int totalind);
void Tokenize(const string& str,vector<string>& tokens,const string& delimiters);
string toStrings(int i);
string toStrings(float i);


string db;

int main(int argc, char* argv[])
{
	

	if(argc != 6)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string mask;
	int msid,totalind;
	string name;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			
			case 1: 
			    mask = argv[i];
				cout << mask << endl;
				break;      
			case 2:
				msid = atoi(argv[i]);
				cout << msid << endl;
				break;
			case 3:
				name = argv[i];
				break;
			case 4:
				totalind = atoi(argv[i]);
				break;
			case 5:
				db = argv[i];
				break;

		
		}
	}

	insertIntoDB(msid, name,totalind);


	return 0;
}

void insertIntoDB(int msid, string name, int totalind)
{
	int towrite[totalind][10];
	float eigen[totalind][5]; 
	try
	{
		for(int fileno=2; fileno<=10; fileno++)
		{
			string file = "structout"+toStrings(fileno)+"_f";
			ifstream myfile(file.c_str());
			if(myfile.is_open())
			{
					
					while(! myfile.eof())
					{
						string line;
						getline(myfile, line);
						if(line.compare("Inferred ancestry of individuals:")==0)
						{
							getline(myfile, line);
							cout<<"found ::: Infere "<<endl;
							break;
						}
					}
					if(myfile.eof())
					{
						cout<<" couldn't found"<<endl;
						return;
					}
					else
					{
						int linecount=0;
						while(! myfile.eof())
						{
							string line;
							getline(myfile, line);
							vector<string> tokens;
							Tokenize(line, tokens,"   ");
							cout<<"label "<<atoi(tokens[1].data())<<endl;
							towrite[linecount][0]=atoi(tokens[1].data());
							//vector<string> tokens1;
							//Tokenize(tokens[3], tokens1,":  ");
							//vector<string> tokens22;
							//cout<<"tokens1[1] "<<tokens1[1].data()<<"tokens[3] "<<tokens[3].data()<<" "<<tokens[4].data()<<" "<<tokens[5].data()<<endl;
	
							//Tokenize(tokens1[1], tokens22," ");
							double maxx = -1;
							int maxpopp = -1;
							for(int kk=0; kk < fileno; kk++)
							{
								double perc = atof(tokens[kk+5].data());
								

								if(perc > maxx)
								{
									cout<<"perc "<<perc<<" "<<maxx<<endl;
									maxpopp = kk+1;
									maxx = perc;
								}
							}
							cout<<"pop "<<maxpopp<<endl;
							//towrite[linecount][fileno-1]=atoi(tokens1[0].data());
							towrite[linecount][fileno-1]=maxpopp;
	
    							linecount++;
							if(linecount==totalind)
								break;
						}
					}
					
			}
			else
			{
				cout<<"file not found: "<<file <<endl;
				return;
			}		

		}

		string file = "eig_coef";
		ifstream myfile(file.c_str());
		if(myfile.is_open())
		{			
			for(int i=0; i < totalind; i++)
			{
				string line;
				getline(myfile, line);
				vector<string> tokens;
				Tokenize(line, tokens,"\t");
				cout<<"eigen1 "<<atof(tokens[0].data())<<"\teigen2 "<<atof(tokens[1].data())<<"\teigen3 "<<atof(tokens[2].data())<<endl;
				eigen[i][0]=atof(tokens[0].data());
				eigen[i][1]=atof(tokens[1].data());
				eigen[i][2]=atof(tokens[2].data());
				eigen[i][3]=atof(tokens[3].data());
				eigen[i][4]=atof(tokens[4].data());
			}
		}	
		else
		{
				cout<<"file not found: "<<file <<endl;
				return;
		}
		

		//insert
		ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str()); 
              Query query = conn.query();
		query << "SELECT createPopStruct(" << msid<< ",'" <<name << "' ) as id";
		StoreQueryResult ares = query.store();
              int id;
              if(!ares)
              {
                      cerr << query.error() << endl;
                      return;
              }
              for(size_t i=0; i < ares.num_rows(); i ++)
              {
                      id = ares[i]["id"];
              }

		//start inserting
		query.reset();
		for(int i=0; i < totalind; i++)
		{
			string qstri;
			qstri = "INSERT INTO structure (popstructid, sampleid, pop2,pop3,pop4,pop5,pop6,pop7,pop8,pop9,pop10,eig1,eig2,eig3,eig4,eig5) VALUES (";
			qstri.append(toStrings(id));
			qstri.append(",");
			for(int p=0; p<10; p++)
			{
				qstri.append(toStrings(towrite[i][p]));
				qstri.append(",");
			}
			for(int p=0; p<4; p++)
			{
				qstri.append(toStrings(eigen[i][p]));
				qstri.append(",");
			}
			qstri.append(toStrings(eigen[i][4]));
			qstri.append(")");
			cout<<qstri<<endl;
              	query << qstri;
                     query.execute();
			cout << query.error() << endl;
			query.reset();

		}
	
                query.reset();
                query << "UPDATE popstruct SET loadcmpt = 1 WHERE id = " << id << ";" << endl;
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

string toStrings(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();

	return s;
}

string toStrings(float i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();

	return s;
}

