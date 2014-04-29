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
		
		q3.reset();
		int wrt[totalind*2][totalSNP+2];
		
		Query q4 = conn.query();
	    q4 << "SELECT sampleid FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [0]["id"] << " ORDER BY sampleid";
		StoreQueryResult ares4 = q4.store();
		
	  for (size_t i = 0; i <ares.num_rows(); i++)
	    {

		cout<<"startedwritemarkerfile"<<endl;
	      Query q2 = conn.query();
	      q2 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [i]["id"] << " ORDER BY sampleid";
		//cout << q2.str() << endl;
	      StoreQueryResult ares2 = q2.store();
	      noidis = ares2.num_rows();
	     // cout << noidis << endl;
		  //int wrt[2][totalSNP];
		  double tocheck;
		  //totalind = noidis;

		  
		for(size_t j = 0; j <ares2.num_rows(); j ++)
		{

			tocheck = (double)ares2[j]["value"];
			cout<<"startedwritemarkerfile--1 "<<tocheck<<endl;

			//add 2 columns//////////////////////
			wrt[(j+1)*2-2][0]=(int)ares4[j]["sampleid"];
			wrt[(j+1)*2-1][0]=(int)ares4[j]["sampleid"];
			wrt[(j+1)*2-2][1]=1;
			wrt[(j+1)*2-1][1]=1;
			if(tocheck==0)
			{
			//j,j+1
			//i+2
				wrt[(j+1)*2-2][i+2]=0;
				wrt[(j+1)*2-1][i+2]=0;
			}
			else if(tocheck==1)
			{
				wrt[(j+1)*2-2][i+2]=1;
				wrt[(j+1)*2-1][i+2]=0;
			}
			else if(tocheck==2)
			{
				wrt[(j+1)*2-2][i+2]=1;
				wrt[(j+1)*2-1][i+2]=1;
			}
			else
			{
				cerr << "I'm sorry. This algorithm cannot be used if the SNP values are not exactly 0,1 or 2. Found value " << tocheck << "\n";
				return -1;
			}
		  //myfile << ares2[j]["value"] << "\t";
		}
			q2.reset();
		}
		for(int in =0 ; in < totalind*2 ; in++)
		{
			for(int mk=0; mk < totalSNP+2; mk++  )
			{
				myfile << wrt[in][mk] << "\t";
			}
			myfile << endl;
		}
		/*q2 = conn.query();
	    q2 << "SELECT sampleid FROM markerval, marker WHERE markerid = marker.id AND markerid = " << ares [i]["id"] << " ORDER BY idx";
	    StoreQueryResult ares3 = q2.store();
		for(int r=0; r<2; r++)
		{
			cout<<"startedwritemarkerfile--write"<<endl;
			myfile << (int)ares3[0]["sampleid"] << "\t" << "1\t";
			for(int c=0; c< noidis; c++)
			{
				myfile << wrt[r][c] << "\t";
			}
	      myfile << endl;
	      
	    }
		//myfile << endl;
		
		}*/
	  myfile.close();
	  
	  //write parameters
	  ofstream myfile1;
      myfile1.open("parms1.txt");
	  myfile1<<"-i "<<filename<<" -o "<<"structout2"<<" -K 2"<< " -L "<< totalSNP<<" -N "<<totalind<<"\n";
	  myfile1<<"standard\n";
	  for(int i=3; i <=10 ; i++)
	  {
		myfile1<<"-i "<<filename<<" -o "<<"structout"<<i<<" -K "<< i << " -L "<< totalSNP<<" -N "<<totalind<<"\n";
		
        
	  }
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
    	myfile1.open("parms2.txt");
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
        myfile.open("parms3.txt");
		myfile <<mask<<" "<<msid<<" "<<name_str<<" "<<totalind<<" "<<db<<endl;
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
