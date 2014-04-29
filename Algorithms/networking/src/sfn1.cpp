#include <iostream>
#include "../inc/filereader.h"
#include "../inc/stats.h"
#include <sstream>
#include <cstdlib>

using namespace std;

int readfile(Filereader* reader, double** arr, int i, int j, int k, int subsz, bool invert);
double abs(double a);
double pow(double a, double power);
string toString(int i);

//argv1 = trait file 1 name
//argv2 = trait file 2 name
//argv3 = methurd
//argv4 = output mask
int main(int argc, char* argv[])
{
	double** arr1 = 0;

	if(argc != 4)
	{
		fprintf(stderr, "Invalid number of arguments\n");
		return -1;
	}
	int k;
	string mask;
	int subset;
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
			case 3:
				subset = atoi(argv[i]);
				cout << subset << endl;
				break;
		}
	}
	arr1 = new double*[subsz];
	
	for(int i = 0; i < subsz; i ++)
	{
		arr1[i] = new double[20];
		for(int j = 0; j < 20; j ++)
		{
			arr1[i][j] = -1;
		}
	}
	Filereader reader1;
	string file1;

	for(int i = 1; i <= k; i +=subsz)
	{
		for(int j = i; j <= k; j +=subsz)
		{
			if(i == subset)
			{
				file1 = mask + "_" + toString(i) + "_" + toString(j) + "res.txt";
				if(!reader1.openFile(file1))
				{
					cerr << "Cannot open one or more of these files " << file1 << endl;
					return -1;
				}
				readfile(&reader1, arr1, i, j, k, subsz, false);
				reader1.close();
			}
			else if(j == subset)
			{
				file1 = mask + "_" + toString(i) + "_" + toString(j) + "res.txt";
				if(!reader1.openFile(file1))
				{
					cerr << "Cannot open one or more of these files " << file1 << endl;
					return -1;
				}
				readfile(&reader1, arr1, i, j, k, subsz, true);
				reader1.close();
			}
		}
	}
	string f = mask + "_" + toString(subset) + "sum.txt";
	ofstream myfile;
	myfile.open(f.c_str());
	for(int i = 0; i < subsz; i ++)
	{
		for(int j = 0; j < 20; j ++)
			myfile << arr1[i][j] << "\t";
		myfile << endl;
	}
	myfile.close();
	for(int i = 0; i < subsz; i ++)
		delete[] arr1[i];
	delete[] arr1;

	return 0;

}

double abs(double a)
{
	if(a < 0)
		return a * -1.0;
	return a;
}

double pow(double a, double power)
{
	double orig = a;
	if(power == 0)
		return 1;

	while(--power > 0)
		a *= orig;
	return a;
}

int readfile(Filereader* reader, double** arr, int i, int j, int k, int subsz, bool invert)
{
	int sz1 = (i + subsz < k ? subsz : k - i + 1);
	int sz2 = (j + subsz < k ? subsz : k - j + 1);

	for(int ii = 0; ii < sz1; ii ++)
	{
		double* line = reader->getNextLine(sz2);
		for(int jj = 0; jj < sz2; jj++)
		{
			int idx = ii;
			if(invert)
				idx = jj;
			for(int kk = 0; kk < 20; kk ++)
				arr[idx][kk] += pow(abs(line[jj]), kk + 1);
		}
		delete[] line;
	}
}

string toString(int i)
{
        std::string s;
        std::stringstream out;
        out << i;
        s = out.str();
        return s;
}

