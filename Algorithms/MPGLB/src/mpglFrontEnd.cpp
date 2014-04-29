#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdlib>
#include <mysql++.h>
#include <math.h>

using namespace std;
using namespace mysqlpp;
//argv1 = project appID

int getmsid(string mask);
int gettsid(string mask);
int writeMarkerFile(string mask, string filename);
int writeTraitFile(string mask);
void createParmFiles1(int totaltrait);
void createParmFiles2(string mask, int popid, int popno, int totaltrait, int totalmarker, string db, string name_str);
int copyfile(char* inputFile,char* outputFile);
int writeStructureFile(int popid, int popnum);
string toStrings(int i);
string db;
string team;

int main(int argc, char* argv[])
{
	cout<<"started\n";

	if(argc != 7)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string file;
	string mask;
	string name;
	string name_str;

	int popid;
	int popno;
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
				
			case 3: popid = atoi(argv[i]);
				break;
				
			case 4: popno = atoi(argv[i]);
				break;
			case 5: team = argv[i];
				break;
			case 6: db = argv[i];
				break;
		}
	}
	std::system("./../exe/copyFilesMPGL.sh");
	cout<<"\n\nstarted2\n"<<endl;
	int msid = getmsid(mask);
	cout<<"\nmsid="<<msid<<endl;
	
	int tsid = gettsid(mask);
	cout<<"\ntsid="<<tsid<<endl;
	string markerFileName = "data.test.X";
	
	 int h= writeMarkerFile(mask, markerFileName);
	 if(h==-1)
		return -1;

	int tts = 	writeTraitFile(mask);
	if(tts==-1)
		return -1;

	int str = writeStructureFile(popid, popno);
	if(h==-1)
		return -1;

	createParmFiles1(tts);	

	createParmFiles2(mask, popid, popno, tts, h, db, name_str);

	return 0;
	
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

int writeStructureFile(int popid, int popnum)
{
	int noidis = 0;
	int tts=-1;
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
				string pp = "pop"+toStrings(popnum);
				query << "SELECT "<<pp<<" FROM structure WHERE popstructid = " << popid << " ORDER BY sampleid";
                StoreQueryResult  ares = query.store();
				
              
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
						ofstream myfile;
						string fname = "data.test.Z";
						myfile.open(fname.c_str());
						tts  = ares.num_rows();
					
					for(size_t i = 0; i < ares.num_rows(); i ++)
					{
						
						myfile << ares[i][pp.c_str()] << "\n";
					
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
	return tts;
}

int writeTraitFile(string mask)
{
	int noidis = 0;
	int tts=-1;
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

                query << "SELECT id FROM trait WHERE traitsetid = " << id << " ORDER BY idx";
                ares = query.store();
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return -1;
                }
                else
                {
			
			tts  = ares.num_rows();
			int fnum=1;
			
			ofstream myfile1;
			string fname1 = "tr_id";
			myfile1.open(fname1.c_str());
			for(size_t i = 0; i < ares.num_rows(); i ++)
			{
				
				ofstream myfile;
				string fname = "tr"+toStrings(fnum);
				myfile.open(fname.c_str());
				Query q2 = conn.query();
				myfile1 << ares[i]["id"] << "\n";
				query << "SELECT value FROM traitval" << team << ",trait WHERE traitid=trait.id AND traitid = " << ares[i]["id"] << " ORDER BY idx";
				StoreQueryResult ares2 = query.store();
				noidis = ares2.num_rows();
				for(size_t j = 0; j < ares2.num_rows(); j ++)
				{
					myfile << ares2[j]["value"] << "\n";
				}
				myfile << endl;

				q2.reset();
				myfile.close();
				fnum++;
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
                cerr << "Conversion error: " << er.what() << endl;
                return -1;
        }
        catch(const Exception& er)
        {
                cerr << "Error: " << er.what() << endl;
                return -1;
        }
	return tts;
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

	    q3 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [0]["id"] << " ORDER BY idx";
		StoreQueryResult ares3 = q3.store();
		int totalind= ares3.num_rows();
		int totalmark = ares.num_rows();
		q3.reset();
		double towrite[totalind][totalmark];
	  ofstream myfile;
	  myfile.open(filename.c_str());
	  
	  ofstream myfile11;
	  string fn = "data.vocab";
	  myfile11.open(fn.c_str());
	  noidis = ares.num_rows();
	  for (size_t i = 0; i <ares.num_rows(); i++)
	    {
		
			myfile11 << ares[i]["id"] << "\n";
	      Query q2 = conn.query();

	      q2 << "SELECT value FROM markerval" << team << ", marker WHERE markerid = marker.id AND markerid = " << ares [i]["id"] << " ORDER BY idx";
		//cout << q2.str() << endl;
	      StoreQueryResult ares2 = q2.store();
	     // noidis = ares2.num_rows();
	      //cout << noidis << endl;
		for(size_t j = 0; j <ares2.num_rows(); j ++)
		{
			towrite[j][i] = (double)ares2[j]["value"] ;
		  //myfile << ares2[j]["value"] << "\t";
		}
	      //myfile << endl;
	      q2.reset();
	    }
		myfile11.close();
		for(int ind=0; ind <totalind ; ind++ )
		{
			for(int mark=0; mark<totalmark; mark++)
			{
				myfile << towrite[ind][mark] << "\t";
			}
			myfile << endl;
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
void createParmFiles2(string mask, int popid, int popno, int totaltrait, int totalmarker, string db, string name_str)
{

	int distinctpop = -1;
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
				string pp = "pop"+toStrings(popno);
				//SELECT COUNT(DISTINCT pop1 ) as number FROM structure where popstructid = 1;
				query << "SELECT COUNT(DISTINCT "<<pp<<" ) as number FROM structure WHERE popstructid = " << popid;
                StoreQueryResult  ares = query.store();
				
              
                if(!ares)
                {
                        cerr << query.error() << endl;
                        return;
                }
                else
                {
					
					for(size_t i = 0; i < ares.num_rows(); i ++)
					{
						
						distinctpop = ares[i]["number"] ;
					
					}
					
			
                }
        }
        catch(BadQuery er)
        {
                cerr << "Error: " << er.what() << endl;
                return;
        }
        catch(const BadConversion& er)
        {
                cerr << "Conversion error: " << er.what() << endl;
                return;
        }
        catch(const Exception& er)
        {
                cerr << "Error: " << er.what() << endl;
                return;
        }
		
        ofstream myfile;
        myfile.open("parms2.txt");
		myfile <<mask<<" "<<popid<<" "<<popno<<" "<<totaltrait<<" "<<totalmarker<<" "<<name_str<<" "<<db<<" "<<distinctpop<< " " << team << endl;
		myfile<<"vanilla"<<endl;
        myfile.close();
}

void createParmFiles1(int totaltrait)
{
	ofstream myfile1;
    myfile1.open("parms1.txt");
	myfile1<<"main_function_parallelRunning.m x_fileName='data.test.X',y_fileName='"<< "tr"<<toStrings(1)<<"',z_fileName='data.test.Z',v_filename='data.vocab',out_file_f='outputf"<<toStrings(1)<<"';main_function_parallelRunning"<<"\n"<<"vanilla\n";
    for(int i=2; i <=totaltrait ; i++)
	{
		myfile1<<"main_function_parallelRunning.m x_fileName='data.test.X',y_fileName='"<< "tr"<<toStrings(i)<<"',z_fileName='data.test.Z',v_filename='data.vocab',out_file_f='outputf"<<toStrings(i)<<"';main_function_parallelRunning"<<"\n";
	}
	myfile1.close();
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

string toStrings(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();

	return s;
}
