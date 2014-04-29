all: build

build: src/* inc/*
	g++ -Wno-deprecated -c -O3 -o obj/DataMatrix3D.o -I inc src/DataMatrix3D.cpp
	g++ -Wno-deprecated -c -O3 -o obj/DataMatrix.o -I inc src/DataMatrix.cpp 
	g++ -Wno-deprecated -c -O3 -o obj/Edges.o -I inc src/Edges.cpp
	g++ -Wno-deprecated -c -O3 -o obj/CoordDescent.o -I inc src/CoordDescent.cpp
	g++ -Wno-deprecated -c -O3 -o obj/Normalizer.o -I inc src/Normalizer.cpp
	condor_compile g++ -Wno-deprecated -O3 -o bin/ggflasso obj/* drivers/gflasso_main.cpp -I inc

clean:
	touch obj/dummy
	rm -rf obj/*
	touch bin/dummy
	rm -rf bin/*

rungc: build
	./bin/gflasso y6.txt x6.txt 114 25 30 testing.config gc y6ntwrk.txt test_gc

rungw1: build
	./bin/gflasso y6.txt x6.txt 100 25 30 testing.config gw1 y6ntwrk.txt test_gw1

rungw2: build
	./bin/gflasso y6.txt x6.txt 100 25 30 testing.config gw2 y6ntwrk.txt test_gw2

run2: build
	./bin/gflasso y6.txt x6.txt 100 25 30 gw2 .5 test_ntwrk

condor: 
	condor_compile g++ -Wno-deprecated -O3 -o bin/ggflasso_condor src/*.cpp -I inc drivers/gflasso_main.cpp








