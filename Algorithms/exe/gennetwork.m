function [H, E, Ecoef, Esign, C]=gennetwork(Y, option)
%H is the V*E incident matrix
%E is E by 2 matrix E(i,1) E(i,2) forms an edge
%C is the V*V correlation matrix;

          if isfield(option,'cortype')
              cortype=option.cortype;  %cortype=1 abs;  cortype=2 ^2
          else
              cortype=2;
          end
          
          if isfield(option,'corthreshold')
              corthreshold=option.corthreshold;  %cortype=1 abs;  cortype=2 ^2
          else
              corthreshold=0.5;
          end   
          
          [nV]=size(Y,2);
          
          C=corrcoef(Y);
          C(abs(C)<corthreshold)=0;
          
          UC=triu(C,1); %upper triangluar of C
          if (cortype==1)
              W=abs(UC);
          else
              W=UC.^2;
          end
          
          nzUC=find(UC~=0);
          [E1,E2]=ind2sub([nV,nV],nzUC);
          E=[E1,E2];
          
          nE=size(E,1);
          Ecoef=W(nzUC);
          Esign=sign(C(nzUC));
          
          H_I=[E1;E2];
          H_J=[(1:nE)';(1:nE)'];
          H_S=[Ecoef, -Ecoef.*Esign];
          H=sparse(H_I, H_J, H_S, nV, nE);
          
end
