#include "../inc/stats.h"
#include <fstream>
#include <iostream>
#include <iostream>
#include <cmath>
#include <string.h>

using namespace std;

//calculate the mean
double Stats::mean(double** arr, int ix, int length)
{
        double sum = 0.0;
        for(int i = 0; i < length; i ++)
        {
		sum += arr[i][ix];
        }
        return sum / (double)length;
}

double Stats::sign(double r)
{
	if(r < 0)
		return -1;
	return 1;
}


double Stats::calcRVals(double** arr1, double** arr2, int l1, int l2, int id, string file, bool isr2)
{
	double*** values = new double**[l1+l2];

	for(int i = 0; i < l1 + l2; i ++)
	{
                values[i] = new double*[id];
		double xbar;
		if(i >= l1)
                	xbar = mean(arr2, i-l1, id);
		else
			xbar = mean(arr1, i, id); 
                double sum = 0.0;

                //calculate x_i - xbar for each subject, and sum over the square
                for(int j=0; j<id; j ++)
                {
                        values[i][j] = new double[2];
			if(i >= l1)
	                        values[i][j][0] = arr2[j][i-l1] - xbar;
			else
				values[i][j][0] = arr1[j][i] - xbar;
                        sum += values[i][j][0] * values[i][j][0];
                }

                //get the standard deviation
                double sx = sqrt(sum / (double)(id - 1));

                //get the (xi-xbar)/sx value
                for(int j=0; j<id; j ++)
                { 
                        values[i][j][1] = values[i][j][0] / sx;
                }
 
	}

	ofstream myfile;
	myfile.open(file.c_str());	
	myfile.precision(10);
	//now that we have all the values, we can get the r value for each pair
        for(int i = 0; i < l1; i ++)
        {
                for(int j = l1; j < l2 + l1; j ++)
                {
                        double r = calcR(values[i], values[j], (double)(id-1));
			if(isr2)
			{
				myfile << (r * r * sign(r)) << '\t';
			}
			else
			{
				myfile << r << '\t';
			}
                }
		myfile << endl;
        }
	myfile.close();

	//clean up our data holder stuff. 
        for(int i = 0;i< l1 + l2; i++)
        {
                for(int j=0; j<id; j ++)
                {
                        delete[] values[i][j];
                }
                delete[] values[i];
        }
        delete[] values;
}

double Stats::calcR(double** x, double** y, double n_1)
{
        double sum = 0;

        for(int i=0; i <= n_1; i ++)
        {
                sum += x[i][1] * y[i][1];
        }

        return sum / n_1;
}

double Stats::calcR(double* x, double* y, double n_1)
{
	double sum = 0;

	for(int i = 0; i <= n_1; i ++)
	{
		sum += x[i] * y[i];
	}

	return sum/n_1;
}

