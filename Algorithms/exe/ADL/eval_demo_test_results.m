function [recall, precision] = eval_demo_test_results(true_beta,output_beta)

true_found=0;
for i=1:size(true_beta,1)
    for j=1:size(true_beta,2)
        if true_beta(i,j)>0 && output_beta(i,j)>0
	    true_found=true_found+1;
	end
    end
end


if nnz(true_beta)>0
    recall=true_found/nnz(true_beta);
else
    recall=0;
end

if nnz(output_beta)>0
    precision=true_found/nnz(output_beta);
else
    precision=0;
end






