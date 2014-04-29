%if ~exist('mygennetwork.m', 'file')
%	copyfile('../exe/mygennetwork.m','.');
%	copyfile('../exe/pre_grad.m','.');
%	copyfile('../exe/accgrad.m','.');
%	copyfile('../exe/shrink.m','.');
%	copyfile('../exe/soft_threshold.m', '.');
%else
%	pause(20);
%end
addpath(genpath('../exe/ADL'));

N = size(x,1);

size(x)
size(y)
size(features)
size(Lambda)

divs = load('div.txt');
xp = x(setdiff(1:N,divs(div,1):divs(div,2)),:);
yp = y(setdiff(1:N,divs(div,1):divs(div,2)),:);

clear y;
clear x;

X = normalize(xp);
Y = normalize(yp);
clear xp;
clear yp;

size(X)
size(Y)

params.features = features;
params.stopVal = 0.0001;
params.verbose = 1;
params.learnWeights = 1;  
params.mcut = groups;

groups

for i=1:size(Lambda,2)
    params.lambda1 = Lambda(1,i);
    params.lambda2 = Lambda(2,i);
    [beta,theta,omega,rho,nu] = adaptive_multi_task_lasso(X, Y, params);
    save(['beta' num2str(type) '/' mask 'betas_' num2str(i) '_' num2str(div) '.txt'], 'beta', '-ascii');
    save(['theta' num2str(type) '/' mask 'thetas_' num2str(i) '_' num2str(div) '.txt'], 'theta', '-ascii');
    save(['omega' num2str(type) '/' mask 'omegas_' num2str(i) '_' num2str(div) '.txt'], 'omega', '-ascii');
    save(['rho' num2str(type) '/' mask 'rhos_' num2str(i) '_' num2str(div) '.txt'], 'rho', '-ascii');
    save(['nu' num2str(type) '/' mask 'nus_' num2str(i) '_' num2str(div) '.txt'], 'nu', '-ascii');
end



