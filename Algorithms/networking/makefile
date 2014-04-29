all: bin/netDriv combo frontend backend bin/sfn1 bin/sfn2 bin/sfn3 bin/tom

clean:
	touch bin/dummy
	rm -f bin/*

OBJ = bin/stats.o bin/filereader.o
MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

bin/netDriv: $(OBJ) bin/netDriv.o
	condor_compile g++ -Wno-deprecated -O3 -o bin/netDriv $(OBJ) bin/netDriv.o 

bin/netDriv.o: src/netDriv.cpp bin/stats.o bin/filereader.o
	g++ -Wno-deprecated -O3 -c -o bin/netDriv.o -I inc src/netDriv.cpp

bin/stats.o: src/stats.cpp
	g++ -Wno-deprecated -O3 -c -o bin/stats.o -I inc src/stats.cpp

bin/filereader.o: src/filereader.cpp
	g++ -Wno-deprecated -O3 -c -o bin/filereader.o -I inc src/filereader.cpp

test: bin/netDriv
	./bin/netDriv traits.txt 114 5637 1000 1 1001 1 TST1007 

test3: bin/netDriv
	./bin/netDriv traits2.txt 100 30 500 1 1 1 TST1008

bin/comboDriv: src/comboDriv.cpp
	condor_compile g++ -Wno-deprecated -O3 -o bin/comboDriv src/comboDriv.cpp

combo: bin/comboDriv

frontend: bin/netFrontEnd

backend: bin/netBackEnd

bin/netFrontEnd: src/netFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/netFrontEnd src/netFrontEnd.cpp

bin/netBackEnd: src/netBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/netBackEnd src/netBackEnd.cpp

bin/sfn1: src/sfn1.cpp
	condor_compile g++ -Wno-deprecated -O3 -o bin/sfn1 src/sfn1.cpp src/filereader.cpp inc/filereader.h

bin/sfn2: src/sfn2.cpp
	condor_compile g++ -Wno-deprecated -O3 -o bin/sfn2 src/sfn2.cpp src/filereader.cpp inc/filereader.h inc/stats.h src/stats.cpp

bin/sfn3: src/sfn3.cpp
	condor_compile g++ -Wno-deprecated -O3 -o bin/sfn3 src/sfn3.cpp src/filereader.cpp inc/filereader.h

bin/tom: src/maketom.cpp
	condor_compile g++ -Wno-deprecated -O3 -o bin/tom src/maketom.cpp src/filereader.cpp inc/filereader.h

test2: bin/netDriv
	valgrind --tool=memcheck --leak-check=yes --max-stackframe=5000000 --show-reachable=yes --suppressions=string.supp ./bin/netDriv CR1404e_1 CR1404e_7 0 TSTING 5 3 2





