
function [w] = L1_L2_usingDAL(X,y, groups, k, lambda, w_init)
nGroups =  length(unique(groups));
p=size(X,2);
t0 = cputime;
numberOfElementsPerGroup = ones(nGroups,1)*k;
w = dallrgl(w_init, [], X, y, lambda);
%w = dallrgl(w_init, 0, X, y, lambda, 'blks', numberOfElementsPerGroup);
fprintf('For lambda %f nonzeros= %d timeTaken= %f\n', lambda, sum(sum(w)~=0), cputime-t0);
end
