all: build exe frontend backend run tree

clean:
	touch obj/dummy
	rm -f obj/*
	touch bin/dummy
	rm -f bin/*

build: src/* headers/*
	g++ -Wno-deprecated -c -o obj/matrix.o -I headers src/matrix.cpp 
	g++ -Wno-deprecated -c -o obj/tree.o -I headers src/tree.cpp
	g++ -Wno-deprecated -c -o obj/pair.o -I headers src/pair.cpp

run: build exe
	./bin/cluster_condor test.txt 5

exe: build
	condor_compile g++ -Wno-deprecated -o bin/cluster_condor obj/* drivers/cluster.cpp

tree: build
	condor_compile g++ -Wno-deprecated -o bin/tree obj/* drivers/mktree.cpp


memcheck: build exe
	valgrind --tool=memcheck --leak-check=yes --max-stackframe=5000000 --show-reachable=yes --suppressions=string.supp bin/cluster_condor test.txt 5


MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

frontend: bin/clusterFrontEnd

backend: bin/clusterBackEnd

bin/clusterFrontEnd: src/clusterFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/clusterFrontEnd src/clusterFrontEnd.cpp

bin/clusterBackEnd: src/clusterBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/clusterBackEnd src/clusterBackEnd.cpp







