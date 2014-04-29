all: frontend backend

MXXFLAGS := -I/usr/include/mysql -I/usr/include/mysql++ -L/usr/lib64/mysql -L/usr/lib64 -lmysqlpp -lmysqlclient -lnsl -lz -lm

clean:
	touch bin/dummy
	rm -f bin/*

frontend: bin/mpglFrontEnd

backend: bin/mpglBackEnd

bin/mpglFrontEnd: src/mpglFrontEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/mpglFrontEnd src/mpglFrontEnd.cpp

bin/mpglBackEnd: src/mpglBackEnd.cpp
	g++ -Wno-deprecated $(MXXFLAGS) -O3 -o bin/mpglBackEnd src/mpglBackEnd.cpp

