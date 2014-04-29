#include <iostream>
#include <fstream>
#include "../inc/DataMatrix.h"
#include <string>
#include <cstdlib>

using namespace std;

// initializes the matrix by setting each value to the same value, all add up to one
// or, if a file name is provided, it reads the information right from the file.
DataMatrix::DataMatrix(int i, int j, string filename)
{
	if(filename.length() == 0)
	{
		double initVal = 1.0 / (i * j);
		rows = i;
		cols = j;

		initArray();
		for(int i=0; i < rows; i ++)
			for(int j = 0; j < cols; j ++)
				vals[i][j] = initVal;
	}
	else
	{
		rows = i;
		cols = j;
		ifstream input(filename.c_str());

		if(!input)
		{
			cout << "File does not exist: "<< filename << endl;
			return;
		}
		initArray();

		for(int i = 0; i < rows; i ++)
		{
			for(int j = 0; j < cols; j ++)
			{
				double temp;
				input >> temp;
				vals[i][j] = temp;
			}
		}
	}
}

DataMatrix::~DataMatrix()
{
	if(vals != 0)
	{
		for(int i = 0; i < rows; i ++)
		{
			free(vals[i]);
		}
		free(vals);
	}
}

//This is important to write out the final values at the end.
void DataMatrix::writeToFile(string filename)
{
	ofstream output;

	output.open(filename.c_str());

	for(int i = 0; i<rows;i++)
	{
		for(int j = 0; j < cols; j ++)
		{
			output << vals[i][j] << " ";
		}
		output << endl;
	}
	output.close();
}

double DataMatrix::getVal(int index1, int index2)
{
	return vals[index1][index2];
}

void DataMatrix::setVal(int index1, int index2, double value)
{
	vals[index1][index2] = value;
}

double* DataMatrix::getCol(int index)
{
	double* toret = new double[rows];

	for(int i = 0; i < rows; i ++)
	{
		toret[i] = vals[i][index];
	}
	return toret;
}

double* DataMatrix::getRow(int index)
{
	double* toret = vals[index];

	return toret;
}

//called to allocate the memory for the matrix
void DataMatrix::initArray()
{
	vals = (double**)malloc(rows*sizeof(double*));
	for(int i=0;i<rows;i++)
	{
		vals[i] = (double*)malloc(cols*sizeof(double));
	}
}

int DataMatrix::Rows()
{
	return rows;
}

int DataMatrix::Cols()
{
	return cols;
}

//copy constructor
DataMatrix::DataMatrix(const DataMatrix* c)
{
	rows = c->rows;
	cols = c->cols;
	initArray();

	for(int i = 0; i<rows;i++)
	{
		for(int j = 0; j < cols; j ++)
		{
			vals[i][j] = c->vals[i][j];
		}
	}
}

DataMatrix::DataMatrix()
{}









