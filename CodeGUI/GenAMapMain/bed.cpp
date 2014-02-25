// bed.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <vector>

#define nmax 4096

using namespace std;

string arrayfile;
string markerfile;
string samplefile;
string insertfile;

int main(int argc, char **argv){
	
	arrayfile = argv[1];//"C:\\Users\\ARCurtis\\Documents\\Visual Studio 2010\\Projects\\bed\\Debug\\WF22.bed";//argv[1];
	markerfile = argv[2];//"C:\\Users\\ARCurtis\\Documents\\Visual Studio 2010\\Projects\\bed\\Debug\\markers1296763446609.txt";//argv[2];
	samplefile = argv[3];//"C:\\Users\\ARCurtis\\Documents\\Visual Studio 2010\\Projects\\bed\\Debug\\samples1296763446609.txt";//argv[3];
	
	fstream in;
	fstream samplesf;
	fstream markersf;
	unsigned char control, format;
	int samples[nmax], ssize = 0;
	
	in.open(arrayfile.c_str(), ios::in | ios::binary);
	markersf.open(markerfile.c_str(), ios::in);
	samplesf.open(samplefile.c_str(), ios::in);

	//ofstream out;
	//out.open(insertfile.c_str());
	
	if(!in){
		// printf("Error\n" << endl;
		exit(1);
	}
	
	//in.read(reinterpret_cast<char*>(&control), sizeof(unsigned char));
	control = in.get();
	//control = in.get();
	if(control != 108){
		//first control byte
		// cout << "Invalid bed file [control 1]" << endl;
		exit(-1);
	}
	
	in.read(reinterpret_cast<char*>(&control), sizeof(unsigned char));
	if(control != 27){
		//second control byte
		// cout << "Invalid bed file [control 2]" << endl;
		exit(-1);
	}
	
	in.read(reinterpret_cast<char*>(&format), sizeof(unsigned char));
	if(format != 0 && format != 1){
		//format
		//printf("%d\n", format);
		// cout << "Invalid bed file [format]" << endl;
		exit(-1);
	}
	
	//reading in sample file
	while(samplesf.good()){
		int sampleid;
		//cout << samplesf << endl;
		samplesf >> sampleid;
		samples[ssize] = sampleid;
		ssize++;
		// samples.push_back(sampleid);
	}
	if(samples[ssize-1] == samples[ssize-2])
	{
		ssize--;
	}
	//cout << ssize;

	unsigned char x;
	int count, markerid;
	markerid = -1;
	int vx[] = {0, -1, 1, 2};
	int i;
	//encoding:
	//00 -> 0, 01 -> 1, 11 -> 2, 10 -> -1

	if(format == 1)
	 while(markersf.good()) {
		int temp = markerid;
		markersf >> markerid;
		if(temp == markerid)
			break;
			
		printf("INSERT INTO markerval (sampleid, markerid, value) VALUES ");	
		//out << "INSERT INTO markerval (sampleid, markerid, value) VALUES ";

		for(count = 0; count < ssize;) {
			in.read(reinterpret_cast<char*>(&x), sizeof(unsigned char));			
			for (i = 0; i < 4 && count < ssize; ++i) {
				printf("(%d,%d,%d),", samples[count], markerid, vx[x & 3]);
				//out << "(" << samples[count] << "," << markerid << "," << vx[x & 3] << "),";
				++count;
				x >>= 2;

				if((count % 1000) == 0){

					//cout << "progress" << endl;
					printf("\b;\n");
					//out << "\b;\n";
					//out << "INSERT INTO markerval (sampleid, markerid, value) VALUES ";
					printf("INSERT INTO markerval (sampleid, markerid, value) VALUES ");
				}
			}
		}
		printf("\b;\n");
		//out << "\b;\n";
	}

	//out.close();
	markersf.close();
	samplesf.close();
	return 0;
}