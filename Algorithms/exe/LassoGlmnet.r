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
part<-as.double(Args[14]);
folder<-Args[15];

print(lchoice);
print(loffset);
print(start);
print(end);
print(N);
print(validsetsz);
print(J);
print(K);
print(cx);
print(y);
print(mask);
print(part);
print(folder);

print("a");

if(lchoice == 0)
{
	lambda = c(0.625e-5, 1.25e-5, 2.5e-5,5e-5, 1e-4, 2e-4, 4e-4, 8e-4, 1.6e-3, 3.2e-3, 6.4e-3, 1.28e-2, 2.56e-2, 5.12e-2, 1.024e-1, 2.048e-1, .4096, .8192, 1.6384, 3.2768, 6.5536, 13.1072, 26.2144, 52.4288, 104.8576);
}
if(lchoice > 0)
{
	l = lchoice;
	lambda = c(l*1, l*2, l*3, l*4, l*5, l*6, l*7, l*8, l*9, l*10, l*11, l*12, l*13, l*14, l*15, l*16, l*17, l*18, l*19, l*20, l*21, l*22, l*23, l*24, l*25);
	lambda = lambda + loffset;
}

zz<-file(paste(folder, '/', mask, "temp.txt",sep=""), "w");
sink(zz, type="message");
print("a2");
library(glmnet, lib.loc="../../r_libraries/");

print("b");
#dir.create(folder);
#Sys.sleep(5);

#print(half == 1);

vals = c(part);
print(vals);

A<-matrix(scan(cx,n=N*J),J,N,byrow=TRUE)
y<-matrix(scan(y,n=N*K),K,N,byrow=TRUE)

A<-t(A)
y<-t(y)

print(dim(A));
print(dim(y));

#normalize
print(mean(A[,1]));
print(mean(y[,1]));
print(var(A[,1]));
print(var(y[,1]));

AcolSums<-colSums(A);
AcolSumsRep<-matrix(c(rep(AcolSums,dim(A)[1])),dim(A)[1],dim(A)[2],byrow=T);
A<-A-AcolSumsRep / dim(A)[1];
ycolSums<-colSums(y);
ycolSumsRep<-matrix(rep(ycolSums,dim(y)[1]),dim(y)[1],dim(y)[2],byrow=T);
y<-y-ycolSumsRep / dim(y)[1];

Asq<-A*A;
AsqColSums<-colSums(Asq);
AsqColAvg<-AsqColSums / (dim(A)[1]-1);
AsqColStd<-sqrt(AsqColAvg);
AsqColStdRep<-matrix(rep(AsqColStd,dim(A)[1]),dim(A)[1],dim(A)[2],byrow=T);
A<-A/AsqColStdRep;
ysq<-y*y;
ysqColSums<-colSums(ysq);
ysqColAvg<-ysqColSums / (dim(y)[1]-1);
ysqColStd<-sqrt(ysqColAvg);
ysqColStdRep<-matrix(rep(ysqColStd,dim(y)[1]),dim(y)[1],dim(y)[2],byrow=T);
y<-y/ysqColStdRep;

print(mean(A[,1]));
print(mean(y[,1]));
print(var(A[,1]));
print(var(y[,1]));

folddir<-paste(mask,"_folds",sep="");
print(cx);
print(folddir);

folds<-matrix(scan(folddir,n=N),N,1);
print(dim(folds));
print(folds[1:20,]);
sink();

print("c");
for(i in vals)
{
	B<-y[,i]

	print(i);

	res<-cv.glmnet(A,B, lambda = lambda, foldid = folds);
	print("c2");
	print(res$lambda);
	lambda<-res$lambda;
	print("c3");
	error<-res$cvm;
	print("c4");
	fit<-res$glmnet.fit;
	dimen<-length(lambda);
        print("c5");
	zz2 <- file(paste(folder, '/', mask, sprintf("_betas%d.txt",i), sep=""), "w");
	zz4 <- file(paste(folder, '/', mask, sprintf("_err%d.txt",i), sep=""),"w");
	print("c6");
        for(q in 1:dimen)
	{
		b<-coef(fit, s=lambda[q]);
		cat(b[2:(J+1)],file=zz2,sep="\t")
		cat("\n", file=zz2)
		print("c7");
		err = error[q];
		cat(err,file=zz4);
		cat("\n",file=zz4);
		print("c8");
	}
	close(zz2);
	close(zz4);
}
print("d");
zz3<-file(paste(mask, sprintf("_lambdas.txt",i),sep=""),"w");
cat(lambda,file=zz3,sep="\t");
close(zz3);

warnings();

print("e");
