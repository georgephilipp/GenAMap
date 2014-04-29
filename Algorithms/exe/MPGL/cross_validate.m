
% isLogistic = 1 if you want to run logistic, else 0 for Gaussian

%L1_L2_validate - responsibilities
%        - split into train and validation set
%        - call validation


function [bestLambda bestmodel] =  cross_validate(X_original, Y_original, Z, ...
    isLogistic, numberOf_validationRuns, percentageSplits, lambdaValues, vocab)

if (nargin < 8)
    vocab = [];
end

k = length(unique(Z));
p=size(X_original,2);
n=size(X_original,1);

ourError = zeros(length(lambdaValues), 1);
for crossValidationRuns=1:numberOf_validationRuns
    
    while(1) % ensure that Z always has at least k
        if(percentageSplits > 0)
            [train test] = crossvalind('HoldOut', n, percentageSplits);
        else
            train = ones(n,1);
            test = zeros(n,1);
        end
        X_train=X_original(train==1,:);
        Y_train=Y_original(train==1);
        Z_train=Z(train==1);
        if(length(unique(Z_train)) == k)
            break;
        end
    end
    
    X_test=X_original(test==1,:);
    Y_test=Y_original(test==1);
    Z_test=Z(test==1);
    clear test;
    clear train;
    
    [ourError1 bestLambda bestmodel] = ...
        validation (X_train,Y_train, Z_train, X_test, Y_test, Z_test, isLogistic, lambdaValues, vocab);
     ourError = ourError + ourError1;
end

ourError = ourError / numberOf_validationRuns
%use this lambda finally
%[a b] = min(ourError);
%bestLambda = lambdaValues(b);

end



