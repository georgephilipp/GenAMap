function [XX YY ZZ groups] = convert(X,Y,Z,k)
if(nargin==4)
    p = size(X,2);
    XX = zeros(size(X,1), k*p);
    YY = zeros(length(Y),1);
    current=1;
    groups=zeros(k*p,1);
    for i=1:k
        l = sum(Z==i);
        XX(current:(current+l-1), i+( 0 : k : ((p-1)*k) ) ) = X(Z==i,:);
        YY(current:(current+l-1)) = Y(Z==i);
        ZZ(current:(current+l-1)) = i;
	groups(i+( 0 : k : ((p-1)*k) ) ) = 1:p;
        current = current+l;
    end
else
    %% convert beta of size k*p stored in X, to a vector
    p = size(X,2);
    k = size(X,1);
    XX = zeros(k*p, 1);
    for i=1:k
        XX(i+( 0 : k : ((p-1)*k) )) = X(i, :);
    end

end
