%mask = '...' param

isADL = strfind(mask, 'ADL');

if(size(isADL,1) == 0)
	isADL = false;
else
	isADL = true;
end

runSCparmFileName = 'parms8.txt';
traitprocessingParmFileName = 'parms9.txt';
traitprocessingScriptName = 'traitprocessing';
if isADL
    runSCparmFileName = 'parms3.txt';
    traitprocessingParmFileName = 'parms4.txt';
    traitprocessingScriptName = 'traitprocessingADL';
end
    


maxsz = 200;
traits = load([mask '_t']);

sz = size(traits,1);

if(sz < maxsz)
	f = fopen(runSCparmFileName,'w');
	fprintf(f,'runSC.m runSC(''n'',-1,''n'');\nvanilla');
	fclose(f);

	g = fopen(traitprocessingParmFileName, 'w');
	fprintf(g, [traitprocessingScriptName '.m mask=''' mask ''';sc=-1;' traitprocessingScriptName ';\nvanilla']);
	fclose(g);
	exit
end

lower = [];

idx = 0;

while(1)
	name = [mask '_subnet_' int2str(idx) '.txt'];
	idx = idx + 1;
	if(~exist(name, 'file'))
		break;
	end

	subby = load(name);

	if(max(size(subby)) > maxsz)
		lower = [lower (idx-1)];
	end
end

if(min(size(lower)) == 0)
	f = fopen(runSCparmFileName,'w');
        fprintf(f,'runSC.m runSC(''n'',-1,''n'');\nvanilla');
        fclose(f);

        g = fopen(traitprocessingParmFileName, 'w');
        fprintf(g, [traitprocessingScriptName '.m mask=''' mask ''';sc=-1;' traitprocessingScriptName ';\nvanilla']);
        fclose(g);
	exit
end

f = fopen(traitprocessingParmFileName, 'w');
fprintf(f, [traitprocessingScriptName '.m mask=''' mask ''';sc=' int2str(lower(1))]);

for i=2:max(size(lower))
	fprintf(f, ';sc=union(sc,');
	fprintf(f, int2str(lower(i)));
	fprintf(f, ')');
end

fprintf(f, [';' traitprocessingScriptName ';\nvanilla\n']);
fclose(f);

g = fopen(runSCparmFileName,'w');

net = load([mask '_net.txt']);

idx = 0;
ki = 0;
writ = 0;
while(1)
        name = [mask '_subnet_' int2str(idx) '.txt'];
        idx = idx + 1;
        if(~exist(name, 'file'))
                break;
        end

        subby = load(name);
	
        if(max(size(subby)) > maxsz)
        	%1 create the file
		mat = zeros(max(size(subby)), max(size(subby)));
		for(i = 1 : max(size(subby)))
			ei = find(net(:,1) == (subby(i)));
			%size(ei)
			%fprintf('%d\n', subby(i))
			edges = net(ei,2:3);
			%pause

			for(j=1:size(edges,1))
				ix = find((subby)==edges(j,1));
				w = abs(edges(j,2));

				%fprintf('%d\n%d\n', ix,j);

				mat(i,ix) = w;
				mat(ix,i) = w;
			end
		end
		s = [mask '_sub_' int2str(idx-1)];
		%ki = ki + 1;
		save(s, 'mat', '-ascii');

		%2 write the params out.
		fprintf(g, ['runSC.m addpath(''../exe/'');runSC(''' s ''',2,''' mask '_sc_' int2str(idx-1) '_2'');\n']);
		if(writ == 0)
			fprintf(g, 'vanilla\n');
			writ = 1;
		end

		for(i = 3:60)
			fprintf(g, ['runSC.m addpath(''../exe/'');runSC(''' s ''',' int2str(i) ',''' mask '_sc_' int2str(idx-1) '_' int2str(i) ''');\n']);
		end

		idx = idx + 1;
	end
end

fclose(g);



exit
