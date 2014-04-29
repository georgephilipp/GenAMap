#include <iostream>
#include "../inc/filereader.h"
#include "../inc/stats.h"
#include <sstream>
#include <cstdlib>

using namespace std;

void parseLine(double** arr1, double* line, int idx, int idis);
void initarrs(double** arr1, double** arr2, int id, int sz1, int sz2);
void delarrs(double** arr1, double** arr2, int id);

//argv1 = trait file 1 name
//argv2 = trait file 2 name
//argv3 = methurd
//argv4 = output mask
int main(int argc, char* argv[])
{
	int CR1 = 0;
	int CR2 = 1;
	int SFN_R2 = 2;
	int SFN_CBN = 3;
	int TOM = 4;
	double** arr1 = 0;
	double** arr2 = 0;

	if(argc != 8)
	{
		fprintf(stderr, "Invalid number of arguments");
		return -1;
	}
	string file1;
	string file2;
	string mask;
	int method;
	int idis;
	int sz1;
	int sz2;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				file1 = argv[i];
				cout << file1 << endl;
				break;
			case 2:
				file2 = argv[i];
				cout << file2 << endl;
				break;
			case 3:
				method = atoi(argv[i]);
				cout << method << endl;
				break;
			case 4:
				mask = argv[i];
				cout << mask << endl;
				break;
			case 5:
				idis = atoi(argv[i]);
				cout << idis << endl;
				break;
			case 6:
				sz1 = atoi(argv[i]);
				cout << sz1 << endl;
				break;
			case 7:
				sz2 = atoi(argv[i]);
				cout << sz2 << endl;
				break;
		}
	}
	arr1 = new double*[idis];
	arr2 = new double*[idis];

	Filereader reader1;
	Filereader reader2;
	if(!reader1.openFile(file1) || !reader2.openFile(file2))
	{
		fprintf(stderr, "Cannot open one or more of the files.");
	}

	int mxsz = sz1;
	if(sz2 > sz1)
		mxsz = sz2;

	for(int i = 0; i < mxsz; i ++)
	{
		switch(method)
		{
			case 0://CR1
			case 1://CR2
				if(i < sz1)
				{
					double* line = reader1.getNextLine(idis);

					if(i == 0)
					{
						initarrs(arr1, arr2, idis, sz1, sz2);
					}			
				
					parseLine(arr1, line, i, idis);
					delete[] line;
				}
				if(i < sz2)
				{
					double* line = reader2.getNextLine(idis);
					parseLine(arr2, line, i, idis);
					delete[] line;
				}

				break;
		}
	}
	reader1.close();
	reader2.close();

	if(method == CR1 || method == CR2)
	{
		//now we have the data that we need.
 		Stats stat;
		//int sz1 = (k-s1 < subsz ? k-s1+1 : subsz);
		//int sz2 = (k-s2 < subsz ? k-s2+1 : subsz);
		//cout << sz2 << endl;
		string s1_, s2_;
		std::stringstream out;
		string file = mask + "res.txt";
		stat.calcRVals(arr1, arr2, sz1, sz2, idis, file, method == CR2);
	}

	delarrs(arr1, arr2, idis);
	return 0;

}

void parseLine(double** arr1, double* line, int idx, int id)
{
	for(int i = 0; i < id; i ++)
	{
		arr1[i][idx] = line[i];
	}
}

void initarrs(double** arr1, double** arr2, int id, int sz1, int sz2)
{
	for(int i = 0; i < id; i ++)
	{
		arr1[i] = new double[sz1];
	}
        
        for(int i = 0; i < id; i ++)
	{
		arr2[i] = new double[sz2];
	}
}

void delarrs(double** arr1, double** arr2, int id)
{
        for(int i = 0; i < id; i ++)
        {
                delete[] arr1[i];
        }
	delete[] arr1;
	for(int i = 0; i < id; i++)
	{
		delete[] arr2[i];
	}
	delete[] arr2;
}
