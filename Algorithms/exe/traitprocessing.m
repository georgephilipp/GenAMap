addpath(genpath('../exe/'));

%mask, sc = indices analyzed using sc.
istree = strfind(mask, 'TLS');
if(size(istree,1) == 0)
	istree = false;
else
	istree = true;
end
tfile = [mask '_tp'];
mfile = [mask '_mp'];
traits = load([mask '_t'])';
markers = load([mask '_m']);
save(tfile, 'traits', '-ascii');
save(mfile, 'markers', '-ascii');

network = load([mask '_net.txt']);
BYU = exist([mask '_glmentbetas.txt']);
vename = 'vegfl';
if(BYU)
	betas = load([mask '_glmentbetas.txt']);
	vename = 'vegfl_orig';
end
%if(J < 13000)
%	betas = zeros(J,K);
%else
%	betas = load([mask '_glmentbetas.txt']);
%end

markerfile = [mask '_mp'];
N = size(markers,1);

div = zeros(10,2);
div(1,1) = 1;
intvl = N / 10;
div(1,2) = round(intvl);
for(hh=2:9)
	div(hh,1) = div(hh-1,2) + 1;
	div(hh,2) = round(div(hh,1) + intvl-1);
end
div(10,1) = div(9,2) + 1;
div(10,2) = N;
save 'div.txt' div -ascii;

J = size(markers,2);

configfile = 'GFL_1.config';
cf = fopen(configfile,'w');
fprintf(cf, '3e-2 3e1 1.4 0\n');
fprintf(cf, '1e-6 1e-6 2 0\n');
fclose(cf);
maxtraitsz = 250;
if(BYU)
	maxtraitsz = 550;
end
exe = 'rungfl';
if(istree)
	maxtraitsz = 450;
	exe = 'runtree';
end
maxsc = 60;

K = size(traits,2);

if(K < maxtraitsz)
	net = zeros(K);
	for(i=1:size(network,1))
		net(network(i,1), network(i,2)) = network(i,3)+0;
		net(network(i,2), network(i,1)) = network(i,3)+0;
	end

	netname = [mask '_ntwrk'];
	save(netname, 'net', '-ascii');

	if(~BYU)
		if(istree)
			parms = ['runtree.m y=load(''' mask '_tp'');x=load(''' markerfile ''');'];
		else
			parms = ['rungfl.m y=load(''' mask '_tp'');x=load(''' markerfile ''');'];
		end
		parms = [parms 'range=load(''' configfile ''');ntwrk=load(''' netname ''');'];
		parms = [parms 'mask=''' mask '_1'';' ];
	else
		parms = [mask '_tp ' markerfile ' ' int2str(N) ' ' int2str(J)];
		parms = [parms ' ' int2str(K) ' ' mask '_glmentbetas.txt '];
		parms = [parms ' ' configfile ' ' netname ' ' mask '_1'];
	end

	f = fopen('parms10.txt', 'w');
	if(BYU) fprintf(f,'%s\n',parms);
	else fprintf(f,'%s\n', [parms 'div=1;' exe ]);
	end
	if(BYU)	fprintf(f, 'standard\n');
	else fprintf(f, 'vanilla\n');
	end
	if(~BYU)
		for(hh=2:10)
			fprintf(f,'%s\n',[parms 'div=' num2str(hh) ';' exe ]);
		end
	end
	fclose(f);

	f = fopen('parms12.txt', 'w');
	if(BYU) fprintf(f,'%s\n',parms);
	else fprintf(f,'%s\n', [parms 'div=1;' exe]);
	end
        if(BYU) fprintf(f, 'standard\n');
        else fprintf(f, 'vanilla\n');
	end
	if(~BYU)
		for(hh=2:10)
			fprintf(f, '%s\n',[parms 'div=' num2str(hh) ';' exe ] );
		end
	end
	fclose(f);

	if(~istree)
		f = fopen('parms14.txt', 'w');
		if(BYU) fprintf(f,'%s\n',parms);
		else fprintf(f,'%s\n', [parms 'div=1;' exe ]);
		end
		if(BYU)	fprintf(f, 'standard\n');
		else fprintf(f, 'vanilla\n');
		end
		if(~BYU)
			for(hh=2:10)
				fprintf(f,'%s\n',[parms 'div=' num2str(hh) ';' exe ]);
			end
		end
		fclose(f);
	end

	f = fopen('parms11.txt', 'w');
	fprintf(f, [vename '.m mask=''' mask ''';type=1;' vename '\n']);
	fprintf(f, 'vanilla\n');
	fclose(f);

	f = fopen('parms13.txt', 'w');
	fprintf(f, [vename '.m mask=''' mask ''';type=2;' vename '\n']);
	fprintf(f, 'vanilla\n');
	fclose(f);

	if(~istree)
		f = fopen('parms15.txt', 'w');
		fprintf(f, [vename '.m mask=''' mask ''';type=3;' vename '\n']);
		fprintf(f, 'vanilla\n');
		fclose(f);
	end

	exit
end

modules = {};

k = 1;
if(sc(1) ~= -1)
	fq = -1;
	for(i=1:max(size(sc)))
		idx = sc(i);
		fq = -1;

		for(q=2:maxsc)
			finame = [mask '_sc_' int2str(idx) '_' int2str(q)];	

			clustering{q} = load(finame);
			maxxie = 0;
			for j=1:q
				t = max(size(find(clustering{q} == j)));
				if(t > maxxie)
					 maxxie = t;
				end
			end

			if(maxxie < maxtraitsz & fq == -1)
				fq = q;
			end
		end

		if fq == -1
			cursor = maxsc;
			finame = [mask '_sc_' int2str(idx) '_' int2str(maxsc)];	
			bigClustering = clustering{maxsc};
			netname = [mask '_sub_' int2str(idx)];
			subnetmatrix = load(netname);
			for j=1:maxsc
				thisClustInds = find(bigClustering == j);
				t = max(size(thisClustInds));
				if t > maxtraitsz
					subclust = sameSizeKmeans(subnetmatrix(thisClustInds,thisClustInds),maxtraitsz);
                    maxsub = max(subclust);
                    bigClustering(thisClustInds) = cursor + subclust;
                    bigClustering(find(bigClustering == cursor + maxsub)) = j;
                    cursor = cursor + max(subclust) - 1;
                end
			end
			fq = cursor;
            clustering{fq} = bigClustering;
		end
			
		q = fq
		subnetloc = [mask '_subnet_' int2str(idx) '.txt'];
		subnet = load(subnetloc);

		for(s=1:q)
			modules{k} = subnet(find(clustering{q} == s));
			k = k + 1;
		end

		if(min(size(modules)) == 0 | q == -1)
			fprintf(2,'Spectral Clustering was unable to split this data up small enough.');
			exit
		end
	end
end

idx = 0;
while(1)
	subnetfile = [mask '_subnet_' int2str(idx) '.txt'];
	if(~exist(subnetfile, 'file'))
		break;
	end

	if(~ismember(idx, sc))
		sz = max(size(modules)) + 1;
		modules{sz} = load(subnetfile);
	end
	idx = idx + 1;
end

modules

structured = [];

for(i=1:max(size(modules)))
	structured = union(structured, modules{i});
end

unstructured = setdiff(1:K, structured);

finalmods = {};
fmsz = 0;

sizes = zeros(max(size(modules)), 1);

for(i=1:max(size(modules)))
	sizes(i) = max(size(modules{i}));
end

if max(size(modules)) > 0
	while(1)
		[ma,i] = max(sizes);
		[mi,j] = min(abs(sizes));

		if(i==j)
			sizes(i) = -1000;
			fmsz = fmsz+1;
			finalmods{fmsz} = modules{i};
		elseif(ma+mi>maxtraitsz-50)
			sizes(i) = -1000;
			fmsz = fmsz+1;
			finalmods{fmsz} = modules{i};
		else
			sizes(j) = -1000;
			modules{i} = union(modules{i}, modules{j});
			sizes(i) = ma+mi;
		end


		if(max(sizes)==-1000)
			break;
		end
	end
end


while(max(size(unstructured)) > maxtraitsz)
	fmsz = fmsz+1;
	finalmods{fmsz} = unstructured(1:maxtraitsz);
	unstructured = setdiff(unstructured, unstructured(1:maxtraitsz));
end

if min(size(unstructured)) > 0
	lastBatchSize = max(size(unstructured));
	fmsz = fmsz+1;
	finalmods{fmsz} = unstructured(1:lastBatchSize);
	unstructured = setdiff(unstructured, unstructured(1:lastBatchSize));
end

%for(t=300:50:maxtraitsz)
%	for(i=fmsz:-1:1)
%		no = t - max(size(finalmods{i}));
%
%		if(no > 0 & max(size(unstructured) > no))
%			finalmods{i} = union(finalmods{i}, unstructured(1:no));
%			unstructured = setdiff(unstructured, unstructured(1:no));
%		elseif(max(size(unstructured) < no))
%			if(min(size(unstructured) > 0))
%				sz = max(size(unstructured));
%				finalmods{i} = union(finalmods{i}, unstructured);
%				unstructured = [];
%			end
%		end
%	end
%end

%finalmods{fmsz} = union(finalmods{fmsz}, unstructured);

save('key.mat', 'finalmods'); %save modules for future reference.

f1 = fopen('parms10.txt', 'w');
f2 = fopen('parms12.txt', 'w');
if(~istree)
	f3 = fopen('parms14.txt', 'w');
else
	f3 = fopen('parms99.txt', 'w');
end

for(i=1:max(size(finalmods)))
	i
	sz = max(size(finalmods{i}));
	mod = finalmods{i};
        net = zeros(sz);
        for(j=1:size(network,1))
		if(ismember(network(j,1), mod) & ismember(network(j,2),mod))
			ix1 = find(mod == network(j,1));
			ix2 = find(mod == network(j,2));
	                net(ix1, ix2) = network(j,3)+0;
        	        net(ix2, ix1) = network(j,3)+0;
		end
        end
	%for speed, we could create the entire network and sub into it, but this will be space efficient ...
        netname = [mask '_ntwrk_' int2str(i)];
        save(netname, 'net', '-ascii');
	traitname = [mask '_t_' int2str(i)];
	ts = traits(:,mod);
	save(traitname, 'ts', '-ascii');
	if(BYU)
		bs = betas(:,mod);
		betaname = [mask '_bet_' int2str(i)];
		save(betaname, 'bs', '-ascii');
	end
	mk = [mask '_' int2str(i)];
if(~BYU)
	if(istree)
	    parms = ['runtree.m y=load(''' traitname ''');x=load(''' markerfile ''');'];
	else
	    parms = ['rungfl.m y=load(''' traitname ''');x=load(''' markerfile ''');'];
	end
    parms = [parms 'range=load(''' configfile ''');ntwrk=load(''' netname ''');'];
	parms = [parms 'mask=''' mk ''';' ];
else
	parms = [traitname ' ' markerfile ' ' int2str(N) ' ' int2str(J)];
    parms = [parms ' ' int2str(sz) ' ' betaname ' '];
    parms = [parms ' ' configfile ' ' netname ' ' mk];
end

	if(BYU) fprintf(f1, '%s\n', [parms]);
	else
        fprintf(f1,'%s\n', [parms 'div=1;' exe]);
	end
	if i==1
	        if(BYU) fprintf(f1, 'standard\n');
		else fprintf(f1, 'vanilla\n');end
	end

	if(BYU) fprintf(f2, '%s\n', [parms]);
	else
        fprintf(f2,'%s\n', [parms 'div=1;' exe]);
	end
        if i==1
		if(BYU) fprintf(f2, 'standard\n');
		else fprintf(f2, 'vanilla\n');end
	end

	if(BYU) fprintf(f3, '%s\n', [parms ]);
	else
        fprintf(f3, '%s\n', [parms 'div=1;' exe]);
	end
	if i==1
		if(BYU) fprintf(f3, 'standard\n');
		else fprintf(f3, 'vanilla\n');end
	end
	if(~BYU)
	for(hh=2:10)
		fprintf(f1,'%s\n',[parms 'div=' num2str(hh) ';' exe]);
		fprintf(f2,'%s\n',[parms 'div=' num2str(hh) ';' exe]);
		fprintf(f3,'%s\n',[parms 'div=' num2str(hh) ';' exe]);
	end
	end
end
fclose(f1);
fclose(f2);
fclose(f3);
f = fopen('parms11.txt', 'w');
fprintf(f, [vename '.m mask=''' mask ''';type=1;' vename '\n']);
fprintf(f, 'vanilla\n');
fclose(f);

f = fopen('parms13.txt', 'w');
fprintf(f, [vename '.m mask=''' mask ''';type=2;' vename '\n']);
fprintf(f, 'vanilla\n');
fclose(f);

if(~istree)
	f = fopen('parms15.txt', 'w');
	fprintf(f, [vename '.m mask=''' mask ''';type=3;' vename '\n']);
	fprintf(f, 'vanilla\n');
	fclose(f);
end
