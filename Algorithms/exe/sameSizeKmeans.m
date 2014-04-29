function [res, initial] = sameSizeKmeans( input, maxSize )
%HIERARCHICALKMEANS Summary of this function goes here
%   Detailed explanation goes here
minnumclust = ceil(size(input, 1) / maxSize);
numclust = minnumclust;

for iter=1:50
    for i=1:10
        [initial, centers] = k_means(input, 'random', ceil(numclust));
        if numel(unique(initial)) >= minnumclust
            break;
        end
    end
    if numel(unique(initial)) >= minnumclust
        break;
    end
    numclust = numclust * 1.1;
end

if iter==50
    error('count not find good initial clustering');
end

N = size(input, 1);
d = size(input, 2);
centers = centers';
centers = centers(:,unique(initial));
k = size(centers,2);

activePoints = ones(N, 1);
clusterAssignments = zeros(1, k);
res = zeros(N, 1);
dataSizes = repmat(sum(input.*input,2),1,k);
centerSizes = repmat(sum(centers.*centers,1),N,1);

for iter=1:(N+1)
    numActivePoints = sum(activePoints);
    numActiveClusters = numel(find(clusterAssignments < maxSize));
    if numActivePoints == 0
        break;
    end
    activePointInds = find(activePoints == 1);
    activeClusterInds = find(clusterAssignments < maxSize);
    activeData = input(activePointInds,:);
    activeDataSizes = dataSizes(activePointInds,activeClusterInds);
    activeCenters = centers(:,activeClusterInds);
    activeCenterSizes = centerSizes(activePointInds,activeClusterInds);
    scores = activeDataSizes + activeCenterSizes - 2*activeData * activeCenters;
    [topScores, topScoreClusterInds] = min(scores,[],2);
    if numActiveClusters > 1
        sorted = sort(scores,2);
        topScores = topScores - sorted(:,2);
    end
    topScoresWithInds = [topScores, topScoreClusterInds];
    [topScoresWithIndsSorted, topScorePointInds] = sortrows(topScoresWithInds,1);
    for i=1:numActivePoints
        thisPointInd = activePointInds(topScorePointInds(i));
        thisClusterInd = activeClusterInds(topScoresWithIndsSorted(i,2));
        if clusterAssignments(thisClusterInd) < maxSize
            res(thisPointInd) = thisClusterInd;
            activePoints(thisPointInd) = 0;
            clusterAssignments(thisClusterInd) = clusterAssignments(thisClusterInd) + 1;
        end
    end    
end

if i==N+1
    error('cannot take that many iterations. Something went wrong');
end

if numel(find(res == 0)) > 0
    error('some point has not been assigned a cluster');
end

if sum(clusterAssignments) ~= N
    error('incorrect number of cluster assignments');
end

for i=1:k
    if numel(find(res == k)) ~= clusterAssignments(k)
        error(['number of points assigned to cluster ' num2str(k) ' is a mismatch']);
    end
    if clusterAssignments(k) > maxSize
        error('clustering has created clusters that exceed maximum size');
    end
end









