#ifndef NETWORKREADER_H
#define NETWORKREADER_H
#include <string>
#include "DataMatrix.h"

using namespace std;

class NetworkReader
{
public:
	NetworkReader();
	DataMatrix* readNetworkFile(string filename, int numgenes);
};

#endif
