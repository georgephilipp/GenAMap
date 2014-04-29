#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <math.h>
#include <cstdlib>

using namespace std;
using namespace mysqlpp;
//argv1 = project appID

int getmsid(string mask);
int writeMarkerFile(string mask, string filename);
int writeMarkerFile_str(string mask, string filename);
void createParmFiles(string mask, int msid, int totalind,string db,string name_str);
int copyfile(char* inputFile,char* outputFile);
string db;
string team;

int main(int argc, char* argv[])
{
	cout<<"started\n";

	if(argc != 5)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string file;
	string mask;
	string name;
	string name_str;

	int netid;
	int id;
	int k;
	int j;
	int method=0;
cout<<"started1\n";

	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				mask = argv[i];
				cout << file << endl<< endl;
				break;
	  	        
			case 2: name_str = argv[i];
				break;

			case 3: team = argv[i];
				break;

			case 4: db = argv[i];
				break;
		}
	}
	std::system("./../exe/copyFilesStructure.sh");
	cout<<"\n\nstarted2\n"<<endl;
	int msid = getmsid(mask);
	
	string markerFileName = mask + "_m";
	int c = copyfile("../exe/mainparams","mainparams");
	 if(c==-1)
		return -1;

	c = copyfile("../exe/extraparams","extraparams");
	 if(c==-1)
		return -1;

	 int h= writeMarkerFile(mask, markerFileName);
	 if(h==-1)
		return -1;
	int totalind= writeMarkerFile_str(mask, markerFileName + "_str");
	if(totalind==-1)
		return -1;
		
	createParmFiles(mask, msid, totalind, db, name_str);
	
	return 0;
	
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

int writeMarkerFile_str(string mask, string filename)
{


	int noidis = 0;
	int totalind =0;
	int totalSNP=0;
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
	query << "Select get_ms_id('"<<mask<<"') as id";
	StoreQueryResult ares = query.store();
	
	
	
	int id;
	if(!ares)
        {
	  cerr<<query.error()<<endl;
	  return -1;
	}
	for(size_t i = 0; i<ares.num_rows(); i++)
	 {
	   id = ares[i]["id"];
	   //cout << id << endl;
	 }
	query.reset();
cout<<"reached";
	//cout << "ID" << id << endl;
	query << "SELECT id FROM marker WHERE markersetid= " << id << " ORDER BY idx";
	//cout << query.str() << endl;
	ares = query.store();
	if(!ares)
	  {
	    cerr << "Right here. " << endl;
	    cerr << query.error()<<endl;
	    return -1;
	  }  
	else
	{

		cout<<"ent else "<<endl;
	  ofstream myfile;
	  myfile.open(filename.c_str());
	  totalSNP = ares.num_rows();
	  Query q3 = conn.query();
	    q3 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [0]["id"] << " ORDER BY sampleid";
		StoreQueryResult ares3 = q3.store();
		totalind= ares3.num_rows();
		
	}

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
	    cerr << "Error :" << er.what() << endl;
	    return -1;
	  }
	//cout << noidis;
	return totalind;
}

int writeMarkerFile(string mask, string filename)
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
	query << "Select get_ms_id('"<<mask<<"') as id";
	StoreQueryResult ares = query.store();
	
	int id;
	if(!ares)
        {
	  cerr<<query.error()<<endl;
	  return -1;
	}
	for(size_t i = 0; i<ares.num_rows(); i++)
	 {
	   id = ares[i]["id"];
	   //cout << id << endl;
	 }
	query.reset();
	//cout << "ID" << id << endl;
	query << "SELECT id FROM marker WHERE markersetid= " << id << " ORDER BY idx";
	//cout << query.str() << endl;
	ares = query.store();
	if(!ares)
	  {
	    cerr << "Right here. " << endl;
	    cerr << query.error()<<endl;
	    return -1;
	  }  
	else
	{
	
		//run here query once to get size of samples and than store it in a 2d int array
		Query q3 = conn.query();
	    q3 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [0]["id"] << " ORDER BY sampleid";
		StoreQueryResult ares3 = q3.store();
		int totalind= ares3.num_rows();
		int totalmark = ares.num_rows();
		q3.reset();
		double towrite[totalind][totalmark];
	  ofstream myfile;
	  myfile.open(filename.c_str());
	  for (size_t i = 0; i <ares.num_rows(); i++)
	    {
		
	      Query q2 = conn.query();
	      q2 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [i]["id"] << " ORDER BY sampleid";
		//cout << q2.str() << endl;
	      StoreQueryResult ares2 = q2.store();
	      noidis = ares2.num_rows();
	      //cout << noidis << endl;
		for(size_t j = 0; j <ares2.num_rows(); j ++)
		{
			towrite[j][i] = (double)ares2[j]["value"];
		  //myfile << ares2[j]["value"] << "\t";
		}
	      //myfile << endl;
	      q2.reset();
	    }
	
		for(int ind=0; ind <totalind ; ind++ )
		{
			for(int mark=0; mark<totalmark; mark++)
			{
				myfile << towrite[ind][mark] << "\t";
			}
			myfile << endl;
		}
	  myfile.close();

	ofstream myfile1;
    	myfile1.open("parms1.txt");
	myfile1<<"PCA_vis.m filename='"<< filename<<"';PCA_vis"<<"\n"<<"vanilla";
    	myfile1.close();
	}

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
	    cerr << "Error :" << er.what() << endl;
	    return -1;
	  }
	//cout << noidis;
	
	return noidis;
}
void createParmFiles(string mask, int msid, int totalind,string db, string name_str)
{
        ofstream myfile;
        myfile.open("parms2.txt");
		myfile <<mask<<" "<<msid<<" "<<name_str<<" "<<totalind<<" "<<team << " " << db<<endl;
		myfile<<"vanilla"<<endl;
        myfile.close();
}


int copyfile(char* inputFile,char* outputFile)
{

	std::ifstream infile(inputFile, std::ios_base::binary);
	std::ofstream outfile(outputFile, std::ios_base::binary);

	outfile << infile.rdbuf();
	return 1;

	char data[50];
 

	ifstream inputFilePatch;

	inputFilePatch.open(inputFile);
 

	if(!inputFilePatch) {

		cout << "Wrong Location!!!\n";


		return -1;

	}

 

	ofstream outputFilePatch;

	outputFilePatch.open(outputFile);

	outputFilePatch<<data;

 

	while(inputFilePatch>>data){

		outputFilePatch<<data;

	};

 

	outputFilePatch.close();

 

	
	return 0;



}
