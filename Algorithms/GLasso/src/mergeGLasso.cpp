#include <iostream>
#include <fstream>
#include <sstream>
#include <mysql++.h>
#include <vector>
#include <math.h>
#include "../inc/stats.h"


using namespace std;
using namespace mysqlpp;

string toString(int i);
//int getOtherDim(int tsid);
string toStringf(float i);
void Tokenize(const string& str,vector<string>& tokens,const string& delimiters);
int calcR2ScaleFreeness(double** arr, int nobins, int k);
double calcMin(double** sums, int idx, int k);
double calcMax(double** sums, int idx, int k);
double* getMaxForBins(double interval, int bins, double min);
double* binValues(double** arr, int idx, int bins, double* maxForBins, int k);

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
	int totaltraits;
	
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 2:
				tsid = atoi(argv[i]);
				cout << tsid << endl;
				break;
			case 3: 
				mask = argv[i];
				cout << mask << endl;
				break;
			case 1: 
				totaltraits = atoi(argv[i]);
				//cout << mask << endl;
				break;
			case 5:
				db = argv[i];
				cout << db << endl;
				break;
			case 4:
				team = argv[i]; cout << team; break;
		}
	}

	//read number of total files
	int totalclusts = 0; 
	string filenns_nosc = mask+ "_" +"finalSelectClust";
	ifstream myfile_nosc(filenns_nosc.c_str());
	cout << "reading "<<filenns_nosc.c_str()<<endl;
	if(myfile_nosc.is_open())
	{
		cout << "in if" << endl;
		while(! myfile_nosc.eof())
		{
			string ll2;
			getline(myfile_nosc, ll2);
			if(ll2.length()<=0)
					continue;
			cout<<"file read "<<ll2.c_str();
			totalclusts = atoi(ll2.c_str());
		}
	}
	cout<<"total clusters "<<totalclusts;
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
				traitsinclust[tc].push_back(trnum);
			}
		}
	}
	
	double **matrixforscaling = new double*[totaltraits];

    	for(int i = 0; i < totaltraits; i++)
       	matrixforscaling[i] = new double[9];
	
	int coll=0;
	
	for(float lambda=.1 ; lambda <1; lambda=lambda+.1)
	{
		cout << "Lambda = "<<lambda << endl;
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
			
			ifstream myfile1(glassout.c_str());
			if(myfile1.is_open())
			{
				cout << "opening file: "<<glassout.c_str()<<endl;
				cout<< "size of clust "<<tc<< " "<<traitsinclust[tc].size()<<endl;
				string ll2;
				//ignore first line having columns name
				getline(myfile1, ll2);
				if(ll2.length()<=0)
					continue;
				int ii=0;
				
				//while(!myfile1.eof())
				while(ii < traitsinclust[tc].size())
				{
					getline(myfile1, ll2);
					if(ll2.length()<=0)
					continue;
					vector<string> tokens;
					Tokenize(ll2, tokens," ");
					int totaltok = (int) tokens.size();
					for(int q=1; q <= traitsinclust[tc].size(); q++)
					{
						//if(q==1 || q == traitsinclust[tc].size())
							//cout << "Values " <<q<<" "<< atof(tokens[q].data())<<" "<<traitsinclust[tc][ii]<< " " << traitsinclust[tc][q-1]<< endl;
						finalmatrix[traitsinclust[tc][ii]][traitsinclust[tc][q-1]] = atof(tokens[traitsinclust[tc].size()+q].data());
					}
					ii++;
					
				}
			}
			
		}
		int nonzero=0;
		float maxx = -9999;
		float minn = 99999;
		for(int t1=0; t1 < totaltraits; t1++ )
		{
			float summ = 0.0;
			for(int t2=t1+1; t2 < totaltraits ; t2++)
			{
					float curc = finalmatrix[t1][t2];
					if(t1 != t2)
						summ += fabs(curc);
					
					if(finalmatrix[t1][t2] !=0)
						nonzero++;
					if(maxx < finalmatrix[t1][t2])
						maxx = finalmatrix[t1][t2];
					if(minn >  finalmatrix[t1][t2])
						minn = finalmatrix[t1][t2];
			}
			matrixforscaling[t1][coll] = summ;
		}
		cout << "Non Zeros " << nonzero << " Max: "<<maxx<<" Min: "<<minn<<endl;
		coll++;
		//here we get the complete matrix for each lambda from glasso results
		//generate sum and write param files for 
			
	}
	
	cout<<"printing matrix for scalefreeness"<<endl;
	
	for(int rr=0; rr < totaltraits ; rr++)
	{
		for(int cc=0; cc < 10; cc++)
		{
			cout<<matrixforscaling[rr][cc]<< " ";
		}
		cout << " " << endl;
	}
	
	
	cout<<"going for Scale freeness "<<endl;
	//double *pArr = *matrixforscaling;
	int k = totaltraits;
	int nobins = k/2 < 50 ? k/2 : 50;
	cout<<"calling function "<<endl;
	int beta = calcR2ScaleFreeness(matrixforscaling, nobins,k);
	cout<<"function beta"<< beta<<endl;

	float finallambda = (beta+1)*.1;
	ofstream myfileee;
	myfileee.open("parms9.txt");
	myfileee<<finallambda << " "<< k << " " << tsid << " " << mask << " "<< team << " " << db << endl;
	myfileee<<"vanilla" << endl;
					
	myfileee.close();
	
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

///from sfn2
int calcR2ScaleFreeness(double** arr, int bins, int k)
{
	int maxindex = -1;
	double maxr = -9999;
	for(int i = 0; i < 9; i ++)
	{
		double min = calcMin(arr, i, k);
		double max = calcMax(arr, i, k);

		double interval = (max - min) / (double)bins;
		double * maxForBins = getMaxForBins(interval, bins, min);

		double * dabins = binValues(arr, i, bins, maxForBins, k);

		for(int j = 0; j < bins; j ++)
		{
			maxForBins[j] -= interval/2.0;
		}

		double * vals1 = new double[bins];
		double * vals2 = new double[bins];

		for(int j = 0; j < bins; j ++)
		{
			if(dabins[j] != 0)
				vals1[j] = log10(dabins[j]/k);
			else
				vals1[j] = 0.0;
		//	cout << vals1[j] << endl;
		}
		//cout << endl;

		for(int j = 0; j < bins; j ++)
		{
			if(maxForBins[j] != 0)
				vals2[j] = log10(maxForBins[j]);
			else
				vals2[j] = 0.0;
		//	cout << vals2[j] << endl;
		}

		Stats stat;		
		double r = stat.calcR(vals1, vals2, bins);
		r = r * r;
		//cout << k << endl;

		cout << r << endl;

		delete[] vals1;
		delete[] vals2;
		delete[] maxForBins;
		delete[] dabins;

		if(maxr <= r)
		{
			maxindex = i;
			maxr = r;
		}
		if(r > .7)
			return i;
	}
	return maxindex;
}

double* binValues(double** arr, int idx, int nobins, double* maxForBins, int k)
{
	double * bins = new double[nobins];
	for(int i = 0; i < nobins; i ++)
		bins[i] = 0.0;

	for(int i = 0; i < k; i ++)
	{
		for(int j = 0; j < nobins; j ++)
		{
			if(arr[i][idx] < maxForBins[j])
			{
				bins[j]++;
				break;
			}
		}
	}

	//not sure why we do this step ...
	for(int i = 0; i < nobins; i ++)
	{
		if(bins[i] == 0 && i != nobins - 1)
		{
			bins[i] = (bins[i-1] + bins[i+1]) / 2.0;	
		}
		else if(i==nobins-1)
		{
			bins[i] = .5;
		}
	}

	return bins;
}

double* getMaxForBins(double interval, int bins, double min)
{
	double* maxForBins = new double[bins];
	maxForBins[0] = min + interval;
	for(int i = 1; i < bins; i ++)
	{
		maxForBins[i] = maxForBins[i-1] + interval;
	}
	return maxForBins;
}

double calcMin(double** sums, int idx, int k)
{
	double min = 1e99;
	for(int i = 0; i < k; i ++)
	{
		if(sums[i][idx] < min)
			min = sums[i][idx];
	}
	return min;
}

double calcMax(double** sums, int idx, int k)
{
	double max = -1e99;
	for(int i = 0; i < k; i ++)
	{
		if(sums[i][idx] > max)
			max = sums[i][idx];
	}
	return max;
}


/// end of from sfn2


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


