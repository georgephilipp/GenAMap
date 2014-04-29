#ifndef DATASETSPLITTER_H
#define DATASETSPLITTER_H
#include "DataMatrix.h"

using namespace std;

class DataSetSplitter
{
public:
	DataSetSplitter();

	DataMatrix** splitData(DataMatrix* X, DataMatrix* y, double perTrn);

private:
};

#endif
