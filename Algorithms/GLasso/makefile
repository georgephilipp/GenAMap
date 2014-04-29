all: frontend backend glassoCorr_2 glassoCorr_3 mergeSC mergeCC mergeGLasso stats filereader

clean:
	touch bin/dummy
	rm -f bin/*

OBJ = bin/stats.o bin/filereader.o
MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

frontend: bin/glassoFrontEnd

backend: bin/glassoBackEnd

glassoCorr_2: bin/glassoCorr_2

glassoCorr_3: bin/glassoCorr_3

mergeSC: bin/mergeSC

mergeCC: bin/mergeCC

mergeGLasso: bin/mergeGLasso

stats: bin/stats.o

filereader: bin/filereader.o

bin/stats.o: src/stats.cpp
	g++ -Wno-deprecated -O3 -c -o bin/stats.o -I inc src/stats.cpp

bin/filereader.o: src/filereader.cpp
	g++ -Wno-deprecated -O3 -c -o bin/filereader.o -I inc src/filereader.cpp

bin/glassoFrontEnd: src/glassoFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/glassoFrontEnd src/glassoFrontEnd.cpp

bin/glassoBackEnd: src/glassoBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/glassoBackEnd src/glassoBackEnd.cpp

bin/glassoCorr_2: $(OBJ) bin/glassoCorr_2.o
	condor_compile g++ -Wno-deprecated -O3 -o bin/glassoCorr_2 $(OBJ) bin/glassoCorr_2.o

bin/glassoCorr_2.o: src/glassoCorr_2.cpp bin/stats.o bin/filereader.o
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -c -o bin/glassoCorr_2.o -I inc src/glassoCorr_2.cpp

bin/glassoCorr_3: src/glassoCorr_3.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/glassoCorr_3 src/glassoCorr_3.cpp

bin/mergeSC: src/mergeSC.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/mergeSC src/mergeSC.cpp
	
bin/mergeCC: src/mergeCC.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/mergeCC src/mergeCC.cpp
	
bin/mergeGLasso: src/mergeGLasso.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/mergeGLasso src/mergeGLasso.cpp inc/stats.h src/stats.cpp	
