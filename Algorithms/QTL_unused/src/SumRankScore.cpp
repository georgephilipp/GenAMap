#include <vector.h>
#include <iostream>
#include <math.h>
#include "../inc/SumRankScore.h"

//just calculate the sum rank score for the zeros
double SumRankScore::calcSumRankScore(vector<double> label, vector<double> ranks)
{
	//cout << label.size() << "\t" << ranks.size() << "\t" << endl;
	
	double N = label.size();
	double n1 = 0;
	double n2 = 0;
	double R1 = 0;
	double R2 = 0;

	for(int i=0; i < N; i ++)
	{
		if(label.at(i) == 0 && ranks.at(i) != -99)
		{
			n1 ++;
			R1 += ranks.at(i)+1;
		}
		else if(ranks.at(i) == -99)
		{
			//cout << "hello cows" << endl;
		}
	}

	//cout << "Rank for 0 sums to " << R1 << endl;

	return R1;
}

double SumRankScore::calcPhenoMean(vector<double> label)
{
	double N = label.size();
	double n1 = 0.0;

	for(int i = 0; i < N; i ++)
	{
		n1 += label.at(i);
	}
	return n1 / N;
}

//n1(n1 + n2 + 1)/2
double SumRankScore::calcMean(vector<double> label, vector<double> ranks)
{
	double N = label.size();
	double rN = 0;
	double n1 = 0;

	//cout << label.size() << "\t" << ranks.size() << endl;

	for(int i=0; i < N; i ++)
	{
		if(ranks.at(i) != -99)
		{
			if(label.at(i) == 0)
			{
				n1 ++;
			}
			rN ++;
		}
	}

	return (n1*(rN+1))/2;
}

//sqrt(n1n2(n1+n2+1)/12)
double SumRankScore::calcSigma(vector<double> label, vector<double> ranks)
{
	double N = label.size();
	double rN = 0;
	double n1 = 0;

	for(int i=0; i < N; i ++)
	{
		if(ranks.at(i) != -99)
		{
			if(label.at(i) == 0)
			{
				n1 ++;
			}
			rN++;
		}
	}

	double n2 = rN-n1;
	return sqrt(n1*n2*(rN+1)/12);
}











