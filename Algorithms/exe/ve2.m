nonzeros = zeros(1,25);
err = zeros(1,25);
lerr = zeros(1,25);
%load cx.txt
%load y.txt
x = load([mask '_m'])';
y = load([mask '_t'])';

size(x)

if(size(x,2) < 2500)
%if(false)
p = fopen('parms4.txt', 'w');
	for i=0:subsz:size(y,2)
        	s = int2str(i+1);

        	while(length(s)<4)
                	s = ['0' s];
        	end

        	traitFileName = [mask '_t_'  s];
        	noTs = subsz;
        	if(i+subsz > size(y,2))
                	noTs = size(y,2) - i;
        	end

        	fprintf(p, ['1e-' int2str(0) ' ' int2str(0) 'e-' int2str(0)  ' ']);
        	fprintf(p, [int2str(size(y,1)) ' ' int2str(0) ' ' int2str(size(x,2)) ' ' int2str(noTs) ' ']);
        	fprintf(p, [mask '_m ' traitFileName ' ' mask '_' s '\n']);

        	if i==0
                	fprintf(p, 'vanilla\n');
        	end
	end

	fclose(p);
	exit
end

betars = zeros(size(x,2), size(y,2));

lambdas = load([mask  '_0001_lambdas.txt']);

for i=1:size(y,2)
	maskidx = floor((i-1) / subsz);
	maskidx = maskidx * subsz + 1;
	s = int2str(maskidx);

	while(length(s) < 4)
		s = ['0' s];
	end

	v = mod(i, subsz);
	if(v == 0)
		v = subsz;
	end

	betas = load([s '/' mask '_' s '_betas' num2str(v) '.txt']);
	i
	for j=1:25
		nz = size(find(abs(betas(j,:)) >= 1e-5), 2);
		nonzeros(j) = nonzeros(j) + nz;

		nzidx = find(abs(betas(j,:)) >= 1e-5);
		cfs = inv(x(1:vss, nzidx)' * x(1:vss,nzidx) + 1e-9*eye(nz,nz)) * x(1:vss,nzidx)' * y(1:vss, i);
		t = zeros(1,size(x,2));
		t(nzidx) = cfs;

		pred = x(vss+1:size(x,1),:) * t';
		errs = y(vss+1:size(x,1),i) - pred;
		err(j) = err(j) + sum(errs.^2);

		pred = x(vss+1:size(x,1),:) * betas(j,:)';
		errs = y(vss+1:size(x,1),i) - pred;
		lerr(j) = lerr(j) + sum(errs.^2);
	end
	%betars(:, i) = betas(10,:)';

	%[nonzeros' err' lerr']
end
%save betas.txt betars -ascii
[nonzeros' err' lerr']

[m idx] = min(err');
lambdas(idx)

for i=1:size(y,2)
        maskidx = floor((i-1) / subsz);
        maskidx = maskidx * subsz + 1;
        s = int2str(maskidx);

        while(length(s) < 4)
                s = ['0' s];
        end

        v = mod(i, subsz);
        if(v == 0)
                v = subsz;
        end

        betas = load([s '/' mask '_' s '_betas' num2str(v) '.txt']);
	delete([s '/' mask '_' s '_betas' num2str(v) '.txt']);
        betars(:, i) = betas(idx,:)';
end

save([mask '_glmentbetas.txt'], 'betars', '-ascii');

l = lambdas(idx);
res = floor(-log10(l)) + 1;
offy = l*10^res;
offy = offy - 3;

if offy <= 0
	offy = 5;
	res = res + 1;
else
	offy = 0;
end

p = fopen('parms4.txt', 'w');

for i=0:subsz:size(y,2)
	s = int2str(i+1);

	while(length(s)<4)
		s = ['0' s];
	end

	traitFileName = [mask '_t_'  s];
	noTs = subsz;
	if(i+subsz > size(y,2))
		noTs = size(y,2) - i;
	end

	fprintf(p, ['1e-' int2str(res) ' ' int2str(offy) 'e-' int2str(res)  ' ']);
	fprintf(p, [int2str(size(y,1)) ' ' int2str(vss) ' ' int2str(size(x,2)) ' ' int2str(noTs) ' ']);
	fprintf(p, [mask '_m ' traitFileName ' ' mask '_' s '\n']);

	if i==0
		fprintf(p, 'vanilla\n');
	end
end

fclose(p);
exit

