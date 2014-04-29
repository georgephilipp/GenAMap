function [X Y Z X_test Y_test Z_test] = generate_simulatedData(n, p, n_test, logistic)

noiseMult=0.5;
X = randn(n,p);
X_test = randn(n_test,p);
X(:,2) = X(:,10);
X(:,22) = X(:,33);
X_test(:,2) = X_test(:,10);
X_test(:,22) = X_test(:,33);
X(:,15) = 1;
X_test(:,15)=1;

Z = round(rand(n,1))+1;
Z_test = round(rand(n_test,1))+1;
Y=zeros(n,1);

w = zeros(p,2);
w(1:5,1) = 1;
w(1:4,2) = 1;
w(6,2) = 1;

Y_test = zeros(n_test,1);
for i=1:2
    Y(Z==i) = X(Z==i,:) * w(:,i) + noiseMult*randn(sum(Z==i),1);
    Y_test(Z_test==i) = X_test(Z_test==i,:) * w(:,i) + noiseMult*randn(sum(Z_test==i),1);
    if(logistic)
        Ymean = mean(Y(Z==i));
        Y(Z==i) = sign(Y(Z==i)-Ymean);
        Ymean = mean(Y_test(Z_test==i));
        Y_test(Z_test==i) = sign(Y_test(Z_test==i) - Ymean);
    end
end


end
