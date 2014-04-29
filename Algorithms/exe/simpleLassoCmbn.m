%mask = 'GTA...'

i=1;
betas = [];
while(true)
	fi = [mask '_betas' num2str(i) '.txt'];
	if(~exist(fi, 'file'))
		break;
	end

	dat = load(fi);
	dat = dat';
	betas = [betas dat];
	i = i + 1;
end

size(betas)
betas = betas + randn(size(betas))/1000;


if(max(max(betas > 50)) | min(min(betas < -50)))
	for(i=1:size(betas,1))
		for(j=1:size(betas,2))
			betas(i,j) = randn(1,1);
		end
	end
end

load 'traitgroups.txt';
load groups.txt;

[sgenes ogenes] = sort(groups);
cntrG = -1;
if(size(betas,1) > 500)
        issame = 0;
        curgrp = 0;
        cntrG = round(size(betas,1)/2);
        while(issame == 0 && cntrG < size(betas,1))
                if(curgrp == 0)
                        curgrp = sgenes(cntrG);
                else
                        if(sgenes(cntrG) ~= curgrp)
                                issame = 1;
                        end
                end
                cntrG = cntrG + 1;
        end
	cntrG = cntrG - 1;
end


for(i=1:size(traitgroups,1))
	ix = traitgroups(i,:);
	ix = ix(find(ix ~= 0));
	b = betas(:,ix);
	if(cntrG == -1)
		fi = ['initB_' num2str(i)];

		save(fi, 'b', '-ascii');
	else
		b = b(ogenes,:);
		fi = ['initB_' num2str(i) 'a'];
		b2 = b(1:cntrG-1,:);
		save(fi, 'b2', '-ascii');

		fi = ['initB_' num2str(i) 'b'];
		b2 = b(cntrG:size(b,1), :);
		save(fi, 'b2', '-ascii');
	end
end

