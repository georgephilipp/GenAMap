

function [X, Y, Z, X_test, Y_test, Z_test, vocab] = readData(dataroot)

iteration =1;
%baseFileName = '../splits-02-dense/Amazon-4domains-domainsubpop.xml_SimpleExactRandomSplitter_TRAINFRACTION_0.8_RANDOMSEED_1_train_partition_0.transpose.dat';
baseFileName = sprintf('../%s/train.transpose.dat', dataroot);

if(iteration ~= -1)
	x_fileName = sprintf('%s.TRANSPOSE.%d', baseFileName, iteration);
else
	x_fileName = baseFileName;
end
y_fileName = strrep(baseFileName, 'transpose', 'class');
z_fileName = strrep(baseFileName, 'transpose', 'cluster');

%% LOAD X
% Load X
X = load(x_fileName);
X = X'; % the data is stored features by samples, find its transpose.

% Load X_test
testFileName = strrep(x_fileName, 'train', 'test');
X_test = load(testFileName);
X_test = X_test';


%% LOAD Y
% Load Y and make it (-1,1)
Y = load(y_fileName);
y_val = unique(Y);
assert(length(y_val)==2)
Y_new = zeros(size(Y));
Y_new(Y == y_val(1)) = 1;
Y_new(Y == y_val(2)) = -1;
Y = Y_new;

% Load Y_test and map it to the same space as Y
Y_test = load(strrep(y_fileName, 'train','test'));
Y_new = zeros(size(Y_test));
Y_new(Y_test == y_val(1)) = 1;
Y_new(Y_test == y_val(2)) = -1;
Y_test = Y_new;

%% LOAD Z
% Load Z and make unique values of Z go from 1 to k
Z = load(z_fileName);
pop = unique(Z);
k = length(pop);
Z_new = zeros(size(Z));
for ii=1:k
    Z_new(Z==pop(ii)) = ii;
end
Z=Z_new;

% Load Z_test and make it match the mapping assigned to Z
Z_test = load(strrep(z_fileName, 'train','test'));
Z_new = zeros(size(Z_test));
for i=1:k
    Z_new(Z_test==pop(i)) = i;
end
Z_test = Z_new;


%% LOAD VOCABULARY
% load feature vocab just once
fprintf('Loading vocab... ');
[status, fname] = unix(sprintf('ls %s/*.vocab', dataroot));
fname = strcat(fname);
if(~status)
    vocab = importdata(strcat(fname));
    fprintf('DONE\n');
else
    fprintf(' Vocabulary not found\n');
    vocab=[];
end

end




