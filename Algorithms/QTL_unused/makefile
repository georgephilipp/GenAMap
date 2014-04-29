all: build

build: 
	condor_compile g++ -Wno-deprecated -I inc src/*.cpp driver/qtlanal.cpp -o bin/qtl

run: build exe
	./bin/cluster test.txt 5

memcheck: build
	valgrind --tool=memcheck --leak-check=yes --max-stackframe=5000000 --show-reachable=yes --suppressions=string.supp ./bin/qtl y6_sub.txt x6_sub.txt 5 6 1 3 1 3 1_5_ 5 0


clean:
	touch bin/dummy
	rm bin/*





