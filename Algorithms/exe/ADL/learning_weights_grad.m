function [f, df] = learning_weights_grad(omega, features, beta)
% return gradient and obj value for theta 
% Author: Seunghak Lee (seunghak@cs.cmu.edu)

T    = size(features, 2);
K   = size(beta,2);
theta = features * omega; % totP * 1 vector
f1 = sum(-K * log(theta));
f2 = sum(theta' * abs(beta));
f = f1 + f2;

% compute gradient
df = zeros(T,1);
for t=1:T
    df1 = sum(-K * features(:,t)./theta);
    df2 = sum(features(:,t)'*abs(beta));
    df(t) = df1 + df2;
end    
  
