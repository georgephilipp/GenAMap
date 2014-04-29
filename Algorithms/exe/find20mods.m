%%parms to this method -> net=load('GMD127e');

%%take absolute value

net = abs(net);

%% Finding the max efficiency

vals = zeros(size(net));

%Go through to find out how many connections are in each row.
for i=1:size(net,1)
    numedges = 0;
    stop = 0;
    for j=i+1:size(net,1)
        numedges = numedges + net(i,j);
        vals(i,j) = numedges;
    end
end

%Now go through to find out how many connections are in each column
for i=size(net,1):-1:1
    stop = 0;
    for(j=size(net,1):-1:i+1)
        vals(i,j) = vals(i,j) + vals(i+1,j);
    end
end

for i=1:size(net,1)
    for(j=i+1:size(net,1))
        n = j-i+1;
        vals(i,j) = (vals(i,j) / (n*(n-1))/2) * (n/size(net,1));
    end
end


fprintf('Found number of edges in each module\n');

for s=1:20

	maxval = 0;
	bigi = 0;
	bigj = -1;
	for i=1:size(net,1)
    		[maxval1 idx] = max(vals(i,i+19:size(net,1)));
    		if(maxval1 > maxval)
        		bigi = i;
        		bigj = i + 18 + idx;
        		maxval = maxval1;
    		end
	end

	if bigi > 0
		vals(1:bigj,bigi:size(net,1)) = 0;
	end

	bigi
	bigj
	bigj-bigi+1
	maxval

	winners(s,:) = [bigi bigj bigj-bigi+1];
end

save modules.txt winners -ascii

%for i=1:20
%    vari = cluster(winners(i,1):winners(i,2));
%    filename = ['module_key' num2str(i) '.txt'];
%    save(filename, 'vari', '-ascii');
%end


























