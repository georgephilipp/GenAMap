#include <iostream>
#include "../inc/DataMatrix.h"
#include "../inc/DataMatrix3D.h"
#include "../inc/CoordDescent.h"
#include "../inc/Fr.h"
#include <math.h>
#include <vector>
#include "../inc/matrix.h"

using namespace std;
using namespace math;

typedef matrix<double> Matrix;

CoordDescent::CoordDescent()
{
}

// Run the coordinate descent algorithm on this data and return the new d_mlj, d_jk, and beta
void CoordDescent::optimize(DataMatrix* Y, DataMatrix* Z, DataMatrix* Eg, DataMatrix* Et, double lambda, double gamma1, double gamma2, DataMatrix* beta)
{
	cout << "Running lambda = " << lambda << " , gamma1 = " << gamma1 << ", gamma2 = " << gamma2 << endl;
	DataMatrix njk(beta->Rows(), beta->Cols(),"");
	DataMatrix3D nkuv(Eg->Rows(), Eg->Cols(),Et->Rows());
	DataMatrix3D njml(Et->Rows(), Et->Cols(),Eg->Rows());
	
	int j = Y->Cols();
	int k = Z->Cols();
	double sumXsquared[j];
	vector<vector<double> > sumyikXij(k, vector<double>(j));
	vector<vector<double> > sumXijXijp(j, vector<double>(j));
	calcSumCnstnts(&sumyikXij, sumXsquared, &sumXijXijp, Y, Z);

	double pastval = outputMinEqVal(Z, Y, beta, Eg, Et, &njk, &nkuv, &njml, lambda, gamma1, gamma2, 1e99);
	double total = pastval;

	updatedjk(&njk, beta);
	updatedmlj(&nkuv, &njml, beta, Eg, Et);
	int off = 0;

	for(int t = 0; t < 10000; t ++)
	{
		double pastval2 = pastval;

		for(int i = 0; i < beta->Rows(); i ++)
		{
			for(int j = 0; j < beta->Cols(); j ++)
			{
				calcBeta(beta, Y, Z, Eg, Et, &njk, &nkuv, &njml, i, j, lambda, gamma1, gamma2, sumyikXij, sumXsquared, sumXijXijp);
			}
		}

		pastval2 = total;
		
		updatedjk(&njk, beta);
		updatedmlj(&nkuv, &njml, beta, Eg, Et);

		total = outputMinEqVal(Z, Y, beta, Eg, Et, &njk, &nkuv, &njml, lambda, gamma1, gamma2, total);

		if(abs((pastval - total)/(Y->Cols() * Z->Cols())) < 1e-7 )//&& (pastval-total)>0 )
		{
			/*if((pastval - total)/Y->Rows() < 0)
			{
				if(++off>1)
				for (int i = 1; i < 10; i ++)
					cout << "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" << endl;
				else
				{
					pastval = total;
					continue;
				}
			}*/
			break;
		}
		off = 0;
		pastval = total;
	}
}

//both djk and dmlj should sum to one.
void CoordDescent::testdjkdmlj(DataMatrix* djk, DataMatrix* dmlj)
{
	double sumdjk = 0;
	for(int i = 0; i < djk->Rows(); i ++)
	{
		for(int j = 0; j < djk->Cols(); j ++)
		{
			sumdjk += djk->getVal(i,j);
		}
	}

	cout << "Sum of djk " << sumdjk << endl;

	double sumdmlj = 0;
	for(int i = 0; i < dmlj->Rows(); i++)
	{
		for(int j = 0; j < dmlj->Cols(); j ++)
		{
			sumdmlj += dmlj->getVal(i,j);
		}
	}

	cout << "Sum dmlj " << sumdmlj << endl;
}

void CoordDescent::updatedmlj(DataMatrix3D* nkuv, DataMatrix3D* njml, DataMatrix* beta, DataMatrix* Eg, DataMatrix* Et)
{
	double sum = 0.0;
	for(int u = 0; u < Eg->Rows(); u ++)
	{
		for(int v = 0; v < Eg->Rows(); v ++)
		{
			double f = Eg->getVal(u,v);
			if(u == v) f = 0;
			double s = 1.0;
			if(f < 0) s = -1.0;
			if(f != 0)
			{
				for(int k = 0; k < Et->Rows(); k ++)
				{
					double d = abs(f*(beta->getVal(u,k) - s * beta->getVal(v,k)));
					nkuv->setVal(u,v,k,d);
					nkuv->setVal(v,u,k,d);
					sum += d;
				}
			}
			else
			{
				for(int k = 0; k < Et->Rows(); k ++)
				{
					nkuv->setVal(u,v,k,0.0);
				}
			}
		}
	}

	for(int u = 0; u < nkuv->Rows(); u ++)
		for(int v = 0; v < nkuv->Cols(); v++)
			for(int k = 0; k < nkuv->D3s(); k ++)
				nkuv->setVal(u,v,k,nkuv->getVal(u,v,k)/sum);

	sum = 0.0;
	for(int m = 0; m < Et->Rows(); m ++)
	{
		for(int l = 0; l < Et->Rows(); l ++)
		{
			double f = Et->getVal(m,l);
			if(m == l) f = 0;
			double s = 1.0;
			if(f < 0) s = -1.0;
			if(f != 0)
			{
				for(int j = 0; j < Eg->Rows(); j ++)
				{
					double d = abs(f*(beta->getVal(j,m) - s * beta->getVal(j,l)));
					njml->setVal(m,l,j,d);
					njml->setVal(l,m,j,d);
					sum += d;
				}
			}
			else
			{
				for(int j = 0; j < Eg->Rows(); j ++)
				{
					njml->setVal(m,l,j,0.0);
				}
			}
		}	
	}

	for(int m = 0; m < njml->Rows(); m ++)
		for(int l = 0; l < njml->Cols(); l++)
			for(int j = 0; j < njml->D3s(); j ++)
				njml->setVal(m,l,j,njml->getVal(m,l,j)/sum);
}


void CoordDescent::updatedjk(DataMatrix* djk, DataMatrix* beta)
{
	double sumbeta = 0;

	for(int j=0;j<beta->Rows();j++)
	{
		for(int k =0;k<beta->Cols();k++)
		{
			double val = beta->getVal(j,k);
			if(val < 0)
				val *= -1;
			sumbeta += val;
		}
	}

	for(int j=0;j<beta->Rows();j++)
	{
		for(int k =0;k<beta->Cols();k++)
		{
			double val = beta->getVal(j,k) / sumbeta;
			if(val < 0)
				val *= -1;
			if(val < 1e-200)
			{
				val = 1e-200;
			}
			djk->setVal(j,k,val);
		}
	}
}

bool CoordDescent::calcBeta(DataMatrix* beta, DataMatrix* X, DataMatrix* y, DataMatrix* Eg, DataMatrix* Et, DataMatrix* njk, DataMatrix3D* nkuv, DataMatrix3D* njml, int r, int c, double lambda, double gamma1, double gamma2, vector<vector<double> > sumyikXij, double* sumXij2, vector<vector<double> > sumXijXijp)
{
	//sum over x and y
	double sum1 = 0; //sum_i (y_ic * X_ir) - (X_ir sum_j!=r (X_ij beta_jc))
	double sum2 = 0; //sum_j!=r (X_ij beta_jc)
	double sum3 = 0; //sum_i x_ir^2

	double sumt1 = 0;
	double sumt2 = sumXij2[r];

	sumt1 = sumyikXij[c][r];
	double toSub = 0.0;

	for(int j = 0; j < X->Cols(); j ++)
	{
		if(j != r)
		{
			toSub -= beta->getVal(j,c) * sumXijXijp[r][j];
		}
	}

	sumt1 += toSub;

	sum1 = sumt1;
	sum3 = sumt2;

	double sum4 = 0; 
	double sum5 = 0; 
	double sum7 = 0;
	double sum8 = 0;

	for(int u = 0; u < beta->Rows(); u ++)
	{
		if(u == r)
			continue;

		double f = Eg->getVal(u,r);
		double s = 1.0;
		if(f < 0) s = -1.0;
		f = f * f;
		if(f != 0)
		{
			double d = f * s * beta->getVal(u,c);
			if(nkuv->getVal(u,r,c) != 0)
			{
				sum4 += d / nkuv->getVal(u,r,c);
				sum5 += f / nkuv->getVal(u,r,c);
			}
		}
	}
	sum4 *= gamma1;
	sum5 *= gamma1;

        for(int m = 0; m < beta->Cols(); m ++)
        {
                if(m == c)
                        continue;

                double f = Et->getVal(m,c);
                double s = 1.0;
                if(f < 0) s = -1.0;
                f = f * f;
                if(f != 0)
                {
                        double d = f * s * beta->getVal(r,m);
                        if(njml->getVal(m,c,r) != 0)
                        {
                                sum7 += d / njml->getVal(m,c,r);
                                sum8 += f / njml->getVal(m,c,r);
                        }
                }
        }
	sum7 *= gamma2;
	sum8 *= gamma2;

	double sum6 = 0;
	if(njk->getVal(r,c) != 0)
		sum6 = lambda / njk->getVal(r,c);
	//cout << sum1 << " " << sum4 << " " << sum7 << " " << sum3 << " " << sum6 << " " << sum5 << " " << sum8 << endl;

	double top = sum1 + sum4 + sum7;
	double bottom = sum3 + sum6 + sum5 + sum8;

	double value = top / bottom;

	if(value == beta->getVal(r,c))
		value = value * 1.01;

	if(isnan(value))
	{
		cout << "Is nan found in beta! " << r << " " << c << endl;
		//return false;
		value = 0.0;
	}

	beta->setVal(r, c, value);

	return true;
}

//calculate the value of the objective function.
//We want to ensure that we are always decreasing
double CoordDescent::outputMinEqVal(DataMatrix* y, DataMatrix* X, DataMatrix* beta, DataMatrix* Eg, DataMatrix* Et, DataMatrix* njk, DataMatrix3D* nkuv, DataMatrix3D* njml, double lambda, double gamma1, double gamma2, double pastval)
{
	double sum1 = 0;
	for(int i = 0; i < y->Rows(); i ++)
	{
		for(int k = 0; k < y->Cols(); k ++)
		{
			double sum2 = 0;
			for(int j = 0; j < X->Cols(); j ++)
			{
				sum2 += X->getVal(i,j) * beta->getVal(j,k);
			}
			sum1 += (y->getVal(i,k) - sum2) * (y->getVal(i,k) - sum2);
		}
	}
	sum1 = sqrt(sum1);

	double sum3 = 0;
	for(int j = 0; j < beta->Rows(); j ++)
	{
		for(int k=0; k < beta->Cols(); k ++)
		{
			if(njk->getVal(j,k) != 0)
				sum3 += (beta->getVal(j,k) * beta->getVal(j,k)) / njk->getVal(j,k);
		}
	}
	sum3 *= lambda;

	double sum5 = 0;
	for(int i = 0; i < beta->Rows(); i ++) //go through each gene
	{
		for(int j = i+1; j < beta->Rows(); j++)//we only want to see each edge once
		{
			double geneEw = Eg->getVal(i,j);
			double s = 1.0;
			if(geneEw < 0) s = -1.0;
			geneEw *= geneEw;

			if(abs(geneEw) > 0)
			{
				for(int k = 0; k < beta->Cols(); k ++)
				{
					double d = beta->getVal(i,k) - s * beta->getVal(j,k);
					d *= d;
					if(nkuv->getVal(i,j,k) != 0)
					{
						d = d / nkuv->getVal(i,j,k);
					}
					else
					{
						d = 0;
					}
					sum5 += geneEw * d;
				}
			}
		}
	}
	sum5 *= gamma1;

	double sum6 = 0;
        for(int i = 0; i < beta->Cols(); i ++) //go through each gene
        {
                for(int j = i+1; j < beta->Cols(); j++)//we only want to see each edge once
                {
                        double tEw = Et->getVal(i,j);
                        double s = 1.0;
                        if(tEw < 0) s = -1.0;
                        tEw *= tEw;

                        if(abs(tEw) > 0)
                        {
                                for(int k = 0; k < beta->Rows(); k ++)
                                {
                                        double d = beta->getVal(k,i) - s * beta->getVal(k,j);
                                        d *= d;
                                        if(njml->getVal(i,j,k) != 0)
                                        {
                                                d = d / njml->getVal(i,j,k);
                                        }
                                        else
                                        {
                                                d = 0;
                                        }
                                        sum6 += tEw * d;
                                }
                        }
                }
        }
        sum6 *= gamma2;


	//cout << sum1 << " " << sum3 << " " << sum5 << " " << sum6 << endl;
	double total = sum1 + sum3 + sum5 + sum6;
	printf("%.6lf\n", total);

	//this happens when checking for one beta, you'll have a change like -4e+62
	//I have never seen this happen after updated the dmlj's
	/*if(pastval - total < -1e+10)
	{
		//cout << sum1 << endl;
		//cout << sum3 << endl;
		//cout << sum5 << endl;
		updatedmlj(d_mlj, beta, E, method);
		if(pastval != -8)
		{
			double temp = outputMinEqVal(y, X, beta, E, d_jk, d_mlj, lambda1, lambda2, -8, method);
			return temp;
		}
		cout << lambda1 << " " << lambda2 << endl;
		beta->writeToFile("err_beta");
		E->writeToFile("err_E");
		d_jk->writeToFile("err_djk");
		d_mlj->writeToFile("err_dmlj");
		X->writeToFile("err_X");
		y->writeToFile("err_y");
		int j = 0;
		for(int i = 0; i < 1e10; i ++)
		{
			j = j + i;
		}
	}*/
	return total;
}

void CoordDescent::calcSumCnstnts(vector<vector<double> >*  sumyikXij, double sumxij2[], vector<vector<double> >* sumxijxijp, DataMatrix* X, DataMatrix* y)
{
	for(int j=0; j < X->Cols(); j ++)
	{
		sumxij2[j] = 0;
		for(int r=0; r < X->Cols(); r++)
		{
			(*sumxijxijp)[j][r] = 0;
		}
		for(int k = 0; k < y->Cols(); k ++)
		{
			(*sumyikXij)[k][j] = 0;
		}
	}

	for(int j = 0; j < X->Cols(); j ++)
	{
		for(int i = 0; i < X->Rows(); i ++)
		{
			sumxij2[j] += X->getVal(i,j) * X->getVal(i,j);
			for(int q = 0; q < X->Cols(); q++)
			{
				if(q != j)
					(*sumxijxijp)[j][q] += X->getVal(i,j) * X->getVal(i,q);
			}
		}
	}

	for(int k=0; k < y->Cols(); k ++)
	{
		for(int j = 0; j < X->Cols(); j ++)
		{
			for(int i = 0; i < X->Rows(); i ++)
			{
				(*sumyikXij)[k][j] += y->getVal(i,k) * X->getVal(i,j);
			}
		}
	}
}














