%mask = '...'; parameter passed in.

markers = load([mask '_m']);

J = size(markers,1);

if(J < 2500)
	s = [mask '_m'];
	markers = markers';
	save(s, 'markers', '-ascii');
	exit
end

markers = markers';
beta = load([mask '_glmentbetas.txt']);

beta = abs(beta);

beta(find(beta < 1e-2)) = 0;
beta(find(beta ~= 0)) = 1;

sums = sum(beta');

thresh = 0;
t = max(size(find(sums > thresh)));

while(t > 2500)
	thresh = thresh + 1
	t = max(size(find(sums > thresh)))
end

idx = find(sums > thresh);
movefile([mask '_glmentbetas.txt'] , [mask '_glmnet_old.txt']);
movefile([mask '_m'], [mask '_m_old']);

beta = beta(idx,:);
m = markers(:,idx);

%save([mask '_glmentbetas.txt'], 'beta', '-ascii');
save([mask '_m'], 'm', '-ascii');
save([mask '_midx'], 'idx', '-ascii');

exit
