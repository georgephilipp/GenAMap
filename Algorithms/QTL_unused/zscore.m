function [pval] = zscore(U, mu, sigma);

[h pval] = ztest(U, mu, sigma);