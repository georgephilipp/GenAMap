function [ newAssocMat, mergeLog ] = reduceAssociationMatrix( assocMat, mode )
%MERGE Summary of this function goes here
%   Detailed explanation goes here

%compute initial score matrix
d = size(assocMat, 1);
t = size(assocMat, 2);
disc = assocMat;
disc(disc > 0) = 1;
normalized = assocMat;
rowsums = sum(normalized, 2);
rowsums(rowsums == 0) = 1;
normalized = normalized ./ repmat(rowsums, 1, size(normalized,2));
scores = normalized*disc';
scores(sub2ind(size(scores), 1:d, 1:d)) = 0;


if strcmp(mode, 'conncomp')
    graph = sparse(d,d);
    graph(scores > 0.5 & scores' > 0.5) = 1;
    [total, connraw] = graphconncomp(graph);
    newAssocMat = zeros(total, t);
    for i=1:d
        newAssocMat(connraw(i),:) = max(newAssocMat(connraw(i)), assocMat(i,:));
    end

    %this stuff is purely for debugging
    maxgroup = 0;
    for i=1:total
        groupmembers = numel(find(connraw == i));
        if maxgroup < groupmembers
            maxgroup = groupmembers;
        end
    end
    mergeLog = zeros(total, maxgroup);
    for i=1:total
        groupmembers = find(connraw == i);
        mergeLog(i,1:numel(groupmembers)) = groupmembers;
    end
elseif strcmp(mode, 'iterative')
    %iterate
    eliminated = [];
    mergeLog = [];
    cursor = 1;
    [m1, m2] = find(scores > 0.5 & scores' > 0.5, 1, 'first');
    while(numel(m1) > 0)
        mergeLog = [mergeLog; [m1 m2 full(scores(m1, m2)) full(scores(m2,m1))]];
        cursor
        cursor = cursor + 1;
        %merge rows
        newrow = max(assocMat(m1,:), assocMat(m2,:));
        assocMat(m1,:) = newrow;
        assocMat(m2,:) = 0;
        %update caches
        eliminated = [eliminated; m2];
        newrowdisc = max(disc(m1,:),disc(m2,:));
        disc(m1,:) = newrowdisc;
        disc(m2,:) = 0;
        newrownorm = newrow / sum(newrow);
        normalized(m1,:) = newrownorm;
        normalized(m2,:) = 0;
        %recompute graph
        newscore1 = newrownorm*disc';
        newscore2 = newrowdisc*normalized';
        newscore1(m1) = 0;
        newscore2(m2) = 0;
        scores(m1,:) = newscore1;
        scores(:,m1) = newscore2;
        scores(m2,:) = 0;
        scores(:,m2) = 0;
        [m1, m2] = find(scores > 0.5 & scores' > 0.5, 1, 'first');
    end

    %prepare result
    goodInds = setdiff(1:size(assocMat,1),eliminated);
    newAssocMat = assocMat(goodInds,:);
end

end
