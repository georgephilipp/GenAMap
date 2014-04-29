#include <iostream>
#include "../inc/filereader.h"
#include "../inc/stats.h"
#include <sstream>
#include <cstdlib>

using namespace std;

void parseLine(double** arr1, double* line, int idx, int idis);
void initarrs(double** arr1, double** arr2, int id, int sz1, int sz2);
void delarrs(double** arr1, double** arr2, int sz1, int sz2);
double calcTO(double* arr1, double* arr2, int k, double sum1, double sum2, double aij);
double sumDotProduct(double* arr1, double* arr2, int k);
double sum(double* arr1, int k);
double abs(double a);

int main(int argc, char* argv[])
{
	double** arr1 = 0;
	double** arr2 = 0;

	if(argc != 9)
	{
		fprintf(stderr, "Invalid number of arguments");
		return -1;
	}
	string file1;
	string file2;
	string mask;
	int sz1;
	int sz2;
	int k;
	int subset1;
	int subset2;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				file1 = argv[i];
				file1 +=  ".txt";
				cout << file1 << endl;
				break;
			case 2:
				file2 = argv[i];
				file2 += ".txt";
				cout << file2 << endl;
				break;
			case 3:
				sz1 = atoi(argv[i]);
				cout << sz1 << endl;
				break;
			case 5:
				mask = argv[i];
				cout << mask << endl;
				break;
			case 4:
				sz2 = atoi(argv[i]);
				cout << sz2 << endl;
				break;
			case 6:
				k = atoi(argv[i]);
				cout << k << endl;
				break;
			case 7:
				subset1 = atoi(argv[i]);
				cout << subset1 << endl;
				break;
			case 8:
				subset2 = atoi(argv[i]);
				cout << subset2 << endl;
				break;
		}
	}
	arr1 = new double*[sz1];
	arr2 = new double*[sz2];

	Filereader reader1;
	Filereader reader2;
	if(!reader1.openFile(file1) || !reader2.openFile(file2))
	{
		fprintf(stderr, "Cannot open one or more of the files.");
	}

	for(int i = 0; i < sz1 || i < sz2; i ++)
	{
		if(i < sz1)
		{
			double* line = reader1.getNextLine(k);
			if(i == 0)
			{
				initarrs(arr1, arr2, k, sz1, sz2);
			}			
		
			parseLine(arr1, line, i, k);
			delete[] line;
		}
		
		if(i < sz2)
		{
			double* line = reader2.getNextLine(k);
			parseLine(arr2, line, i, k);
			delete[] line;
		}
	
	}
	reader1.close();
	reader2.close();

	double* sum1 = new double[sz1];
	double* sum2 = new double[sz2];

	//cout << "WHO?" << endl;

	for(int i = 0; i < sz1; i++)
	{
		sum1[i] = sum(arr1[i],k);
	}
	for(int i = 0; i < sz2; i ++)
	{
		sum2[i] = sum(arr2[i],k);
	}

	//cout << "WHAT?" << endl;

	ofstream myfile;
	string name = mask + "res.txt";
	myfile.open(name.c_str());
	for(int i = 0; i < sz1; i ++)
	{
		for(int j = 0; j < sz2; j ++)
		{
			//cout << "WHEN?" << endl;
			//cout << i+subset1-1 << endl;
			//cout << "NO" << endl;
			//cout << j+subset2-1 << endl;
			//cout << sum1[i] << endl;
			//cout << "YES" << endl;
			//cout << arr1[i][j+subset2-1] << endl;
			myfile << calcTO(arr1[i], arr2[j], k, sum1[i], sum2[j], arr1[i][j+subset2-1]) << "\t";
			//cout << "WHERE?" << endl;
		}
		myfile << endl;
	}
	myfile.close();

	delete[] sum1;
	delete[] sum2;
	delarrs(arr1, arr2, sz1, sz2);
	return 0;

}

double calcTO(double* arr1, double* arr2, int k, double sum1, double sum2, double aij)
{
	//cout << sum1 << endl;
	//cout << sum2 << endl;
	//cout << aij << endl;
	//cout << "WHY?" << endl;
	double lij = sumDotProduct(arr1, arr2,k);
	//cout << "HOW?" << endl;
	double ki = sum1;
	double kj = sum2;
	double kk = ki;
	if(kk > kj)
		kk = kj;

	if(aij > 0)
		return (lij + abs(aij)) / (kk + 1 - abs(aij));
	else
		return -1.0 * (lij + abs(aij)) / (kk + 1 - abs(aij));
}

double sumDotProduct(double* arr1, double* arr2, int k)
{
	double toRet = 0.0;
	for(int i = 0; i < k; i ++)
	{
		toRet += abs(arr1[i] * arr2[i]);
	}
	return toRet;
}

double sum(double* arr1, int k)
{
	double sums=0.0;
	for(int i = 0; i < k; i ++)
	{
		sums += abs(arr1[i]);
	}
	return sums;
}

double abs(double a)
{
	if(a > 0)
		return a;
	return a * -1.0;
}

void parseLine(double** arr1, double* line, int idx, int id)
{
	for(int i = 0; i < id; i ++)
	{
		arr1[idx][i] = line[i];
		if(idx == i && line[i] == 1)
		{
			arr1[idx][i] = 0;
		}
	}
}

void initarrs(double** arr1, double** arr2, int id, int sz1, int sz2)
{
	for(int i = 0; i < sz1; i ++)
	{
		arr1[i] = new double[id];
	}
        
        for(int i = 0; i < sz2; i ++)
	{
		arr2[i] = new double[id];
	}
}

void delarrs(double** arr1, double** arr2, int sz1, int sz2)
{
        for(int i = 0; i < sz1; i ++)
        {
                delete[] arr1[i];
        }
	delete[] arr1;
	for(int i = 0; i < sz2; i++)
	{
		delete[] arr2[i];
	}
	delete[] arr2;
}
