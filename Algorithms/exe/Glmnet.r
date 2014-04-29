Args<-commandArgs();
lchoice<-as.double(Args[4]);
loffset<-as.double(Args[5]);
start<-1;
end<-as.double(Args[6]);
N<-as.double(Args[7]);
validsetsz<-as.double(Args[8]);
J<-as.double(Args[9]);
K<-as.double(Args[10]);
cx<-Args[11];
y<-Args[12];
mask<-Args[13];
print(mask);
str <- y;
a<-nchar(str);
folder<-substr(str,a-3,a);
#if(J < 1500)
#{
#	quit('no');
#}

if(lchoice == 0)
{
	lambda = c(1.6e-3, 3.2e-3, 6.4e-3, 1.28e-2, 2.56e-2, 5.12e-2, 1.024e-1, 2.048e-1, .3, .4096, .5, .6, .8192, .9, 1.3, 1.6384, 2, 3.2768, 5, 6.5536, 10, 13.1072, 26.2144, 52.4288, 104.8576);
}
if(lchoice > 0)
{
	l = lchoice;
	lambda = c(l*1, l*2, l*3, l*4, l*5, l*6, l*7, l*8, l*9, l*10, l*11, l*12, l*13, l*14, l*15, l*16, l*17, l*18, l*19, l*20, l*21, l*22, l*23, l*24, l*25);
	lambda = lambda + loffset;
}

zz<-file(paste(folder,"/temp.txt",sep=""), "w");
sink(zz, type="message");
library(glmnet, lib.loc="../../r_libraries/");

vals=c(start:end);
A<-matrix(scan(cx,n=N*J),J,N,byrow=TRUE)
y<-matrix(scan(y,n=N*K),K,N,byrow=TRUE)
A<-t(A)
y<-t(y)

sink();

#print(vals);

for(i in vals)
{
	B<-y[,i]

	fit<-glmnet(A[1:validsetsz,],B[1:validsetsz], lambda = lambda)
	cat(fit$lambda);
	print(i);
	dimen<-dim(fit$beta)[2]
	#dimen<-dim(fit$lambda);
	

	zz2 <- file(paste(folder, '/', mask, sprintf("_betas%d.txt",i), sep=""), "w");
	for(q in 1:dimen)
	{
		cat(fit$beta[,q],file=zz2,sep="\t")
		cat("\n", file=zz2)
	}
}

zz3<-file(paste(mask, sprintf("_lambdas.txt",i),sep=""),"w");
cat(fit$lambda,file=zz3,sep="\t");
close(zz3);
close(zz2);

warnings();
