#ifndef FILEREADER_H
#define FILEREADER_H

#include <vector>

using namespace std;

class FileReader
{
public:
	vector<vector<double> > static readFile(string filename, int numCols, int startIndex, int endIndex);

};

#endif
