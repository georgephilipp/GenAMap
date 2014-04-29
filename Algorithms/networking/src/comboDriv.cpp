#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdlib> 

using namespace std;

//argv1 = trait file name
//argv2 = num indv
//argv3 = num traits
//argv4 = what to do
//argv5 = mask

void combineFiles(int subsz, int k, string mask);
void createParmFiles(int subsz, int k, int method, string mask, int id, string file);
string toString(int i);

int main(int argc, char* argv[])
{
	int CR1 = 0;
	int CR2 = 1;
	int SFN_R2 = 2;
	int SFN_CBN = 3;
	int TOM = 4;
	int subsz = 500;

	if(argc != 6 && argc != 3)//this is a splitting action.
	{
		fprintf(stderr, "Invalid number of arguments: %d", argc);
		return -1;
	}
	string file;
	string mask;
	int id;
	int k;
	int method;
	for(int i = 0; i < argc; i ++)
	{
		switch(i)
		{
			case 1: 
				if(argc == 6)
				{
					file = argv[i];
					cout << file << endl;
				}
				else
				{
					k = atoi(argv[i]);
					cout << k << endl;
				}
				break;
			case 2:
				if(argc == 6)
				{
					id = atoi(argv[i]);
					cout << id << endl;
				}
				else
				{
					mask = argv[i];
					cout << mask << endl;
				}
				break;
			case 3:
				k = atoi(argv[i]);
				cout << k << endl;
				break;
			case 4:
				method = atoi(argv[i]);
				cout << method << endl;
				break;
			case 5:
				mask = argv[i];
				cout << mask << endl;
				break;
		}
	}

	if(argc == 6)
		createParmFiles(subsz, k, method, mask, id, file);
	else
		combineFiles(subsz, k, mask);

	return 0;
}

void createParmFiles(int subsz, int k, int method, string mask, int id, string file)
{
        ofstream myfile;
        myfile.open("parms1.txt");
        bool isNotWritten = true;

        for(int i = 1; i <= k; i += subsz)
        {
                for(int j = 1; j <= k; j += subsz)
                {
                        myfile << file << " " << id << " " << k << " ";
                        myfile << subsz << " " << i << " " << j << " ";
                        myfile << method << " " << mask << endl;
                        if(i==1 && isNotWritten)
                        {
                                myfile << "standard" << endl;
                                isNotWritten = false;
                        }
                }
        }

        myfile.close();

        ofstream secondfile;
        secondfile.open("parms2.txt");
        secondfile << k << " " << mask << endl;
        secondfile << "standard" << endl;
        secondfile.close();
}

void combineFiles(int subsz, int k, string mask)
{
	ofstream results;
	string filename = mask + "results.txt";
	results.open(filename.c_str());

	results << "00";
	int numfis = (int)(k / subsz);
	if(k % subsz > 0)
		numfis ++;

	ifstream infile[numfis];

        for(int i = 1; i <= k; i += subsz)
        {
		int p = 0;
                for(int j = 1; j <= k; j += subsz)
                {
			string s = mask + "_" + toString(i) + "_" + toString(j) + "res.txt";
			infile[p++].open(s.c_str());
                }

		for(int j = 0; j < subsz; j ++)
		{
			string s;
			for(int k = 0; k < numfis; k ++)
			{
				getline(infile[k], s);
				results << s;
				//cout << s;
			}
			results << endl;
			//cout << endl;
		}

		for(int j = 0; j < numfis; j ++)
		{
			infile[j].close();
		}
        }
	results.close();

}

string toString(int i)
{
	std::string s;
	std::stringstream out;
	out << i;
	s = out.str();
	return s;
}
