#ifndef EDGES_H
#define EDGES_H
#include <string>
#include "DataMatrix.h"

using namespace std;

class Edges
{
public:
	Edges();

	DataMatrix* calcEdges(double threshold, DataMatrix * y);

private:
	double mean(double* column, int length);
	double calcR(double** x, double** y, double n_1);

};

#endif
