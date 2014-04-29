
%Since the data is large, we split it into smaller portions.
% If the original data has n samples, with d dimensions, we split it into m groups, each of size n samples, and (d/m) features.
% Thus, each portion has all the samples, but only some of the features.

%To parallelize this code, simpy remove the for-loop over the iterations, and run it after providing it the iteration number.

[status, dataroot] = unix('cat ./dataroot', '-echo');
dataroot = strtrim(dataroot);

%Find out how many splits have we generated of the data
unix(sprintf('rm -f  /tmp/numberOfRuns; ls ../%s/train.transpose.dat.TRANSPOSE.* | wc -l > /tmp/numberOfRuns', dataroot));
numberOfRuns = load('/tmp/numberOfRuns');
fprintf('Running for %d runs\n', numberOfRuns);

% repeat for all iterations 
for iteration=1:numberOfRuns
	main_function_parallelRunning(iteration, dataroot);
end

