#ifndef OPTIMIZER_H
#define OPTIMIZER_H
#include "DataMatrix.h"
#include <string>

using namespace std;

class Optimizer
{
public:
	Optimizer();

	void optimize(DataMatrix* X, DataMatrix* y, DataMatrix* E, string file_prefix, string ConfigFile, string method);

};

#endif
