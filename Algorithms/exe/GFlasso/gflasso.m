function [Beta,obj,time]=gflasso(Y,X,R,lambda,gamma,final_scale)

% a paremeter controls how accuracy changes.
% its behavior is complicated
tol_decrease=0.8;
final_scale = 5e-3;

% other constants

% remove small beta values, possibly due the smallest precision of the
% machine
threshold=1e-5;

maxiter=5000;

verbose=true;

% get the correlation graph
G=corr_graph(Y,R,gamma);

% normalization
X=normalize(X);
Y=normalize(Y);
XX=X'*X;
XY=X'*Y;


tol=sum(sum(Y.^2))/2;
final_tol=sum(sum(Y.^2))*final_scale;


J=size(X,2);
K=size(Y,2);
E=size(G,1);

dmax=max(sum(G.^2,1));
D=1/2*J*(K+E);
X_eig=eigs(XX,1);


obj=zeros(1,maxiter);
time=zeros(1,maxiter);


w=zeros(J,K);
b=w;


tic
iter=0;
theta=1;

%fprintf('dmax= %e\tD=%e\n',dmax,D)
derivative=1e20;


while tol>final_tol*tol_decrease
        
    mu=tol/D; % tol
    L=X_eig+dmax/mu; % tol leaving out the fusion terms
    
    
    
    ite_inner=0;
    
    
    w=b;
    
    ita=sqrt(tol);
    %    rough_ita=sqrt(L*final_tol);
    
    %    fprintf('tol= %d\t mu=%e\tL=%e\tita=%e\n',tol,mu,L,ita);
    %while iter<=10 || ite_inner<=10 || abs(obj(iter)-obj(iter-1))>mu
    %while iter<10 || abs(obj(iter)-obj(iter-1))>tol
    while iter<1 || derivative>ita
        
        %grad_norm>ita
        iter=iter+1;
        ite_inner=ite_inner+1;
        
        %compute grad(f(x_k))
        R=shrink(G*w'/mu,1); % the format has been changed
        
        grad_w=XX*w-XY+R'*G;
        clear R; 
        
        %        fprintf('gradiant_norm %e\tita=%e\n',grad_norm,ita)
        
        v=w-grad_w/L;
        
        % to change
        b_new=soft_threshold(v, lambda/L);
        
        theta_new=2/(iter+2);
        
        w=b_new+(1-theta)/theta*(theta_new)*(b_new-b);
        
        % to change
        obj(iter)= sum(sum((Y-X*b_new).^2))/2+lambda*sum(sum(abs(b_new)))+sum(sum(abs(G*b_new')));
        
        if(iter>1) derivative=abs((-obj(iter)+obj(iter-1)))/sqrt(sum(sum((b_new-b).^2))); end
        %        fprintf('derivative=%e\tita=%e\n',derivative,ita)
        
        %        if(iter>1 && derivative<rough_ita) break; end
        
        
        b=b_new;
        theta=theta_new;
        time(iter)=toc;
        
        if (verbose && (iter==1 || mod(iter,1)==0))
            fprintf('Iter %d: Obj: %g\n', iter, obj(iter));
        end
        
        %        fprintf('epsilon=%e ita=%e rough_ita%e derivative%e\n',tol,ita,rough_ita, derivative)
        
        
        if (iter>30 && (obj(iter)>obj(iter-3) && obj(iter)>obj(iter-2) &&obj(iter)>obj(iter-1)))
            %warning('There is increasing in obj, a smaller mu!');
            %break;
            warning_flag=true;
        end
        
        if iter>maxiter
            break;
        end
    end
    tol=tol*tol_decrease;
    
end


%    if (warning_flag)
%        warning('There is increasing in obj, a smaller mu!');
%    end

%    b(abs(b)<threshold) =0;
%fprintf('Iter %d: Obj: %g\n', iter, obj(iter));
fprintf('Later steps\n');
b(abs(b)<threshold) =0;
Beta=b;
obj=obj(1:iter);
time=time(1:iter);

Beta_indicator=Beta;
Beta_indicator(abs(Beta_indicator)>1e-6)=1;
Beta_indicator(abs(Beta_indicator)<1e-6)=0;
Beta_rereg=zeros(J,K);

for k=1:K
    X_re=X(:,logical(Beta_indicator(:,k)));
    Y_re=Y(:,k);
    b = pinv(X_re)*Y_re;
    
    j_ind=0;
    for j=1:J
        if Beta_indicator(j,k)==1
            j_ind=j_ind+1;
            Beta_rereg(j,k)=b(j_ind);
        end
    end
end

Beta=Beta_rereg;
fprintf('done.');
end


