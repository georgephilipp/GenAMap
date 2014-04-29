% objdalds - objective function of DAL with the dual spectral norm
%            (trace norm) regularization
%
% Copyright(c) 2009 Ryota Tomioka
% This software is distributed under the MIT license. See license.txt

function varargout=objdalds(aa, info, prob, ww, uu, A, AT, B, lambda, eta)

m = length(aa);
vv = ww/eta+AT(aa);

if nargout<=3
  [floss, gloss, hmin]=prob.floss.d(aa, prob.floss.args{:});
else
  [floss, gloss, hloss, hmin]=prob.floss.d(aa, prob.floss.args{:});
end

if isinf(floss)
  tmp={fval, nan*ones(size(aa)), []};
  varargout(1:end-1)=tmp(1:nargout-1);
  varargout{end}=info;
  return;
end

[vsth, ss, info] = prob.softth(vv,lambda,info);


fval = floss+0.5*eta*sum(ss.^2);

if ~isempty(uu)
  u1   = uu/eta+B'*aa;
  fval = fval + 0.5*eta*sum(u1.^2);
end


varargout{1}=fval;

if nargout<=2
  varargout{2} = info;
else
  gg  = gloss+eta*(A(vsth));
  soc = sum((vsth-ww/eta).^2);
  if ~isempty(uu)
    gg  = gg+eta*B*u1;
    soc = soc+sum((B'*aa).^2);
  end
  
  info.ginfo = norm(gg)/sqrt(eta*hmin*soc);
  varargout{2} = gg;
  
  if nargout==3
    varargout{3} = info;
  else
    switch(info.solver)
     case 'cg'
      prec=hloss;
      varargout{3} = struct('hloss',hloss,'lambda',lambda,'info',info,'prec',prec,'B',B);
     otherwise
      error('currently only ''qn'' or ''cg'' is supported');
    end
    varargout{4} = info;
  end
end

