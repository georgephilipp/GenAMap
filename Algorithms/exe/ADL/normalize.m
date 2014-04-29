function [ Y ] = normalize( X )
%NORMALIZE Summary of this function goes here
%   Detailed explanation goes here
I=size(X,2);
N=size(X,1);
Y=zeros(size(X,1),size(X,2));
for i=1:I
    x=X(:,i);
    norm_x=norm(x-mean(x));
    if norm_x==0
        Y(:,i)=x-mean(x);
    else
        Y(:,i)=(x-mean(x))/norm_x;
    end
end
end

