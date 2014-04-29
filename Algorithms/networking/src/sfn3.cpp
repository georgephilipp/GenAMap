#include <iostream>
#include "../inc/filereader.h"
#include <sstream>
#include <cstdlib>

using namespace std;

int readfile(Filereader* reader, double** arr, int i, int j, int k, int subsz, bool invert, int kk);
double pow(double a, double power);
double sign(double a);
string toString(int i);

int main(int argc, char* argv[])
{
	double** arr1 = 0;

	if(argc != 5)
	{
		fprintf(stderr, "Invalid number of arguments\n");
		return -1;
	}
	int k;
	string mask;
	int subset;
	bool isTOMformat;
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
			case 4:
				isTOMformat = (atoi(argv[i]) == 1);
				cout << isTOMformat << endl;
				break;
		}
	}
	arr1 = new double*[subsz];
	
	for(int i = 0; i < subsz; i ++)
	{
		arr1[i] = new double[k];
		for(int j = 0; j < k; j ++)
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
				file1 = mask + "_" + toString(i) + "_" + toString(j) + "res.txt_cor.txt";
				if(!reader1.openFile(file1))
				{
					cerr << "Cannot open one or more of these files " << file1 << endl;
					return -1;
				}
				readfile(&reader1, arr1, i, j, k, subsz, false, j-1);
				reader1.close();
			}
			else if(j == subset)
			{
				file1 = mask + "_" + toString(i) + "_" + toString(j) + "res.txt_cor.txt";
				if(!reader1.openFile(file1))
				{
					cerr << "Cannot open one or more of these files " << file1 << endl;
					return -1;
				}
				readfile(&reader1, arr1, i, j, k, subsz, true, i-1);
				reader1.close();
			}
		}
	}

	ifstream myfile("beta.txt");
	if(!myfile.is_open())
	{
		cerr << "Cannot open the beta file " << endl;
		return -1;
	}
	string line;
	getline(myfile,line);
	myfile.close();
	int beta = atoi(line.c_str());
	cout << beta << endl;

	if(isTOMformat)
	{
		ofstream outfile;
		string outname = mask + "_" + toString(subset) + ".txt";
		outfile.open(outname.c_str());

		for(int i = 0; i < subsz; i ++)
		{
			for(int j = 0; j < k; j ++)
			{
				outfile << sign(arr1[i][j]) * pow(arr1[i][j],beta) << "\t";
			}
			outfile << endl;
		}
		outfile.close();
	}
	else
	{
		ofstream outfile;
		for(int r = subset; r < k; r += subsz)
		{
			ofstream outfile;
			string outname = mask + "_" + toString(subset) + "_" + toString(r) + "res.txt";
			outfile.open(outname.c_str());

			for(int i = 0; i < subsz && i < k; i ++)
			{
				for(int j = r-1; j < r-1+subsz && j < k; j ++)
				{
					outfile << sign(arr1[i][j]) * pow(arr1[i][j],beta) << "\t";
				}
				outfile << endl;
			}
		}
	}

	for(int i = 0; i < subsz; i ++)
		delete[] arr1[i];
	delete[] arr1;

	return 0;

}

double sign(double a)
{
	if(a < 0)
		return -1.0;
	return 1.0;
}

double pow(double a, double power)
{
	double orig = a;
	if(power == 0)
		return 1;

	while(--power > 0)
		a *= orig;
	if(a > 0)
		return a;
	return a * -1;
}

int readfile(Filereader* reader, double** arr, int i, int j, int k, int subsz, bool invert, int kk)
{
	int sz1 = (i + subsz < k ? subsz : k - i + 1);
	int sz2 = (j + subsz < k ? subsz : k - j + 1);

	for(int ii = 0; ii < sz1; ii ++)
	{
		double* line = reader->getNextLine(sz2);
		for(int jj = 0; jj < sz2; jj++)
		{
			int idx = ii;
			int idx2 = jj + kk;
			if(invert)
			{
				idx = jj;
				idx2 = ii + kk;
			}
			//cout << idx << " " << idx2 << endl;
			arr[idx][idx2] = line[jj];
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

