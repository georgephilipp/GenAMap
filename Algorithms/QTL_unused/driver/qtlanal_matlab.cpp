#include <iostream.h>
#include <fstream.h>
#include <string>
#include <vector.h>
//#include "../lib/libzscore.h"
#include <math.h>
//#include "mclmcrrt.h"
//#include "mclcppclass.h"
#include "../inc/FileReader.h"
#include "../inc/QuickSort.h"
#include "../inc/SumRankScore.h"

using namespace std;

//needs to be compiled with mbuild driver/qtlanal.cpp -Llib -lzscore -Iinc
// ./qtlanal phenfile.txt genfile.txt ngen nphen sgen egen sphen ephen

extern "C" 
{
	void calerf_(double* x, double* y, int* jint);
}

vector<vector<double> > calcRscore(vector<vector<double> > labels, vector<vector<double> > rankings)
{
	vector<vector<double> > rankscores;

	for(int i=0; i < labels.size(); i ++)
	{
		vector<double> scoresL_i;
		for(int j = 0; j < rankings.size(); j ++)
		{
			scoresL_i.push_back(SumRankScore::calcSumRankScore(labels.at(i), rankings.at(j)));
		}
		rankscores.push_back(scoresL_i);
	}

//	for(int i = 0; i < labels.size(); i ++)
//	{
//		for(int j = 0; j < rankings.size();j ++)
//		{
//			cout << rankscores.at(i).at(j) << " ";
//		}
//		cout << endl;
//	}
	return rankscores;
}


// takes an array of values and converts it into nonparametric rankings
vector<vector<double> > changeValsToRanks(vector<vector<double> >* vals)
{
	vector<vector<double> > rankings;
	for(int n=0; n < vals->at(0).size(); n ++)
	{
		vector<double> v;
		for(int i = 0; i < vals->size(); i ++)
		{
			v.push_back(vals->at(i).at(n));
		}
		vector<double> ranks = QuickSort::rank(v);

		rankings.push_back(ranks);
	}
	return rankings;
}

//swaps the rows and columns of the vector
vector<vector<double> > transpose(vector<vector<double> > SNPs)
{
	vector<vector<double> > labels;

	//transpose the SNPs into the labels
	for(int i = 0; i < SNPs.at(0).size(); i ++)
	{
		vector<double> v;
		for(int n = 0; n < SNPs.size(); n ++)
		{
			v.push_back(SNPs.at(n).at(i));
		}
		labels.push_back(v);
	}
	return labels;
}

vector<vector<double> > calcPVals(vector<vector<double> > rankscores, vector<double> mus, vector<double> sigmas)
{
	vector<vector<double> > toReturn = rankscores;

	for(int i = 0; i < rankscores.size(); i ++)
	{
		for(int j = 0; j < rankscores.at(0).size(); j ++)
		{
			double mu = mus.at(i);
			if(mus.at(i) > rankscores.at(i).at(j))
				mu -= .5;
			else
				mu += .5;


			double z = (rankscores.at(i).at(j) - mu) / sigmas.at(i);
			z = z  / sqrt(2);

			if(z < 0)
				z *= -1;
			//cout << z << endl;

//			double data1[] = {rankscores.at(i).at(j)};
//			double data2[] = {mu};
//			double data3[] = {sigmas.at(i)};
//			mwArray in1(1,1,mxDOUBLE_CLASS,mxREAL);
//			mwArray in2(1,1,mxDOUBLE_CLASS,mxREAL);
//			mwArray in3(1,1,mxDOUBLE_CLASS,mxREAL);
//			in1.SetData(data1,1);
//			in2.SetData(data2,1);
//			in3.SetData(data3,1);

//			mwArray out;
//			mwArray out1;

//			zscore(1, out, in1, in2, in3);

//			cout << out << endl;


			/*double data4[] = {z};
			cout << "1" << endl;
			mwArray in4(1,1,mxDOUBLE_CLASS,mxREAL);
			cout << "2" << endl;
			in4.SetData(data1,1);
			cout << "3" << endl;
			calcpval(1, out1, in4);
			cout << "4" << endl;
			//cout << "Zscore: " << out << endl;

			cout << out1 << endl << endl;*/
			double pval;
			int bogus = 1;

			calerf_(&z, &pval, &bogus);

//			cout << pval << endl;

			//toReturn.at(i).at(j) = out;
			toReturn.at(i).at(j) = pval;
		}
	}

//	for(int i = 0; i < toReturn.size(); i ++)
//	{
//		for(int j = 0; j < toReturn.at(0).size();j ++)
//		{
//			cout << toReturn.at(i).at(j) << " ";
//		}
//		cout << endl;
//	}

	return toReturn;
}

void createTPTable(double* thresholds, vector<vector<double> > pvals, int* cnt)
{
	for(int k=0; k < 18; k ++)
		cnt[k] = 0;

	for(int i = 0; i < pvals.size(); i ++)
	{
		for(int j = 0; j < pvals.at(i).size(); j ++)
		{
			for(int k = 0; k < 18; k ++)
			{
				if(pvals.at(i).at(j) < thresholds[k])
					cnt[k]++;
			}
		}
	}
}

double decidePi0(vector<vector<double> > scores)
{
	double lambda[] = {.4, .5, .6, .7, .8};
	double cnt[]    = { 0,  0,  0,  0,  0};
	double pis[]    = { 0,  0,  0,  0,  0};
	double m;

	for(int i = 0; i < scores.size(); i ++)
	{
		for(int j = 0; j < scores.at(i).size(); j ++)
		{
			m++;
			for(int k = 0; k < 5; k ++)
			{
				if(scores.at(i).at(j) > lambda[k])
					cnt[k]++;
			}
		}
	}


	for(int i = 0; i < 5; i ++)
	{
//		cout << cnt[i] << endl;
		pis[i] = cnt[i]/(m * (1.0 - lambda[i]));
//		cout << pis[i] << endl;
	}

	return (pis[0] + pis[1] + pis[2] + pis[3] + pis[4]) / 5;
}

vector<vector<double> > permuteLabels(vector<vector<double> > labels, vector<double> num1s)
{
	cout << "\tpermuting data . . . " << endl;
	vector<vector<double> > toRet;
	for(int i = 0; i < labels.size(); i ++)
	{
		//cout << "\t\t" << i << endl;
		double per = num1s.at(i) / labels.at(i).size();
		int found = 0;
		vector<double> v;
		for(int j = 0; j < labels.at(i).size(); j ++)
		{
			double r = (   (double)rand() / ((double)(RAND_MAX)+(double)(1)) );

			if(r > per && found < num1s.at(i))
			{
				v.push_back(1);
				found++;
			}
			else v.push_back(0);
		}
		int k = 0;
		while(found < num1s.at(i))
		{
			if(v.at(k) == 0)
			{
				v.at(k) = 1;
				found++;
			}
			k++;
		}
		toRet.push_back(v);
	}
	return toRet;
}

void createFPTable(double* thresholds, int B, vector<vector<double> > labels, vector<vector<double> > rankings, vector<double> mus, vector<double> sigmas, double* FPtable)
{
	for(int i = 0; i < 18; i ++)
	{
		FPtable[i] = 0.0;
	}

	//get number of ones for each column
	vector<double> n2sz;
	for(int i = 0; i < labels.size(); i ++)
	{
		n2sz.push_back(0);
		for(int j=0; j < labels.at(i).size(); j ++)
		{
			n2sz.at(i) += labels.at(i).at(j);
		}
	}

//	for(int i = 0; i < n2sz.size(); i ++)
//	{
//		cout << n2sz.at(i) << endl;
//	}

	for(int i = 0; i < B;  i++)
	{
		cout << "Permutation " << i << " . . . " << endl;
		vector<vector<double> > permutes = permuteLabels(labels, n2sz);
		cout << "\tcalculating ranks . . . " << endl;

		vector<vector<double> > rankscores = calcRscore(permutes, rankings);

		cout << "\tcalculating pvals . . . " << endl;
		vector<vector<double> > pvals = calcPVals(rankscores, mus, sigmas);
		int cnt[18];
		createTPTable(thresholds, pvals, cnt);
		for(int j = 0; j < 18; j ++)
			FPtable[j] += cnt[j];
	}

	for(int i = 0; i < 18; i ++)
	{
		FPtable[i] /= B;
	}
}

void createFDRTable(double pi0, int* TP, double* FP, double* threshold, double* FDR)
{
	for(int i = 0; i < 18; i ++)
	{
		if(TP[i] == 0)
			FDR[i] = 0;
		else
		{
			FDR[i] = pi0 * FP[i] / TP[i];
		}
	}
}

vector<vector<double> > prepSol(vector<vector<double> > solution, double thresh)
{
	for(int i = 0; i < solution.size(); i ++)
	{
		for(int j = 0; j < solution.at(i).size(); j ++)
		{
			if(solution.at(i).at(j) < thresh)
			{
				solution.at(i).at(j) = 1;
			}
			else solution.at(i).at(j) = 0;
		}
	}

	return solution;
}

int run_main(int argc, char** argv)
{
	//read in files
	if(argc != 10)
	{
		cout << "Bad parameters, will abort . . . " << endl;
		return -1;
	}

	string phenfile, genfile;
	int ngen, nphen, sgen, sphen, egen, ephen;

	phenfile = argv[1];
	genfile = argv[2];
	ngen = atoi(argv[3]);
	nphen= atoi(argv[4]);
	sgen = atoi(argv[5]);
	egen = atoi(argv[6]);
	sphen= atoi(argv[7]);
	ephen= atoi(argv[8]);

	cout << nphen << " phenotypes in file " << phenfile << endl;
	cout << ngen  << " genotypes in file " << genfile << endl;
	cout << "Phenotype index " << sphen << ":" << ephen << endl;
	cout << "Genotype index " << sgen << ":" << egen << endl;
	
	vector<vector<double> > SNPs = FileReader::readFile(genfile, ngen, sgen, egen);
	vector<vector<double> > TRTs = FileReader::readFile(phenfile,nphen,sphen,ephen);	

	cout << "Read SNPs for n= " << SNPs.size() << endl;
	cout << "Read Traits for n= " << TRTs.size() << endl;

	vector<vector<double> > rankings = changeValsToRanks(&TRTs);
	vector<vector<double> > labels = transpose(SNPs);

	vector<vector<double> > rankscores = calcRscore(labels, rankings);

	//prepare mu and sigma tables
	vector<double> mus;
	vector<double> sigmas;

	for(int i = 0; i < labels.size(); i ++)
	{
		mus.push_back(SumRankScore::calcMean(labels.at(i)));
		sigmas.push_back(SumRankScore::calcSigma(labels.at(i)));
	}

//	mclInitializeApplication(NULL,0);
//	libzscoreInitialize();

	vector<vector<double> > baseline_p = calcPVals(rankscores, mus, sigmas);

	double thresholds[] = {.05, .01, .001, .0001, .00001, .000001, .0000001, .00000001, .000000001, .0000000001,
				.00000000001, .000000000001, .0000000000001, .00000000000001, .000000000000001, 
				.0000000000000001, .00000000000000001, .000000000000000001};

	int TPtable[18];
	createTPTable(thresholds, baseline_p, TPtable);

	cout << "TP table:" << endl;
	for(int i = 0; i < 18; i ++)
	{
		cout << thresholds[i] << " " << TPtable[i] << endl;
	}

	cout << endl;

	double pi0 = decidePi0(baseline_p);

	cout << "pi_0 was determined to be: " << pi0 << endl;

	double FPtable[18];
	createFPTable(thresholds, 2, labels, rankings, mus, sigmas, FPtable);

	cout << "FP table:" << endl;
	for(int i = 0; i < 18; i ++)
	{
		cout << thresholds[i] << " " << FPtable[i] << endl;
	}

	double FDR[18];
	createFDRTable(pi0, TPtable, FPtable, thresholds, FDR);

	cout << "FDR table:" << endl;
	int found = 0;
	for(int i = 0; i < 18; i ++)
	{
		cout << thresholds[i] << " " << FDR[i] << endl;
		if(found == 0 && FDR[i] < .05)
		{
			found = i;
		}
	}

	double thresh = thresholds[found];

	string prefix = argv[9];
	ofstream pvalfile;
	string filename = prefix + "_pvals.txt";
	pvalfile.open(filename.c_str());

	for(int i = 0; i < baseline_p.size(); i ++)
	{
		for(int j = 0; j < baseline_p.at(i).size(); j++)
		{
			pvalfile << baseline_p.at(i).at(j) << " ";
		}
		pvalfile << endl;
	}
	pvalfile.close();

	ofstream solfile;
	filename = prefix + "_eQTLs.txt";
	solfile.open(filename.c_str());

	vector<vector<double> > solution = prepSol(baseline_p, thresh);

	for(int i = 0; i < solution.size(); i ++)
	{
		for(int j = 0; j < solution.at(i).size(); j++)
		{
//			cout << solution.at(i).at(j) << " ";
			solfile<<solution.at(i).at(j) << " ";
		}
//		cout << endl;
		solfile<<endl;
	}
	solfile.close();

//	libzscoreTerminate();
//	mclTerminateApplication();

	return 0;
}


int main(int argc, char* argv[])
{
	return run_main(argc, argv);
	//mclmcrInitialize();
	//return mclRunMain((mclMainFcnType)run_main,argc,(const char**)argv);
}
























