#ifndef COORDASCENT_H
#define COORDASCENT_H
#include <vector>
#include "DataMatrix.h"
#include "DataMatrix3D.h"

using namespace std;

class CoordDescent
{
public:
	CoordDescent();

	void optimize(DataMatrix* X, DataMatrix* y, DataMatrix* E, DataMatrix* parms, double lambda1, double lambda2, double gamma2, DataMatrix* initB);

	bool calcBeta(DataMatrix* beta, DataMatrix* X, DataMatrix* y, DataMatrix* Eg, DataMatrix* Et, DataMatrix* njk, DataMatrix3D* nkuv, DataMatrix3D* njml, int r, int c, double lambda, double gamma1, double gamma2, vector<vector<double> > sumyikXij, double* sumXij2, vector<vector<double> > sumXijXijp);

	double outputMinEqVal(DataMatrix* y, DataMatrix* X, DataMatrix* beta, DataMatrix* Eg, DataMatrix* Et, DataMatrix* njk, DataMatrix3D* nkuv, DataMatrix3D* njml, double lambda, double gamma1, double gamma2, double pastval);

private:
	
	void updatedjk(DataMatrix* djk, DataMatrix* beta);
	void updatedmlj(DataMatrix3D* nkuv, DataMatrix3D* njml, DataMatrix* beta, DataMatrix* Eg, DataMatrix* Et);
	void testdjkdmlj(DataMatrix* djk, DataMatrix* dmlj);
	void calcSumCnstnts(vector<vector<double> >* sumyikXij, double sumxij2[], vector<vector<double> >*  sumxijxijp, DataMatrix* X, DataMatrix* y);
};

#endif
