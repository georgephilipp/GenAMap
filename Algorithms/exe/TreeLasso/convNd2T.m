function [T Tw] = convNd2T(Nd, w, w_max)
% Nd : node list
% w : a vector of weights for internal nodes
% T : VxK matrix
%	V is the number of leaf nodes and internal nodes
%	K is the number of tasks
%	Element (v,k) is set to 1 if task k has a membership to
%	the cluster represented by node v. Otherwise, it's 0.
% Tw : V vector


% # of leaf nodes
K = Nd(1,1)-1;	
%V = Nd(size(Nd,1),1);
%V = Nd(size(Nd,1),1)-1;		% without the root
V = find(w<w_max, 1,'last')+K; 	% only the internal nodes with w<w_max

% for leaf nodes
I = [1:K];
J = [1:K];

Tw = ones(V,1);

% for internal nodes
for i=K+1:V
	Jt = [];

	Tw(i) = Tw(i)*(1-w(i-K));
	[Jt Tw] = find_leaves(Nd, find(Nd(:,1)==i), K, Jt, w(i-K), Tw);

	I = [I ones(1,length(Jt))*i];
	J = [J Jt];
end

T = sparse(I, J, ones(1,length(I)), V, K);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [Jt Tw] = find_leaves(Nd, ch, K, Jt, w, Tw);

for i=1:length(ch)
	if Nd(ch(i),2)>K
		[Jt Tw] = find_leaves(Nd, find(Nd(:,1)==Nd(ch(i),2)), K, Jt, w, Tw);
	else
		Jt = [Jt Nd(ch(i),2)];
	end
end

%pr = Nd(ch(1),1);
%Tw(pr) = Tw(pr)*(w);
Tw(Nd(ch,2)) = Tw(Nd(ch,2))*w;
%Tw
