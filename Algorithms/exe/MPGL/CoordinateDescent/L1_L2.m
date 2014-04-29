
function w = L1_L2(X,y, groups, isLogistic, lambda, w_init)

debug = true;
nGroups =  length(unique(groups(groups>0)));
p=size(X,2);

if(debug)
    fprintf('Calling optimization routine for %d by %d data with %d groups and lambda= %f: ', size(X,1), p, nGroups, lambda);
end
t0 = cputime;
%% Minimize Gaussian or Logistic Loss function with the different regularizers
if(isLogistic)
    if(debug)
        fprintf('Using Logistic Loss\n');
    end
    funObj_sub = @(w)LLoss(w,X,y);
else
    if(debug)
        fprintf('Using Gaussian Loss\n');
    end
    funObj_sub = @(w)GaussianLoss(w,X,y);
end

if(sum(abs(w_init)) == 0)
    w_init = randn(p,1);
end
w_init(isnan(w_init)) = randn(sum(sum(isnan(w_init))), 1);

% Make Initial Value
w_init = [w_init;zeros(nGroups,1)];
for g = 1:nGroups
    w_init(p+g) = norm(w_init(groups==g));
end

% Make Objective and Projection Function
funObj = @(w)auxGroupLoss(w,groups,lambda,funObj_sub);
funProj = @(w)auxGroupL2Proj(w,groups);

% Solve
wAlpha = minConF_SPG(funObj,w_init,funProj);

w = wAlpha(1:p);

if(debug)
    fprintf('For lambda %f nonzeros= %d timeTaken= %f\n', lambda, sum(w~=0), cputime-t0);
end
end
