#include <iostream>
#include <fstream>
#include "../inc/DataMatrix3D.h"
#include <string>
#include <cstdlib>

using namespace std;

// initializes the matrix by setting each value to the same value, all add up to one
// or, if a file name is provided, it reads the information right from the file.
DataMatrix3D::DataMatrix3D(int i, int j, int k)
{
	double initVal = 1.0 / (i * j * k);
	rows = i;
	cols = j;
	d3 = k;

	initArray();
	for(int i=0; i < rows; i ++)
		for(int j = 0; j < cols; j ++)
			for(int k = 0; k < d3; k ++)
				vals[i][j][k] = initVal;
}

DataMatrix3D::~DataMatrix3D()
{
	if(vals != 0)
	{
		for(int i = 0; i < rows; i ++)
		{
			for(int j = 0; j < cols; j ++)
			{
				free(vals[i][j]);
			}
			free(vals[i]);
		}
		free(vals);
	}
}

double DataMatrix3D::getVal(int index1, int index2, int index3)
{
	return vals[index1][index2][index3];
}

void DataMatrix3D::setVal(int index1, int index2, int index3, double value)
{
	vals[index1][index2][index3] = value;
}

//called to allocate the memory for the matrix
void DataMatrix3D::initArray()
{
	vals = (double***)malloc(rows*sizeof(double**));
	for(int i=0;i<rows;i++)
	{
		vals[i] = (double**)malloc(cols*sizeof(double*));
		for(int j = 0; j < cols; j ++)
		{
			vals[i][j] = (double*)malloc(d3*sizeof(double));
		}
	}
}

int DataMatrix3D::Rows()
{
	return rows;
}

int DataMatrix3D::Cols()
{
	return cols;
}

int DataMatrix3D::D3s()
{
	return d3;
}










