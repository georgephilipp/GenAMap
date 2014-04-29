function x = standardize(x)

mean_list  = mean(x);

for i = 1:size(x,2)
    x(:,i) = x(:,i) - mean_list(i);
    s = sqrt(sum(x(:,i).^2));
    if s~=0
        x(:,i)  = x(:,i)/s;
    end
end
