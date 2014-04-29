#include "../inc/FileReader.h"
#include <vector>
#include <iostream>
#include <sstream>
#include <fstream.h>

using namespace std;

/*vector<vector<double> > FileReader::readFile(string filename, int numCols, int startIndex, int endIndex)
{
	ifstream input(filename.c_str());

	vector<vector<double> > toReturn;

	if(!input)
	{
		cout << "File does not exist: " << filename << endl;
		return toReturn;
	}

	double temp;
	input >> temp;
	if(temp == 2)
		temp = 1;
	int ind = 0;
	int idx = 0;

	while(!input.eof())
	{
		vector<double> v;

		for(int j = 0; j < numCols; j ++)
		{

			if(j >= startIndex - 1 && j < endIndex)
			{
				v.push_back(temp);
			}

			input >> temp;
			if(temp == 2)
				temp = 1;

		}

		toReturn.push_back(v);
	}

	return toReturn;
}*/

vector<vector<double> > FileReader::readFile(string filename, int numRows, int startIndex, int endIndex)
{
        ifstream input(filename.c_str());

        vector<vector<double> > toReturn;

        if(!input)
        {
                cout << "File does not exist: " << filename << endl;
                return toReturn;
        }

        double temp;
	char buffer[50000];

        input.getline(buffer, 50000);
//        if(temp == 2)
//                temp = 1;
        int ind = 0;
        int idx = 0;
	int ix = 0;

	string s = buffer;

	istringstream iss(s);

	do
	{
		double sub;
		iss >> sub;
		if(iss)
		{
	//		cout << sub << endl;
			vector<double> v;
			v.push_back(sub);
			toReturn.push_back(v);
		}
	} while(iss);

	cout << endl;
	//toReturn.pop_back();
	//int n = toReturn.size();
//	cout << "HAPPY DAY!" << endl;

        while(!input.eof())
        {
                input.getline(buffer, 50000);
		s = buffer;
		//cout << ++ix << endl;

//		if(s.length() > 0)
		{
			istringstream iss2(s);

			int i = 0;
//			for(int i = 0; i < n; i ++)
			do
			{
				double sub;
				iss2 >> sub;
				if(iss2)
				{
					toReturn[i++].push_back(sub);
				}
				//cout << i << endl;
			}
			while(iss2);
		}
        }

//	for(int i = 0; i < toReturn.size(); i ++)
//	{
//		for(int j =0; j < toReturn[0].size(); j ++)
//		{
//			cout << toReturn[i][j] << " ";
//		}
//		cout << endl;
//	}
//
        return toReturn;
}






















