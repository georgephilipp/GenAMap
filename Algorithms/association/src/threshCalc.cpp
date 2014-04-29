#include <iostream>
#include <fstream>
#include <sstream>
#include <stdlib.h>
#include <string>
#include <math.h>
#include <cstdlib>
#include <cstring>
using namespace std;
using std::string;
int main(int argc, char* argv[])
{
	string mask = "";
	int numM = 1;
	int numT = 1;
	int subsz = 0;
	double thresh = 0.05;
	ofstream myfile;
	myfile.open("thresh.txt");

	for(int i = 0; i<argc; i++)
	{
	      	switch(i)
		{
			case 1:
		        	mask = argv[i];
	  			break;
			case 2:
				numM = atoi(argv[i]);
				break;
			case 3:
				numT = atoi(argv[i]);
				break;
			case 4:
	 			subsz = atoi(argv[i]);
				break;
		}
    	}
  
	double FDR = 1;
	int pValCount = 1;
	char lineNum [100];
	int numLines = 0;
	string fiName = mask + "_1_1_TP.txt";  
	FILE* test = fopen(fiName.c_str(), "rt");
	while(fgets(lineNum, 100, test))
		numLines++;
	int numPVals = numLines -1;
	double pCount [numPVals][2];
	cout<< "Read " << numLines << " pvalues " << endl;

	for(int i = 0; i < numPVals; i++)
    	{
      		pCount[i][1] = 0.0;
      		pCount[i][2] = 0.0;
    	}

	for(int i = 0; i < ceil((double)numT/(double)subsz); i++)
	{
		for(int j = 0; j< ceil((double)numM/(double)subsz); j++)
		{
			string k;
			string m;
	       		std::stringstream sk;
			sk << (i*subsz +1);
			std::stringstream sm;
			sm << (j*subsz +1);
			sk >> k;
			sm >> m;
	       
		       	string TPname = mask + "_" + m + "_" + k + "_TP.txt";
		 	string FPname = mask + "_" + m + "_" + k  + "_FP.txt";

			cout << TPname << endl;
			cout << FPname << endl;
		
			FILE* fp = fopen(FPname.c_str(), "rt");
			FILE* tp = fopen(TPname.c_str(), "rt");
	        	char line [100];
			if(fp != NULL && tp!= NULL)
		  	{
		  		char* token;
		  		int i = 0;
		  		while( fgets(line, 100,tp)!= NULL && i < numPVals)
		  		{
		   
		    			// myfile << line << endl;
		    			token =strtok(line, " ");
		     			token = strtok(NULL, " ");
		     
		      			pCount[i][1] = pCount[i][1]+ atof(token);
		      			i++;
		      			//myfile << pCount[0][1]<< endl;
		  		}

		  		int j = 0;
		  		while(fgets(line, 100, fp) != NULL && j< numPVals)
		    		{
		    
		      			token = strtok(line, " ");
		      			token = strtok(NULL, " ");

		      			pCount[j][2] = pCount[j][2] + atof(token);
		      			j++;
  					//   myfile << pCount[0][2] << endl;

		    		}
		    
		  	}
		      	else
			{
				myfile <<"Error opening files" << endl;
				return -1;     
			}
	           
		        fclose(fp);
	       		fclose(tp);
		}
	}
	      	      
	thresh = 0.05;	

	int x = -1;
	while(FDR >= thresh)
	{
		x++;
		double FPs =  pCount[x][2];
	    	double TPs =  pCount[x][1];
	    	FDR =FPs/TPs;
		cout << FDR << endl;
	    	// myfile << pCount[0][2] << " " << pCount[0][1] << endl;  
	}
	//myfile << x<< endl;
	string fileName = mask+ "_1_1_TP.txt";
	FILE* f = fopen(fileName.c_str(), "rt");
	char pLine [100];
	for( int i = 0; i<=x; i++)
	{
		fgets(pLine, 100, f);
	}
	
	char* pValue = strtok(pLine, " ");
	fclose(f);

       	myfile << pValue;
  	myfile.close();
  	return 1;
}


