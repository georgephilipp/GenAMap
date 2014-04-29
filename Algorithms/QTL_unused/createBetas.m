J = 25;
K = 30;
N = 100;

for i=1:N
    for j = 1:J
        if(rand > .66)
            X(i,j) = 1;
        else
            X(i,j) = 0;
        end
    end
end

betas = zeros(J, K);

betas(2,:) = 2;
betas(10, 6:15) = 1;
betas(20, 15:30) = 4;

y = X * betas + randn(N,K);


        