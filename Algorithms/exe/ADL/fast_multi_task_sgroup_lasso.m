function [new_beta,funVal] = fast_multi_task_sgroup_lasso(X_s, Y_s, params)
% Seunghak Lee

addpath(genpath('SLEP_package_4.1/SLEP'));

if ~isfield(params,'theta')
     params.theta = ones(1,totP);
end
if ~isfield(params,'rho')
     params.rho = ones(1,totP);
end
if ~isfield(params,'stopVal')
     params.stopVal = 10^-5;
end

K = size(Y_s,2);
p = size(X_s,2);
N = size(X_s,1);

params.in_gr=cell(p,1);
for i=1:p
    params.in_gr{i}=i;
end

theta=params.theta;
rho=params.rho;

params.output_group=1;

%max_iter_num = 1;
%logging = 0;


%warning off;
%[X_s, xmeans] = std_mat(X);
%[Y_s, ymeans] = std_mat(Y);
%warning on;


        

% convert multi-task to uni-task problem
Y_large = zeros(K * N,1);

for i=1:K
    Y_large((i-1)*N+1:i*N) = Y_s(:,i);
end

lambdas = [0, 1]; % use only group lasso penalty
                  % L1 penalty will be enforced by this too.
%lambdas = [0, params.lambda2];

% assume input group is default

dims = size(X_s,2);
T = size(Y_s,2);

group = params.in_gr;
groupsize = length(group)*T;

% input group

opts.G = zeros(1,groupsize); % initialization
opts.ind = zeros(3,groupsize);
base_pos = 0;
groucnt=1;
for k=1:T
    start_pos = base_pos+1;
    for gcnt=1:length(group)
        tmp=group{gcnt};
        tmp=tmp+base_pos;
        end_pos=start_pos+length(tmp)-1;
        opts.G(start_pos:end_pos) = tmp;
        opts.ind(1,groucnt) = start_pos;
        opts.ind(2,groucnt) = end_pos;
        opts.ind(3,groucnt) = theta(gcnt)*params.lambda1; %sqrt(length(tmp));
        groucnt=groucnt+1;
        start_pos=end_pos+1;
    end
    base_pos = dims*k;
end

% output group

if params.output_group==1
    %fprintf('!! use of output group !!\n');
    group=cell(1,size(X_s,2));
    output_group = 1:dims:dims*T;
    for j=0:dims-1
        group{j+1} = output_group+j;
    end
    for gcnt=1:length(group)
        tmp=group{gcnt};
        end_pos=start_pos+length(tmp)-1;
        opts.G(start_pos:end_pos) = tmp;
        opts.ind(1,groucnt) = start_pos;
        opts.ind(2,groucnt) = end_pos;
        opts.ind(3,groucnt) = rho(gcnt)*params.lambda2; %sqrt(length(tmp));
        groucnt=groucnt+1;
        start_pos=end_pos+1;
    end
end

opts.init = 2; % start from 0
opts.nFlag = 0;
opts.tFlag = 0;
opts.tol = params.stopVal; %10^-10;
opts.rFlag = 0; % use original regularization params
opts.T = T;
[grad_beta, funVal, ValueL, res] = SIOL_overlapping_LeastR(X_s, Y_large, lambdas, opts);

num_non_zero = nnz(grad_beta);
if num_non_zero == 0
    new_beta = zeros(p,K); 
else
    new_beta = reshape(grad_beta,p,K);
end








