#include <iostream.h>
#include <fstream.h>
#include <string>
#include <vector.h>
#include <math.h>
#include "../inc/FileReader.h"
#include "../inc/QuickSort.h"
#include "../inc/SumRankScore.h"

using namespace std;

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
			if(vals->at(i).at(n) != -99.0)
			{
				v.push_back(vals->at(i).at(n));
			}
			else
			{
				v.push_back(9e99);
			}
		}

		vector<double> ranks = QuickSort::rank(v);

		for(int i = 0; i < vals->size(); i ++)
		{
			if(vals->at(i).at(n) == -99)
			{
				//cout << ranks.at(i) << endl;
				ranks.at(i) = -99;
			}
			//cout << ranks.at(i) << endl;
		}

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

vector<vector<double> > calcPVals(vector<vector<double> > rankscores, vector<vector<double> > mus, vector<vector<double> > sigmas, vector<vector<double> > SNPs)
{
	vector<vector<double> > toReturn = rankscores;

	for(int i = 0; i < rankscores.size(); i ++)
	{
		for(int j = 0; j < rankscores.at(0).size(); j ++)
		{
			double mu = mus.at(i).at(j);
			if(mus.at(i).at(j) > rankscores.at(i).at(j))
				mu -= .5;
			else
				mu += .5;

			double z = (rankscores.at(i).at(j) - mu) / sigmas.at(i).at(j);
			z = z  / sqrt(2);

			if(z < 0)
				z *= -1;
			double pval;
			int bogus = 1;

			calerf_(&z, &pval, &bogus);

			if(pval == 0)
			{
				double val = -99;
				int isok = 0;
				for(int k = 0; k < SNPs.size(); k ++)
				{
					if(val == -99)
						val = SNPs.at(k).at(i);
					else
					{
						if(SNPs.at(k).at(i) != val)
						{
							isok ++;
						}
					}
				}
				if(isok < 10) pval = 1;
			}
			toReturn.at(i).at(j) = pval;
		}
	}
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
	//double lambda[] = {.4, .5, .6, .7, .8};
	double lambda[] = {.6};
	double cnt[]    = { 0,  0,  0,  0,  0};
	double pis[]    = { 0,  0,  0,  0,  0};
	double m;

	for(int i = 0; i < scores.size(); i ++)
	{
		for(int j = 0; j < scores.at(i).size(); j ++)
		{
			m++;
			for(int k = 0; k < 1; k ++)
			{
				if(scores.at(i).at(j) > lambda[k])
					cnt[k]++;
			}
		}
	}


	for(int i = 0; i < 1; i ++)
	{
		pis[i] = cnt[i]/(m * (1.0 - lambda[i]));
	}

	return pis[0];
//	return (pis[0] + pis[1] + pis[2] + pis[3] + pis[4]) / 5;
}

vector<vector<double> > permuteLabels(vector<vector<double> > labels, vector<double> num1s)
{
	cout << "\tpermuting data . . . " << endl;
//	cout << num1s.size();
//	cout << labels.size();
	vector<vector<double> > toRet;
	for(int i = 0; i < labels.size(); i ++)
	{
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
//	cout << "hello?" << endl;
	return toRet;
}

void createFPTable(double* thresholds, int B, vector<vector<double> > labels, vector<vector<double> > rankings, vector<vector<double> > mus, vector<vector<double> > sigmas, double* FPtable, vector<vector<double> > SNPs)
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
			n2sz.at(i) += labels.at(i).at(j) != 0 ? 1 : 0;
		}
	}

	for(int i = 0; i < B;  i++)
	{
		cout << "Permutation " << i << " . . . " << endl;
		vector<vector<double> > permutes = permuteLabels(labels, n2sz);
		//vector<vector<double> > permutes = labels;
		cout << "\tcalculating ranks . . . " << endl;

		vector<vector<double> > rankscores = calcRscore(permutes, rankings);

		cout << "\tcalculating pvals . . . " << endl;
		vector<vector<double> > pvals = calcPVals(rankscores, mus, sigmas, SNPs);
		int cnt[18];
		createTPTable(thresholds, pvals, cnt);
		for(int j = 0; j < 18; j ++)
		{
			cout << cnt[j] << endl;
			FPtable[j] += cnt[j];
		}
		cout << endl << endl;
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

vector<vector<double> > checkOutSnps(vector<vector<double> > d)
{
	double maxxie = 0.0;

	for(int i = 0; i < d.size(); i ++)
	{
		for(int j = 0; j < d.at(i).size(); j ++)
		{
			if(maxxie < d.at(i).at(j))
				maxxie = d.at(i).at(j);
		}
	}

	if(maxxie == 1)
		return d;

	double thresh = maxxie / 2;
	for(int i = 0; i < d.size(); i ++)
	{
		for(int j = 0; j < d.at(i).size(); j ++)
		{
			if(d.at(i).at(j) < thresh)
				d.at(i).at(j) = 0;
			else
				d.at(i).at(j) = 1;
		}
	}

	return d;
}

//last parm is output type 1=eqt, 0 = tables
//./bin/qtl y6.txt x6.txt 25 30 1 5 1 5 prefix 100 1
int run_main(int argc, char** argv)
{
	//read in files
	if(argc != 12)
	{
		cout << "Bad parameters, will abort . . . " << endl;
		return -1;
	}

	string phenfile, genfile;
	int ngen, nphen, sgen, sphen, egen, ephen, perms, output;

	phenfile = argv[1];
	genfile = argv[2];
	ngen = atoi(argv[3]);
	nphen= atoi(argv[4]);
	sgen = atoi(argv[5]);
	egen = atoi(argv[6]);
	sphen= atoi(argv[7]);
	ephen= atoi(argv[8]);
	perms= atoi(argv[10]);
	output= atoi(argv[11]);

	cout << nphen << " phenotypes in file " << phenfile << endl;
	cout << ngen  << " genotypes in file " << genfile << endl;
	cout << "Phenotype index " << sphen << ":" << ephen << endl;
	cout << "Genotype index " << sgen << ":" << egen << endl;
	
	vector<vector<double> > SNPs = FileReader::readFile(genfile, ngen, sgen, egen);
	vector<vector<double> > TRTs = FileReader::readFile(phenfile,nphen,sphen,ephen);	

	SNPs = checkOutSnps(SNPs);

	cout << "Read SNPs for n= " << SNPs.size() << endl;
	cout << "Number of SNPs= " << SNPs[0].size() << endl;
	cout << "Read Traits for n= " << TRTs.size() << endl;
	cout << "Number of Traits " << TRTs[0].size() << endl;

	vector<vector<double> > rankings = changeValsToRanks(&TRTs);
	vector<vector<double> > labels = transpose(SNPs);

	cout << "Rank scores ... " << endl;

	vector<vector<double> > rankscores = calcRscore(labels, rankings);
	vector<vector<double> > rankst = (rankings);
	//cout << rankst.size() << "\t" << rankst.at(0).size() << endl;

	//prepare mu and sigma tables
	cout << "Prepare mu and sigma tables" << endl;
	vector<vector<double> > mus;
	vector<vector<double> > sigmas;

	for(int i = 0; i < labels.size(); i ++)
	{
		vector<double> must;
		vector<double> sigmast;
		for(int j = 0; j < rankst.size(); j ++)
		{
			//cout << i << "\t" << j << endl;
			must.push_back(SumRankScore::calcMean(labels.at(i), rankst.at(j)));
			sigmast.push_back(SumRankScore::calcSigma(labels.at(i), rankst.at(j)));
		}
		mus.push_back(must);
		sigmas.push_back(sigmast);
	}

	cout << "Calculate pvalues ..." << endl;
	vector<vector<double> > baseline_p = calcPVals(rankscores, mus, sigmas, SNPs);

	double thresholds[] = {.05, .01, .001, .0001, .00001, .000001, .0000001, .00000001, .000000001, .0000000001,
				.00000000001, .000000000001, .0000000000001, .00000000000001, .000000000000001, 
				.0000000000000001, .00000000000000001, .000000000000000001};

//	double thresholds[] = {3.3e-5, 3.4e-5, 3.5e-5, 3.6e-5, 3.7e-5, 3.8e-5, 3.9e-5, 4e-5, 4.1e-5, 4.2e-5, 4.3e-5, 4.4e-5, 4.5e-5, 4.6e-5, 4.7e-5, 4.8e-5, 4.9e-5, 5e-5};

	int TPtable[18];
	cout << "Create TP table ... " << endl;
	createTPTable(thresholds, baseline_p, TPtable);

	cout << "TP table:" << endl;
	for(int i = 0; i < 18; i ++)
	{
		cout << thresholds[i] << " " << TPtable[i] << endl;
	}

	double pi0 = decidePi0(baseline_p);

	cout << "pi_0 was determined to be: " << pi0 << endl;


	if(output == 0)
	{
		string prefix = argv[9];
		ofstream TPTfile;
		TPTfile.precision(10);
		string filename = prefix + "_TP.txt";
		TPTfile.open(filename.c_str());
		for(int i = 0; i < 18; i ++)
		{
			TPTfile << thresholds[i] << " " << TPtable[i] << endl;
		}
		TPTfile << endl;
		TPTfile.close();

		ofstream pifile;
		pifile.precision(20);
		filename = prefix + "_pi0.txt";
		pifile.open(filename.c_str());
		pifile << pi0 << endl;
		pifile.close();
	}

	cout << endl;


	double FPtable[18];
	createFPTable(thresholds, perms, labels, rankings, mus, sigmas, FPtable, SNPs);

	cout << "FP table:" << endl;
	for(int i = 0; i < 18; i ++)
	{
		cout << thresholds[i] << " " << FPtable[i] << endl;
	}

	if(output == 0)
	{
		string prefix = argv[9];
		ofstream FPTfile;
		FPTfile.precision(10);
		string filename = prefix + "_FP.txt";
		FPTfile.open(filename.c_str());
		for(int i = 0; i < 18; i ++)
		{
			FPTfile << thresholds[i] << " " << FPtable[i] << endl;
		}
		FPTfile << endl;
		FPTfile.close();
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
	string filename = prefix + "res.txt";
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

	if(output == 1)
	{
		ofstream solfile;
		filename = prefix + "_eQTLs.txt";
		solfile.open(filename.c_str());

		vector<vector<double> > solution = prepSol(baseline_p, thresh);

		for(int i = 0; i < solution.size(); i ++)
		{
			for(int j = 0; j < solution.at(i).size(); j++)
			{
				solfile<<solution.at(i).at(j) << " ";
			}
			solfile<<endl;
		}
		solfile.close();
	}

	return 0;
}


int main(int argc, char* argv[])
{
	return run_main(argc, argv);
}
























