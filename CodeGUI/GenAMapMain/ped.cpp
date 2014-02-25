// ped.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#include <iostream>
#include <fstream>
#include <string>
#include <vector>

using namespace std;

struct col {
	char c1, c2, c3;
	int m1, m2, m3;
};

vector<struct col> vec;
vector<int> markers;
int rows;
string arrayfile;
string markerfile;
string samplefile;
string insertfile;

void firstpass() {
	ifstream arrayf;
	arrayf.open(arrayfile.c_str());
	
	char ch;
	int column = 0, realc;
	bool newcolstarts = true;
	while(arrayf.good()) {
		ch = arrayf.get();
		
		if (ch == '\t' || ch == ' ') {
			if(!newcolstarts) column++, newcolstarts = false;
		} else if (ch != '\n' && ch != '\r'){
			newcolstarts = false;
			if(column > 5) 
			{
				realc = (column - 6) / 2;
				if((int)vec.size() <= realc) {
					struct col c; c.c1 = c.c2 = c.c3 = '\n'; c.m1 = c.m2 = c.m3 = 0;
					vec.push_back(c);
				}
			
				struct col c = vec[realc];
				if(c.c1 == ch || c.c1 == '\n') {
					c.c1 = ch;
					c.m1++;
				} else if(c.c2 == ch || c.c2 == '\n') {
					c.c2 = ch;
					c.m2++;
				} else {
					c.c3 = ch;
					c.m3++;
				}
				vec[realc] = c;
			}
		}
		
		if (ch == '\n') {
			column = 0;
			newcolstarts = true;
			rows++;
		}
	}

	arrayf.close();
}

int transform(char c1, char c2, struct col c) {
	char cc1, cc2;
	if(c.m1 >= c.m2 && c.m1 >= c.m1) {
		cc1 = c.c1;
		if(c.m2 >= c.m3) cc2 = c.c2;
		else cc2 = c.c3;
	}
	if(c.m2 >= c.m1 && c.m2 >= c.m3) {
		cc1 = c.c2;
		if(c.m1 >= c.m3) cc2 = c.c1;
		else cc2 = c.c3;
	}
	if(c.m3 >= c.m1 && c.m3 >= c.m2) {
		cc1 = c.c3;
		if(c.m1 >= c.m2) cc2 = c.c1;
		else cc2 = c.c2;
	}
	
	if(c1 == cc1 && c2 == cc1) return 0;
	else if(c1 == cc1 && c2 == cc2) return 1;
	else if(c1 == cc2 && c2 == cc1) return 1;
	else if(c1 == cc2 && c2 == cc2) return 2;
	else return -1;
}

void secondpass() {
	ifstream arrayf;
	arrayf.open(arrayfile.c_str());
	
	ifstream sampleidf;
	sampleidf.open(samplefile.c_str());
	
	ifstream markeridf;
	markeridf.open(markerfile.c_str());
	while(markeridf.good()) {
		int markerid;
		markeridf >> markerid;
		markers.push_back(markerid);
	}
	markeridf.close();
	
	ofstream out;
	out.open(insertfile.c_str());
	
	char ch, lastch;
	int column = 0, realc, crtId, currentrows = 0;
	bool newcolstarts = true;
	
	sampleidf >> crtId;
	//cout << crtId << ":";
	
	int cntr = 4000;
	while(arrayf.good()) 
	{
		ch = arrayf.get();
		
		if (ch == '\t' || ch == ' ') 
		{
			if(!newcolstarts) column++, newcolstarts = false;
		} 
		else if (ch != '\n' && ch != '\r')
		{
			newcolstarts = false;
			if(column > 5) 
			{
				realc = (column - 6) / 2;
				if ((column - 6) % 2 == 0) lastch = ch;
				else 
				{
					struct col c = vec[realc];
					int x = transform(lastch, ch, c);
					//cout << x;
					int markerId = markers[realc];
					//cout << "[" << markerId << "] ";
					if(cntr == 4000)
					{
						out << "INSERT INTO markerval (sampleid, markerid, value) VALUES ";
						out << " (" << crtId << "," << markerId <<
						"," << x << ")";
						cntr --;
					}
					else
					{
						out << ", (" << crtId << "," << markerId <<
						"," << x << ")";
						cntr --;
					}
					if(cntr < 0)
					{
						cntr = 4000;
						out << ';' << endl;
					}
				}
			}
		}
		
		if (ch == '\n') {
			column = 0;
			newcolstarts = true;
			//cout << endl;
			sampleidf >> crtId;
			//cout << crtId << ":";
			currentrows++;
			int progress = 100 * currentrows / rows;
			cout << progress << endl;
			
		}
	}

	if(cntr != 4000)
	{
		cntr = 4000;
		out << ';' << endl;
	}

	out.close();
	sampleidf.close();
	arrayf.close();
}

int main(int argc, char ** argv) {
	
	arrayfile = argv[1];
	markerfile = argv[2];
	samplefile = argv[3];
	insertfile = argv[4];
	//cout << arrayfile << endl;
	//cout << insertfile << endl;
	
	firstpass();	
	secondpass();
	
	//cout << "done!" << endl;
	
	return 0;
}