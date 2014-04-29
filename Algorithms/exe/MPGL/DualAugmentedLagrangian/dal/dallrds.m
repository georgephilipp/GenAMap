% dallrds - DAL with logistic loss and the dual spectral norm
%           (trace norm) regularization
%
% Overview:
%  Solves the optimization problem:
%   ww = argmin sum(log(1+exp(-yy.*(A*w+b)))) + lambda*||w||_DS
%
%   where ||w||_DS = sum(svd(w)) 
%
% Syntax:
%  [ww,bias,status]=dallrds(ww, bias, A, yy, lambda, <opt>)
%
% Inputs:
%  ww     : initial solution ([nn,1])
%  A      : the design matrix A ([mm,nn]) or a cell array {fA, fAT, mm, nn}
%           where fA and fAT are function handles to the functions that
%           return A*x and A'*x, respectively, and mm and nn are the
%           numbers of rows and columns of A.
%  yy     : the target label vector (-1 or +1) ([mm,1])
%  lambda : the regularization constant
%  <opt>  : list of 'fieldname1', value1, 'filedname2', value2, ...
%   stopcond : stopping condition, which can be
%              'pdg'  : Use relative primal dual gap (default)
%              'fval' : Use the objective function value
%           (see dal.m for other options)
% Outputs:
%  ww     : the final solution ([nn,1])
%  status : various status values
%
% Copyright(c) 2009 Ryota Tomioka
% This software is distributed under the MIT license. See license.txt

function [ww,bias,status]=dallrds(ww, bias, A, yy, lambda, varargin)

opt=propertylist2struct(varargin{:});
opt=set_defaults(opt,'solver','cg',...
                     'stopcond','pdg',...
                     'blks',[]);


if isempty(opt.blks)
  opt.blks=size(ww);
  ww = ww(:);
end

prob.floss    = struct('p',@loss_lrp,'d',@loss_lrd,'args',{{yy}});
prob.fspec    = @(ww)ds_spec(ww,opt.blks);
prob.dnorm    = @(ww)ds_dnorm(ww,opt.blks);
prob.obj      = @objdalds;
prob.softth   = @ds_softth;
prob.stopcond = opt.stopcond;
prob.ll       = min(0,yy);
prob.uu       = max(0,yy);
prob.Ac       =[];
prob.bc       =[];
prob.info     = struct('blks',opt.blks,'nsv',5*ones(1,size(opt.blks,1)));

if isequal(opt.solver,'cg')
  prob.hessMult = @hessMultdalds;
end

if isnumeric(A)
  A = A(:,:);
  [mm,nn]=size(A);
  At=A';
  fAT = @(x)At*x;
  fA  = @(x)A*x;
  clear At;
elseif iscell(A)
  fA  = A{1};
  fAT = A{2};
  mm = A{3};
  nn = A{4};
else
  error('A must be either numeric or cell {@(x)A*x, @(y)(A''*y), mm, nn}');
end
prob.mm       = mm;
prob.nn       = nn;

if isempty(bias)
  B = [];
else
  B = ones(mm,1);
end

[ww,bias,status]=dal(prob,ww,bias,fA,fAT,B,lambda,opt);



