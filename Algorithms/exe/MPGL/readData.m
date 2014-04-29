

function [X, Y, Z, vocab] = readData(x_fileName,y_fileName,z_fileName,v_filename)

%baseFileName = dataroot;

%x_fileName = sprintf('%s.train.X', baseFileName);
%y_fileName = sprintf('%s.train.Y', baseFileName);
%z_fileName = sprintf('%s.train.Z', baseFileName);

%% LOAD X
% Load X
X = load(x_fileName);

% Load X_test
% testFileName = strrep(x_fileName, 'train', 'test');
% X_test = load(testFileName);

%% LOAD Y
Y = load(y_fileName);
y_val = unique(Y);

% If Y is binary, map it to {-1,1}
if(length(y_val)==2)
	Y_new = zeros(size(Y));
	Y_new(Y == y_val(1)) = 1;
	Y_new(Y == y_val(2)) = -1;
	Y = Y_new;
end

% Load Y_test and map it to the same space as Y
% Y_test = load(strrep(y_fileName, 'train','test'));
% if(length(y_val)==2)
% 	Y_new = zeros(size(Y_test));
% 	Y_new(Y_test == y_val(1)) = 1;
% 	Y_new(Y_test == y_val(2)) = -1;
% 	Y_test = Y_new;
% end

%% LOAD Z
% Load Z and make unique values of Z go from 1 to k
Z = load(z_fileName, '-ascii');
pop = unique(Z);
k = length(pop);
Z_new = zeros(size(Z));
for ii=1:k
    Z_new(Z==pop(ii)) = ii;
end
Z=Z_new;

% Load Z_test and make it match the mapping assigned to Z
% Z_test = load(strrep(z_fileName, 'train','test'), '-ascii');
% Z_new = zeros(size(Z_test));
% for i=1:k
%     Z_new(Z_test==pop(i)) = i;
% end
% Z_test = Z_new;


%% LOAD VOCABULARY
% load feature vocab just once
fprintf('Loading vocab... ');
%status = exist(sprintf('%s.vocab',baseFileName), 'file');
%status = true;
%if(status)
    vocab = load(v_filename, '-ascii');
 %importdata(v_filename);
   % fprintf('Vocabulary found of size %d\n', length(vocab));
%else
 %   fprintf(' Vocabulary not found\n');
  %  vocab=[];
%end

end




