#ifndef DATAMATRIX3_H
#define DATAMATRIX3_H
#include <string>

using namespace std;

class DataMatrix3D
{
public:
	DataMatrix3D();
	DataMatrix3D(int i, int j, int k);
	~DataMatrix3D();
	double getVal(int index1, int index2, int index3);
	void setVal(int index1, int index2, int index3, double value);
	int Rows();
	int Cols();
	int D3s();

private:
	int rows;
	int cols;
	int d3;
	double*** vals;
	void initArray();
};

#endif
