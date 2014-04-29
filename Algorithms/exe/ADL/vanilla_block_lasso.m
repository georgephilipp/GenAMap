function [beta, dictionary] = vanilla_block_lasso(dictionary, Y, params)
% Last updated: April 8th
% Author: Seunghak Lee (seunghak@cs.cmu.edu)
% This is the implementation of the following paper
% Blockwise Coordinate Descent Procedures for the Multi-task Lasso, with
% Applications to Neural Semantic Basis Discovery, ICML, 2009. 
% Han Liu, Mark Palatucci, and Jian Zhang 
% 
% (demo)
% dictionary = rand(100,100);
% true_beta = repmat(round(rand(100,1)*0.6),1,100);
% y = dictionary * true_beta;
% y = y + normrnd(0, 1, size(y));
% params.verbose = 1;
% params.lambda = 10;
% beta = vanilla_block_lasso(dictionary, y, params);
% figure; hold on; 
% subplot(1,2,1)
% imagesc(true_beta);
% subplot(1,2,2)
% imagesc(beta);


if nargin < 3
     error('3 input arguments needed'); 
end

if ~isfield(params,'lambda')
     params.lambda = 0.01; 
end
if ~isfield(params,'verbose')
     params.verbose = 0; 
end
if ~isfield(params,'stopVal')
     params.stopVal = 10^-5;
end
if ~isfield(params,'MaxIterNum')
     params.MaxIterNum = 200;
end
if ~isfield(params,'initBeta')
     params.initBeta = []; 
end

lambda = params.lambda;
verbose = params.verbose;
stopVal = params.stopVal;
MaxIterNum = params.MaxIterNum;


% standardize params
T = size(Y,2);
N = size(Y,1);
totP = size(dictionary,2);

warning off;
dictionary = standardize(dictionary);
for t=1:T
   Y(:,t) = standardize(Y(:,t));
end
warning on;



%if verbose == 1 
%    fprintf('----- run block Lasso ----\n');
%    fprintf('# obs: %d, # traits: %d \n', N, T);
%end



% initialize beta coefficients

c = zeros(totP,T);
d = zeros(totP,totP);
for k=1:T
    for j=1:totP
        c(j,k) = Y(:,k)' * dictionary(:,j);
    end
    for i=1:totP
        for j=1:totP
            d(i,j) = dictionary(:,i)' * dictionary(:,j);
        end
    end
end    

dictionary = sparse(dictionary);
beta = zeros(totP,T);
a = zeros(totP,T);

% run until convergence
obj_fn = realmax;
iternum = 0;
while(1)
    %dictionary * beta
    prev_obj_fn = obj_fn;
    obj_fn = sum(sum((Y - (dictionary * beta)).^2))/2;
    obj_fn = obj_fn + (lambda * sum(max(beta')));
    
    %if verbose == 1, fprintf('obj %.10f\n', obj_fn); end
    
    if (prev_obj_fn - obj_fn)<stopVal || iternum>MaxIterNum
        %fprintf('L1/infty Lasso done!\n');
        break;
    end
        
    for j=1:totP
        
        for k=1:T
            a(j,k) = c(j,k) - sum(beta(:,k)' * d(:,j)) + (beta(j,k) * d(j,j));
        end
        
        if sum(abs(a(j,:))) <= lambda
            beta(j,:) = 0;
        else
            [val, sortIdx] = sort(abs(a(j,:)),'descend');
            m = zeros(1,T);
            for k=1:T
                m(k) = (sum(abs(a(j,sortIdx(1:k))))-lambda)/k;
            end
            [val, m_star] = max(m);
            for k=1:T
                if k>m_star
                    beta(j,sortIdx(k)) = a(j,sortIdx(k));
                else
                    beta(j,sortIdx(k)) = sign(a(j,sortIdx(k)))/m_star ...
                                            * (sum(abs(a(j,sortIdx(1:m_star))))-lambda);
                end
            end
        end
    end; % covariate
    iternum = iternum + 1;
end



function x = standardize(x)
mean_ls = mean(x);
for i = 1:size(x,2)
    x(:,i) = x(:,i) - mean_ls(i);
    s = sqrt(sum(x(:,i).^2));
    if s~=0
        x(:,i)  = x(:,i)/s;
    end
end
    

