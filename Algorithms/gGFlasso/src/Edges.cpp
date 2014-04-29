#include "../inc/Edges.h"
#include <string>
#include "../inc/DataMatrix.h"
#include <iostream>
#include <cmath>
#include <stdio.h>
#include <vector>

using namespace std;

// Builds a DataMatrix of edges based on Pearson's coefficient
Edges::Edges()
{}

//compare each y to every other y and see if it's Pearson coef is
//above the threshold.
DataMatrix* Edges::calcEdges(double threshold, DataMatrix * y)
{
	//initialization of data holder
	int numSubjs = y->Rows();
	int phens = y->Cols();
	double*** values = new double**[phens];

	//3D array [phenotype][subjectID][1=x_i-xbar, 2=[1]/s_x]
	for(int i = 0;i< phens; i++)
	{
		values[i] = new double*[numSubjs];
		double* col = y->getCol(i);

		double xbar = mean(col, numSubjs);
		double sum = 0.0;

		//calculate x_i - xbar for each subject, and sum over the square
		for(int j=0; j<numSubjs; j ++)
		{
			values[i][j] = new double[2];
			values[i][j][0] = col[j] - xbar;
			sum += values[i][j][0] * values[i][j][0];
		}

		//get the standard deviation
		double sx = sqrt(sum / (double)(numSubjs - 1));

		//get the (xi-xbar)/sx value
		for(int j=0; j<numSubjs; j ++)
		{
			values[i][j][1] = values[i][j][0] / sx;
		}
		delete[] col;
	}

	//now that we have all the values, we can get the r value for each 
	//pair.
	int numEdges = 0;
	vector<double> edges;
	for(int i = 0; i < phens; i ++)
	{
		for(int j = i + 1; j < phens; j ++)
		{
			double r = calcR(values[i], values[j], (double)(numSubjs-1));


			if(r >= threshold || r * -1 >= threshold)
			{
				numEdges ++;
				edges.push_back(i);
				edges.push_back(j);
				edges.push_back(r);
			}
		}
	}

	cout << "Number of edges found: " << numEdges << endl;

	//let's put all our edges into a DataMatrix.
	DataMatrix* toReturn = new DataMatrix(numEdges, 3, "");
	for(int i = 0; i < numEdges; i ++)
	{
		toReturn->setVal(i,2,edges.back());
		edges.pop_back();
		toReturn->setVal(i,1,edges.back());
		edges.pop_back();
		toReturn->setVal(i,0,edges.back());
		edges.pop_back();
	}

	//clean up our data holder stuff. 
	for(int i = 0;i< phens; i++)
	{
		for(int j=0; j<numSubjs; j ++)
		{
			delete[] values[i][j];
		}
		delete[] values[i];
	}
	delete[] values;

	return toReturn;
}

//calculate the mean
double Edges::mean(double* column, int length)
{
	double sum = 0.0;
	for(int i = 0; i < length; i ++)
	{
		sum += column[i];
	}
	return sum / (double)length;
}

//calculate the r value between the two columns
double Edges::calcR(double** x, double** y, double n_1)
{
	double sum = 0;

	for(int i=0; i <= n_1; i ++)
	{
		sum += x[i][1] * y[i][1];
	}

	return sum / n_1;
}


















