%mask = 'GFLc0...'
%type=1,2,or 3

mini = 1e99;

x = load([mask '_mp']);
y = load([mask '_tp']);
N = size(x,1);
J = size(x,2);
K = size(y,2);

thresh = 1e-3

save('thresh.txt', 'thresh', '-ascii');
load('key.mat');

Lambda

errorsums = zeros(1,size(Lambda,2));
divs = load('div.txt');
for div=1:10
	itrn = setdiff(1:N,divs(div,1):divs(div,2));
	itst = divs(div,1):divs(div,2);
	xtrn = x(itrn,:);
	xtst = x(itst,:);

	ytst = y(itst,:);
	ytrn = y(itrn,:);

	i = 1;
	clear t;
	for i=1:size(Lambda,2)
                fprintf('%d\t', i);
		beta = zeros(size(x,2),size(y,2));
	
		for j=1:(max(size(keys)))
			betaname = ['beta' int2str(type) '/' mask '_' int2str(j) 'betas_' int2str(i) '_' num2str(div) '.txt'];	
			beta(:, keys{j}) = load(betaname);
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
    end
end

[s i] = min(errorsums);
l1 = Lambda(1,i)
l2 = Lambda(2,i)
i
errorsums'

%write out the new config file based on the type
if(type == 1)
	%make lambdas
    Lambda = zeros(2,49);
    for i=1:7
        for j=1:7
            Lambda(1,j + 7*(i-1)) = l1 * 2^(i-4);
            Lambda(2,j + 7*(i-1)) = l2 * 2^(j-4);
        end
    end
    lambdaname = [mask '_lambda'];
    save(lambdaname, 'Lambda', '-ascii');    
elseif(type == 2)
	%make lambdas
    Lambda = zeros(2,49);
    for i=1:7
        for j=1:7
            Lambda(1,j + 7*(i-1)) = l1 * 1.25^(i-4);
            Lambda(2,j + 7*(i-1)) = l2 * 1.25^(j-4);
        end
    end
    lambdaname = [mask '_lambda'];
    save(lambdaname, 'Lambda', '-ascii');
elseif(type == 3)
	ix = find(lambda == l);
	betsav = zeros(J,K);
	
	for mydiv = 1:10
		for j=1:max(size(keys))
            filename = ['beta' int2str(type) '/' mask '_' int2str(j) 'betas_' int2str(i) '_' num2str(mydiv) '.txt'];
            bettemp = load(filename);
            beta(:, keys{j}) = bettemp;
        end
		beta = abs(beta);
		beta(beta < thresh) = 0;
		beta(beta ~= 0) = 0.1;
		betsav = betsav + beta;
	end

	for j=1:250:J
		for k=1:250:K
			endJ = j + 249;
			if(endJ > J)
				endJ = J;
			end
			endK = k + 249;
			if(endK > K)
				endK = K;
			end

			temp = betsav(j:endJ, k:endK);
			s = [mask '_' int2str(j) '_' int2str(k) 'res.txt'];
			save(s, 'temp', '-ascii');
		end
	end
end

%save 'temp.txt' betsav;
