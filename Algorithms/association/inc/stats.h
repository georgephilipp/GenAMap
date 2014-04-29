#ifndef STATS_H
#define STATS_H

#include <string>

using namespace std;

class Stats
{
public:
	double calcRVals(double** arr1, double** arr2, int l1, int l2, int id, string file, bool isr2);
	double mean(double** arr, int a, int b);
	double calcR(double** x, double** y, double n_1);
	double sign(double r);
	double calcR(double* x, double* y, double n_1);
};

#endif

