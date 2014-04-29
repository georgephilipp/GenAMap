%mask = 'GFLc0...'
%type=1,2,or 3
istree = strfind(mask, 'TLS');

if(size(istree,1) == 0)
	istree = false;
else
	istree = true;
end

mini = 1e99;

x = load([mask '_mp']);

if(size(x,1) == 1)
	x = x.x;
end

y = load([mask '_tp']);

addpath('../exe');
x = normalizeData(x);
y = normalizeData(y);

N = size(x,1);
J = size(x,2);
K = size(y,2);
itrn = [1:N-10];
itst = [N-9:N];
xtrn = x(itrn,:);
xtst = x(itst,:);

ytst = y(itst,:);
ytrn = y(itrn,:);
thresh = 1e-3

save('thresh.txt', 'thresh', '-ascii');

if(exist('key.mat', 'file'))
	load('key.mat');
	keys = finalmods;
else
	keys{1} = 1:size(y,2);
end

conf = 'GFL_1.config';
if(type == 2)
	conf = 'GFL_2.config';
elseif(type == 3)
	conf = 'GFL_3.config';
end

GFL_1 = load(conf);

%create a lambda and gamma array based on teh configuration file
if(type == 1 | type == 3)
	gamma = GFL_1(2,1);
elseif(type == 2)
	lambda = GFL_1(1,1);
	gamma = GFL_1(2,1);
end

if(type == 1)
	lambda(1) = GFL_1(1,1);
	t = lambda(1);
	i=2;
	while(1)
		if(t >= GFL_1(1,2))
			break;
		end

		lambda(i) = lambda(i-1) * GFL_1(1,3);
		gamma(i) = gamma(i-1);
		i = i + 1;
		t = lambda(i-1);
	end
elseif type == 2 & ~istree
        gamma(1) = GFL_1(2,1);
        t = gamma(1);
        i=2;
        while(1)
                if(t >= GFL_1(2,2))
                        break;
                end

                gamma(i) = gamma(i-1) * GFL_1(2,3);
		lambda(i) = lambda(i-1);
                i = i + 1;
                t = gamma(i-1);
        end
elseif type == 3 | istree
        lambda(1) = GFL_1(1,1);
        t = lambda(1);
        i=2;
        while(1)
                if(t >= GFL_1(1,2))
                        break;
                end

                lambda(i) = lambda(i-1) + GFL_1(1,3);
		gamma(i) = gamma(i-1);
                i = i + 1;
                t = lambda(i-1);
        end
end

lambda 
gamma

i = 1;
clear t;
while(1)
	fprintf('%d\t', i);
	beta = zeros(size(x,2),size(y,2));

	for j=1:(max(size(keys)))
		moveitnow= 1;
                filename = [mask '_' int2str(j) 'betas_' int2str(i) '.txt'];
                if(~exist(filename))
                      filename = [num2str(type) '/' filename];
                      moveitnow = 0;
                end
                bettemp = load(filename);
                if(~exist(int2str(type),'dir'))
                        mkdir(int2str(type));
                end
                if(moveitnow == 1)
                       movefile(filename,int2str(type));
                end
                beta(:, keys{j}) = bettemp;
	end

	beta(find(isnan(beta))) = 0;

	toterr = 0;
	bets = zeros(size(x,2), size(y,2));
	for(j=1:size(y,2))
%		nz{j} = find(abs(beta(:,j))>thresh);
%		xp{j} = [ones(size(xtrn,1),1) xtrn(:,nz{j})];
%		q{j} = inv((xp{j})' * xp{j} + 1e-9 * eye(size(xp{j},2))) * (xp{j})' * ytrn(:,j);
%		t{j} = zeros(size(x,2),1);
%		t{j}(nz{j}) = q{j}(2:size(q{j},1));
%		bets(:,j) = t{j};
%
%		xr{j} = [ones(size(xtst,1),1) xtst];
%		pred{j} = xr{j} * [q{j}(1) (t{j})']';
%		err{j} = ytst(:,j) - pred{j};
%		err{j} = err{j}.^2;
%		toterr = toterr + sum(err{j});

		j
		nz = find(abs(beta(:,j))>thresh);
                xp = [ones(size(xtrn,1),1) xtrn(:,nz)];
                q = inv((xp)' * xp + 1e-9 * eye(size(xp,2))) * (xp)' * ytrn(:,j);
                t = zeros(size(x,2),1);
                t(nz) = q(2:size(q,1));
                bets(:,j) = t;

                xr = [ones(size(xtst,1),1) xtst];
                pred = xr * [q(1) (t)']';
                err = ytst(:,j) - pred;
                err = err.^2;
                toterr = toterr + sum(err);
		clear nz;
		clear xp;
		clear q;
		clear t;
		clear xr;
		clear pred;
		clear err;
	end

	fprintf('%d\n', toterr);

	if(toterr < mini)
		l = lambda(i);
		g = gamma(i);
		mini = toterr;
		betsav = bets;
	end

        i = i + 1;
        if(~exist([mask '_1betas_' int2str(i) '.txt']) & ...
                   ~exist([num2str(type) '/' mask '_1betas' int2str(i) '.txt']))
                break;
        end
end

%write out the new config file based on the type
if(type == 1 & ~istree)
	GFL_1(1,1) = l;
	GFL_1(1,2) = l;
	GFL_1(2,1) = 1e-5;
	GFL_1(2,2) = 1e0;
	save 'GFL_2.config' GFL_1 -ascii
elseif((type == 2 & ~istree) | (type ==1 & istree))
	GFL_1(1,1) = l / 10;
	GFL_1(1,2) = l + l / 2;
	GFL_1(1,3) = l / 10;
	GFL_1(1,4) = 1;
	GFL_1(2,1) = g;
	GFL_1(2,2) = g;
	save 'GFL_3.config' GFL_1 -ascii
elseif(type == 3 | (type == 2 & istree))
	for(i=1:250:J)
		for(j=1:250:K)
			endJ = i + 249;
			if(endJ > J)
				endJ = J;
			end
			endK = j + 249;
			if(endK > K)
				endK = K;
			end

			temp = betsav(i:endJ, j:endK);
			s = [mask '_' int2str(i) '_' int2str(j) 'res.txt'];
			save(s, 'temp', '-ascii');
		end
	end
end

save 'temp.txt' betsav;
