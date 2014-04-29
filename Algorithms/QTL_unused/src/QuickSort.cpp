#include <vector.h>
#include <iostream>
#include "../inc/QuickSort.h"

vector<double> QuickSort::rank(vector<double> arr)
{
	int n,low,high,i;
	n = arr.size();

	vector<vector<double> > order;

	for(i=0;i<n;i++)
	{
		vector<double> v;
		v.push_back(arr.at(i));
		v.push_back(i);
		order.push_back(v); 
	}
	
//	cout<< "Initial Order of elements\n";
// 	for(i=0;i<n;i++)
//	{
//	  	cout << order.at(i).at(0) << " " << order.at(i).at(1) << endl;
//	}
//  	cout<<"\n";

	high=n-1;
	low=0;

	Quick_sort(low,high,&order);

//	cout<<"Final Array After Sorting\n";

	for(i=0;i<n;i++)
	{
		order.at(i).at(0) = order.at(i).at(1);
		order.at(i).at(1) = i;
		//cout<<order.at(i).at(0) <<" " << order.at(i).at(1) << endl;
	}

	for(i=0;i<n;i++)
	{
		arr.at((int)order.at(i).at(0)) = order.at(i).at(1);
	}

/*	Quick_sort(0, n-1, &order);

// 	for(i=0;i<n;i++)
//	{
//	  	cout << order.at(i).at(0) << " " << order.at(i).at(1) << endl;
//	}

	for(int i = 0; i < n; i ++)
	{
		arr.at(i) = order.at(i).at(1);
	}	
*/

	return arr;
}

/*Function for partitioning the array*/
int QuickSort::Partition(int left,int right, vector<vector<double> >* arr)
{
	double pivot = arr->at(right).at(0);

	int storeIndex = left;

	for(int i = left; i <= right-1; i ++)
	{
		if(arr->at(i).at(0) <= pivot)
		{
			vector<double> temp = arr->at(i);
			arr->at(i) = arr->at(storeIndex);
			arr->at(storeIndex) = temp;
			storeIndex++;
		}
	}

	vector<double> temp = arr->at(storeIndex);
	arr->at(storeIndex) = arr->at(right);
	arr->at(right) = temp;

	return storeIndex;
}

void QuickSort::Quick_sort(int left,int right,vector<vector<double> >* arr)
{
	int Piv_index;
	if(right > left)
	{
  		Piv_index=Partition(left,right,arr);
  		Quick_sort(left,Piv_index-1,arr);
  		Quick_sort(Piv_index+1,right,arr);
	}
}

