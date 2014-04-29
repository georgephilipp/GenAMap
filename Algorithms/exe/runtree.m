%if ~exist('mygennetwork.m', 'file')
%	copyfile('../exe/mygennetwork.m','.');
%	copyfile('../exe/pre_grad.m','.');
%	copyfile('../exe/accgrad.m','.');
%	copyfile('../exe/shrink.m','.');
%	copyfile('../exe/soft_threshold.m', '.');
%else
%	pause(20);
%end
addpath('../exe/TreeLasso');

%if(size(x,1) == 1)
%	x = x.x;
%end

N = size(x,1);
J = size(x,2)
K = size(y,2)
divs = load('div.txt');
xp = x(setdiff(1:N,divs(div,1):divs(div,2)),:);%1:N-10,:);
yp = y(setdiff(1:N,divs(div,1):divs(div,2)),:);%1:N-10,:);
size(xp)
size(yp)
xp = normalize(xp);
yp = normalize(yp);

option.maxiter = 10000;
option.threshold = 0;
option.tol = 1e-6;
h=0.9;

myDist = tril(1-abs(ntwrk), -1);
myCluster = linkage(myDist(myDist ~= 0)', 'complete');
[T Tw] = convH2T(myCluster, h);
clear myCluster myDist;
idx = full(sum(T,2)==1);
T(idx,:)=[];
Tw(idx)=[];
[X,Y,XY,C,g_idx,TauNorm,L1] = pre_grad(xp, yp, T, Tw);
epsilon = 1000;
mu=0.01;

%clear ntwrk;
clear y;
clear x;
clear xp;
clear yp;
idx = 1;
%curix = 1;

si = range(1,1);

%if(exist([mask 'pp' num2str(div) '.mat']))
%	load([mask 'pp' num2str(div)]);
%end

if(range(1,4) == 0)
	i = si;
	while(i <= range(1,2))
%		if(curix < idx)
%			curix = curix + 1;
%		else
%			curix = 1e9;
			fprintf('%d\t\n',i);
			L = L1 + i^2*TauNorm/mu;
			b = accgrad(Y,X,i,T,XY,C,g_idx,L,mu,option);
			save([mask 'betas_' num2str(idx) '_' num2str(div) '.txt'], 'b', '-ascii');
			clear b;
			idx = idx + 1;
%			save([mask 'pp' num2str(div)], 'idx');
%		end
		i = i * range(1,3);
	end
else
	i = si;
	while(i <= range(1,2))
%		if(curix < idx)
%			curix = curix + 1;
%		else
%			curix = 1e9;
			fprintf('%d\t\n',i);
			L = L1 + i^2*TauNorm/mu;
			b = accgrad(Y,X,i,T,XY,C,g_idx,L,mu,option);
			save([mask 'betas_' num2str(idx) '_' num2str(div) '.txt'], 'b', '-ascii');
			clear b;
			idx = idx + 1;
%			save([mask 'pp' num2str(div)], 'idx');
%		end
		i = i + range(1,3);
	end
end

%delete([mask 'pp' num2str(div) '.mat']);

