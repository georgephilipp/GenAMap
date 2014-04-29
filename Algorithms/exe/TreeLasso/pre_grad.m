function [X, Y, XY, C, g_idx, TauNorm, L1] = pre_grad(X_orig, Y_orig, T, Tw)
    
    [N] = size(X_orig, 1);
    [V K] = size(T);
        
%    Y = (Y_orig-ones(N,1)*mean(Y_orig,1));
%    X = (X_orig-ones(N,1)*mean(X_orig,1));
   X = X_orig;
   Y = Y_orig;
        
    sum_col_T=full(sum(T,2));
    SV=sum(sum_col_T);
    csum=cumsum(sum_col_T);
    g_idx=[[1;csum(1:end-1)+1], csum, sum_col_T]; %each row is the range of the group
    
    J=zeros(SV,1);
    W=zeros(SV,1);
    for v=1:V
       J(g_idx(v,1):g_idx(v,2))=find(T(v,:));
       W(g_idx(v,1):g_idx(v,2))=Tw(v);
    end 

    C=sparse(1:SV, J, W, SV, K);
    
    %XX=X'*X;
    XY=X'*Y;
    
    TauNorm=repmat(Tw, 1, K).*T;
    TauNorm=max(sum(TauNorm.^2));  
    
    L1=eigs(X'*X,1);
    
end

