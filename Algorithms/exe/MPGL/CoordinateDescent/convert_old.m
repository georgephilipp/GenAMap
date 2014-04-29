function [XX YY ZZ] = convert(X,Y,Z,k)
%% convert X, Y, Z into the correct format
if(nargin==4) 
	p = size(X,2);
	XX = zeros(size(X,1), k*p);
    YY = zeros(length(Y),1);
    current=1;
    for i=1:k
        l = sum(Z==i);
        XX(current:(current+l-1), ((i-1)*p+1):((i)*p)) = X(Z==i,:);
        YY(current:(current+l-1)) = Y(Z==i);
        ZZ(current:(current+l-1)) = i;
        current = current+l;
    end
else
    %% convert beta of size k*p stored in X, to a vector
    p = size(X,2);
    k = size(X,1);
    XX = zeros(k*p, 1);
    for i=1:k
        XX(((i-1)*p+1):((i)*p)) = X(i, :);
    end
end
