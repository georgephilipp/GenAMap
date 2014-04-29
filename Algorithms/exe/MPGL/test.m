
function ourError = test (model, X_test, Y_test, Z_test)

k = size(model.predictedBeta, 1);

%% Standardize the data
for i=1:k
    n = sum(Z_test==i);
    X_test(Z_test==i,:) = (X_test(Z_test==i,:) - repmat(model.xMean(i,:), n, 1)) ./ repmat(model.xStd(i,:), n, 1);
    Y_test(Z_test==i) = (Y_test(Z_test==i) - model.yMean(i)) ./ model.yStd(i);
end

X_test(isnan(X_test)) = 0;
X_test(isinf(X_test)) = 0;

n = size(X_test, 1);

%% make Y predictions
Y_pred = zeros(n,1);
for i=1:k
    Y_pred(Z_test==i) = X_test(Z_test==i, :) * model.predictedBeta(i, :)' + model.bias(i);
end

%% find accuracy
if(model.isLogistic)
    ourError = 1-mean(sign(Y_pred).*sign(Y_test) == 1);
else
    ourError = mean((Y_test-Y_pred).^2);
end

end


