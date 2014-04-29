function runSC(filename, numClust, resfile)

if(numClust == -1)
	exit
end

ntwrk = load(filename);
ntwrk = 1-ntwrk;%need a dissimiliarity matrix

[predict_labels evd_time kmeans_time total_time] = ...
    sc(ntwrk, 0, numClust);


save(resfile, 'predict_labels', '-ascii');




