function [Beta, obj, time, iter] = accgrad ( Y, X, lambda, gamma, H, XX, XY, L, mu, option)

%Y Centered Matrix: N by K
%X Centered Matrix: N by J(p)
%lam: lambda
%gamma: gamma
%mu: mu in nesterov paper


    [J] = size(X,2);
    [K] = size(Y,2);
    
    if isfield(option,'maxiter')
        maxiter=option.maxiter;
    else
        maxiter=5000;
    end
    
    if isfield(option, 'tol')
        tol=option.tol;
    else
        tol=1e-7;
    end
    
    if isfield(option, 'threshold')
        threshold=option.threshold;
    else
        threshold=1e-10;
    end  
    
    if isfield(option, 'verbose')
        verbose=option.verbose;
    else
        %verbose=true;
        verbose=false;
    end  

    obj=zeros(1,maxiter);
    time=zeros(1,maxiter);

    %C=[eye(K)*lambda,gamma*H];
    C=gamma*H;
    %CT=C';
    theta=1;
    w=zeros(J,K);
    b=w;
    
    warning_flag=false;
    
    tic
    for iter=1:maxiter
        %compute grad(f(x_k))
        R=shrink(w*C/mu,1);
        
        grad_w=XX*w-XY+R*(C');
        
        v=w-grad_w/L;
        
        b_new=soft_threshold(v, lambda/L);
	fprintf('%d\n', lambda/L);
        
        theta_new=2/(iter+2);
        
        w=b_new+(1-theta)/theta*(theta_new)*(b_new-b);
        
        obj(iter)= sum(sum((Y-X*b_new).^2))/2+lambda*sum(abs(b_new(:)))+sum(sum(abs(b_new*C)));
        
        b=b_new;
        theta=theta_new;
        time(iter)=toc;
        
        if (verbose && (iter==1 || mod(iter,1)==0))
            fprintf('Iter %d: Obj: %g\n', iter, obj(iter));    
        end 
        
          
        if (iter>10 && abs(obj(iter)-obj(iter-1))<tol) %increasing
            break;
        end        
        
        if (iter>30 && (obj(iter)>obj(iter-3) && obj(iter)>obj(iter-2) &&obj(iter)>obj(iter-1)))
             %warning('There is increasing in obj, a smaller mu!');
             %break;
             warning_flag=true;
        end     
    end
    
    fprintf('In total: Iter: %d, Obj: %g\n', iter, obj(iter));
%    if (warning_flag)
%        warning('There is increasing in obj, a smaller mu!');
%    end
    
    b(abs(b)<threshold) =0;
    Beta=b;
    obj=obj(1:iter);
    time=time(1:iter);    
end
