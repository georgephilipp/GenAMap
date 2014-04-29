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

limits = 1:50:totaltts;
lambdas = load([int2str(round) '_mpgllmb.txt']);
total_lambda = size(lambdas,2);
er = zeros(1,total_lambda); 
for s = 1:size(limits,2)
    filename = [int2str(limits(1,s)) '_' int2str(round) '_mpgllmb.txt'];
    k = load(filename);
    er= er + k;
end

for i=1:10
er(i)
end

[miner,ind] = min(er(:));
bestlembda = lambdas(1,ind);
 
if(towrite)
 fID1 = fopen('bestlambda','w');
 fprintf(fID1,'%d', ind );
else
    filename11 = [int2str(round+1) '_mpgllmb.txt'];
     fID11 = fopen(filename11,'w');
      for tl=-6:6
         fprintf(fID11,'%e\t', (bestlembda-tl) );
     end
     
%      for tl=5:9
%          fprintf(fID,'%e\t', (tl*(bestlembda/10)) );
%      end
%       for tl=1:5
%           fprintf(fID,'%e\t', (tl*bestlembda) );
%       end
     fclose(fID11);
end



