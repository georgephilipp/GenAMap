
function w = groupLasso_CallMe(X,y, lambdaValues, groups, isLogistic)
nGroups =  length(unique(groups(groups>0)));
p=size(X,2);

%% Minimize Gaussian Loss function with the different regularizers
if(isLogistic)
	fprintf('Using Logistic Loss\n');
	funObj_sub = @(w)LLoss(w,X,y);
else
	fprintf('Using Gaussian Loss\n');
	funObj_sub = @(w)GaussianLoss(w,X,y);
end

%% L1L2 (parameterized in terms of lambda)

i = 1;
w_init = zeros(p,1);
for lambda = lambdaValues

    t0     = cputime;	
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
	fprintf('For lambda %f nonzeros= %d timeTaken= %f\n', lambda, sum(wAlpha(1:p)~=0), cputime-t0);
    w(:,i) = wAlpha(1:p);
    w_init = w(:,i);
    i = i + 1;
end
end

