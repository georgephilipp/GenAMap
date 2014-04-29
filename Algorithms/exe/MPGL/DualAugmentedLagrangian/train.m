
function model = train (X_train , Y_train, Z_train, isLogistic, lambda, startingPoint, vocab, remove)
debug=2;
k=length(unique(Z_train));
p = size(X_train,2);
n = size(X_train,1);

if(nargin < 6 || isempty(startingPoint))
    startingPoint = zeros(k,p);
end

if(nargin < 7 )
    vocab = [];
end

%% remove elements with too small variance
%remove = zeros(p,1);
%remove(var(X_train) < 1e-3) = 1;
%remove = noPerfectCorrs(X_train, Z_train, k, remove);

%% normalize the data for each population
xMean = zeros(k, p);
xStd = ones(k, p);
yMean = zeros(k, 1);
yStd = ones(k, 1);
for i=1:k
    [nn mm ss]=normalizeData(X_train(Z_train==i, :));
    xMean(i,:) = mm;
    xStd(i,:) = ss;
    X_train(Z_train==i, :) = nn;
    remove(ss <= 10^-10) = 1;  %% remove 

    if(~isLogistic)
        [nn mm ss] = normalizeData(Y_train(Z_train==i));
        yMean(i) = mm; yStd(i) = ss;
        Y_train(Z_train==i) = nn;
    end
end

X_train = X_train(:, remove==0);
startingPoint = startingPoint(:, remove==0);
fprintf('Removed %d features, original=%d current=%d\n', sum(remove==1), p, size(X_train, 2));
p=size(X_train,2);

%% if logistic, add a bias term to the mix
if(isLogistic)
   X_train = [X_train ones(n,1)]; 
   startingPoint = [startingPoint zeros(k, 1)];
   p=size(X_train,2);
end

%% convert training data into right format
[X_tr Y_tr Z_tr groups] = convert(X_train, Y_train, Z_train, k);
if(~isLogistic)
    groups(groups==0) = p;
end


%% Run the optimization to find the non-zeros

%pred = L1_L2 (X_tr, Y_tr, groups, isLogistic, lambda, startingPoint);
%pred(pred<10^-7) = 0;
%predictedBeta = reshape(pred, [], k)';

predictedBeta = L1_L2_usingDAL (X_tr, Y_tr, groups, k, lambda, startingPoint);

%% find MLE estimate for non-zeros
for i=1:k
    Xs = X_train(Z_train==i, predictedBeta(i,1:p)~=0);
    if(isLogistic)
        predictedBeta(i, predictedBeta(i, :)~=0) = logistic( Xs, Y_train(Z_train==i), [], 1 )' ;
    else
%	size(Xs)
        predictedBeta(i, predictedBeta(i, :)~=0) = (Xs'*Xs) \ (Xs'* Y_train(Z_train==i));
    end
end

%fprintf('Inverse found\n');
%% remove the bias term, if logistic
if(isLogistic)
    model.bias = predictedBeta(:, end);
    predictedBeta = predictedBeta(:, 1:(p-1));
else
    model.bias = zeros(k, 1);
end

%% Put the removed features back in
p_new = length(remove);
finalBeta = zeros(k, p_new);
finalBeta(:, remove==0) = predictedBeta;
predictedBeta = finalBeta;
clear finalBeta;


%% Print the features selected
if(debug>1)
    %print_nonzeroBeta(predictedBeta, vocab);
end

%% return relevant things
model.predictedBeta = predictedBeta; % k by p matrix of weights beta for each population
model.xMean = xMean; % k by p matrix of mean for each population
model.yMean = yMean; % k vector of mean
model.xStd = xStd;  % k by p matrix
model.yStd = yStd; % k vector
model.isLogistic = isLogistic;

end

