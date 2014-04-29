all: frontend backend

OBJ = bin/stats.o bin/filereader.o
MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

clean:
	touch bin/dummy
	rm -f bin/*

frontend: bin/genemodFrontEnd

backend: bin/genmodBackEnd

bin/genemodFrontEnd: src/genemodFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/genemodFrontEnd src/genemodFrontEnd.cpp

bin/genmodBackEnd: src/genmodBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/genmodBackEnd src/genmodBackEnd.cpp










