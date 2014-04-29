function [best_val, best_par, err_list, plot_vals] = holdOutMethod(dictionary, y, params)
% Author: Seunghak Lee (seunghak@cs.cmu.edu)
% Last updated: May 3th, 2010

if ~isfield(params,'lambda1_list')
     params.lambda1_list = 1:2:50;
end
if ~isfield(params,'lambda2_list')
     params.lambda2_list = 0.01:0.02:0.3;
end


lambda1_list = params.lambda1_list;
lambda2_list = params.lambda2_list;


% standardize params
warning off;
dictionary = standardize(dictionary);
for t=1:size(y,2)
    y(:,t) = standardize(y(:,t));
end
warning on;

sample_size = size(y,1);
rp = randperm(sample_size);

num_of_test_sample = round(sample_size*0.3);
test_set = rp(1:num_of_test_sample); 
train_set  = rp(num_of_test_sample+1:end);

idx=1;
err_list = cell(1,1);

params.stopVal = 10^-5;
best_par = [0,0];

plot_vals = [];

best_val = realmax;

for l1 = 1:length(lambda1_list) % over l1

    for l2 = 1:length(lambda2_list)

        params.lambda1 = lambda1_list(l1);
        params.lambda2 = lambda2_list(l2);
        
        
        [totBeta,theta,omega,rho,nu] = adaptive_multi_task_lasso(dictionary(train_set,:), y(train_set,:), params);
            

        err = 1/2 * sum(sum((y(test_set,:) - dictionary(test_set,:) * totBeta).^2));

        err_list{idx,1} = err;
        err_list{idx,2} = [params.lambda1, params.lambda2];
        if err_list{idx,1} < best_val
            best_val = err_list{idx,1};
            best_par = err_list{idx,2};
        end    
        plot_vals = [plot_vals; params.lambda1, params.lambda2, err];
        idx = idx+1;
    
    end % for loop  l2   
end % for loop  l1







