#include <iostream>
#include "../inc/filereader.h"
#include "../inc/stats.h"
#include <sstream>
#include <math.h>
#include <cstdlib>

using namespace std;

int readfile(Filereader* reader, double** arr, int i, int j, int k, int subsz, bool invert);
int calcR2ScaleFreeness(double** arr, int nobins, int k);
double calcMin(double** sums, int idx, int k);
double calcMax(double** sums, int idx, int k);
double* getMaxForBins(double interval, int bins, double min);
double* binValues(double** arr, int idx, int bins, double* maxForBins, int k);

string toString(int i);

//argv1 = trait file 1 name
//argv2 = trait file 2 name
//argv3 = methurd
//argv4 = output mask
int main(int argc, char* argv[])
{
	double** arr1 = 0;

	if(argc != 3)
	{
		fprintf(stderr, "Invalid number of arguments\n");
		return -1;
	}
	int k;
	string mask;
	int subsz = 1000;//must be the same across all of them
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 2: 
				mask = argv[i];
				cout << mask << endl;
				break;
			case 1:
				k = atoi(argv[i]);
				cout << k << endl;
				break;
		}
	}
	arr1 = new double*[k];
	
	/*for(int i = 0; i < k; i ++)
	{
		arr1[i] = new double[20];
		for(int j = 0; j < 20; j ++)
		{
			arr1[i][j] = 0;
		}
	}*/
	Filereader reader1;
	string file1;
	int idx = 0;

	for(int i = 1; i <= k; i +=subsz)
	{
		file1 = mask + "_" + toString(i) + "sum.txt";
		if(!reader1.openFile(file1))
		{
			cerr << "Cannot open one or more of these files " << file1 << endl;
			return -1;
		}
		for(int j = 0; j < subsz && j < k+1-i; j ++)
		{
			arr1[idx++] = reader1.getNextLine(20);	
		}
		reader1.close();

		for(int j = i; j <=k; j += subsz)
		{
			int result;
			file1 = mask + "_" + toString(i) + "_" + toString(j) + "res.txt";
			string newname = file1 + "_cor.txt";
			result = rename( file1.c_str(), newname.c_str());
			if(result == 0)
				cout << "File successfully renamed" << endl;
			else
				cerr << "Error renaming files !!! " << file1 << " " << newname << endl;
		}
	}
/*	for(int i = 0; i < k; i ++)
	{
		for(int j = 0; j < 20; j ++)
			cout << arr1[i][j] << " ";
		cout << endl;
	}
*/
	int nobins = k/2 < 50 ? k/2 : 50;
	int beta = calcR2ScaleFreeness(arr1, nobins,k);

	ofstream myfile;
	myfile.open("beta.txt");
	myfile << beta << endl;
	myfile.close();

	for(int i = 0; i < k; i ++)
		delete[] arr1[i];
	delete[] arr1;

	return 0;

}

int calcR2ScaleFreeness(double** arr, int bins, int k)
{
	for(int i = 0; i < 20; i ++)
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
			vals1[j] = log10(dabins[j]/k);
		//	cout << vals1[j] << endl;
		}
		//cout << endl;

		for(int j = 0; j < bins; j ++)
		{
			vals2[j] = log10(maxForBins[j]);
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

		if(r > .8)
			return i + 1;
	}
	return 20;
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

string toString(int i)
{
        std::string s;
        std::stringstream out;
        out << i;
        s = out.str();
        return s;
}

