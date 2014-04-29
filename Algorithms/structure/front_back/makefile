all: frontend backend frontend_M backend_M

clean:
	touch bin/dummy
	rm -f bin/*

OBJ = bin/stats.o bin/filereader.o
MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

frontend: bin/structureFrontEnd

backend: bin/structureBackEnd

frontend_M: bin/structureFrontEnd_M

backend_M: bin/structureBackEnd_M

bin/structureFrontEnd: src/structureFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/structureFrontEnd src/structureFrontEnd.cpp

bin/structureBackEnd: src/structureBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/structureBackEnd src/structureBackEnd.cpp

bin/structureFrontEnd_M: src/structureFrontEnd_M.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/structureFrontEnd_M src/structureFrontEnd_M.cpp

bin/structureBackEnd_M: src/structureBackEnd_M.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/structureBackEnd_M src/structureBackEnd_M.cpp

