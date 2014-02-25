%clear all
%close all
rng     = RandStream.getDefaultStream;
rng.reset(1);  % random seed

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% global parameters
N = 250; % sample size of training set
M = 125; % sample size of validation set
P = 100;  % or 100, number of SNPs
J = 500; % number of genes
K = 20;  % number of traits
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% simulate the genotype matrix X
X = zeros(N+M, P);

for(i=1:N+M)
    for(j=1:P)
        x = rand(1);
        if(x > .33 && x < .66)
            X(i,j) = 1;
        elseif(x > .66)
            X(i,j) = 2;
        end
    end
end

train_X = X(1:N, :);
valid_X = X(N+1:M, :);


%There are 9043 SNPs with greater than 10% MAF. We'll use these
geno = braingenotype(1:250,:);
geno = geno(:,find(sum(geno) > 25));
geno = geno(:, find(sum(geno) < 225));

train_X = geno(:,round(rand(1,100)*9043));

save('final_X.txt', 'train_X', '-ascii', '-tabs')
save('valid_Xn.txt', 'valid_X', '-ascii', '-tabs')
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
C_1 = 50;   % number of disconnected components in genes
k_1 = 3;    % number of causal SNPs for each gene component 
b_1 = 1;    % the value of coefficients in B_1

% simulate B_1 matrix
B_1 = zeros(P,J);
for i=1:C_1
    rperm = randperm(P);
    % each component has k_1 (different) causal SNPs
    B_1( rperm(1:k_1) , (i-1)*(J/C_1)+1:i*(J/C_1) ) = b_1;
end


%% add one causal SNP for first half groups of genes
%rperm = randperm(P);
%B_1(rperm(1), 1:J/2) = b_1;

% add one housekeeping SNP
rperm = randperm(P);
B_1(rperm(1), :) = b_1;
save B_1final.txt B_1 -ascii;

% view B_1 matrix
figure
imagesc(B_1)
%axis square
colormap(gray)
colorbar
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

X=train_X;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% generate gene expression matrix Y and gene network topology genenet
Y           = X * B_1 + randn(N+M, J)/.5;
train_Y     = Y(1:N, :);
valid_Y     = Y(N+1:M, :);
rho         = 0.7;    % threshold of correlation matrix
genenet     = corrcoef(train_Y);
genenet     = (genenet + genenet')/2;
% IMPORTANT: set the diagonal of genenet to zeros
genenet     = genenet - eye(J);
I           = (genenet <= rho);
genenet(I)  = 0;

%% check the number of components in genenet
%D           = diag(sum(genenet,2));
%min(eig(D-genenet))
%rank(D-genenet)

figure
set(gcf, 'Position', [214 775 1026 549]);
set(gcf, 'Color', 'w');
ax(1) = subplot(1,2,1);
imagesc(corrcoef(Y));
colormap(gray);
axis square;
ax(2) = subplot(1,2,2);
imagesc(genenet);
colormap(gray);
axis square;
h = colorbar('location','SouthOutside');
set(h, 'Position', [.1314 .13 .7681 .0350]);
axes(ax(1));
set(ax(1), 'Position', [.1584 0.1100 0.3347 0.8150]);
set(ax(2), 'Position', [0.5603 0.1100 0.3347 0.8150]);
%export_fig(gcf, 'genenet.pdf');

save('Y_finaln4.txt', 'train_Y', '-ascii', '-tabs');
save('valid_Y.txt', 'valid_Y', '-ascii', '-tabs');
save('genenet.txt', 'genenet', '-ascii', '-tabs');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
C_2  = 5;   % number of groups of correlated traits
k_2  = 3;   % number of causal gene GROUPS for each group of trait
b_2  = 1;   % the value of coefficients in B_2
%b_2  = 0.8;   % the value of coefficients in B_2

% simulate B_2 matrix
B_2  = zeros(J,K);
for i=1:C_2
    rperm = randperm(C_1);
    rowindex = [];
    for j=1:k_2
        rowindex = [rowindex (rperm(j)-1)*(J/C_1)+1:rperm(j)*(J/C_1) ];
    end
    B_2(  rowindex, (i-1)*(K/C_2)+1:i*(K/C_2) ) = b_2;
end

%% add one housekeeping GROUP of genes
%rperm = randperm(C_1);
%B_2( (rperm(1)-1)*(J/C_1)+1:rperm(1)*(J/C_1), :) = b_2;

% view B_2 matrix
figure
imagesc(B_2)
%axis square
colormap(gray)
colorbar
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

save B_2final.txt B_2 -ascii

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% generate trait matrix Z and trait network traitnet
Z           = Y * B_2 + randn(N+M,K)*4;
train_Z     = Z(1:N, :);
valid_Z     = Z(N+1:M, :);
rho         = 0.7;    % threshold of correlation matrix
traitnet    = corrcoef(train_Z);
traitnet    = (traitnet + traitnet')/2;
% IMPORTANT: set the diagonal of traitnet to zeros
traitnet    = traitnet - eye(K);
I           = (traitnet <=rho);
traitnet(I) = 0;

%% check the number of components in traitnet
%D           = diag(sum(traitnet,2));
%min(eig(D-traitnet))
%rank(D-traitnet)

figure
set(gcf, 'Position', [214 775 1026 549]);
set(gcf, 'Color', 'w');
ax(1) = subplot(1,2,1);
imagesc(corrcoef(Z));
colormap(gray);
axis square;
ax(2) = subplot(1,2,2);
imagesc(traitnet);
colormap(gray);
axis square;
h = colorbar('location','SouthOutside');
set(h, 'Position', [.1314 .13 .7681 .0350]);
axes(ax(1));
set(ax(1), 'Position', [.1584 0.1100 0.3347 0.8150]);
set(ax(2), 'Position', [0.5603 0.1100 0.3347 0.8150]);
%export_fig(gcf, 'traitnet.pdf');

save('Z_finaln16.txt', 'train_Z', '-ascii', '-tabs');
save('valid_Z.txt', 'valid_Z', '-ascii', '-tabs');
save('traitnet.txt', 'traitnet', '-ascii', '-tabs');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

amyb3 = zeros(100,20);
for i=1:100
    for j=1:500
        for(k=1:20)
            if(abs(B_1(i,j)) > 1e-4)
                if(abs(B_2(j,k)) > 1e-4)
                    amyb3(i,k) = 1;
                end
            end
        end
    end
end
save('B_3final.txt', 'amyb3', '-ascii');
imagesc(1-amyb3);



size(find(a == 1 & a == B_1))
size(find(a == 1 & a ~= B_1))
size(find(a == 0 & a ~= B_1))

size(find(b2 == 1 & b2 == B_2))
size(find(b2 == 1 & b2 ~= B_2))
size(find(b2 == 0 & b2 ~= B_2))

b3 = zeros(100,20);
for i=1:100
    for j=1:500
        for(k=1:20)
            if(abs(a(i,j)) > 1e-2)
                if(abs(b2(j,k)) > 1e-2)
                    b3(i,k) = 1;
                end
            end
        end
    end
end
imagesc(b3);
size(find(b3 == 1 & b3 == B_3))
size(find(b3 == 1 & b3 ~= B_3))
size(find(b3 == 0 & b3 ~= B_3))

