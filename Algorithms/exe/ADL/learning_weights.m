function [theta,omega, rho, nu] = learning_weights(beta, features, params)
% Learning adaptive weights
% Author: Seunghak Lee (seunghak@cs.cmu.edu)

if nargin < 3
     error('Too few input arguments'); 
end

totP = size(beta,1);
K   = size(beta,2);
T   = size(features, 2);
zero_val = 10^-20; % internal parameter

if ~isfield(params,'verbose')
     params.verbose = 0; 
end
if ~isfield(params,'stopVal')
     params.stopVal = 10^-8;
end
if ~isfield(params,'MaxIterNum')
     params.MaxIterNum = 100;
end
if ~isfield(params,'omega')
     params.omega = ones(T,1)/T; 
end
if ~isfield(params,'nu')
     params.nu = ones(T,1)/T; 
end
if ~isfield(params,'lambda1')
     params.lambda1 = 0.1; 
end
if ~isfield(params,'lambda2')
     params.lambda2 = 0.1; 
end

omega = params.omega;
nu    = params.nu;


verbose = params.verbose;
MaxIterNum = params.MaxIterNum;


step_size = 0.001;

for iter=1:MaxIterNum
    [fval, grad] = learning_weights_grad(omega, features, beta);
    %fprintf('theta-> obj: %6f\n', fval);
    learning_rate = step_size / sqrt(iter);
    omega2 = omega - (learning_rate * grad);
    omega = projection_routine(omega2);
end
theta = features * omega;



for iter=1:MaxIterNum
    %[x fx c] = minimize(omega, 'learning_weights_grad', 3, ...
    %                 features, beta);
    % projection
    [fval, grad] = learning_weights_grad2(nu, features, beta);
    %fprintf('rho-> obj: %6f\n', fval);
    learning_rate = step_size / sqrt(iter);
    nu2 = nu - (learning_rate * grad);
    nu = projection_routine(nu2);
end
rho = features * nu;



    %[x fx c] = minimize(omega, 'learning_weights_grad', 3, ...
    %                 features, beta);
    % projection