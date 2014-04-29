#include <iostream>


int writeFile(File results, int id, int pval);
int main pvals(int argc, char* argv [])
{
  double thresh = 0.05;

  /*Obtain pvalues for this marker/trait:
    File infile = argv[?];
    infile.open();
    ...
    int size = ...
  */
  File results ...
  int pval;
  int FDR;
  for(...)
    {
  int notFound = 1; 
  while(notFound)
    {
      //Get 
      FDR = FP/TP; 
      if(FDR<0.05)
	notFound = 0;
      else
	pval++;
    }
  notFound = 1;
  writeFile(results, id, pval);
    }


}
int writeFile(File results, int id, int pval)
{
  results << id << " " << pval << endl;

}
