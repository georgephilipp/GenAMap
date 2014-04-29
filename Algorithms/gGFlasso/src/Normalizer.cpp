#include <iostream>
#include "../inc/DataMatrix.h"
#include "../inc/Normalizer.h"
#include <string>
#include <fstream>
#include <sstream>
#include <math.h>

using namespace std;

Normalizer::Normalizer()
{
}

void Normalizer::normalizeMat(DataMatrix* dm)
{
	cout << "Normalizing matrix ... " << endl;
	subtractMean(dm);
	divideBySTD(dm);
}

void Normalizer::divideBySTD(DataMatrix* dm)
{
	int i = dm->Rows();
	int j = dm->Cols();

	for(int k = 0; k < j; k ++)
	{
		double std = calcSTD(dm->getCol(k), i);
		for(int m=0; m<i;m++)
		{
			dm->setVal(m,k,dm->getVal(m,k)/std);
		}
	}
}

double Normalizer::calcSTD(double* array, int length)
{
	double sum = 0.0;
	for(int i = 0; i < length; i ++)
	{
		sum += array[i] * array[i];
	}
	delete[] array;
	sum = sum / ((double) length - 1.0);
	return sqrt(sum);
}

void Normalizer::subtractMean(DataMatrix* dm)
{
	int i = dm->Rows();
	int j = dm->Cols();

	for(int k = 0; k < j; k ++)
	{
		double mean = calcMean(dm->getCol(k), i);

		for(int m=0; m < i; m ++)
		{
			dm->setVal(m,k,dm->getVal(m,k) - mean);
		}
	}

}

//Calculate the mean of this array
double Normalizer::calcMean(double* array, int length)
{
	double sum = 0.0;

	for(int i = 0; i < length; i ++)
	{
		sum += array[i];
	}

	delete[] array;//because getCol creates a new array

	return sum / (double) length;
}

double Normalizer::abs_min2(double val1, double val2)
{
	if(fabs(val1) < fabs(val2))
		return val1;
	return val2;
}

double Normalizer::sign(double val)
{
	if(val < 0)
		return -1.0;
	return 1.0;
}





