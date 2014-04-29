addpath(genpath('../exe/MPGL/'))
dal=true;

if(dal)
    addpath(genpath('../exe/MPGL/DualAugmentedLagrangian/'))
    rmpath('../exe/MPGL/CoordinateDescent/')
    fprintf('Running Dual Augmented Lagrangian\n\n');
else
    rmpath('../exe/MPGL/DualAugmentedLagrangian/')
    addpath(genpath('../exe/MPGL/CoordinateDescent/'))
    fprintf('Running Coordinate Descent\n\n');
end

logistic=false;

if(exist([int2str(round) '_' int2str(trstart) '_key.mat'], 'file'))
    load([int2str(round) '_' int2str(trstart) '_key.mat']);
else
    %load markers
    X = load(x_fileName);
    
    %load traits
    Y = load(y_fileName);
    Y = Y';
    y_val = unique(Y);
    
    % If Y is binary, map it to {-1,1}
    if(length(y_val)==2)
        Y_new = zeros(size(Y));
        Y_new(Y == y_val(1)) = 1;
        Y_new(Y == y_val(2)) = -1;
        Y = Y_new;
    end
    
    %load population
    Z = load(z_fileName, '-ascii');
    pop = unique(Z);
    k = length(pop);
    Z_new = zeros(size(Z));
    for ii=1:k
        Z_new(Z==pop(ii)) = ii;
    end
    Z=Z_new;
    
    vocab = load(v_filename, '-ascii');
    
    s=size(Y);
    total_traits = s(2);
    
    lambdas = load([int2str(round) '_mpgllmb.txt']);
    total_lambda = size(lambdas,2);
    
    error = zeros(total_traits,total_lambda);
    error(:,:)  = -1;
    
    
    %split data into test and train part
    up = unique(Z);
    Xupl = X(Z==up(1),:);
    Yupl = Y(Z==up(1),:);
    
    totalp = size(Xupl,1);
    testp = ceil(totalp/10);
    
    Xtr = Xupl((testp+1:totalp),:);
    Xts = Xupl((1:testp),:);
    Ytr = Yupl((testp+1:totalp),:);
    Yts = Yupl((1:testp),:);
    Zts = zeros(testp,1);
    Ztr = zeros(totalp-testp,1);
    Ztr(:,1) = up(1);
    Zts(:,1) = up(1);
    for upl = 2: length(up)
        Xupl = X(Z==up(upl),:);
        Yupl = Y(Z==up(upl),:);
        totalp = size(Xupl,1);
        testp = ceil(totalp/10);
        
        Xtr = [Xtr; Xupl((testp+1:totalp),:)];
        Xts = [Xts; Xupl((1:testp),:)];
        Ytr = [Ytr; Yupl((testp+1:totalp),:)];
        Yts = [Yts; Yupl((1:testp),:)];
        Zts1 = zeros(testp,1);
        Ztr1 = zeros(totalp-testp,1);
        Ztr1(:,1) = up(upl);
        Zts1(:,1) = up(upl);
        Ztr = [Ztr; Ztr1];
        Zts = [Zts; Zts1];

    end
end

for t=trstart:trend
    for l=1:total_lambda
        if(error(t,l) == -1)
            t
            lambdas(1,l)
             Xtrr = Xtr((Ytr(:,t) ~= -99),:);
             Ztrr = Ztr((Ytr(:,t) ~= -99),:);
             Ytrr = Ytr((Ytr(:,t) ~= -99),t);
             
             Xtss = Xts((Yts(:,t) ~= -99),:);
             Ztss = Zts((Yts(:,t) ~= -99),:);
             Ytss = Yts((Yts(:,t) ~= -99),t);
             
            model = train(Xtrr, Ytrr, Ztrr, logistic, lambdas(1,l), [], vocab);
            predictedBeta = model.predictedBeta;
            error(t,l) = test(model, Xtss, Ytss, Ztss);
            if(towrite)
                filename = [int2str(l) '_' int2str(t) '.txt'];
                fID = fopen(filename,'w');
                %fprintf('Non-zero betas:\n');
                k=size(predictedBeta,1);
                
                for sp=1:k
                    fprintf(fID,'\n');
                    [sortedbeta,ix] = sort(predictedBeta(sp,:));
                    for i=1:length(sortedbeta)
                        if ( sortedbeta(i) ~= 0 )
                            if(size(vocab,1) >= ix(i))
                                fprintf(fID,'%d\t %e\n', (vocab(ix(i))), sortedbeta(i) );
                            else
                                fprintf(fID,'%d\t %e\n', ix(i), sortedbeta(i));
                            end
                        end
                    end
                end
                fclose(fID);
            end
            save([int2str(round) '_' int2str(trstart) '_key.mat']);
        end
    end
end

er = sum(error);
%[miner,ind] = min(er(:));
%bestlembda = lambdas(1,ind);
 
%if(towrite)
 %   fID1 = fopen('bestlambda','w');
  %  fprintf(fID1,'%d', ind );

     filename11 = [int2str(trstart) '_' int2str(round) '_mpgllmb.txt'];%[int2str(round+1) '_mpgllmb.txt'];
     %fID11 = fopen(filename11,'w');
     save(filename11,'er','-ASCII');
%      for tl=5:9
%          fprintf(fID,'%e\t', (tl*(bestlembda/10)) );
%      end
%      for tl=1:5
%          fprintf(fID,'%e\t', (tl*bestlembda) );
%      end
    % fclose(fID11);
%end


