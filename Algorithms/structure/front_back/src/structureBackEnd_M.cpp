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

string team;
string db;

int main(int argc, char* argv[])
{
	

	if(argc != 7)
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
			case 5: team = argv[i]; break;
			case 6:
				db = argv[i];
				break;

		
		}
	}

	insertIntoDB(msid, name,totalind);


	return 0;
}

void insertIntoDB(int msid, string name, int totalind)
{
	//int towrite[totalind][10];
	float eigen[totalind][5]; 
	try
	{
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


		//get sample ids in order of idx
		ifstream sqlparmfile("../exe/SQLparms.txt");
		string servername, dbname, username, password;
		getline(sqlparmfile,servername);
		getline(sqlparmfile,dbname);
		getline(sqlparmfile,username);
		getline(sqlparmfile,password);
		Connection conn( dbname.c_str(), servername.c_str(), username.c_str(), password.c_str()); 
		Query query = conn.query();
		query << "SELECT id FROM marker WHERE markersetid= " << msid << " ORDER BY idx";
		StoreQueryResult ares = query.store();
		StoreQueryResult ares4;
		if(!ares)
		{
			cerr << "Right here. " << endl;
			cerr << query.error()<<endl;
			//return -1;
		}  
		else
		{
			Query q4 = conn.query();
			q4 << "SELECT sampleid FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [0]["id"] << " ORDER BY sampleid";
			ares4 = q4.store();
			cerr << q4.error()<< endl;
		}
		
		//update table with eigs when we have popstructid and sampleid

		for (size_t i = 0; i <ares4.num_rows(); i++)
	    {
			Query q2 = conn.query();
			string qstri = "UPDATE structure SET ";
			
			for(int p=0; p<4; p++)
			{
				qstri.append(" eig"+toStrings(p+1)+"="+toStrings(eigen[i][p]));
				qstri.append(",");
			}
			qstri.append(" eig5="+toStrings(eigen[i][4]));
			qstri.append(" WHERE popstructid="+name +" AND sampleid="+toStrings((int)ares4[i]["sampleid"]));
			q2 << qstri;
			q2.execute();
			cout << qstri << endl << endl;
			cerr << q2.error() << endl;
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

