#include <vector>
#include "../headers/tree.h"
#include <iostream>
#include <fstream>

using namespace std;

tree::tree(int v)
{
	val = v;
	right = 0;
	left = 0;
}

tree::tree(tree* l, tree* r)
{
	left = l;
	right = r;
	val = 0;
}

tree::~tree()
{
	if(right != 0)
	{
		delete right;
		delete left;
	}
}

void tree::createIndexList(vector<int>* v)
{
	if(right != 0)
	{
		left->createIndexList(v);
		right->createIndexList(v);
	}
	else
	{
		v->push_back(val);
	}
}

int tree::size()
{
	if(right != 0)
	{
		return left->size() + right->size();
	}
	else
	{
		return 1;
	}
}

int tree::writeToFile(ofstream& myfile, int lev, int id, int parent)
{
	if(right != 0)
	{
		myfile << id << " " << -1 << " " << lev << " " << parent << endl;
		int me = id;
		id = right->writeToFile(myfile, lev+1, ++id, me);
		id = left->writeToFile(myfile, lev+1, ++id, me);
	}
	else
	{
		myfile << id << " " << val << " " << lev << " " << parent << endl;
	}
	return id;
}




