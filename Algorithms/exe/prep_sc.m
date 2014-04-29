%mask = '...' param

traits = load([mask '_t']);

sz = size(traits,1);

if(sz < 650)
	f = fopen('parms8.txt','w');
	write(f,'sc.m sc\nvanilla');
	fclose(f);

	f = fopen('parms9.txt', 'w');
	write(f, ['traitprocessing.m "mask=' mask '; sc=-1;traitprocessing;"\nvanilla']);
	fclose(f);
	exit
end

lower = [];

idx = 0;

while(1)
	name = [mask '_subnet_' int2str(idx++)];
	if(~exist(name, 'file'))
		break;
	end

	subby = load(name);

	if(max(size(subby)) > 650)
		lower = [lower (idx-1)];
	end
end

if(min(size(lower)) == 0)
        f = fopen('parms8.txt','w');
        write(f,'sc.m sc\nvanilla');
        fclose(f);

        f = fopen('parms9.txt', 'w');
	write(f, ['traitprocessing.m "mask=' mask '; sc=-1;traitprocessing;"\nvanilla']);
        fclose(f);
        exit
end

f = fopen('parms9.txt', 'w');
write(f, ['traitprocessing.m "mask=' mask '; sc=[']);

for i=1:max(size(lower))
	write(f, int2str(lower(i)));
	write(' ');
end

write(f, ';traitprocessing;"\nvanilla');







exit
