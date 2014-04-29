Args<-commandArgs();
N<-as.double(Args[4]);
J<-as.double(Args[5]);
K<-as.double(Args[6]);
x<-Args[7];
y<-Args[8];
mask<-Args[9];
print(mask);
i<-as.double(Args[10]);
str <- y;
a<-nchar(str);
folder<-substr(str,a-3,a);

lambda = .1000;

zz<-file(paste("temp.txt",sep=""), "w");
sink(zz, type="message");
library(glmnet, lib.loc="../../r_libraries/");

A<-matrix(scan(x,n=N*J),J,N,byrow=TRUE)
y<-matrix(scan(y,n=N*K),K,N,byrow=TRUE)
A<-t(A)
y<-t(y)

sink();


fit<-glmnet(A,y[,i], lambda = lambda)
print(i);

zz2 <- file(paste(mask, sprintf("_betas%d.txt",i), sep=""), "w");
cat(fit$beta[,1],file=zz2,sep="\t")
cat("\n", file=zz2)


close(zz2);

warnings();
