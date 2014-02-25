#include "stdafx.h"

#include <stdio.h>
#include <iostream>
#include <fstream>
#include <vector>

#define nmax 200000

using namespace std; 

string arrayfile;
string markerfile;
string samplefile;

int main(int argc, char **argv){
	
	//cout << "Hello!!" << endl;
	arrayfile = argv[1];
	markerfile = argv[3];
	samplefile = argv[2];
	//cout << "Happy!! " <<endl;
//	arrayfile = "C:\\Users\\rcurtis\\Documents\\Visual Studio 2010\\Projects\\matrix\\tlgenotype.txt";
//	markerfile = "C:\\Users\\rcurtis\\Documents\\Visual Studio 2010\\Projects\\matrix\\markers1300109164338.txt";
//	samplefile = "C:\\Users\\rcurtis\\Documents\\Visual Studio 2010\\Projects\\matrix\\samples1300109164338.txt";
	
	fstream in;
	fstream samplesf;
	fstream markersf;
	int samples[nmax];
	int ssize = 0;
	
	in.open(arrayfile.c_str());
	markersf.open(markerfile.c_str());
	samplesf.open(samplefile.c_str());

	//cout << "GREAT!" << endl;
	if(!in){
		// printf("Error\n" << endl;
		exit(1);
	}
	//printf("?");
	
	//reading in sample file
	while(samplesf.good())
	{
		//cout << ssize << endl;
		int sampleid;
		samplesf >> sampleid;
		//cout << sampleid << endl;
		if(sampleid != samples[ssize-1])
		{
			samples[ssize] = sampleid;
			ssize++;
		}
	}
	//printf("??");
	//cout << "??" << endl;
	double x;
	int count, markerid, arrayval;
	int lastmarkerid = -1;
	while(markersf.good() && in.good()) {
		markersf >> markerid;
		if(markerid == lastmarkerid)
			continue;
		lastmarkerid = markerid;	
		printf("INSERT INTO markerval (sampleid, markerid, value) VALUES ");	
		for(count = 0; count < ssize; ++count) {
			in >> x;	
			arrayval = (int) x;	
			printf("(%d,%d,%d),", markerid, samples[count], arrayval);
				
			if((count % 1000) == 0 && count != 0){
					printf("\b;\n");
					printf("INSERT INTO markerval (sampleid, markerid, value) VALUES ");
			}
		}
		printf("\b;\n");
	}
	//printf("\nok!\n");
	
	markersf.close();
	samplesf.close();
	in.close();
	exit(1);
}