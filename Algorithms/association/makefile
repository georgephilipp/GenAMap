all: bin/stats.o bin/filereader.o frontend backend gtfront gtback pop pvals

clean:
	touch bin/dummy
	rm bin/*

OBJ = bin/stats.o bin/filereader.o
MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

bin/stats.o: src/stats.cpp
	g++ -Wno-deprecated -O3 -c -o bin/stats.o -I inc src/stats.cpp

bin/filereader.o: src/filereader.cpp
	g++ -Wno-deprecated -O3 -c -o bin/filereader.o -I inc src/filereader.cpp

frontend: bin/assocFrontEnd

backend: bin/assocBackEnd

gtfront: bin/gtassocFrontEnd

gtback: bin/gtassocBackEnd

pop: bin/popBackEnd

pvals: bin/thresh

bin/thresh: src/threshCalc.cpp
	g++ -Wno-deprecated -O3 -o bin/thresh src/threshCalc.cpp

bin/assocFrontEnd: src/assocFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/assocFrontEnd src/assocFrontEnd.cpp

bin/gtassocFrontEnd: src/gtassocFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/gtassocFrontEnd src/gtassocFrontEnd.cpp

bin/assocBackEnd: src/assocBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/assocBackEnd src/assocBackEnd.cpp

bin/gtassocBackEnd: src/gtassocBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/gtassocBackEnd src/gtassocBackEnd.cpp

bin/popBackEnd: src/popBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/popBackEnd src/popBackEnd.cpp

test2: bin/assocDriv
	valgrind --tool=memcheck --leak-check=yes --max-stackframe=5000000 --show-reachable=yes --suppressions=string.supp ./bin/assocDriv CR1404e_1 CR1404e_7 0 TSTING 5 3 2





