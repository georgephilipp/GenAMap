%mask = mask


%1) find groupings (because this will tell us what genes to ignore)
addpath('../exe');
fi = [mask '_assoc'];
assoc = load(fi);
mat = sparse(10000,10000);
m1 = sparse(10000,10000);
trtsz = 12;
genesz = 500;

minIdx = min(min(assoc(:,3))) - 1
max(max(assoc(:,3)))

for(i=1:size(assoc,1))
    mat(assoc(i,3)-minIdx, assoc(i,1)) = abs(assoc(i,2));
    m1(assoc(i,3)-minIdx, assoc(i,1)) = 1;
end

idx1 = (find(sum(m1)==1));
idx2 = (find(sum(m1)>1));
idx3 = find(sum(m1')==1);
idx4 = find(sum(m1')>1);
size(sum(m1'))

ix = [idx3, idx4];
ix2 = [idx1, idx2];%this will be what we need later on for traits!
save 'traitidx.txt' ix2 -ascii;
a = mat(ix, ix2);

a = reduceAssociationMatrix(a, 'conncomp');
idx = find(sum(a') ~= 0);
a = a(idx,:);

%imagesc(a)
if(min(size(a)) ~= 1)
	b = a;
else
	b = mat(ix,ix2);
	a = mat(ix,ix2);
end

b(find(b~=0)) = 1;

liners = find(sum(b) > 1);

for(i = 1:size(liners,2))
    j = liners(i);
    
    [maxxie mplace] = max(a(:,j));
    a(:,j) = 0;
    a(mplace,j) = maxxie;    
end

[x y] = max(a)
save 'groups.txt' y -ascii

fi = [mask '_g'];
tr = load(fi);
numt = size(tr,1);

N = size(tr,2);
trnNu = round(N * .9);
trnN = 1:trnNu;
tstN = (trnNu+1):N;

tr = tr';
tr = normalizeData(tr);
trn = tr(trnN, ix2);
tst = tr(tstN, ix2);
save 'genes.txt' trn -ascii
save 'genes_v.txt' tst -ascii

cntrG = -1;
if(size(trn,2) > genesz)
	[sgenes ogenes] = sort(y);
	trn = trn(:,ogenes);
	tst = tst(:,ogenes);

	issame = 0;
	curgrp = 0;
	cntrG = round(size(trn,2)/2);
	while(issame == 0 && cntrG < size(trn,2))
		if(curgrp == 0)
			curgrp = sgenes(cntrG);
		else
			if(sgenes(cntrG) ~= curgrp)
				issame = 1;
			end
		end
		cntrG = cntrG + 1;
	end

	cntrG = cntrG - 1;
	trn1 = trn(:, 1:cntrG-1);
	trn2 = trn(:, cntrG:size(trn,2));
	tst1 = tst(:, 1:cntrG-1);
	tst2 = tst(:, cntrG:size(trn,2));

    ogenes
    cntrG
	save 'genes1.txt' trn1 -ascii;
	save 'genes2.txt' trn2 -ascii;
end

genemat = sparse(numt,numt);
net = load([mask 'G_net.txt']);
for(i=1:size(net,1))
	fprintf('%d\n', i);
	if(ismember(net(i,1),ix2) & ismember(net(i,2),ix2))
		genemat(net(i,1), net(i,2)) = net(i,3);
		genemat(net(i,2), net(i,1)) = net(i,3);
	end
end

genemat = genemat(ix2,ix2);
size(genemat)
tosave = zeros(size(genemat));

for(i=1:size(genemat,1))
	for(j=1:size(genemat,1))
		tosave(i,j) = genemat(i,j);
	end
end

save 'genenet.txt' tosave -ascii;
if(cntrG ~= -1)
	gn = tosave(1:cntrG-1, 1:cntrG-1);
	save 'genenet1.txt' gn -ascii;
	gn = tosave(cntrG:size(tosave,2), cntrG:size(tosave,2));
	save 'genenet2.txt' gn -ascii;
end

traitmat = load([mask '_t']);
noT = size(traitmat,1);
tmat=zeros(noT,noT);
traitmat = traitmat';
traitmat = normalizeData(traitmat);
trnT = traitmat(trnN,:);
tstT = traitmat(tstN,:);

save 'traits.txt' trnT -ascii
save 'traits_v.txt' tstT -ascii

net = load([mask 'T_net.txt']);
for(i=1:size(net,1))
	tmat(net(i,1), net(i,2)) = net(i,3);
	tmat(net(i,2), net(i,1)) = net(i,3);
end

save 'traitnet.txt' tmat -ascii


fi = fopen('parms2.txt', 'w');

for(i=1:noT)
	fprintf(fi, [num2str(size(trnT,1)) ' ' num2str(size(genemat,1)) ' ' num2str(size(traitmat,2)) ' genes.txt traits.txt ' mask ' ' num2str(i) '\n']);
	if(i==1)
		fprintf(fi, 'vanilla\n');
	end
end
fclose(fi);

fi = fopen('parms3.txt', 'w');
fprintf(fi, ['simpleLassoCmbn.m mask=''' mask ''';simpleLassoCmbn\n']);
fprintf(fi, 'vanilla\n');
fclose(fi);

sparset = sparse(size(tmat));
for(i=1:noT)
	for (j=1:noT)
		sparset(i,j) = tmat(i,j);
	end
end

[s c] = graphconncomp(sparset);
s

m = 0;
for(i=1:s)
	if(max(size(find(c==i))) > m)
		m = max(size(find(c==i)));
	end
end
m

while(m > trtsz)
	idx = find(sparset ~= 0);
	sparset(idx) = abs(sparset(idx));
	mn = min(sparset(idx));

	mn = mn + .05;
	sparset(find(sparset < mn)) = 0;

	[s c] = graphconncomp(sparset);
	s

	m = 0;

	for(i=1:s)
		if(max(size(find(c==i))) > m)
			m = max(size(find(c==i)));
		end
	end
	m
end

c

save 'conncomps.txt' c -ascii;

tgrps = zeros(noT,1);

grpsz = zeros(s,2);
for(i=1:s)
	grpsz(i,1) = i;
	grpsz(i,2) = max(size(find(c==i)));
	grpsz(i,3) = 0;
end

curix = 1;

grpsz

hasempty = 1;

grpidx = 1;
while(hasempty == 1)
	hasempty = 0;
	grpsz
	for(i=1:size(grpsz,1))
		if(grpsz(i,3) == 0)
			hasempty = 1;
		end
	end
	if(hasempty == 0)
		continue;
	end

	grp{grpidx} = [];
	for r=1:3
	for(i=1:size(grpsz,1))
		sz = max(size(grp{grpidx}));
		if(sz + grpsz(i,2) <= trtsz & grpsz(i,3) == 0)
			grp{grpidx} = [grp{grpidx} find(c == i)];
			grpsz(i,3) = 1;
		end
	end
	end

	while(max(size(grp{grpidx})) < trtsz)
		grp{grpidx} = [grp{grpidx} 0]
	end

	grpidx = grpidx + 1
end

grp

fi = fopen('traitgroups.txt', 'w');
for(i =1:max(size(grp)))
	gr = grp{i};
	gr = gr(find(gr ~= 0));
	for(j=1:trtsz)%max(size(gr)))
		if(j <= max(size(gr)))
			if(var(trnT(:,gr(j))) ~= 0)
				fprintf(fi, [num2str(gr(j)) '\t']);
			else
				fprintf(fi, '0\t');
				gr(j) = 0;
				grp{i}(j) = 0;
			end
		else
			fprintf(fi, '0\t');
		end
	end
	gr = gr(find(gr ~= 0));
	subtrait = trnT(:, gr);
	save(['traits_' num2str(i)], 'subtrait', '-ascii');
	subnet = tmat(gr,gr);
	save(['traitnet_' num2str(i)], 'subnet', '-ascii');

	fprintf(fi, '\n');
end
fclose(fi);


fi = fopen('parms4.txt', 'w');
for(i = 1:max(size(grp)))
	gr = grp{i};
	gr = gr(find(gr ~= 0));

	if(cntrG == -1)
		fprintf(fi, [num2str(size(trnT,1)) ' ' num2str(size(trn,2)) ' ' num2str(max(size(gr))) ' ' 'genes.txt' ' ' 'traits_' num2str(i) ' ' 'genenet.txt' ' '  'traitnet_' num2str(i) ' ' mask '_' num2str(i) ' ' '1 ' '1 ' '1e-3' ' ' '100' ' ' '1e-4 1e-5 1e-5' ' ' 'initB_' num2str(i) '\n']);
		if(i == 1)
			fprintf(fi, 'standard\n');
		end
	else
		fprintf(fi, [num2str(size(trnT,1)) ' ' num2str(size(trn(:, 1:cntrG-1),2)) ' ' num2str(max(size(gr))) ' ' 'genes1.txt' ' ' 'traits_' num2str(i) ' ' 'genenet1.txt' ' '  'traitnet_' num2str(i) ' ' mask '_' num2str(i) 'a ' '1 ' '1 ' '1e-3' ' ' '100' ' ' '1e-4 1e-5 1e-5' ' ' 'initB_' num2str(i) 'a\n']);
		if(i==1)
			fprintf(fi, 'standard\n');
		end
		fprintf(fi, [num2str(size(trnT,1)) ' ' num2str(size(trn(:,cntrG:size(trn,2)),2)) ' ' num2str(max(size(gr))) ' ' 'genes2.txt' ' ' 'traits_' num2str(i) ' ' 'genenet2.txt' ' '  'traitnet_' num2str(i) ' ' mask '_' num2str(i) 'b ' '1 ' '1 ' '1e-3' ' ' '100' ' ' '1e-4 1e-5 1e-5' ' ' 'initB_' num2str(i) 'b\n']);

	end
end
fclose(fi);


fi = fopen('parms5.txt', 'w');
fprintf(fi, ['ggflassoVE.m step=1;mask=''' mask ''';ggflassoVE\nvanilla\n']);







