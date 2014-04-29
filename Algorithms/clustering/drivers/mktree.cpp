#include "../headers/matrix.h"
#include "../headers/tree.h"
#include "../headers/pair.h"
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cstdlib>

using namespace std;

//Input: network to be clustered.  High values indiciate high correlation, low none.  0s across diagonal
//Input: number of rows and columns - no error checking!

void doHClust(matrix m);


int main(int argc, char* argv[])
{
	//Input Data
	int rows;
	string network_file;
	
	//Read in the parameters
	if(argc == 3)
	{
		network_file = argv[1];
		rows = atoi(argv[2]);

		//Confirm values
		cout << "Will use network in " << network_file << endl;
		cout << "Num Rows: "  << rows  << endl;

		//Initialize the matrix from the file
		matrix m;
		if(!m.loadFromFile(network_file, rows))
			return -1;
		//m.printToScreen();

		doHClust(m);

//		ofstream myfile;
//		myfile.open("cluster.txt");
//		for(int i = 0; i < list.size(); i ++)
//		{
//			cout << (list.at(i) + 1) << endl;
//			myfile << (list.at(i) + 1) << endl;
//		}
	}
	else
	{
		cout << "Bad parameters, please use: ntwrk_file numRows" << endl;
		return -1;
	}

	return 0;
}




void doHClust(matrix m)
{
	//m.printToScreen();
	while(m.trees.size() > 1)
	{
		pair<int, int> max = m.getMax();
		m.combineEntries(max);

		cout << m.trees.size() << endl;

		//m.printToScreen();

		//vector<int> temp;
		//temp.push_back(2);
		//return temp;
	}

	vector<int> toRet;
	ofstream myfile;
	myfile.open("tree.txt");
	m.trees.at(0)->writeToFile(myfile, 1, 1, -1);
	myfile.close();
	cout << m.trees.at(0)->size() << endl;
	delete m.trees.at(0);
}



















