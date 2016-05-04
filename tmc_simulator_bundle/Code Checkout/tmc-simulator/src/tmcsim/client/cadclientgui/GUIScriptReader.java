package tmcsim.client.cadclientgui;

import java.io.File;
import java.rmi.RemoteException;

import javax.xml.parsers.SAXParserFactory;

import tmcsim.client.cadclientgui.data.CADData;

/**
 * This class loads the XML script and parses through it in the ScriptHandler. Finally it stores the data from the XML 
 * into the CADData.
 * @author Nicholas
 *
 */
public class GUIScriptReader {
    
    public GUIScriptReader(String fp, CADData cadData) throws RemoteException
    {
        loadScriptFile(new File(fp),cadData);
    }
    
    /*
     * loads the script from the XML and stores the data in the CADData class.
     */
    public static void loadScriptFile(File scriptFile,CADData cadData) throws RemoteException {     
        try {
                
            ScriptHandler sh = new ScriptHandler();
            SAXParserFactory.newInstance().newSAXParser().parse(scriptFile, sh);
            //cadData.setUnitsFromXML(sh.getUnits());
            cadData.setIncidentsFromXML(sh.getIncidents());
               
        }
        catch (Exception e) {e.printStackTrace();}
    }   
}
