function C=fastCorr(A)
       n=size(A,1);
       B=zscore(A);
       C=B'*B/(n-1);
       
end 