function [proj] = projection_routine(alpha_hat)
% Seunghak Lee (seunghak@cs.cmu.edu)
% Euclidean projection

small_epislon = 10^-3;

N = length(alpha_hat);
H = eye(N);
f = -alpha_hat';
A = -eye(N);
b = zeros(N,1) - small_epislon;
Aeq = ones(1,N);
beq =  1;

warning off;
options = optimset('display','off');
proj = quadprog(H,f,A,b,Aeq,beq,[],[],[],options);
warning on;

