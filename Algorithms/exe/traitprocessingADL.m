addpath(genpath('../exe/'));

tfile = [mask '_tp'];
mfile = [mask '_mp'];
traits = load([mask '_t'])';
markers = load([mask '_m'])';
save(tfile, 'traits', '-ascii');
save(mfile, 'markers', '-ascii');

network = load([mask '_net.txt']);
vename = 'veADL';
exe = 'runADL';

markerfile = [mask '_mp'];
featurefile = [mask '_f'];
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

%make lambdas
Lambda = zeros(2,49);
for i=1:7
	for j=1:7
		Lambda(1,j + 7*(i-1)) = 0.0001 * 10^(i-1);
		Lambda(2,j + 7*(i-1)) = 0.0001 * 10^(j-1);
	end
end
lambdaname = [mask '_lambda'];
save(lambdaname, 'Lambda', '-ascii');

maxtraitsz = 250;
maxsc = 60;

K = size(traits,2);

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
modIndeces = {};
finalModIndeces = {};
fmsz = 0;

sizes = zeros(max(size(modules)), 1);

for(i=1:max(size(modules)))
	sizes(i) = max(size(modules{i}));
    modIndeces{i} = repmat(i,1,sizes(i));
end

if max(size(modules)) > 0
	while(1)
		[ma,i] = max(sizes);
		[mi,j] = min(abs(sizes));

		if(i==j)
			sizes(i) = -1000;
			fmsz = fmsz+1;
			finalmods{fmsz} = modules{i};
            finalModIndeces{fmsz} = collapseIDs(modIndeces{i});
		elseif(ma+mi>maxtraitsz-50)
			sizes(i) = -1000;
			fmsz = fmsz+1;
			finalmods{fmsz} = modules{i};
            finalModIndeces{fmsz} = collapseIDs(modIndeces{i});
		else
			sizes(j) = -1000;
			modules{i} = [modules{i}; modules{j}];
            modIndeces{i} = [modIndeces{i} modIndeces{j}];
			sizes(i) = ma+mi;
        end

		if(max(sizes)==-1000)
			break;
		end
	end
end


while(max(size(unstructured)) > maxtraitsz)
	fmsz = fmsz+1;
	finalmods{fmsz} = unstructured(1:maxtraitsz)';
    finalModIndeces{fmsz} = 1:maxtraitsz;
	unstructured = setdiff(unstructured, unstructured(1:maxtraitsz));
end

if min(size(unstructured)) > 0
	lastBatchSize = max(size(unstructured));
	fmsz = fmsz+1;
	finalmods{fmsz} = unstructured(1:lastBatchSize)';
    finalModIndeces{fmsz} = 1:lastBatchSize;
	unstructured = setdiff(unstructured, unstructured(1:lastBatchSize));
end

save('key.mat', 'finalmods'); %save modules for future reference.
save('groups.mat', 'finalModIndeces'); 

f1 = fopen('parms5.txt', 'w');
f2 = fopen('parms7.txt', 'w');
f3 = fopen('parms9.txt', 'w');

disp('writing parameter files');
size(finalmods)

for(i=1:max(size(finalmods)))
	i
	sz = max(size(finalmods{i}));
	mod = finalmods{i};
	traitname = [mask '_t_' int2str(i)];
	ts = traits(:,mod);
	save(traitname, 'ts', '-ascii');
    groupname = [mask '_g_' int2str(i)];
    group = finalModIndeces{i};
    save(groupname, 'group', '-ascii');
    
	mk = [mask '_' int2str(i)];

    parms = [exe '.m y=load(''' traitname ''');x=load(''' markerfile ''');'];
    parms = [parms 'Lambda=load(''' lambdaname ''');groups=load(''' groupname ''');'];
	parms = [parms 'mask=''' mk ''';features=load(''' featurefile ''');' ];
    parms1 = [parms 'type=1;'];
    parms2 = [parms 'type=2;'];
    parms3 = [parms 'type=3;'];
    
    fprintf(f1,'%s\n', [parms1 'div=1;' exe]);
	if i==1
		fprintf(f1, 'vanilla\n');
	end

    fprintf(f2,'%s\n', [parms2 'div=1;' exe]);
    if i==1
        fprintf(f2, 'vanilla\n');
    end
    
    fprintf(f3, '%s\n', [parms3 'div=1;' exe]);
	if i==1
		fprintf(f3, 'vanilla\n');
    end
    
	for hh=2:10
		fprintf(f1,'%s\n',[parms1 'div=' num2str(hh) ';' exe]);
		fprintf(f2,'%s\n',[parms2 'div=' num2str(hh) ';' exe]);
		fprintf(f3,'%s\n',[parms3 'div=' num2str(hh) ';' exe]);
	end
end

disp('writing ve parameter files');

fclose(f1);
fclose(f2);
fclose(f3);
f = fopen('parms6.txt', 'w');
fprintf(f, [vename '.m mask=''' mask ''';Lambda=load(''' lambdaname ''');type=1;' vename '\n']);
fprintf(f, 'vanilla\n');
fclose(f);

f = fopen('parms8.txt', 'w');
fprintf(f, [vename '.m mask=''' mask ''';Lambda=load(''' lambdaname ''');type=2;' vename '\n']);
fprintf(f, 'vanilla\n');
fclose(f);

f = fopen('parms10.txt', 'w');
fprintf(f, [vename '.m mask=''' mask ''';Lambda=load(''' lambdaname ''');type=3;' vename '\n']);
fprintf(f, 'vanilla\n');
fclose(f);

for i=1:3
    mkdir(['beta' int2str(i)]);
    mkdir(['theta' int2str(i)]);
    mkdir(['omega' int2str(i)]);
    mkdir(['rho' int2str(i)]);
    mkdir(['nu' int2str(i)]);
end
