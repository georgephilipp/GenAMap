#ifndef FILEREADER_H
#define FILEREADER_H

#include <string>
#include <fstream>
#include <iostream>
#include <string.h>

using namespace std;

class Filereader
{
public:
	bool openFile(string file);
	double* getNextLine(int length);
	void close();

private:
	ifstream infile;
	double* parseString(double* temp, string s);
};

#endif

