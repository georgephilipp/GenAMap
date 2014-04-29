#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include "../headers/matrix.h"

using namespace std;

matrix::matrix()
{
}

bool matrix::loadFromFile(string file, int rows)
{
	vals.clear();
	ifstream input(file.c_str());

	if(!input)
	{
		cout << "File does not exist: "<< file << endl;
		return false;
	}

	for(int i = 0; i < rows; i ++)
	{
		vector<double> v;
		for(int j = 0; j < rows; j ++)
		{
			double temp;
			input >> temp;
			v.push_back(temp);
		}
		vals.push_back(v);
	}

	//Initialize trees for everything
	for(int i = 0; i < rows; i ++)
	{
		trees.push_back(new tree(i));
	}

	return true;
}

matrix::~matrix()
{
	//delete trees.at(0);
	//for(int i = 0; i < trees.size(); i ++)
	//{
	//	delete trees.at(i);
	//}
}

void matrix::printToScreen()
{
	for(int i = 0; i < vals.size(); i ++)
	{
		vector<double> tmp = vals.at(i);
		int l = tmp.size();
		for(int j = 0; j < l; j++)
		{
			cout << tmp.at(j) << " ";
		}
		cout << endl;
	}
}

pair<int,int> matrix::getMax()
{
	double max = 0;
	pair<int, int> toRet;
	for(int i = 0; i < vals.size(); i ++)
	{
		vector<double> tmp = vals.at(i);
		int l = tmp.size();
		for(int j = i; j < l; j ++)
		{
			if(max < tmp.at(j))
			{
				max = tmp.at(j);
				toRet.first = i;
				toRet.second = j;
			}
		}
	}
	return toRet;
}

void matrix::combineEntries(pair<int,int> idx)
{
	int highest = idx.first;
	int lowest = idx.second;
	
	if(lowest > highest)
	{
		int temp = highest;
		highest = lowest;
		lowest = temp;
	}


	//this is now order dependent
	combineInMatrix(highest, lowest);     

	combineTrees(highest, lowest);
	
	//printToScreen();
	
}

void matrix::combineTrees(int highest, int lowest)
{
	vector<tree*>::iterator titer = trees.begin();
	titer += highest;

	tree* t1 = trees.at(highest);
	trees.erase(titer);

	titer = trees.begin();
	titer += lowest;

	tree* t2 = trees.at(lowest);
	trees.erase(titer);

	trees.push_back(new tree(t1, t2));
}

void matrix::combineInMatrix(int highest, int lowest)
{
	int n = trees.at(highest)->size();
	int m = trees.at(lowest)->size();

	vector<double>::iterator iter;
	for(int i = 0; i < vals.size(); i ++)
	{
		iter = vals.at(i).begin();
		iter += highest;
		double v1 = vals.at(i).at(highest);
		vals.at(i).erase(iter);

		iter = vals.at(i).begin();
		iter += lowest;
		double v2 = vals.at(i).at(lowest);
		vals.at(i).erase(iter);

		vals.at(i).push_back(((v1*n)+(v2*m))/(n+m));
	}

	vector<vector<double> >::iterator iter2;
	iter2 = vals.begin();
	iter2 += highest;
	vector<double> vals1 = vals.at(highest);
	vals.erase(iter2);

	iter2 = vals.begin();
	iter2 += lowest;
	vector<double> vals2 = vals.at(lowest);
	vals.erase(iter2);

	vector<double> vals3;
	for(int i = 0; i < vals.size(); i ++)
	{
		//vals3.push_back(max(vals1.at(i),vals2.at(i)));
		vals3.push_back(((vals1.at(i)*n) + (vals2.at(i)*m))/(n+m));
	}
	vals3.push_back(0);
	vals.push_back(vals3);
}

double matrix::max(double i1, double i2)
{
	if(i1 < i2)
		return i1;
	return i2;
}











