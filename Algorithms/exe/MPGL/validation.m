% run over many lambdas and use validation set to pick best lambda.

function [ourError bestLambda bestNonZeroIdx] = validation (X_train , Y_train, Z_train, X_test, Y_test, Z_test, ...
    isLogistic, lambdaValues, vocab)

bestLambda = -1;
bestError = 100000;
bestNonZeros = -1;
bestNonZeroIdx = -1;

startingPoint = zeros(length(unique(Z_train)),size(X_train, 2));
ourError = zeros(length(lambdaValues), 1);

%% remove elements with too small variance
p = size(X_train,2);
remove = zeros(p,1);
remove(var(X_train) < 1e-3) = 1;
k=length(unique(Z_train));
remove = noPerfectCorrs(X_train, Z_train, k, remove);

for rounds = length(lambdaValues):-1:1
    lambda = lambdaValues(rounds);
    model = train(X_train , Y_train, Z_train, isLogistic, lambda, startingPoint, vocab, remove);
    %model = train(X_train , Y_train, Z_train, isLogistic, lambda);
    startingPoint = model.predictedBeta;
    ourError(rounds) = test(model, X_test, Y_test, Z_test);
    nonZeros = sum( sum(abs(model.predictedBeta)) ~=0);
    if(ourError(rounds) <= bestError) % > instead of >= to encourage more sparse outputs.
        bestError = ourError(rounds);
        bestLambda = lambda;       
        bestNonZeros = nonZeros;
	bestNonZeroIdx = model.predictedBeta;
    end
    fprintf('Current lambda= %f, error= %f nonZeros=%f\n', lambda, ourError(rounds), nonZeros);   
end
ourError'
fprintf('With %d train samples, and %d test samples, bestLambda= %f error= %f nonZeros= %d\n\n', ...
        size(X_train,1), size(X_test,1), bestLambda, bestError, bestNonZeros );

cnt = 1;
for i=bestLambda-5:.3:bestLambda+5
    lambda = i;
    model = train(X_train, Y_train, Z_train, isLogistic, lambda, startingPoint, vocab, remove);
    startingPoint = model.predictedBeta;
    err(cnt) = test(model, X_test, Y_test, Z_test);
    cnt = cnt + 1;
    nonZeros = sum(sum(abs(model.predictedBeta)) ~= 0 ) ;
    if(err(cnt - 1) <= bestError)
         bestError = err(cnt-1);
         bestLambda = lambda;
         bestNonZeros = nonZeros;
         bestNonZeroIdx = model.predictedBeta;
    end
    fprintf('Current lambda = %f, error = %f, nonZeros = %f\n', lambda, err(cnt-1), nonZeros);

end
 
err'

fprintf('With %d train samples, and %d test samples, bestLambda= %f error= %f nonZeros= %d\n\n', ...
        size(X_train,1), size(X_test,1), bestLambda, bestError, bestNonZeros );


end
