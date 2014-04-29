function [ output ] = collapseIDs( input )
%COLLAPSEIDS Summary of this function goes here
%   Detailed explanation goes here
ids = unique(input);
output = zeros(size(input));
for i=1:numel(ids)
    output(input == ids(i)) = i;
end


end

