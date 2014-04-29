function [xx m s] = normalizeData(x) 
	assert (size(x,1) > 1)
	m = mean(x, 1);
	s = std(x);
	%% uncomment this if you do not want to renormalize
	%s = ones(size(s));
        xx = (x - repmat(m, size(x,1), 1))./repmat(s, size(x,1),1);
end

