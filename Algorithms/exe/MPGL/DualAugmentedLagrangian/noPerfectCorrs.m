function remove = noPerfectCorrs(X, Z, nogroups, remove)

	size(X,2)
%	for(i=1:1:nogroups)
%		remove(var(X(Z==i,:)) < 1e-3) = 1;
%	end

        rmd = 0;
        for(i=1:1:nogroups)
		%s = X(Z==i,:);
                %a = corrcoef(s);

                for(j=1:size(X,2))
			if(remove(j) == 0)
                        	for(k=j+1:size(X,2))
					if(remove(k) ~= 1)
						a = corrcoef(X(Z==i,j), X(Z==i,k));
                               			if(abs(a) > .98)
                                        		remove(k) = 1;
                                        		rmd = rmd + 1;
						end
					end
                                end
                        end
                end
        end

