#ifndef QUICKSORT_H
#define QUICKSORT_H

#include <vector>

using namespace std;

class QuickSort
{
public:
	//Takes as input a vector and then will return a 
	//vector that has the ranks (1 being low) of the
	//elements
	vector<double> static rank(vector<double> arr);

	int static Partition(int low,int high,vector<vector<double> >* arr);
	void static Quick_sort(int low,int high,vector<vector<double> >* arr);


};

#endif
