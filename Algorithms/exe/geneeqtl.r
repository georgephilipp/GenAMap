Args<-commandArgs();
mymod<-as.double(Args[4])+1;
myassocfile<-(Args[5]);
mykey<-(Args[6]);

print(mykey);
print(myassocfile);
print(mymod);

assocs<-read.table(myassocfile, header=F, as.is=T);
mkey<-read.table(mykey, header=F, as.is=T);

lenm<-(dim(mkey)[1]);
lent<-(dim(assocs)[2]);

winners<-read.table('modules.txt', header=F, as.is=T);
mystart<-winners[mymod,1];
myend<-winners[mymod,2];
print(mystart);
print(myend);

valm<-c(1:lenm);
valt<-c(1:lent);

zz3<-file(paste("eQTLres", mymod, sep=""), "w");

for(i in valm)
{
	print(i);
	x<-0;
	n<-0;
	X<-0;
	N<-0;
	for(j in valt)
	{
		if(j<=myend && j >= mystart)
		{
			if(assocs[i,j] != 0)
			{
				x = x+ 1;
			}
			else
			{
				X = X + 1;
			}
		}
		else
		{
			if(assocs[i,j] != 0)
			{
				n=n+1;
			}
			else
			{
				N=N+1;
			}
		}
	}
	m<-matrix(c(x,n,X,N), nrow=2);
	if(x > 4)
	{
	pval<-fisher.test(m)$p.value;
	
	key<-(mkey[i,1]);
	print(key);
	if(pval < .01 && x > 2)
	{
		print('foundsome!');
		cat(sprintf('%d,%d,%d,%d,%e,%d\n', x, n+x, X+x, N+X+n+x, pval, key), file=zz3);
	}
	}
}
close(zz3);
quit('no');











