function [Beta, obj, time, iter] = accgrad( Y, X, lambda, T, XY, C, g_idx, L, mu, option)

%Y Centered Matrix: N by K
%X Centered Matrix: N by J(p)
%lam: lambda
%T: sparse matrix: group info. rows: number of group, cols: number of tasks
%Tw: n_group by 1: weight for each group
%C: note \sum_|g| by K
%g_idx: n_group by 2, group index
%L1, Lipschitz cond
%TauNorm: \|\Tau\|_1,2^2 
%mu: mu in nesterov paper
%maxiter

    [J] = size(X,2);
    [K] = size(T,2);
    
    if isfield(option,'maxiter')
        maxiter=option.maxiter;
    else
        maxiter=1000;
    end
    
    if isfield(option, 'tol')
        tol=option.tol;
    else
        tol=1e-7;
    end
    
    if isfield(option, 'threshold')
        threshold=option.threshold;
    else
        threshold=1e-4;
    end  
    
    if isfield(option, 'verbose')
        verbose=option.verbose;
    else
        verbose=true;
    end  

    obj=zeros(1,maxiter);
    time=zeros(1,maxiter);

    C=C*lambda;
    
    bw=zeros(J,K);    
    bx=bw;    
    theta=1;
    tic
    for iter=1:maxiter
        %compute grad(f(w_k))
        R=shrink(sparse(C*bw')/mu, g_idx);
                
        grad_bw=X'*(X*bw)-XY+R'*C;
        
        bv=bw-1/L*grad_bw;
        
        bx_new=sign(bv).*max(0,abs(bv)-lambda/L);
                
        obj(iter)= sum(sum((Y-X*bx_new).^2))/2+cal2norm(C*bx_new', g_idx);
        
        theta_new=2/(iter+2);
        
        bw=bx_new+(1-theta)/theta*theta_new*(bx_new-bx);
        
        time(iter)=toc;
        
        if (verbose && (iter==1 || mod(iter,1)==0))
            fprintf('Iter %d: Obj: %g\n', iter, obj(iter));    
        end         
         
        theta=theta_new;
        bx=bx_new;
        
        if (iter>10 && (abs(obj(iter)-obj(iter-1))/abs(obj(iter-1))<tol)) %increasing
            break;
        end        
            
    end
    
    fprintf('In total: Iter: %d, Obj: %g\n', iter, obj(iter));
    
    bx(abs(bx)<threshold) =0;
    Beta=bx;
    obj=obj(1:iter);
    time=time(1:iter);
    
end