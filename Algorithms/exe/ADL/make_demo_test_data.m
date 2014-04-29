function make_demo_test_data()
% Author: Seunghak Lee(seunghak@cs.cmu.edu)
% Make test data set for demo

K=1;  % number of responses
N=1000; % number of samples
p=1000; % number of predictors
T=10;  % number of features of predictors

dictionary = rand(N,p);
true_beta = zeros(p,K);
features = zeros(p,T);

% make correlated responses
for i=1:K
    true_beta(1:10,i)=round(rand(10,1)*0.8);
    true_beta(11:p,i)=0;
end

y = dictionary * true_beta;
y = y + normrnd(0, 1, size(y)); % add noise


for i=1:T
    if i<=5
        x = mvnrnd(1,1,10);
        features(1:10,i) = sigmoid(x);
        x = normrnd(2,1,p-10);
        features(11:p,i) = sigmoid(x);
    else
        x = mvnrnd(3,0.5,10);
        features(1:10,i) = sigmoid(x);
        x = normrnd(1,0.5,p-10);
        features(11:p,i) = sigmoid(x);
    end
end

warning off;
dictionary = standardize(dictionary);
for t=1:K
   y(:,t) = standardize(y(:,t));
end
warning on;


%save('dictionary.txt','dictionary','-ascii');
%save('y.txt','y','-ascii');
%save('features.txt','features','-ascii');
%save('true_beta.txt','true_beta','-ascii');

dlmwrite('dictionary.txt',dictionary,'\t');
dlmwrite('y.txt',y,'\t');
dlmwrite('features.txt',features,'\t');
dlmwrite('true_beta.txt',true_beta,'\t');


function ret=sigmoid(x)
ret = zeros(length(x),1);
for i=1:length(x)
    ret(i)= 1/(1+exp(x(i)));
end




