#ifndef TREE_H
#define TREE_H
#include <string.h>
#include <iostream>

using namespace std;

class tree
{
public:
	tree(int v);
	tree(tree* l, tree* r);
	~tree();

	void createIndexList(vector<int>* v);
	int writeToFile(ofstream& myfile,int id, int idx, int par);
	int size();

	tree* left;
	tree* right;
	int val;
};

#endif
