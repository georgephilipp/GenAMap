%First, we want to create our chromosomes

chr = zeros(250,5000);
for(i=1:5000)
    MAF = rand / 3;
    MAF2 = MAF*MAF;
    for(j=1:250)
        x = rand;
        if(x < MAF2)
            chr(j,i) = 2;
        elseif (x < MAF)
            chr(j,i) = 1;
        end
    end
end

%Next our true beta matrix

beta = zeros(5000,2000);

%mod1 = 150 genes
beta(1,1:150) = .5 + (.5-rand(1,150))/3;

%mod2 = 150 genes
beta(2,151:300) = .25 + (.5 - rand(1,150))/3;

%mod3 = 100 genes
beta(3,301:400) = .66 + (.5 - rand(1,100))/3;
beta(4,301:400) = .32 + (.5 - rand(1,100))/3;

%mod4 = 100 genes
beta(5,401:500) = .4 + (.5 - rand(1,100))/3;

%mod5 = 50 genes
beta(6,501:550) = .9 + (.5 - rand(1,50))/3;
beta(7,501:550) = .1 + (.5 - rand(1,50))/3;

%mod6 = 50 genes
beta(8,551:600) = .77 + (.5 - rand(1,50))/3;

%mod7 = 50 genes
beta(9,601:650) = .2 + (.5 - rand(1,50))/3;

%mod8 = 50 genes
beta(10,651:700) = .21 + (.5 - rand(1,50))/3;

%mod9 = 40 genes
beta(11,701:740) = .11 + (.5 - rand(1,40))/4;
beta(12,701:740) = .05 + (.5 - rand(1,40))/4;
beta(13,701:740) = .08 + (.5 - rand(1,40))/4;

%mod10 = 30 genes
beta(14,741:770) = .01 + (.5 - rand(1,30))/4;

%mod11 = 25 genes
beta(15,771:795) = 1.2 + (.5 - rand(1,25))/3;

%mod12 = 25 genes
beta(16, 796:820) = .31 + (.5 - rand(1,25))/3;

%mod13 = 20 genes
beta(17, 821:840) = .5 + (.5 - rand(1,20))/3;

%mod14 = 15 genes
beta(18, 841:855) = .57 + (.5 - rand(1,15))/5;
beta(19, 841:855) = .57 + (.5 - rand(1,15))/5;

%mod15 = 10 genes
beta(20, 856:865) = .33 + (.5 - rand(1,10));

i = 1;
    for(idx = 1:10:2000)
        i = i + 1;
        beta(i, idx) = rand;
    end


gene = chr * beta + randn(250,2000)/2;
save 'ex2gene.txt' gene -ascii;
%save 'ex2chr.txt' chr -ascii;
save 'beta1.txt' beta -ascii;

%now prepare the gene expression to phenotype mat
beta = zeros(2000, 200);

%1 - mod1
beta(1:150, 1:10) = .1 + (.5-rand(150,10))/3;

%2 - mod2 and 6 = 150 genes
beta(151:300, 11:20) = .05 + (.5 - rand(150,10))/3;
beta(551:600, 11:20) = .18 + (.5 - rand(50,10))/3;

%3 - mod3 = 100 genes
beta(301:400, 21:25) = .11 + (.5 - rand(100,5))/3;

%4 - mod4 and 9 = 100 genes
beta(401:500, 26:50) = .09 + (.5 - rand(100,25))/3;
beta(701:740, 26:50) = .30 + (.5 - rand(40,25))/4;

%5 - mod5 and 12 = 50 genes
beta(501:550, 51:53) = .05 + (.5 - rand(50,3))/3;
beta(796:820, 51:53) = .08 + (.5 - rand(25,3))/3;

%6 - mod7 = 50 genes
beta(601:650, 54:60) = .2 + (.5 - rand(50,7))/3;

%7 - mod8 = 50 genes
beta(651:700, 61) = .1 + (.5 - rand(50,1))/3;

%8 - mod10 = 30 genes
beta(741:770, 62:65) = 1.5 + (.5 - rand(30,4))/2;

%9 - mod11 = 25 genes
beta(771:795, 66:67) = .09 + (.5 - rand(25,2))/4;

%10 - mod13 = 20 genes
beta(821:840, 68:70) = .17 + (.5 - rand(20,3))/3;

%11 - mod14 = 15 genes
beta(841:855, 71:73) = .027 + (.5 - rand(15, 3))/2;

%12 - mod15 = 10 genes
beta(856:865, 74:100) = .14 + (.5 - rand(10,27))/4;

phens = gene * beta + randn(250,200)*2;


save 'beta2.txt' beta -ascii;
save 'ex2phen.txt' phens -ascii;















