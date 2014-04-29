#include "../inc/DataMatrix.h"
#include "../inc/CoordDescent.h"
#include <iostream>
#include <string>
#include <sys/stat.h>
#include "../inc/Normalizer.h"
#include <sstream>
#include <cstdlib>

using namespace std;


//check to see if a file exists in the file system
//http://www.techbytes.ca/techbyte103.html
bool fileExists(string strFilename)
{
	struct stat stFileInfo;
	bool blnReturn;
	int intStat;

	// Attempt to get the file attributes
	intStat = stat(strFilename.c_str(),&stFileInfo);
	if(intStat == 0) 
	{
		// We were able to get the file attributes so the file obviously exists.
		blnReturn = true;
	} 
	else 
	{
	    // We were not able to get the file attributes.
	    // This may mean that we don't have permission to
	    // access the folder which contains this file. If you
	    // need to do that level of checking, lookup the
	    // return values of stat which will give you
	    // more details on why stat failed.	
		blnReturn = false;
	}
  
	return(blnReturn); 	
}

string toString(int i)
{
        std::string s;
        std::stringstream out;
        out << i;
        s = out.str();

        while(s.length() < 4)
        {
                s = "0" + s;
        }

        return s;
}

//Main is the main driver.  It will read in the data and init the needed values.
//Once everthing has been initialized it will call the algorithms needed to process
//ars = (1) n (2) j (3) k (4) genefile (5) phenfile (6) Eg (7) Et (8) prefix (9) reg parm to vary 1=l, 2=g1, 3=g2 (10) 0 = linear 1 = log (11) start (12) end (13) lambdai (14) gamma1i (15) gamma2i (16) initb
int main(int argc, char* argv[])
{
	//Input Data
	int n,j,k; //individuals, genes, traits
	string phenotype_file, genotype_file, prefix;
	string edge_gene_file, edge_trait_file;

	//Read in the parameters
	if(argc == 17)
	{
		phenotype_file = argv[5];
		genotype_file = argv[4];

		if(!fileExists(phenotype_file))
		{
			cerr << "Trait file does not exist in file system." << endl;
			return -1;
		}
		if(!fileExists(genotype_file))
		{
			cerr << "SNP file does not exist in file system." << endl;
			return -1;
		}

		prefix = argv[8];
		n = atoi(argv[1]);
		j = atoi(argv[2]);
		k = atoi(argv[3]);

		//Confirm values
		cout << "Will use phenotypes in " << phenotype_file << endl;
		cout << "Will use genotypes in "  << genotype_file  << endl;
		cout << "n,j,k: " << n << " " << j << " " << k << endl << flush;		

		Normalizer no;
		//Read in X
		DataMatrix X(n,j,genotype_file);
		no.normalizeMat(&X);
		//X.writeToFile("XTest.txt"); //this test passed successfully

		//Read in y
		DataMatrix y(n,k,phenotype_file);
		no.normalizeMat(&y);
		//y.writeToFile("Ytest.txt");

		edge_gene_file = argv[6];
		edge_trait_file = argv[7];

		string initB = argv[16];

		DataMatrix Eg(j,j,edge_gene_file);
		DataMatrix Et(k,k,edge_trait_file);
		cout << "Running with provided edges file: " << edge_gene_file << " " << edge_trait_file << endl;

		CoordDescent cd;
		double s = atof(argv[11]);
		double e = atof(argv[12]);
		double l1 = atof(argv[13]);
		double g1 = atof(argv[14]);
		double g2 = atof(argv[15]);
		double step = 0;
		switch(atoi(argv[9]))
		{
			case 1: step = l1; break;
			case 2: step = g1; break;
			case 3: step = g2; break;
		}
		int i = 0;
		if(atoi(argv[10]) == 0) //linear search
		{
			for(double d = s; d <= e; d += step)
			{
				DataMatrix initb(j,k,initB);
                                switch(atoi(argv[9]))
                                {
                                        case 1: l1 = d; break;
                                        case 2: g1 = d; break;
                                        case 3: g2 = d; break;
                                }
				cd.optimize(&X,&y,&Eg,&Et,l1,g1,g2,&initb);
				string s = prefix + "_" + toString(i++);
				initb.writeToFile(s);
			}
		}
		else //log10 search
		{
			for(double d = s; d <= e; d *=10)
			{
				DataMatrix initb(j,k,initB);
				switch(atoi(argv[9]))
				{
					case 1: l1 = d; break;
					case 2: g1 = d; break;
					case 3: g2 = d; break;
				}
				cd.optimize(&X,&y,&Eg,&Et,l1,g1,g2,&initb);
				string s = prefix + "_" + toString(i++);
				initb.writeToFile(s);
			}
		}
	}
	else
	{
		cerr << "invalid number of parameters ..." << endl;
		return -1;
	}

	return 0;
}



