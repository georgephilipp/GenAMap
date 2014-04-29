function [U,S,V]=svdmaj(A, lambda, varargin)
opt=propertylist2struct(varargin{:});
opt=set_defaults(opt, 'kinit', 10, 'kstep', 5);

MM=min(size(A));

if max(size(A))<500 || opt.kinit==MM
  [U,S,V]=svd(A);
else

  mm = inf;

  fprintf('[svdmaj]\n');

  kk=opt.kinit-opt.kstep;
  while mm>lambda && kk<MM
    kk=min(kk+opt.kstep, MM);
    fprintf('kk=%d\n',kk);
    [U,S,V]=lansvd(A, kk);
    mm=min(diag(S));
  end
end