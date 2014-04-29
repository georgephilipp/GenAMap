Args<-commandArgs();
strin <- Args[4];
strout <- Args[6];
lambda<-as.double(Args[5]);


data <- read.table(strin, header=F, as.is=T)
print("reading data ...");
data1 <- data
print("loading lib ...");
library(glasso, lib.loc="../../r_libraries/");
	
#	zz<-file(paste("outerr_", strout, "_", lambda), "w");
#	sink(zz, type="output");

for(i in 1:dim(data1)[1])
{
	if(var(t(data1[i,]))==0)
	{
		data1[i,] = 1;
	}
	if(var(t(data1[i,]))!=0)
	{
		data1[i,] = (data1[i,]-mean(t(data1[i,]))) / sqrt(var(t(data1[i,])));
	}
}

data1<-t(data1);

	print(Sys.time());
	print("finding cov ...");
	cm = cov(data1);
	print("finding res ...");
	result = glasso(cm,lambda);
	print("writing res ...");
	write.table(result, file = strout, sep=" "); 
	print('\n');
	print(Sys.time());
#	sink();


