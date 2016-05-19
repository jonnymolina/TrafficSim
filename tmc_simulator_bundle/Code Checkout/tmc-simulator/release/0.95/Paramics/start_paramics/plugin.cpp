 /* ----------------------------------------------------------------------- 
 * Loading Paramics automatically when CAD simulator is started
 * Version:	Paramics Build V5
 * Author:	Lianyu Chu 
 * California ATMS Testbed, University of California, Irvine 
 * Email: lchu@translab.its.uci.edu
 * -----------------------------------------------------------------------*/  

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include "Markup.h"
#include <iostream.h>

typedef struct simulation_network SIM_NET;
struct simulation_network
{
	int ID;
	char dir[100];
	char name[100];
};

// Input files' name
static char *g_control_file = "exchange.xml";
static char *g_networkFinder = "networkFinder.cfg";
static char *g_camera_status = "camera_status.xml";
static char *TMC_ROOT_DIR = "c:/tmc_simulator";

static int g_scenario;
static int g_comInterval;
static int g_networkNum;
static short g_simulationStart;
static short g_incidentStart; 
static SIM_NET *abc;

int pp_open_exchange_file(void)
{
	CString csText, csNotes;
	CFile file;
	char buffer[256];
	CFileFind finder;
	BOOL found = FALSE;

	sprintf(buffer, "%s/%s", TMC_ROOT_DIR, g_control_file);
	found = finder.FindFile(buffer);
	if (!found)
		return -1;

	if (!file.Open(buffer, CFile::modeRead))
	{
		return 0;
	}
	int nFileLen = (int)file.GetLength();

	// Allocate buffer for binary file data
	unsigned char* pBuffer = new unsigned char[nFileLen + 2];
	nFileLen = file.Read( pBuffer, nFileLen );
	pBuffer[nFileLen] = '\0';
	pBuffer[nFileLen+1] = '\0'; // in case 2-byte encoded
	file.Close();

	// Windows Unicode file is detected if starts with FEFF
	if ( pBuffer[0] == 0xff && pBuffer[1] == 0xfe )
	{
		// Contains byte order mark, so assume wide char content
		// non _UNICODE builds should perform UCS-2 (wide char) to UTF-8 conversion here
		csText = (LPCWSTR)(&pBuffer[2]);
		csNotes += _T("File starts with hex FFFE, assumed to be wide char format. ");
	}
	else
	{
		// _UNICODE builds should perform UTF-8 to UCS-2 (wide char) conversion here
		csText = (LPCSTR)pBuffer;
	}
	delete [] pBuffer;

	// If it is too short, assume it got truncated due to non-text content
	if ( csText.GetLength() < nFileLen / 2 - 20 )
	{
		printf("Error converting file to string (may contain binary data)\n");
		return 2;
	}

	CMarkup xml;
	BOOL bResult = xml.SetDoc( csText );
	BOOL bFinished = FALSE;

	xml.ResetPos();
	if (!xml.FindElem())
		return 1;

	if (!xml.FindChildElem("Basic"))
	{
		printf("missing element: Basic\n");
		return 2;
	}
	else
	{
		xml.IntoElem();
		if (!xml.FindChildElem("Comm_Interval"))
		{
			printf("missing element: Comm_Interval\n");
			return 2;
		}
		else
			g_comInterval = atoi(xml.GetChildData());

		xml.ResetChildPos();
		if (!xml.FindChildElem("Network_ID"))
		{
			printf("missing element: Network_ID\n");
			return 2;
		}
		else
			g_scenario = atoi(xml.GetChildData());

		xml.ResetChildPos();
		if (!xml.FindChildElem("Simulation"))
		{
			printf("missing element: Simulation\n");
			return 2;
		}
		else
		{
			if (!strcmp(xml.GetChildData(), "TRUE"))
				g_simulationStart = TRUE;
			else
				g_simulationStart = FALSE;
		}

		xml.ResetChildPos();
		if (!xml.FindChildElem("Incident"))
		{
			printf("missing element: Incident\n");
			return 2;
		}
		else
		{
			if (!strcmp(xml.GetChildData(), "TRUE"))
				g_incidentStart = TRUE;
			else
				g_incidentStart = FALSE;
		}
		xml.OutOfElem();
	}
	
/*
	// Simulation_Data
	if(g_simulationStart)
	{
		printf("CAD simulator has started simulation but Paramics doesn't warm up yet\n");
		printf("It's a possible error from CAD Simulator\n");
	}

	// incident data
	if(g_incidentStart)
	{
		printf("CAD simulator has started incident but Paramics doesn't warm up yet\n");
		printf("It's a possible error from CAD Simulator\n");
	}
*/

	file.Close();

	return 3;
}

BOOL pp_write_status_file()
{
	CFile file;
	char buffer[256], tmp[30];
	
	sprintf(buffer, "%s/paramics_status.xml", TMC_ROOT_DIR);
	if (!file.Open(buffer, CFile::modeCreate | CFile::modeWrite))
		return FALSE;

	CMarkup xml;
	xml.AddElem("Paramics");
	
	xml.AddChildElem("Network_Status", "LOADING");

	sprintf(tmp, "%d", g_scenario);
	xml.AddChildElem("Network_ID", tmp);
	CString csXML = xml.GetDoc();

	file.Write(csXML, csXML.GetLength());
	file.Close();

	return TRUE;
}

int pp_call_paramics()
{
	// ? how to close Paramcis window
	int i;
	short found = -1;
	char buffer[100];

	for (i = 0; i < g_networkNum; i++)
	{
		if(abc[i].ID == g_scenario)
		{
			found = i;
			break;
		}
	}

	if (found >= 0)
	{
		cout<<"...simulation network: "<<abc[found].name <<endl;
		cout<<"...located at "<< abc[found].dir <<endl;
		sprintf(buffer, "Modeller -cmd -going %s", abc[found].dir);
		if (pp_write_status_file())
			system(buffer);
		return 1;
	}
	else
		return 0;
}

int main()
{
	int status, i, back = 0;
	FILE *file, *file1;
	char buffer[100];

	// program head
	printf("-------------------------------------------\n\n");
	printf("Program name: TMC Simulator\n");
	printf("Author:       California ATMS Testbed\n\n");
	printf("-------------------------------------------\n");

	// open network_finder files
	sprintf(buffer, "%s/%s", TMC_ROOT_DIR, g_networkFinder);
	if ((file = fopen(buffer, "rt")) == NULL)
		cout<<"ERROR: cann't open the networkFinder file under "<<TMC_ROOT_DIR<<endl; 
	fscanf(file, "number of networks: %d\n", &g_networkNum);
	abc = new SIM_NET[g_networkNum];
	for (i = 0; i < g_networkNum; i++)
		fscanf(file, "%d %s %s\n", &abc[i].ID, abc[i].name, abc[i].dir);

	// check the existence of camera_status.xml file
	sprintf(buffer, "%s/%s", TMC_ROOT_DIR, g_camera_status);
	if ((file1 = fopen(buffer, "rt")) == NULL)
	{
		cout<<"camera_status.xml doesn't exist under folder"<<TMC_ROOT_DIR<<endl; 
		cout<<"... create file: camera_status.xml"<<endl;
		if ((file1 = fopen(buffer, "wt")) == NULL)
			cout<<"can't create camera_status.xml"<<endl; 
		fclose(file1);
	}


	// waiting fro data from CAD Simulator
	cout<<endl<<"Waiting for CAD Simulation Manager to start"<<endl;

	status = pp_open_exchange_file();
	if(status == 3)
	{
		back = pp_call_paramics();
		if (back == 1)
		{
			cout<<"......load Paramics network successfully!"<<endl;
			exit(0);
		}
		else
			cout<<"......No corresponding network is found"<<endl;
	}
	else
	{ 
		if (status == 1)
			cout<<"...empty file"<<endl;
		else if (status == -1) 
			cout<<"? no file found"<<endl;
		else if(status == 0) 
			cout<<"? unable to open file"<<endl;
		else if(status == -3)
			cout<<"? unable to clear file"<<endl;
		else if(status == 2)
		{
			cout<<"? incomplete data from CAD"<<endl;
			cout<<"? check exchange.xml file"<<endl;
			exit(0);
		}
		Sleep(2000);
		cout<<"...Waiting..."<<endl;

		// Waiting for data from CAD
		while(1)
		{
			Sleep(2000);
			cout<<"."<<endl;
			if(pp_open_exchange_file() == 3)
			{
				back = pp_call_paramics();
				if (back == 1)
				{
					cout<<"......Load Paramics network successfully!"<<endl;
					exit(0);
				}
				else
					cout<<"......No corresponding network is found"<<endl;
			}
		}
	}
	return 0;
}

