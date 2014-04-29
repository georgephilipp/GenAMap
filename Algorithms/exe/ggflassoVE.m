%step = 1,2,3,4,5,6,or 7
%mask = mask
addpath('../exe');
load 'genes_v.txt'
load 'genes.txt'
load 'traits_v.txt'
load 'groups.txt'

idxG = 1:size(genes_v,2);
if(size(genes_v,2) > 500)
	[sgenes ogenes] = sort(groups);
	[bogus idxG] = sort(ogenes);	
end

load 'traitgroups.txt'

idxT = [];
for(i=1:size(traitgroups,1))
	idxT = [idxT traitgroups(i,:)];
end
idxT = idxT(find(idxT ~= 0));
[bogus idxT] = sort(idxT);

load 'traits.txt';
missing = [];
for(i=1:size(traits,2))
	if(var(traits(:,i)) == 0)
		missing = [missing i];
	end
end
idxT = [idxT missing];

istwostep = 0;
if(size(genes_v,2) > 500)
	istwostep = 1;
end

numsteps = 0;
for(i=1:1000)
	betaname = [mask '_' num2str(i) '_0000'];
	if(exist(betaname, 'file'))
		numsteps = numsteps + 1;
	end	
end
for(i=1:1000)
	betaname = [mask '_' num2str(i) 'a_0000'];
	if(exist(betaname, 'file'))
		numsteps = numsteps + 1;
	end
end

cont = 1;
beta = [];
minerr = 1e99;
for(i=0:1000)
	beta = [];
	nmix = num2str(i);
	while(length(nmix) < 4)
		nmix = ['0' nmix];
	end

	for(j=1:numsteps)
		betaname = [mask '_' num2str(j) '_' nmix];
		b1name = [mask '_' num2str(j) 'a_' nmix];
		b2name = [mask '_' num2str(j) 'b_' nmix];

		if(~exist(betaname,'file') & ~exist(b1name,'file'))
			cont = 0;
		end
	
		if(istwostep == 0 & cont == 1)
			b = load(betaname);
		elseif(cont == 1)
			b1 = load(b1name);
			b2 = load(b2name);
			b = [b1' b2']';
		end
		if(cont)
			beta = [beta b];
		end
	end
	if(cont == 1)
	if(size(missing) > 0)
        	beta = [beta zeros(size(beta,1), size(missing))];
	end

	size(beta)
	size(idxG)
	size(idxT)
	beta = beta(idxG, idxT);
	
	x = (genes);
	y = (traits);
	
	nonzeros(i+1) = 0;
	err(i+1) = 0;
	for j=1:size(beta,2)
                nz = size(find(abs(beta(:,j)) >= 1e-4), 1);
                nonzeros(i+1) = nonzeros(i+1) + nz;

                nzidx = find(abs(beta(:,j)) >= 1e-4);
                cfs = inv(x(:, nzidx)' * x(:,nzidx) + 1e-9*eye(nz,nz)) * x(:,nzidx)' * y(:, j);
                t = zeros(1,size(x,2));
                t(nzidx) = cfs;

                pred = genes_v * t';
                errs = traits_v(:,j) - pred;
                err(i+1) = err(i+1) + sum(errs.^2);
		
        end
		if(err(i+1) < minerr)
			minerr = err(i+1);
			save 'betas.txt' beta -ascii
		end
	end
end

[nonzeros' err']

[m idx] = min(err');
m

disp('moving files');

type = num2str(step);
cont = 1;
for(i=0:1000)
        beta = [];
        nmix = num2str(i);
        while(length(nmix) < 4)
                nmix = ['0' nmix];
        end

        for(j=1:numsteps)
                betaname = [mask '_' num2str(j) '_' nmix];
                b1name = [mask '_' num2str(j) 'a_' nmix];
                b2name = [mask '_' num2str(j) 'b_' nmix];

                if(~exist(betaname,'file') & ~exist(b1name,'file'))
                        cont = 0;
                end

		if(~exist(type,'dir'))
        	        mkdir(type);
	        end
		if(istwostep == 0 & cont == 1)
			if((i+1)==idx)
				%[s m] = copyfile(betaname, ['initB_' num2str(i+1)], 'f')
				ini = load(betaname);
				ini = ini + randn(size(ini))/1000;
				save(['initB_' num2str(j)], 'ini', '-ascii');
			end
        		movefile(betaname,type);
		elseif(cont == 1)
			if((i+1)==idx)
				%copyfile(b1name, ['initB_' num2str(j+1) 'a'], 'f');
				%copyfile(b2name, ['initB_' num2str(j+1) 'b'], 'f');
				ini = load(b1name);
                                ini = ini + randn(size(ini))/1000;
                                save(['initB_' num2str(j) 'a'], 'ini', '-ascii');

				ini = load(b2name);
                                ini = ini + randn(size(ini))/1000;
                                save(['initB_' num2str(j) 'b'], 'ini', '-ascii');

			end
			movefile(b1name,type);
			movefile(b2name,type);
		end
	end
end

disp('starting to print');

myfi = 'parms4.txt';
%fi = fopen('parms4.txt', 'r');
%C = textscan(fi, '%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s');
%fclose(fi);

%write out parms for next ggfl step
if(step == 1)
	fi=fopen('parms7.txt', 'w');
	fi2 = fopen('parms6.txt', 'w');
	fprintf(fi, ['ggflassoVE.m step=2;mask=''' mask ''';ggflassoVE\nvanilla\n']);
elseif(step == 2)
	fi = fopen('parms9.txt', 'w');
	fi2 = fopen('parms8.txt', 'w');
	fprintf(fi, ['ggflassoVE.m step=3;mask=''' mask ''';ggflassoVE\nvanilla\n']);
	myfi = 'parms6.txt';
elseif(step == 3)
	fi = fopen('parms11.txt', 'w');
	fi2 = fopen('parms10.txt', 'w');
	fprintf(fi, ['ggflassoVE.m step=4;mask=''' mask ''';ggflassoVE\nvanilla\n']);
	myfi = 'parms8.txt';
elseif(step == 4)
	fi = fopen('parms13.txt', 'w');
	fi2 = fopen('parms12.txt', 'w');
	fprintf(fi, ['ggflassoVE.m step=5;mask=''' mask ''';ggflassoVE\nvanilla\n']);
	myfi = 'parms10.txt';
elseif(step == 5)
	fi = fopen('parms15.txt', 'w');
	fi2 = fopen('parms14.txt', 'w');
	fprintf(fi, ['ggflassoVE.m step=6;mask=''' mask ''';ggflassoVE\nvanilla\n']);
	myfi = 'parms12.txt';
elseif(step == 6)
	fi = fopen('parms17.txt', 'w');
	fi2 = fopen('parms16.txt', 'w');
	fprintf(fi, ['ggflassoVE.m step=7;mask=''' mask ''';ggflassoVE\nvanilla\n']);
	myfi = 'parms14.txt';
elseif(step == 7)
	exit
end
fclose(fi);

fi = fopen(myfi, 'r');
C = textscan(fi, '%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s');
fclose(fi);


step = step + 1;
mystep = step;
code = '1 ';
mystart = '1e-4 ';
myend = '.1 ';
mylambda = [C{13}{1} ' '];
mygamma = [C{14}{1} ' '];
mygamma2 = [C{15}{1} ' '];

if(step == 2)
	if(idx == 1)
		mylambda = '1e-3 ';
	elseif(idx == 2)
		mylambda = '1e-2 ';
	elseif(idx == 3)
		mylambda = '1e-1 ';
	elseif(idx == 4)
		mylambda = '1e-0 ';
	elseif(idx == 5)
		mylambda = '1e1 ';
	else
		mylambda = '1e2 ';
	end
elseif(step == 3)
	if(idx == 1)
		mygamma = '1e-4 ';
	elseif(idx == 2)
		mygamma = '1e-3 ';
	elseif(idx == 3)
		mygamma = '1e-2 ';
	elseif(idx == 4)
		mygamma = '1e-1 ';
	elseif(idx == 5)
		mygamma = '1e0 ';
	else
		mygamma = '1e1 ';
	end
end

if(step > 3)
	code = '0 ';
end
if(step == 4)
        if(idx == 1)
                mygamma2 = '1e-4 ';
        elseif(idx == 2)
                mygamma2 = '1e-3 ';
        elseif(idx == 3)
                mygamma2 = '1e-2 ';
        elseif(idx == 4)
                mygamma2 = '1e-1 ';
        elseif(idx == 5)
                mygamma2 = '1e0 ';
        else
                mygamma2 = '1e1 ';
        end

	mystep = 1;
	mystart = [num2str(str2num(mylambda) / 2) ' ']; 
	myend = [num2str(str2num(mylambda) * 2) ' '];
	mylambda = [num2str(str2num(mylambda) / 5) ' '];
elseif(step == 5)
	mystep = 2;
	mylambda = [num2str(str2num(C{11}{1}) + (idx-1) * str2num(C{13}{1})) ' ']
	mystart = [num2str(str2num(mygamma) / 2) ' '];
	myend = [num2str(str2num(mygamma) * 2) ' '];
	mygamma = [num2str(str2num(mygamma) / 5) ' '];
elseif(step == 6)
	mygamma = [num2str(str2num(C{11}{1}) + (idx-1) * str2num(C{14}{1})) ' '];
	mystep = 3;
	mystart = [num2str(str2num(mygamma2) / 2) ' '];
	myend = [num2str(str2num(mygamma2) * 2) ' '];
	mygamma2 = [num2str(str2num(mygamma2) / 5) ' '];
elseif(step == 7)
	mygamma2 = [num2str(str2num(C{11}{1}) + (idx-1) * str2num(C{15}{1})) ' '];
	mystep = 1;
	mystart = [num2str(str2num(mylambda) / 2) ' '];
	myend = [num2str(str2num(mylambda) * 3) ' '];
	mylambda = [num2str(str2num(mylambda) / 3) ' '];
end

for i=1:size(C{1},1)

	if(i==2)
		fprintf(fi2, 'standard\n');
		continue;
	end
	for j=1:8
		fprintf(fi2, [C{j}{i} ' ']);
	end
	fprintf(fi2, [num2str(mystep) ' ']);
	fprintf(fi2, code);

	fprintf(fi2, mystart);
	fprintf(fi2, myend);
	fprintf(fi2, mylambda);
	fprintf(fi2, mygamma);
	fprintf(fi2, mygamma2);

	fprintf(fi2, [C{16}{i} '\n']);	
end

disp('done');





