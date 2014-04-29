function [f, df] = learning_weights_grad2(nu, features, beta)
% return gradient and obj value for nu
% Author: Seunghak (seunghak@cs.cmu.edu)

T    = size(features, 2);
K   = size(beta,2);

rho = features * nu; % totP * 1 vector

f1 = sum(-K * log(rho));
f2 = sum(rho' * sqrt(sum((beta.^2),2)));
f = f1 + f2;

% compute gradient
df = zeros(T,1);
for t=1:T
    df1 = sum(-K * features(:,t)./rho);
    df2 = sum(features(:,t)'*sqrt(sum(beta.^2,2)));
    df(t) = df1 + df2;
end    
  
