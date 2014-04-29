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

int main(int argc, char* argv[])
{
	int subsz = 1000;

	if(argc != 5)
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string db;
	string mask;
	int tsid;
	int totaltraits;
	
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1:
				tsid = atoi(argv[i]);
				cout << tsid << endl;
				break;
			case 2: 
				mask = argv[i];
				cout << mask << endl;
				break;
			case 3: 
				totaltraits = atoi(argv[i]);
				//cout << mask << endl;
				break;
			case 4:
				db = argv[i];
				cout << db << endl;
				break;
		}
	}

	vector<int> mergedtraitnum;
	int fc=0;
	//read the correlation matrix to use for wrting files for spectral clustering
	double corrmat[totaltraits][totaltraits];
	string corfname = mask + "_" +"finalcor.txt";
	ifstream corfile(corfname.c_str());	
	if(corfile.is_open())
	{
		int idx1 = 0;
		while(! corfile.eof())
		{
			string line;
			getline(corfile, line);
			if(line.length()<=0)
					continue;
			vector<string> tokens;
			Tokenize(line, tokens,"\t");
			int totaltok = (int) tokens.size();
			
			for(int q=0; q< totaltok; q++)
			{
				corrmat[idx1][q] = atof(tokens[q].data()) ;
			}
			
			idx1++;
		}
	}
	
	int isdone[totaltraits]; 
	for(int tnr =0; tnr < totaltraits; tnr++ )
	{
		isdone[tnr] = 0;
	}
	cout<<"cor matrix read"<<endl;
	//file for writing filenames of files having trait ids which directly needs to be go for glasso
	string filenn = mask+"_noSC";
	ofstream myfilenn;
	myfilenn.open(filenn.c_str());
	
	string filenns = mask+"_SC";
	ofstream myfilenns;
	myfilenns.open(filenns.c_str());
	
	bool vanillaadded = false;
	int filecount=0;
	int scfilecount = 0;
	
	ofstream secondfile11;
    secondfile11.open("parms5.txt");
	bool isSCDone = false;	
	if(totaltraits > 3000)
	{
		while(true)
		{
			string filename = mask + "_subnet_" + toString(fc)+".txt";
			ifstream myfile(filename.c_str());
			cout<<"reading "<<filename <<endl;
			if(myfile.fail())
			{
				int writtencount=0;
				while(true)
				{
					bool towrite = false;
					cout<<"Total files: "<<fc<< "\n";
					cout<<"in end merged size: "<<(int)mergedtraitnum.size()<< "\n";
					string filennamee = mask+"_noSC_"+toString(filecount);
					ofstream myfilenname;
					myfilenname.open(filennamee.c_str());
						
					if(((int)mergedtraitnum.size()>0))
					{
						//write mergedtraitnum for glasso direct
						towrite =true;
						for(int ss=0; ss< (int) mergedtraitnum.size(); ss++)
						{
							myfilenname<<mergedtraitnum[ss]<<"\n";
							writtencount++;
							isdone[mergedtraitnum[ss]] =1;
						}
						
						mergedtraitnum.clear();
					}
				
					for(int tnr =0; tnr < totaltraits; tnr++ )
					{
						if(isdone[tnr] != 1)
						{	
							towrite =true;
							myfilenname<<tnr<<"\n";
							writtencount++;
							isdone[tnr] =1;
						}
						
						if(writtencount > 3000)
						{
							myfilenname.close();
							if(towrite)
								myfilenn << filennamee<<"\n";
					
							filecount++;
							break;
						}
					}
					if(writtencount > 3000)
					{
						writtencount = 0;
						continue;
					}
					else
					{
						myfilenname.close();
						if(towrite)
							myfilenn << filennamee<<"\n";
					
						filecount++;
						break;
					}
					
				}
				
				
				break;
			}
			string line;
			vector<int> traitnum;
			if(myfile.is_open())
			{
				while(! myfile.eof())
				{
					getline(myfile, line);
					if(line.length()<=0)
						continue;
					int  traitnumber = atoi(line.c_str());
					bool isexist = false;
					for(int ss=0; ss< (int) traitnum.size(); ss++)
					{
						if(traitnum[ss] == traitnumber)
						{
							isexist = true;
							break;
						}
					}
					if(!isexist)
						traitnum.push_back(traitnumber);
				}
			}
			cout<<"traitnum: "<<(int)traitnum.size()<< " merged: " << (int)mergedtraitnum.size() <<" "<<fc<<"\n";
				
			if(((int) traitnum.size() > 3000))
			{
				//write for spectral clustering
				//write index files
				//write matrix
				//write params for sc and merging
				cout<<"in more than 3000: "<<(int)traitnum.size()<< " " <<  fc<<"\n";
				
				string filennamee = mask+"_SC_"+toString(scfilecount);
				ofstream myfilenname;
				myfilenname.open(filennamee.c_str());
				for(int ss=0; ss< (int) traitnum.size(); ss++)
				{
					myfilenname<<traitnum[ss]<<"\n";
					isdone[traitnum[ss]] =1;
				}
				myfilenname.close();
				
				
				
				
				int minclust= ((int) traitnum.size() / 3000)+1;
				int maxclust = minclust+10;
				string toclustfilen = mask + "_" +"finalcor_"+toString(scfilecount);
				secondfile11 << "runSC.m addpath('../exe/');runSC('"<<toclustfilen<<"',"<<minclust<<",'"<<scfilecount<<"_"<<minclust<<"_sc');" <<endl;//change here
				if(!vanillaadded)
				{
					secondfile11 << "vanilla" << endl;
					vanillaadded = true;
				}
				int c =minclust+1;
				for(; c <= maxclust; c++  )
				{
					secondfile11 << "runSC.m addpath('../exe/');runSC('"<<toclustfilen<<"',"<<c<<",'"<<scfilecount<<"_"<<c<<"_sc');" <<endl;
				}
				myfilenns << scfilecount << " "<<minclust<<" "<<c-1<<" "<<"\n";
				
				ofstream myfname;
				myfname.open(toclustfilen.c_str());
				for(int ssi=0; ssi< (int) traitnum.size(); ssi++)
				{
					for(int ssj=0; ssj< (int) traitnum.size(); ssj++)
					{
					
						if( fabs(corrmat[(int)traitnum[ssi]][(int)traitnum[ssj]]) > .4)
						{
							myfname<<corrmat[(int)traitnum[ssi]][(int)traitnum[ssj]]<<"\t";
						}
						else
						{
							myfname<<0.0<<"\t";
						}
					}
					myfname<<"\n";
				}
				myfname.close();
				
				scfilecount++;
				isSCDone = true;
			}
			if(((int) traitnum.size() <= 3000) && ((int) traitnum.size() >= 1000))
			{
				cout<<"in less than 3000 and more than 1000: "<<(int)traitnum.size()<< " " <<  fc<<"\n";
				
				//write for final glasso params for merge sc
				string filennamee = mask+"_noSC_"+toString(filecount);
				ofstream myfilenname;
				myfilenname.open(filennamee.c_str());
				for(int ss=0; ss< (int) traitnum.size(); ss++)
				{
					myfilenname<<traitnum[ss]<<"\n";
					isdone[traitnum[ss]] =1;
				}
				myfilenname.close();
				myfilenn << filennamee<<"\n";
				filecount++;
			}
			if(((int) traitnum.size() < 1000) && ((int)mergedtraitnum.size()<2000))
			{
				cout<<"in less than 3000: "<<(int)traitnum.size()<< " "<< (int)mergedtraitnum.size()<<" " <<  fc<<"\n";
				
				for(int ss=0; ss< (int) traitnum.size(); ss++)
				{
					int tnum = traitnum[ss];
					bool isexist = false;
					for(int sss=0; sss< (int) mergedtraitnum.size(); sss++)
					{
						if(mergedtraitnum[sss] == tnum)
						{
							isexist = true;
							break;
						}
					}
					if(!isexist)
						mergedtraitnum.push_back(tnum);
				}
			}
			if(((int)mergedtraitnum.size()>=2000))
			{
				cout<<"in merge more than 2000: "<<(int)traitnum.size()<< " "<< (int)mergedtraitnum.size()<<" " <<  fc<<"\n";
				
				//write mergedtraitnum for glasso direct
				string filennamee = mask+"_noSC_"+toString(filecount);
				ofstream myfilenname;
				myfilenname.open(filennamee.c_str());
				for(int ss=0; ss< (int) mergedtraitnum.size(); ss++)
				{
					myfilenname<<mergedtraitnum[ss]<<"\n";
					isdone[mergedtraitnum[ss]] =1;
				}
				myfilenname.close();
				myfilenn << filennamee<<"\n";
				filecount++;
				mergedtraitnum.clear();
			}
			
			fc++;
		}
	}
	else
	{
			string filennamee = mask+"_noSC_"+toString(filecount);
			ofstream myfilenname;
			myfilenname.open(filennamee.c_str());
			for(int tnr =0; tnr < totaltraits; tnr++ )
			{
				myfilenname<<tnr<<"\n";				
			}
					
			myfilenname.close();
			myfilenn << filennamee<<"\n";	
			filecount++;
	}
	
	
	
	if(!isSCDone)
	{
		secondfile11 << "runSC.m addpath('../exe/');runSC('"<<"dummyfile"<<"',"<<-1<<",'"<<"dummy"<<"_sc');" <<endl;//change here
		if(!vanillaadded)
		{
			secondfile11 << "vanilla" << endl;
			vanillaadded = true;
		}
	}
	myfilenn.close();
	myfilenns.close();
	secondfile11.close(); 
	

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


