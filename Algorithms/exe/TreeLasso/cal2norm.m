function s=cal2norm(A, g_idx)
         V=size(g_idx,1);
         s=0;
         for v=1:V
             idx=g_idx(v,1):g_idx(v,2);
             gnorm=sqrt(sum(A(idx,:).^2));
             s=s+sum(gnorm);
         end
end