%if ~exist('mygennetwork.m', 'file')
%	copyfile('../exe/mygennetwork.m','.');
%	copyfile('../exe/pre_grad.m','.');
%	copyfile('../exe/accgrad.m','.');
%	copyfile('../exe/shrink.m','.');
%	copyfile('../exe/soft_threshold.m', '.');
%else
%	pause(20);
%end
addpath('../exe/GFlasso');

%if(size(x,1) == 1)
%	x = x.x;
%end

N = size(x,1);
J = size(x,2)
K = size(y,2)

divs = load('div.txt');
xp = x(setdiff(1:N,divs(div,1):divs(div,2)),:);%1:N-10,:);
yp = y(setdiff(1:N,divs(div,1):divs(div,2)),:);%1:N-10,:);

%xp = x(1:N-10,:);
%yp = y(1:N-10,:);

%h = corr_graph(y,ntwrk,0);
%clear ntwrk;
clear y;
clear x;
%E = size(h,2);
%[X, Y, XX, XY, maxd, L1] = pre_grad(xp, yp, h);
[X, Y] = pre_grad(xp, yp);
clear xp;
clear yp;
%D = 1 / 2 * J * (K + E);
e = 1e-3;
%mu = e / (2 * D);
idx = 1;
%curix = 1;

si = range(1,1);
sj = range(2,1);

%if(exist([mask 'pp' num2str(div) '.mat']))
%	load([mask 'pp' num2str(div)]);
%end

if(range(1,4) == 0)
	i = si;
	while(i <= range(1,2))
		if(range(2,4) == 0)
			j = range(2,1)
			while(j <= range(2,2))
				j
				%if(curix < idx)
				%	curix = curix + 1;
				%	j = j * range(2,3);
				%else
				%	curix = 1e9;
					fprintf('%d\t%d\n',i,j);
					b = gflasso (Y, X, ntwrk, i, j,1);
					save([mask 'betas_' num2str(idx) '_' num2str(div) '.txt'], 'b', '-ascii');
					clear b;
					idx = idx + 1;

					j = j * range(2,3);

				%	save([mask 'pp' num2str(div)], 'idx');
				%end
			end
		else
			for j=range(2,1):range(2,3):range(2:2)
				%if(curidx < idx)
				%	curix = curix + 1;
				%else
				%	curix = 1e9;
					fprintf('%d\t%d\n',i,j);
				%	L = L1  + (i^2 + 2 * j^2 * maxd) / mu;
					b = gflasso(Y, X, ntwrk, i, j, 1);
				 	save([mask 'betas_' num2str(idx) '_' num2str(div) '.txt'], 'b', '-ascii');
					clear b;
					idx = idx + 1;

                                %	save([mask 'pp' num2str(div)], 'idx');
				%end
			end
		end
		i = i * range(1,3);		
	end
else
	for i=si:range(1,3):range(1,2)
                if(range(2,4) == 0)
                        j = range(2,1);
                        while(j <= range(2,2))
                                %if(curix < idx)
				%	curix = curix + 1;
				%	j = j * range(2,3);
				%else
				%	curix = 1e9;
					fprintf('%d\t%d\n',i,j);
                                %	L = L1  + (i^2 + 2 * j^2 * maxd) / mu;
                                	b = gflasso (Y, X, ntwrk, i, j, 1);
				 	save([mask 'betas_' num2str(idx) '_' num2str(div) '.txt'], 'b', '-ascii');

					clear b;
                                	idx = idx + 1;

                                	j = j * range(2,3);

                                %	save([mask 'pp' num2str(div)], 'idx');
				%end
                        end
                else
                        for j=range(2,1):range(2,3):range(2:2)
                                %if(curix < idx)
				%	curix = curix + 1;
				%else
				%	curix = 1e9;
					fprintf('%d\t%d\n',i,j);
                                %	L = L1  + (i^2 + 2 * j^2 * maxd) / mu;
                                	b = gflasso(Y, X, ntwrk, i, j, 1);
				 	save([mask 'betas_' num2str(idx) '_' num2str(div) '.txt'], 'b', '-ascii');
					clear b;
                                	idx = idx + 1;

                                %	save([mask 'pp' num2str(div)], 'idx');
				%end
                        end
                end

	end
end

%delete([mask 'pp' num2str(div) '.mat']);

