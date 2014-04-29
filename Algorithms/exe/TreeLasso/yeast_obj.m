clear;

addpath(genpath('../mylib'));
data.Y = load('./tpheno_m0.30.txt'); %N by K matrix
data.Y = data.Y(:, 1:2000);
data.X = load('./chrall_x_cStrain.txt'); %N by J matrix
option.maxiter=10000;
option.threshold=0;
option.tol=1e-6;
h=0.7;
myDist = tril(1-abs(fastCorr(data.Y)), -1);
myCluster=linkage(myDist(myDist~=0)', 'complete'); 
[T Tw] = convH2T(myCluster, h);
clear myCluster myDist;
idx=full(sum(T,2)==1);
T(idx,:)=[];
Tw(idx)=[];
[X, Y, XY, C, g_idx, TauNorm, L1] = pre_grad(data.X, data.Y, T, Tw);

epsilon=1000;
mu=0.01;
best_lam=10;
L=L1+best_lam^2*TauNorm/mu;
[Beta, obj, time] = accgrad( Y, X, best_lam, T,  XY, C, g_idx, L, mu, option);                
%save('yeast_result', 'Beta', 'obj', 'time');

%load yeast_result

plot(1:length(obj),obj, 'r-*');
set(gca, 'FontSize',18);
xlabel('Iteration','FontSize',25);
ylabel('Objective Value', 'FontSize', 25);
xlim([0,length(obj)+10])
