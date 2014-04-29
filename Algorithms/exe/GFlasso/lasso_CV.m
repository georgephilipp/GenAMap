function [beta,lambda] = lasso_CV( X,Y,fold,final_scale)
%LASSO_CV Summary of this function goes here
%   Detailed explanation goes here
X=normalize(X);
Y=normalize(Y);

choices=0.01:0.01:1;
best_error=1e99;
best_l=-1;

N=size(X,1);
J=size(X,2);
K=size(Y,2);
size_subset=N/fold;

for lambda=choices
    er=zeros(1,fold);
    for f=1:fold
        selection=ones(1,N);
        if f==fold
            N_test=N-(fold-1)*size_subset;
            selection((fold-1)*size_subset+1:N)=0;
        else
            N_test=size_subset;
            selection((f-1)*size_subset+1:f*size_subset)=0;
        end
        
        X_r=X(logical(selection),:);
        Y_r=Y(logical(selection),:);
        
        X_cv=X(logical(1-selection),:);
        Y_cv=Y(logical(1-selection),:);
        X_cv=normalize(X_cv);
        Y_cv=normalize(Y_cv);
%         
%         Beta=zeros(size(X_r,2),size(Y_r,2));
%         for k=1:K
%             y=Y_r(:,k);
%             [b]=LassoShooting(X_r,y,lambda);
%             b_ind=b;
%             b_ind(abs(b_ind)>0.001)=1;
%             b_ind(abs(b_ind)<=0.001)=0;
%             X_rereg=X_r(:,logical(b_ind));
%             b_rereg=pinv(X_rereg'*X_rereg)*(X_rereg'*y);
%             
%             j_ind=0;
%             for j=1:J
%                 if b_ind(j,1)==1
%                     j_ind=j_ind+1;
%                     b(j,1)=b_rereg(j_ind);
%                 else
%                     b(j,1)=0;
%                 end
%             end
%             
%             Beta(:,k)=b;
%         end
        
        [Beta]=gflasso(X_r,Y_r,1,lambda,0,final_scale);
        er(1,f)=sqrt(sum(sum((Y_cv-X_cv*Beta).^2))/size(Y_cv,2));
    end
    er_mn=mean(er);
    er_st=std(er,1);
    fprintf('lambda=%g\ter_mn=%g\ter_st=%g\n',lambda,er_mn,er_st);
    if er_mn<best_error
        best_l=lambda;
        best_error=er_mn;
    end
end

fprintf('for the best value:\nlambda=%g\ter_mn=%g\n',best_l,best_error);

lambda=best_l;
end

