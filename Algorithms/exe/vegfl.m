%mask = 'GFLc0...'
%type=1,2,or 3
istree = strfind(mask, 'TLS');

if(size(istree,1) == 0)
	istree = false;
else
	istree = true;
end

x = load([mask '_mp']);

if(size(x,1) == 1)
	x = x.x;
end

y = load([mask '_tp']);
N = size(x,1);
J = size(x,2);
K = size(y,2);

%itrn = [1:N-10];
%itst = [N-9:N];
%xtrn = x(itrn,:);
%xtst = x(itst,:);

%ytst = y(itst,:);
%ytrn = y(itrn,:);
thresh = 1e-3

save('thresh.txt', 'thresh', '-ascii');

if(exist('key.mat', 'file'))
	load('key.mat');
	keys = finalmods;
else
	keys{1} = 1:size(y,2);
end

conf = 'GFL_1.config';
GFL_1 = load(conf);

%create a lambda and gamma array based on teh configuration file
lambda = [];
gamma = [];

if(type == 1)
	t = GFL_1(1,1);
	i = 1;
	while(1)
		if(t > GFL_1(1,2))
			break;
		end
		lambda(i) = t;
		gamma(i) = GFL_1(2,1);
		i = i+1;
		t = t * GFL_1(1,3);
	end
elseif type == 2 & ~istree
	t = GFL_1(2,1);
	i = 1;
	while(1)
		if(t > GFL_1(2,2))
			break;
		end
		lambda(i) = GFL_1(1,1);
		gamma(i) = t;
		i = i+1;
		t = t * GFL_1(2,3);
	end
elseif type == 3 | istree
        lambda = GFL_1(1,1):GFL_1(1,3):GFL_1(1,2);
	gamma = repmat(GFL_1(2,1),size(lambda,1),size(lambda,2));
end

lambda 
gamma
errorsums = zeros(1,numel(lambda));
divs = load('div.txt');
for div=1:10
    mini = 1e99;
    
	itrn = setdiff(1:N,divs(div,1):divs(div,2));%[1:N-10];
	itst = divs(div,1):divs(div,2);%[N-9:N];
	xtrn = x(itrn,:);
	xtst = x(itst,:);

	ytst = y(itst,:);
	ytrn = y(itrn,:);


	i = 1;
	clear t;
	for i=1:numel(lambda)
                fprintf('%d\t', i);
		beta = zeros(size(x,2),size(y,2));
	
		for j=1:(max(size(keys)))
			filename = [mask '_' int2str(j) 'betas_' int2str(i) '_' num2str(div) '.txt'];	
			if(~exist(int2str(type),'dir'))
				mkdir(int2str(type));	
			end
			movedfilename = [int2str(type),'/',filename];
			if(exist(movedfilename))
				bettemp = load(movedfilename);
			else
				bettemp = load(filename);
				movefile(filename,int2str(type));
			end
			beta(:, keys{j}) = bettemp;
		end

		toterr = 0;
		bets = zeros(size(x,2), size(y,2));
		for(j=1:size(y,2))
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

		errorsums(i) = errorsums(i) + toterr;
		if(toterr < mini)
			l = lambda(i);
			g = gamma(i);
			mini = toterr;
			betsav = bets;
		end
	end
	l
	g
end

[s i] = min(errorsums);
l = lambda(i)
g = gamma(i)
mydiv = i
errorsums'

%write out the new config file based on the type
if(type == 1 & ~istree)
	GFL_1(1,1) = l;
	GFL_1(1,2) = l;
	GFL_1(2,1) = 1e-5;
	GFL_1(2,2) = 1e0;
	save 'GFL_1.config' GFL_1 -ascii
elseif((type == 2 & ~istree) | (type ==1 & istree))
	GFL_1(1,1) = l / 10;
	GFL_1(1,2) = l + l / 2 + l / 20;
	GFL_1(1,3) = l / 10;
	GFL_1(1,4) = 1;
	GFL_1(2,1) = g;
	GFL_1(2,2) = g;
	save 'GFL_1.config' GFL_1 -ascii
elseif(type == 3 | (type == 2 & istree))
	ix = find(lambda == l);
	betsav = zeros(size(betsav));
	
	for(mydiv = 1:10)
		for j=1:(max(size(keys)))
            filename = [int2str(type) '/' mask '_' int2str(j) 'betas_' int2str(i) '_' num2str(mydiv) '.txt'];
            bettemp = load(filename);
            beta(:, keys{j}) = bettemp;
        end
		beta = abs(beta);
		beta(beta < thresh) = 0;
		beta(beta ~= 0) = 0.1;
		betsav = betsav + beta;
	end

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

%save 'temp.txt' betsav;
