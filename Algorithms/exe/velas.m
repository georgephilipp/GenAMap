lambdas = load([mask  '_0001_lambdas.txt']);
numLambdas = numel(lambdas);
nonzeros = zeros(1,numLambdas);
err = zeros(1,numLambdas);
lerr = zeros(1,numLambdas);
x = load([mask '_m'])';
y = load([mask '_t'])';

size(x)

%betars = zeros(size(x,2), size(y,2));

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

	err(:) = err(:) + load([s '/' mask '_' s '_err' num2str(v) '.txt']);
	%if(~writeb == 1)
		delete([s '/' mask '_' s '_err' num2str(v) '.txt']);
	%end

	if(~writeb == 1)
		 delete([s '/' mask '_' s '_betas' num2str(v) '.txt']);
	end
end

lambdas
err
[m idx] = min(err');
lambdas(idx)

if(writeb == 1)
	tfi = 1;
	if(size(y,2) > subsz)
		betars = zeros(size(x,2), subsz);
	else
		betars = zeros(size(x,2),size(y,2));
	end

	cntr = 1;

	t = 1e-5;

	save('thresh.txt', 't', '-ascii');
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
                betars(:, cntr) = betas(idx,:)';
                cntr = cntr + 1;


		if(mod(i,subsz) == 0 | i == size(y,2))
			for(j=1:subsz:size(x,2))
				e = min(size(x,2),j+subsz-1);

				name = [mask '_' num2str(j) '_' num2str(tfi) 'res.txt'];
				%tfi = tfi + subsz;
				betars_t = betars(j:e,:);

				save(name, 'betars_t', '-ascii');
			end
			tfi = tfi + subsz;
			cntr=1;
			if(i+subsz <= size(y,2))
				betars = zeros(size(x,2),subsz);
			else
				betars = zeros(size(x,2),mod(size(y,2),subsz));
			end
		end
	end

	%save([mask '_glmentbetas.txt'], 'betars', '-ascii');
end

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

if(writeb ~= 1)
	p = fopen('parms3.txt', 'w');

	for i=0:subsz:size(y,2)
		if i == size(y,2)
			break;
		end

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
		fprintf(p, [mask '_m ' traitFileName ' ' mask '_' s ' 1 ' s '\n']);

		if i==0
			fprintf(p, 'vanilla\n');
		end

		for ii=2:noTs
		        fprintf(p, ['1e-' int2str(res) ' ' int2str(offy) 'e-' int2str(res)  ' ']);
		        fprintf(p, [int2str(size(y,1)) ' ' int2str(vss) ' ' int2str(size(x,2)) ' ' int2str(noTs) ' ']);
		        fprintf(p, [mask '_m ' traitFileName ' ' mask '_' s ' ' int2str(ii) ' ' s '\n']);
		end

	end
	fclose(p);
end

exit

