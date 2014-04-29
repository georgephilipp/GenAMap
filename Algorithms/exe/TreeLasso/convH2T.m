function [T Tw] = convH2T(H, w_max)
% input: matlab hierarchical clustering results


K = size(H,1)+1;

Nd = [reshape(ones(2,1)*[K+1:2*K-1],1,(K-1)*2)', reshape(H(:,1:2)', 1, (K-1)*2)']; 

W_norm = H(:,3)/max(H(:,3));

%[H(:,3), W_norm]

[T Tw] = convNd2T(Nd, W_norm, w_max);
