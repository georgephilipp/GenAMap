
% isLogistic = 1 if you want to run logistic, else 0 for Gaussian

function [bestLambda bestModel] =  BIC_kriti(X, Y, Z, ...
    isLogistic, lambdaValues, vocab)

if (nargin < 6)
    vocab = [];
end

k = length(unique(Z));
p=size(X,2);
n=size(X,1);

bestLambda = -1;
bestBIC = 100000;
bestNonZeros = -1;
bestModel = [];

startingPoint = zeros(length(unique(Z)),size(X, 2));
BIC = zeros(length(lambdaValues), 1);
for rounds = length(lambdaValues):-1:1
    lambda = lambdaValues(rounds);
    model = train(X, Y, Z, isLogistic, lambda, startingPoint, vocab);
    startingPoint = model.predictedBeta;

    BIC(rounds) = findBIC(startingPoint, X, Y, Z);
    nonZeros = sum( sum(abs(model.predictedBeta)) ~=0);
    if(BIC(rounds) < bestBIC) % > instead of >= to encourage more sparse outputs.
        bestBIC = BIC(rounds);
        bestLambda = lambda;
        bestNonZeros = nonZeros;
	bestModel = model;
    end
    fprintf('Current lambda= %f, BIC= %f nonZeros=%f\n', lambda, BIC(rounds), nonZeros);
end
BIC'
fprintf('With %d train samples, bestLambda= %f BIC= %f nonZeros= %d\n\n', ...
        size(X,1), bestLambda, bestBIC, bestNonZeros );


end



