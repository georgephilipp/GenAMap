function [] =  print_nonzeroBeta(predictedBeta, vocab, out_file_f)

fID = fopen(out_file_f,'w');
%fprintf('Non-zero betas:\n');
k=size(predictedBeta,1);

for sp=1:k
    %fprintf('Sub-population: %d\n', sp);
    fprintf(fID,'\n');
    [sortedbeta,ix] = sort(predictedBeta(sp,:));
    if(size(vocab,1) > 0)
	%fprintf('Feature-name\t Beta-value\n');
    else
	%fprintf('Feature-index\t Beta-value\n');
    end
    for i=1:length(sortedbeta)
        if ( sortedbeta(i) ~= 0 )
            if(size(vocab,1) > ix(i))
                fprintf(fID,'%s\t %e\n', cell2mat(vocab(ix(i))), sortedbeta(i) );
            else
                fprintf(fID,'%d\t %e\n', ix(i), sortedbeta(i));
            end
        end;
    end
end
fclose(fID);
end
