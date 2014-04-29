#ifndef SUMRANKSCORE_H
#define SUMRANKSCORE_H

#include <vector>

using namespace std;

class SumRankScore
{
public:
	//takes two vectors - one with labels and one with ranks and returns the smallest U score
	double static calcSumRankScore(vector<double> label, vector<double> ranks);
	double static calcMean(vector<double> label, vector<double> ranks);
	double static calcPhenoMean(vector<double> label);
	double static calcSigma(vector<double> label, vector<double> ranks);

};

#endif
