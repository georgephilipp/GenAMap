#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>

using namespace std;
using namespace mysqlpp;

void Tokenize(const string& str,vector<string>& tokens,const string& delimiters);
string toStrings(int i);
string toStrings(float i);
int getmsid(string mask);
int gettsid(string mask);

string db;

int main(int argc, char* argv[])
{
	
		
	if(argc != 10)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	
	string mask;
	int popid,popno,totaltrait,totalmarker,distpop;
	string name, team;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			
			case 1: 
			    mask = argv[i];
				cout << mask << endl;
				break;      
			case 2:
				popid = atoi(argv[i]);
				cout << popid << endl;
				break;
			case 3:
				popno = atoi(argv[i]);
				break;
			case 4:
				totaltrait = atoi(argv[i]);
				break;
			case 5:
				totalmarker = atoi(argv[i]);
				break;
			case 6:
				name = argv[i];
				break;
			case 7:
				db = argv[i];
				break;
			case 8:
				distpop = atoi(argv[i]);
				break;
			case 9: 
				team = argv[i];
				break;		
		}
	}

	int msid = getmsid(mask);
	cout<<"\nmsid="<<msid<<endl;
	
	int tsid = gettsid(mask);
	cout<<"\ntsid="<<tsid<<endl;
	
	int traitids[totaltrait];
	int markerids[totalmarker];
	
	string file = "tr_id";
	ifstream myfile(file.c_str());
	if(myfile.is_open())
	{
		int i=0;
		while(! myfile.eof())
		{
			string line;
			getline(myfile, line);
			traitids[i] = atoi(line.c_str());
			i++;
		}
	}
	cout<<"\nread tr_id "<<endl;

	string file1 = "data.vocab";
	ifstream myfile1(file1.c_str());
	if(myfile1.is_open())
	{
		int i=0;
		while(! myfile1.eof())
		{
			string line;
			getline(myfile1, line);
			markerids[i] = atoi(line.c_str());
			i++;
		}
	}
	
	cout<<"\nread data.vocab "<<endl;

	bool pval = false;
	ifstream sqlparmfile("../exe/SQLparms.txt");
	string servername, dbname, username, password;
	getline(sqlparmfile,servername);
	getline(sqlparmfile,dbname);
	getline(sqlparmfile,username);
	getline(sqlparmfile,password);
	Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str()); 
    Query query = conn.query();
	query << "SELECT createAssoc_noNet(" << tsid << "," <<msid  <<"," <<0 << ",'" << name << "'," << pval << " ) as id";
	StoreQueryResult ares = query.store();
//SELECT COUNT(DISTINCT pop1 ) as number FROM structure where popstructid = 1;

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
	cout<<"\nread id from database  "<< id<<" "<<endl;
	cout << distpop << endl;
	query<<"UPDATE assocset SET popid="<<popid<<", popnum="<<distpop<<" where id = "<<id;
	query.execute();
	cout << query.error() << endl;
	query.reset();

	for(int trt=1; trt <= totaltrait; trt++)
	{
		cout<<"\nin for "<<" "<<endl;

		int pp=0;
		int trtid = traitids[trt-1];
		string tfile = "outputf"+toStrings(trt);
		ifstream myfilet(tfile.c_str());
		if(myfilet.is_open())
		{	
			while(! myfilet.eof())
			{
				string line;
				getline(myfilet, line);
				cout<<"\nreadline:"<<line<<endl;
				//if(line.compare(" ") == 0)
				//{
					
					//cout<<"\nincomaprison"<< line<<" "<<endl;

					//pp++;
				//}
				//else
				//{
					cout<<"\ninelse:"<<endl;
					vector<string> tokens;
					Tokenize(line, tokens,"\t ");
					if(tokens.size() <=0)
					{
						pp++;
						continue;
					}
					cout<<"label "<<atoi(tokens[1].data())<<endl;
					int markid = atoi(tokens[0].data());
					double beta =  atof(tokens[1].data());
					 query << "INSERT INTO association" << team << " (markerid, traitid, value, assocsetid,popref) VALUES("<<markid<<","<<trtid<<","<<beta<<","<<id<<","<<pp<<")";
					cout<<"\nquery:"<<query<<endl;
					query.execute();
					cout << query.error() << endl;
					query.reset();
				//}
			}
		}
		
	}
	
	query.reset();
	query<<"UPDATE assocset SET loadcmpt=1 where id = "<<id;
	query.execute();
	cout << query.error() << endl;
	query.reset();
	
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
      //cout << conn.error() << endl;
      Query query = conn.query();
      query << "SELECT get_ts_id('" << mask << "') as id";
      StoreQueryResult ares = query.store();

      if(!ares)
	{
	  cerr << query.error() << endl;
	  return -1;

	}
      int id;
      for(size_t i = 0; i < ares.num_rows(); i++)
	{
	  id = ares[i]["id"];
	  return id;
	}  


    }
  catch(BadQuery er)
    {
      cerr<< "Error: " <<er.what() <<endl;
      return -1;
    }
  catch(const BadConversion& er)
    {
      cerr <<"Conversion error: " << er.what() << endl;
      return -1;
    }
  catch (const Exception& er)
    {
      cerr << "Error: " <<er.what() << endl;
      return -1;
    }

}

int getmsid(string mask)
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
      //cout << conn.error() << endl;
      Query query = conn.query();
      query << "SELECT get_ms_id('" << mask << "') as id";
      StoreQueryResult ares = query.store();

      if(!ares)
	{
	  cerr << query.error() << endl;
	  return -1;

	}
      int id;
      for(size_t i = 0; i < ares.num_rows(); i++)
	{
	  id = ares[i]["id"];
	  return id;
	}  


    }
  catch(BadQuery er)
    {
      cerr<< "Error: " <<er.what() <<endl;
      return -1;
    }
  catch(const BadConversion& er)
    {
      cerr <<"Conversion error: " << er.what() << endl;
      return -1;
    }
  catch (const Exception& er)
    {
      cerr << "Error: " <<er.what() << endl;
      return -1;
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

