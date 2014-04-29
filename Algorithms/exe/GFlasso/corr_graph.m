function G=corr_graph(Y, R,gamma)

% G with size (E,K)

% correlations
%R=corr(Y);

% size
K=size(Y,2);

% preset size for E
E_max=2*K;

G=zeros(E_max,K);
row_cnt=0;

for i=1:K-1 
    for j=i+1:K
        if abs(R(i,j))>=0%thrs
            row_cnt=row_cnt+1;
            r=R(i,j);
            G(row_cnt,i)=gamma*r;
            G(row_cnt,j)=-gamma*sign(r)*r;
            if row_cnt==E_max
                G=[G; zeros(2*K,K)];
                E_max=E_max+2*K;
            end
        end
    end
end

G(row_cnt+1:E_max,:)=[];
end
