#include "../inc/filereader.h"
#include <string.h>
#include <cstdlib>

using namespace std;

bool Filereader::openFile(string file)
{
	int num;
	infile.open(file.c_str());

	if(!infile)
	{
		return false;
	}
	return true;
}

double* Filereader::getNextLine(int length)
{
	double * temp = new double[length];

	//cout << length << endl;
	string s;
	getline(infile, s);

	return parseString(temp, s);
}

double* Filereader::parseString(double* temp, string s)
{
	int len = s.length();
	string cur;
	int idx = 0;

	for(int i = 0; i < len; i ++)
	{
		if(isspace(s[i]))
		{
			temp[idx++] = atof(cur.c_str());
			cur = "";
		}
		else
		{
			cur += s[i];
		}
	} 
	
	//cout << idx << endl;
	return temp;
}

void Filereader::close()
{
	infile.close();
}
