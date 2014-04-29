%function [ output_args ] = PCA_vis(filename)
%PCA_VIS Summary of this function goes here
%   Detailed explanation goes here
d = load (filename);
[COEFF,SCORE,latent,tsquare] = princomp(d);
dlmwrite('eig_coef', SCORE(:,1:5), 'delimiter', '\t');
%fID = fopen('eig_coef','w');
%fprintf(fID, '\t', SCORE(:,1:5));
%fclose(fID);
%end

