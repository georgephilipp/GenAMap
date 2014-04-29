%function [X, Y, XX, XY, maxd, L1] = pre_grad(X_orig, Y_orig, H)
function [X, Y] = pre_grad(X_orig, Y_orig)    

    [N] = size(X_orig, 1);
        
    Y = normalize(Y_orig);
    X = normalize(X_orig);
        
    %XX=X'*X;
    %XY=X'*Y;    
    
    %maxd=full(max(sum(H.^2,2)));  
    %L1=eigs(XX,1);
    
end

