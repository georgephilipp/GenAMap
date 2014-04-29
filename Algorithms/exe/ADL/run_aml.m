% Running script for adaptive multi-task Lasso
% Author: Seunghak Lee (seunghak@cs.cmu.edu)

% Read input files

y = load('y.txt');                   % responses
dictionary = load('dictionary.txt'); % predictors
features = load('features.txt');     % priors on predictors
true_beta = load('true_beta.txt');
params.features = features;


% standardize the data
warning off;
dictionary = standardize(dictionary);
for t=1:size(y,2)
   y(:,t) = standardize(y(:,t));
end
warning on;




% Hold out method to find the best lambda_1 and lambda_2
% If you want to use holdout method for choosing parameters lambda_1 and lambda_2
% you need to run the code from [Start: holdout method] to [End: holdout method].
% Instead you need to specify lambda_1 and lambda_2 at [PARAM: labmda_1] and [PARAM: labmda_2]


% [Start: hold out method]
%params.verbose = 1;		%   set it to 1 if you want to see logging; or 0 if you don't
%params.lambda1_list = 1:5:30;   % range of search space for lambda_1
%params.lambda2_list = 1:5:30;   % range of search space for lambda_2
%[best_val, best_par, err_list, plot_vals] = holdOutMethod(dictionary, y, params);
%params.lambda1 = best_par(1);   %   [PARAM: labmda_1]
%params.lambda2 = best_par(2);   %   [PARAM: labmda_2]
% [End: holdout method]


% This part is to run adaptive multi-task Lasso

params.lambda1 = 0.0001;   %   [PARAM: labmda_1]
params.lambda2 = 0.0001;   %   [PARAM: labmda_2]


params.stopVal = 10^-8;        %   This value may be changed if you want to stop early (e.g. 10^-5)
params.verbose = 1;	        %   This value should be set to 1 if you want to see logging; or 0 otherwise
params.learnWeights = 1;        %   If it is set to 1: we learn scaling params (theta and rho)
				%   If it is set to 0: we don't learn scaling params
				%   If you want to take advantage of prior knowledge on predictors
				%   this value should be set to 1
				%   We recommend to set it to 1 if you have useful features to use


%% clustering options
params.mcut = ones(1,size(y,2));	%   It specifies correlated responses.
					%   The same number implies the same cluster index.
					%   (e.g.) params.mcut = [1,1,2,2] means that first two responses are in the same
					%   cluster, and the third and fourth responses are in the same cluster
					%   If you use "params.mcut = ones(1,size(y,2));" it means that 
					%   all responses are correlated with each other
					%
					%   Note: If you don't know which responses are correlaated you should comment this line.
					%   Then, you need to set a value for params.cut_off_threshold below,
					%   which will be used for hierarchical clustering in our program. 
					%   (Cluster means that responses in the same cluster are correlated with each other)

params.cut_off_threshold = 0.8; %   If you specify mcut above, you don't need to specify cut_off_threshold
				%   Otherwise, you should choose a value for cut_off_threshold.
				%   We will perform hierarchical clustering on the responses 
				%   based on this cut_off_threshold

[outputBeta,theta,omega,rho,nu] = adaptive_multi_task_lasso(dictionary, y, params);



[recall, precision] = eval_demo_test_results(true_beta,outputBeta);


[recall, precision]




