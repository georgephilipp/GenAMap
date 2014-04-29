
%function main_function_parallelRunning()

%% PARAMETERS TO PLAY WITH -- BEGIN
simulate = false;
n=500; p=500; n_test=1000; % needed only if you are simulating data
logistic=false;
validationRounds=2;
percentageSplits=0.25;
lambdaValues = [5:2:100];
dal=true;
BIC=false;
%% PARAMETERS TO PLAY WITH -- END

addpath(genpath('../exe/MPGL/'))

if(dal)
	addpath(genpath('../exe/MPGL/DualAugmentedLagrangian/'))
	rmpath('../exe/MPGL/CoordinateDescent/')
	fprintf('Running Dual Augmented Lagrangian\n\n');
else
	rmpath('../exe/MPGL/DualAugmentedLagrangian/')
	addpath(genpath('../exe/MPGL/CoordinateDescent/'))
	fprintf('Running Coordinate Descent\n\n');
end

fprintf('Logistic= %d, numberOfValidationRounds= %d percentageSplits= %f\n', ...
    logistic, validationRounds, percentageSplits);

%% Get the data
if(simulate)
    [X Y Z X_test Y_test Z_test] = generate_simulatedData(n, p, n_test, logistic);
    vocab = [];
else
    [X Y Z vocab] = readData(x_fileName,y_fileName,z_fileName,v_filename);
end

 X = X((Y ~= -99),:);
 Z = Z((Y ~= -99),:);
 Y = Y((Y ~= -99),:);

%normalize
X = X - repmat(mean(X),size(X,1),1);
Y = Y - repmat(mean(Y),size(Y,1),1);
X = X ./ repmat(sqrt(var(X)), size(X,1),1);
Y = Y ./ repmat(sqrt(var(Y)), size(Y,1),1);

%%find best lambda
if(BIC)
	[bestLambda model] = BIC_kriti(X, Y, Z, logistic, lambdaValues, vocab);
	predictedBeta = model.predictedBeta;
else
	[bestLambda model] = cross_validate(X, Y, Z, logistic, validationRounds, percentageSplits, lambdaValues, vocab);
	predictedBeta = model;
	% Find Predicted Beta using whole train data set, and bestLambda
	% model = train(X, Y, Z, logistic, bestLambda, [], vocab);
end
fprintf('\n Final model trained on the entire data, with lambda=%g, has nonzeros : %d\n', bestLambda, sum(sum(abs(predictedBeta))~=0));

%predictedBeta = model.predicteidBeta;
fID = fopen(out_file_f,'w');
%fprintf('Non-zero betas:\n');
k=size(predictedBeta,1);

for sp=1:k
    %fprintf('Sub-population: %d\n', sp);
    fprintf(fID,'\n');
    [sortedbeta,ix] = sort(predictedBeta(sp,:));
    if(size(vocab,1) > 0)
	%fprintf('Feature-name\t Beta-value\n');
    else
	%fprintf('Feature-index\t Beta-value\n');
    end
    for i=1:length(sortedbeta)
        if ( sortedbeta(i) ~= 0 )
            if(size(vocab,1) >= ix(i))
                fprintf(fID,'%d\t %e\n', (vocab(ix(i))), sortedbeta(i) );
            else
                fprintf(fID,'%d\t %e\n', ix(i), sortedbeta(i));
            end
        end;
    end
end
fclose(fID);


%print_nonzeroBeta(model.predictedBeta, vocab, out_file_f);

% Report accuracy on training data
%ourError = test(model, X, Y, Z);
%fprintf('\n\nL1_L2_Predict : Train Error : %f\n', ourError);

% Test results on test data
%ourError = test(model, X_test, Y_test, Z_test);
%fprintf('\n\nL1_L2_Predict : Test Error : %f\n', ourError);



%end


