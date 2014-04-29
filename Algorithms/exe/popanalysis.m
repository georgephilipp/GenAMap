%pass in the mask!!!!! mask
%pass in the trait!!!! tidx

%% Data acquisision
addpath('../exe');
snp = load([mask '_m']);
phenos= load([mask '_t']);
phenos= phenos(tidx,:);
snp = snp';
phenos = phenos';
race = load('pops.txt');
race = race(:, 3);

idx = phenos ~= -99;
phenos = phenos(idx,1);
pops = race(idx,1);
snp = snp(idx,:);

X = snp;
k = max(unique(pops));
p = size(X,2);

Y = phenos;
Z = pops;
divisions = zeros(size(Z));
div = 1;
for(ix = 1:k)
	for(i = 1:size(Z,1))
		if(Z(i) == ix)
			divisions(i) = div;
			div = div + 1;
			if(div > 10)
				div = 1;
			end
		end	
	end
end
cvres = zeros(k,p);
pvals = zeros(k,p);
ttstp = zeros(k,p);
plink = zeros(k,p);

for(i=1:k)
	col = 6;
	if(exist(['pop' num2str(i) '.P' num2str(tidx) '.qassoc']))
                filename = ['pop' num2str(i) '.P' num2str(tidx) '.qassoc'];
	else
		filename = ['pop' num2str(i) '.P' num2str(tidx) '.assoc'];
        end
	%read the file line by line.
	fid = fopen(filename, 'r');
	tline = fgets(fid);

	tline = fgets(fid);
	iline = 1;
	while(ischar(tline))
		r = regexp(tline, ' ', 'split');
		ix = 0;
		for(w=1:size(r,2))
			if(~strcmp(r(w), ''))
				ix = ix + 1;
				if(ix == 9)
					if(~strcmp(r{w}, 'NA'))
						plink(i,iline) = str2num(r{w});
					else
						plink(i,iline) = 1.0;
					end
				end
			end	
		end
%		fprintf('%s\n', tline);
		tline = fgets(fid);
		iline = iline + 1;
	end
end

for(s=1:p)
	%% Cross validation
	for(round = 1:11)
		%normalize the data for each population
		X_train = X(divisions ~= round,s);
		Y_train = Y(divisions ~= round,:);
		Z_train = pops(divisions ~= round);
		X_test = X(divisions == round,s);
		Y_test = Y(divisions == round,:);
		Z_test = pops(divisions == round);

		xMean = zeros(k,1);
		xStd = ones(k,1);
		yMean = zeros(k,1);
		yStd = ones(k,1);

		for i=1:k
			[nn mm ss] = normalizeData(X_train(Z_train == i,:));
			xMean(i,:) = mm;
			xStd(i,:) = ss;
			X_train(Z_train==i,:) = nn;
	
			[nn mm ss] = normalizeData(Y_train(Z_train==i));
			yMean(i) = mm; yStd(i) = ss;
			Y_train(Z_train==i) = nn;
		end

		%find MLE estimate for non-zeros
		for i=1:k
			Xs = X_train(Z_train == i,:);
			predictedBeta(i,:) = (Xs'*Xs) \ (Xs'*Y_train(Z_train==i));
		end
		predictedBetao= zeros(size(predictedBeta));

		% make y predictions
		for i=1:k
			n = sum(Z_test == i);
			X_test(Z_test==i,:) = (X_test(Z_test==i,:) - repmat(xMean(i,:), n, 1)) ./ repmat(xStd(i,:), n, 1);
        		Y_test(Z_test==i) = (Y_test(Z_test==i) - yMean(i)) ./ yStd(i);   
	        end

        	Y_pred = zeros(n,1);
	        for i=1:k
        	    Y_pred(Z_test==i) = X_test(Z_test==i, :) * predictedBeta(i, :)' + 0;
	        end
        	% make Y predictions null
	        Y_pred_o = zeros(n,1);
        	for i=1:k
	            Y_pred_o(Z_test==i) = X_test(Z_test==i, :) * predictedBetao(i, :)' + 0;
        	end

	        % find accuracy
		for i=1:k
        		ourError(round,i) = mean((Y_test(Z_test==i)-Y_pred(Z_test==i)).^2);
		        ourError_o(round,i) = mean((Y_test(Z_test==i) - Y_pred_o(Z_test==i)).^2);
		end
	end
	cvres(:,s) = sum(ourError_o(1:10,:) - ourError(1:10,:))' / 10;
	
	for i=1:size(predictedBeta,1)
        	beta = predictedBeta(i);
        	beta2 = 0;
        	x = X_train(Z_train == i, :);
        	y = Y_train(Z_train == i, :);
        	n = size(x,1);

        	L0 = n* log(1/(sqrt(2*pi))) - 1/2 * -sum((y-x * beta2).^2);
        	La = n* log(1/(sqrt(2*pi))) - 1/2 * -sum((y-x * beta).^2);
        	diff = 2 * (L0-La);
        	pvals(i,s) = 1-chi2cdf(diff,1);
    	end

	for(i=1:k)
		[h1 p1] = ttest2(Y_train(Z_train==i & X(:,s) == 0), ...
				     Y_train(Z_train==i & X(:,s) == 1));
                [h2 p2] = ttest2(Y_train(Z_train==i & X(:,s) == 0), ...
                                     Y_train(Z_train==i & X(:,s) == 1));
                [h3 p3] = ttest2(Y_train(Z_train==i & X(:,s) == 0), ...
                                     Y_train(Z_train==i & X(:,s) == 1));
		ttstp(i,s) = min([p1 p2 p3]);
	end

	s
end

cvres(find(isnan(cvres))) = 0.0;
ttstp(find(isnan(ttstp))) = 1.0;
pvals(find(isnan(pvals))) = 1.0;

finalres =  [cvres' pvals' ttstp' plink'];
tosave = ['results' num2str(tidx) '.txt'];
save(tosave,'finalres', '-ascii');
