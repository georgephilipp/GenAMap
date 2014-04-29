% Multi-task sparse group Lasso
function [beta,obj_val_loss] = multi_task_sgroup_lasso(dictionary, Y, params)
% Last updated: May 4th
% Author: Seunghak Lee (seunghak@cs.cmu.edu)

if nargin < 3
     error('Too few input arguments'); 
end

K = size(Y,2);
N = size(Y,1);
totP = size(dictionary,2);

zero_val = 10^-20; % internal parameter

if ~isfield(params,'lambda1')
     params.lambda1 = 0.1; 
end
if ~isfield(params,'lambda2')
     params.lambda2 = 0.1; 
end

lambda1 = params.lambda1;
lambda2 = params.lambda2;

if ~isfield(params,'verbose')
     params.verbose = 0; 
end
if ~isfield(params,'stopVal')
     params.stopVal = 10^-8;
end
if ~isfield(params,'MaximumIterNum')
     params.MaximumIterNum = 100;
end
if ~isfield(params,'initBeta')
     params.initBeta = []; 
end
if ~isfield(params,'theta')
     params.theta = ones(1,totP); 
end
if ~isfield(params,'rho')
     params.rho = ones(1,totP); 
end
if ~isfield(params,'beta')
     params.beta = 0;
     Block_Lasso_warmStart = 1;
else
     Block_Lasso_warmStart = 0;
end


initBeta = []; %params.initBeta;

theta = lambda1 * params.theta;
rho = lambda2 * params.rho;

beta = params.beta;
verbose = params.verbose;
stopVal = params.stopVal;
MaximumIterNum = params.MaximumIterNum;

% standardize params
warning off;
dictionary = standardize(dictionary);
for t=1:K
   Y(:,t) = standardize(Y(:,t));
end
warning on;


if verbose == 1 
    fprintf('----- run multi-task sparse group Lasso ----\n');
    fprintf('# obs: %d, # traits: %d \n', N, K);
end

% initialize beta coefficients
% init beta using Lasso

if Block_Lasso_warmStart == 1
    if verbose == 1, fprintf('init beta using block lasso\n'); end
    params.stopVal = 10^-2;
    beta = vanilla_block_lasso(dictionary, Y, params);
    params.stopVal = stopVal;
end

% generate residual 
inner_prod = dictionary' * Y; % p by K matrix

dic_prod = zeros(1,totP);
for j=1:totP
    dic_prod(j) = dictionary(:,j)' * dictionary(:,j);
end
dictionary = sparse(dictionary);
non_zero_idx = find(sum(beta,2)~=0);% non_zero index for beta, it is only for j

c1 = find(theta<0);
c2 = find(rho<0);
if isempty(c1) == 0 || isempty(c2) == 0
    error('weights should be positive');
end

% run until convergence
obj_val = 10^20;
iternum = 0;
while(1)
    for j=1:totP % for each covariate
        % compute J(t) and check if J(t)<=1, beta(j,:) = 0;
        a_j = inner_prod(j,:) - ...
                (dictionary(:,j)'*dictionary(:,non_zero_idx)*beta(non_zero_idx, :)); 
        residual_row = a_j;        
        a_j = a_j + dic_prod(j) * beta(j,:);
        
        
        t_j = zeros(1,K);
        for k=1:K
            if abs(a_j(k)/theta(j))<=1
                t_j(k) = a_j(k)/theta(j);
            else
                t_j(k) = sign(a_j(k)/theta(j));
            end
        end
        J_t = sum((a_j - (theta(j)*t_j)).^2)/rho(j)^2;
        
        if J_t <=1
            beta(j,:) = 0; 
            non_zero_idx = setdiff(non_zero_idx, j);
            continue;
        end
        
        % if J_t>1; beta(j,:) is non-zero
        for k=1:K
            y_tild = residual_row(k) +  dic_prod(j) * beta(j,k);
            if abs(y_tild) <= theta(j);
                beta(j,k) = 0; 
            else
                % minimizing the upper bound
                
                beta_tild2 = beta(j,:);
                beta_tild2(k) = 0;
                if sum(abs(beta_tild2))==0 % if group term is zero
                    beta(j,k) = softThresHolding(y_tild, theta(j) + rho(j));
                else
                    gw = 1+(rho(j)/sqrt(sum(beta(j,:).^2)));
                    beta(j,k) = softThresHolding(y_tild/gw, theta(j)/gw);
                    %if beta(j,k)<zero_val, beta(j,k) = 0; end
                end
            end
        end
        
        if sum(abs(beta(j,:)))>0
            non_zero_idx = union(non_zero_idx, j);
        end; % end of each trait k
    end; % end of each covariate j
    
    pre_obj_val = obj_val;
    % evaluate objective function
    obj_val = 1/2*sum(sum((Y - dictionary*beta).^2));
    obj_val_loss = obj_val;
    obj_val = obj_val + theta * sum(abs(beta),2);
    obj_val = obj_val + rho * sqrt(sum(beta.^2,2));
    if verbose == 1
	fprintf('aml obj: %f\n', obj_val);
        if pre_obj_val - obj_val < stopVal || iternum > MaximumIterNum
            fprintf('(finished) sparse multi-task lasso, obj: %f, total iteration: %d\n', obj_val, iternum);
            break;
        end
    end
    iternum = iternum + 1;
end

