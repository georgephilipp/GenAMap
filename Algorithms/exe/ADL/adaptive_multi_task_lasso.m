function [best_beta_ls,best_theta_ls,best_omega_ls,best_rho_ls,best_nu_ls,mcut] = adaptive_multi_task_lasso(dictionary, y_total, params)
% Author: Seunghak Lee (seunghak@cs.cmu.edu)

if nargin < 3
     error('Too few input arguments'); 
end


if ~isfield(params,'features')
     error('feature should be included');
end
T   = size(params.features, 2);

if ~isfield(params,'verbose')
     params.verbose = 0; 
end
if ~isfield(params,'stopVal')
     params.stopVal = 10^-8;
end
if ~isfield(params,'MaxIterNum')
     params.MaxIterNum = 3;
end
if ~isfield(params,'learnWeights')
     params.learnWeights = 1;
end
if ~isfield(params,'lambda1')
     params.lambda1 = 0.1; 
end
if ~isfield(params,'lambda2')
     params.lambda2 = 0.1; 
end
if ~isfield(params,'mcut')
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % run clustering algorithm
    if ~isfield(params,'cut_off_threshold')
	error('if you use option mcut, you need to specify threshold for hierarchical clustering');
    end 
    cut_off_threshold = params.cut_off_threshold;
    %cut_off_threshold = 10;
    if size(y_total,2) == 1
        params.mcut = 1;
    else    
        y__ = pdist(y_total','corr'); % n by p matrix
        Z = linkage(y__,'average');
        params.mcut = cluster(Z,'cutoff', cut_off_threshold);
    end
end

mcut = params.mcut;
verbose = params.verbose;

lambda1 = params.lambda1;
lambda2 = params.lambda2;

learnWeights = params.learnWeights;
features = params.features;
MaxIterNum = params.MaxIterNum;

if learnWeights == 0
    MaxIterNum = 1;
end


best_theta = 0;
best_omega = 0;
best_rho = 0;
best_nu = 0;
            





clusNum = length(unique(mcut));
if verbose == 1
    fprintf('number of clusters of responses : %d\n', clusNum);
end


best_beta_ls = zeros(size(dictionary,2), size(y_total,2));
best_theta_ls = zeros(size(dictionary,2),clusNum);
best_rho_ls = zeros(size(dictionary,2),clusNum);
best_omega_ls = zeros(size(features,2),clusNum);
best_nu_ls = zeros(size(features,2),clusNum);

for cnum=1:clusNum
    row_idx =  find(mcut==cnum);
    y = y_total(:, row_idx);

    best_fval = realmax;

    for iter=1:MaxIterNum % multiple starts 
	if isfield(params, 'beta')
	    params = rmfield(params,'beta');
	end
	if verbose == 1
	    fprintf('Outer iterations: %d\n', iter);
	end
	
        omega = ones(T,1); 
        nu = ones(T,1); 
        if iter == 1
            theta = 0.001*ones(size(dictionary,2),1)';
            rho = 0.001*ones(size(dictionary,2),1)';
            params.theta = theta;
            params.rho = rho;
            if lambda1 == 0
                [outputBeta,fval] = multi_task_group_lasso(dictionary, y, params);
            else
                [outputBeta,fval] = fast_multi_task_sgroup_lasso(dictionary, y, params);
                %[outputBeta,fval] = multi_task_sgroup_lasso(dictionary, y, params);
            end
        elseif iter == 2
            theta = 0.01*ones(size(dictionary,2),1)';
            rho = 0.01*ones(size(dictionary,2),1)';
            params.theta = theta;
            params.rho = rho;
            if lambda1 == 0
                [outputBeta,fval] = multi_task_group_lasso(dictionary, y, params);
            else
                [outputBeta,fval] = fast_multi_task_sgroup_lasso(dictionary, y, params);
                %[outputBeta,fval] = multi_task_sgroup_lasso(dictionary, y, params);
            end
        else
            outputBeta = 0.01 * rand(size(dictionary,2), size(y,2));
        end
        inner_iter = 0;

	for iter2=1:30
   	    if verbose == 1
	        fprintf('Inner iterations: %d\n', iter2);
	    end
            if learnWeights == 1
                % learn weights
                prev_omega = omega;
                prev_nu = nu;
                [theta,omega,rho,nu] = learning_weights(outputBeta, features, params);

                dif1 = sqrt(sum((omega - prev_omega).^2));
                dif2 = sqrt(sum((nu - prev_nu).^2));

                % if stopping criterion is satisfied, STOP
                if dif1 < 10^-5 && dif2 < 10^-5
                    break;
                end 
                params.theta = theta';
                params.rho = rho';
		params.beta = outputBeta;
            else
                params.theta = ones(size(dictionary,2),1)';
                params.rho = ones(size(dictionary,2),1)';
            end

            if lambda1 == 0
                [outputBeta,fval] = multi_task_group_lasso(dictionary, y, params);
            else
                [outputBeta,fval] = fast_multi_task_sgroup_lasso(dictionary, y, params);
                %[outputBeta,fval] = multi_task_sgroup_lasso(dictionary, y, params);
            end

            inner_iter = inner_iter +1;
            if learnWeights == 0, break; end;
    end

        last_fval = fval(1,numel(fval));
        if best_fval>last_fval
            best_beta = outputBeta;
            best_fval = last_fval;
            best_theta = theta;
            best_omega = omega;
            best_rho = rho;
            best_nu = nu;
        end
    end
    

    best_beta_ls(:, row_idx) = best_beta;
    best_theta_ls(:, cnum) = best_theta;
    best_omega_ls(:, cnum) = best_omega;
    best_rho_ls(:, cnum) = best_rho;
    best_nu_ls(:, cnum) = best_nu;

end
