#ifndef MATRIX_H
#define MATRIX_H
#include <string.h>
#include <vector>
#include "tree.h"

using namespace std;

class matrix
{
public:
	matrix();
	~matrix();

	bool loadFromFile(string file, int rows);
	void printToScreen();
	pair<int,int> getMax();
	void combineEntries(pair<int,int> idx);
	vector<tree*> trees;

private:
	vector<vector<double> > vals;
	void combineTrees(int highest, int lowest);
	void combineInMatrix(int highest, int lowest);
	double max(double i1, double i2);
};

#endif
