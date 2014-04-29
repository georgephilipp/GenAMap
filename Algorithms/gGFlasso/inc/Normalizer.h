#ifndef DESCENT_H
#define DESCENT_H
#include <string>
#include "DataMatrix.h"

using namespace std;

class Normalizer
{
public:
	Normalizer();

	void normalizeMat(DataMatrix* dm);
	void subtractMean(DataMatrix* dm);
	void divideBySTD(DataMatrix* dm);
	double calcSTD(double* array, int length);
	double calcMean(double* array, int length);
	double abs_min2(double val1, double val2);
	double sign(double val);
};

#endif
