#include <algorithm>
#include <cstdio>
#include <string>
#include <map>

using namespace std;

char s[10000];

map<string, int> ids;

char r[10000], *p;

int n;

int getID(string z)
{
	if (ids.count(z)) return ids[z];
	ids[z] = n++;
	return n - 1;
}

int main()
{
	n = 0;

	while(gets(s))
	{
		p = r;
		for(int i = 0; s[i]; i++)
		if (!strncmp(s + i, " id=\"", 5))
		{
			for(int j = 0; j < 5; j++) *p++ = s[i++];
			string name = "";
			while(s[i] != '\"') name += s[i++];

			sprintf(p, "%d", getID(name));
			p += strlen(p);

			*p++ = '\"';
		}
		else
		if (!strncmp(s + i, " first=\"", 8))
		{
			for(int j = 0; j < 8; j++) *p++ = s[i++];
			string name = "";
			while(s[i] != '\"') name += s[i++];

			sprintf(p, "%d", getID(name));
			p += strlen(p);

			*p++ = '\"';
		}
		else
		if (!strncmp(s + i, " second=\"", 9))
		{
			for(int j = 0; j < 9; j++) *p++ = s[i++];
			string name = "";
			while(s[i] != '\"') name += s[i++];

			sprintf(p, "%d", getID(name));
			p += strlen(p);

			*p++ = '\"';
		}
		else *p++ = s[i];

		*p = 0;

		puts(r);
	}	
	return 0;
}