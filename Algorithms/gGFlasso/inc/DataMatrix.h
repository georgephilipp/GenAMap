#ifndef DATAMATRIX_H
#define DATAMATRIX_H
#include <string>

using namespace std;

class DataMatrix
{
public:
	DataMatrix();
	DataMatrix(int i, int j, string filename);
	DataMatrix(const DataMatrix* c);
	~DataMatrix();
	void writeToFile(string filename);
	double getVal(int index1, int index2);
	void setVal(int index1, int index2, double value);
	int Rows();
	int Cols();
	double* getCol(int index);
	double* getRow(int index);

private:
	int rows;
	int cols;
	double** vals;
	void initArray();
};

#endif
