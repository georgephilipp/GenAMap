function loss = findBIC(w,X,Y,Z)
% w(feature,1)
% X(instance,feature)
% y(instance,1)
% This is LogisticLoss

loss=0;
k = length(unique(Z));

for i=1:k
	[n,p] = size(X(Z==i, :));
	Xw = X(Z==i, :)*w(i,:)';
	yXw = Y(Z==i).*Xw;
	b = [ zeros(n,1) -yXw];
	B = max(b,[],2);
	lse = log(sum(exp(b-repmat(B,[1 size(b,2)])),2))+B;
	loss = loss + sum(lse);
end

loss

% Bayesian Information Criterion (BIC) is -2*log-likelihood + k*log(n) where k is the number of estimated parameters and n is the sample size.
KK = sum(sum(abs(w))~=0)
loss = loss*2 + KK*log(n);   
%loss = loss + KK;
 
end
