package integration_tests;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tmcsim.cadsimulator.CADSimulator;
import tmcsim.cadsimulator.CADSimulator;
import tmcsim.cadsimulator.CADSimulator;
import tmcsim.cadsimulator.viewer.CADConsoleViewer;
import tmcsim.client.cadclientgui.enums.CADScriptTags;
import tmcsim.common.CADEnums;
import static org.mockito.Mockito.*;
import tmcsim.cadsimulator.managers.ParamicsSimulationManager;
import tmcsim.common.ScriptException;
import tmcsim.interfaces.CADClientInterface;
import tmcsim.interfaces.SimulationManagerInterface;


/**
 * Integration test class for the CADConsoleViewer.
 * @author Jonathan Molina
 */
public class CADSimulatorConsoleTest extends TestCase
{
    private CADSimulator cadSim;
    private CADConsoleViewer view;
    private StringWriter writer;
    private String initialState;
    private String cadSimConfigFileName = "cad_simulator_config.properties";
    private String cmsDiversionsFileName = "cmsdiversions.xml";
    private String cadSimParamicsConfigFileName =
        "cad_simulator_paramics_config.properties";
    private String cadSimATMSConfigFileName = "cad_simulator_atms_config.properties";
    private String cadSimMediaConfigFileName = "cad_simulator_media_config.properties";

    @Override
    protected void setUp() throws Exception
    {
        initialState = 
            "--- CAD Simulator ---\n" +
            "Elapsed Simulation Time     : 0:00:00\n" +
            "Status                      : No Script\n" +
            "Connected CAD Terminals     : 0\n" +
            "Simulation Manager Connected: No\n" +
            "Connected to Paramics       : No\n" +
            "Network Loaded              : None\n" +
            "-- Info Messages --\n" +
            "\n" +
            "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
        PrintWriter pWriter = new PrintWriter(cadSimConfigFileName);
        pWriter.print(
            "CADClientPort          = 4444 \n" +
            "CoordinatorRMIPort     = 4445 \n" +
            "CADRmiPort             = 4446\n" +
            "CMSDiversionXML        = " + cmsDivsersionsXML() + "\n" +
            "AudioFileLocation      = " + audioDir() + "\n" +
            "ParamicsProperties     = " + cadSimParamicsConfigFileName + "\n" +
            "ATMSProperties         = "+ cadSimATMSConfigFileName +"\n" +
            "MediaProperties        = " + cadSimMediaConfigFileName + "\n" +
            "UserInterface          = tmcsim.cadsimulator.viewer.CADConsoleViewer");
        pWriter.close();
        pWriter = new PrintWriter(cadSimParamicsConfigFileName);
        pWriter.print(
            "ParamicsCommHost       = 192.168.251.45\n" +
            "ParamicsCommPort       = 4450\n" +
            "IncidentUpdateInterval = 30\n" +
            "IncidentUpdateFile     = exchange.xml\n" +
            "ParamicsStatusInterval = 15\n" +
            "ParamicsStatusFile     = paramics_status.xml\n" +
            "CameraStatusInterval   = 30\n" +
            "CameraStatusFile       = camera_status.xml");
        pWriter.close();
        pWriter = new PrintWriter(cadSimATMSConfigFileName);
        pWriter.print(
            "ATMSHost = 192.168.251.27\n" +
            "Username = atms_mgr\n" +
            "Password = atms_d12uci1\n" +
            "ImageDir = /opt/d12uci/user_config/cctv\n");
        pWriter.close();
        pWriter = new PrintWriter(cadSimMediaConfigFileName);
        pWriter.print(
            "DVDPlayerXML           = config/dvdplayers.xml\n" +
            "StillImagesXML         = config/stillimages.xml");
        pWriter.close();
        writer = new StringWriter();
        cadSim = new CADSimulator(cadSimConfigFileName);
        view = (CADConsoleViewer) CADSimulator.theViewer;
    }
    
    /**
     * Creates the XML script file and returns the file name.
     * @return file name
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public String practiceScriptXML()
        throws ParserConfigurationException, TransformerException
    {
        String scriptFileName = "practice_script.xml";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        // root element
        Element rootElement = doc.createElement(CADScriptTags.SCRIPT_LEVEL_TAGS.TMC_SCRIPT.tag);
        rootElement.setAttribute("title", "Practice Simulation");
        doc.appendChild(rootElement);

        // script event
        Element scriptEvent = doc.createElement(CADScriptTags.SCRIPT_LEVEL_TAGS.SCRIPT_EVENT.tag);
        rootElement.appendChild(scriptEvent);

        // time index
        Element timeIndex = doc.createElement(CADScriptTags.SCRIPT_EVENT_TAGS.TIME_INDEX.tag);
        timeIndex.appendChild(doc.createTextNode("00:00:00"));
        scriptEvent.appendChild(timeIndex);
        
        // incdent
        Element incident = doc.createElement(CADScriptTags.SCRIPT_EVENT_TAGS.INCIDENT.tag);
        incident.setAttribute("LogNum", "100");
        incident.appendChild(doc.createTextNode("Media Log"));
        scriptEvent.appendChild(incident);
        
        // cad data
        Element cadData = doc.createElement(CADScriptTags.SCRIPT_EVENT_TAGS.CAD_DATA.tag);
        scriptEvent.appendChild(cadData);
        
        // header info
        Element headerInfo = doc.createElement(CADScriptTags.SCRIPT_LEVEL_TAGS.HEADER_INFO.tag);
        cadData.appendChild(headerInfo);
        
        // type
        Element type = doc.createElement(CADScriptTags.UNIT_TAGS.TYPE.tag);
        headerInfo.appendChild(type);
        
        // beat
        Element beat = doc.createElement(CADScriptTags.INCIDENT_LOCATION_TAGS.BEAT.tag);
        headerInfo.appendChild(beat);
        
        // truncloc
        Element truncLoc = doc.createElement(tmcsim.common.CADScriptTags.INCIDENT_HEADER_TAGS.TRUNC_LOCATION.tag);
        headerInfo.appendChild(truncLoc);
        
        // fullloc
        Element fullLoc = doc.createElement(tmcsim.common.CADScriptTags.INCIDENT_HEADER_TAGS.FULL_LOCATION.tag);
        headerInfo.appendChild(fullLoc);
        
        // cad incident event
        Element cadIncidentEvent = doc.createElement(tmcsim.common.CADScriptTags.SCRIPT_LEVEL_TAGS.CAD_INCIDENT_EVENT.tag);
        cadData.appendChild(cadIncidentEvent);
        
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(scriptFileName));
        transformer.transform(source, result);
        
        return scriptFileName;
    }
    
    /**
     * Creates the cms diversions XML file and returns the file name.
     * @return the file name
     * @throws ParserConfigurationException
     * @throws TransformerException 
     */
    public String cmsDivsersionsXML()
        throws ParserConfigurationException, TransformerException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        // root element - diversion list
        Element rootElement = doc.createElement("DIVERSION_LIST");
        doc.appendChild(rootElement);
        
        // cms
        Element cms = doc.createElement("CMS");
        rootElement.appendChild(cms);
        
        // id
        Element id = doc.createElement("ID");
        id.appendChild(doc.createTextNode("95 - NB 5 @ El Toro"));
        cms.appendChild(id);
        
        // postmile
        Element postmile = doc.createElement("POSTMILE");
        postmile.appendChild(doc.createTextNode("19.0"));
        cms.appendChild(postmile);
        
        // init route
        Element initRoute = doc.createElement("INIT_ROUTE");
        initRoute.appendChild(doc.createTextNode("NB 5"));
        cms.appendChild(initRoute);
        
        // diversion
        Element diversion = doc.createElement("DIVERSION");
        cms.appendChild(diversion);
        
        // orig path
        Element origPath = doc.createElement("ORIG_PATH");
        origPath.appendChild((doc.createTextNode("NB 5")));
        diversion.appendChild(origPath);
        
        // new path
        Element newPath = doc.createElement("NEW_PATH");
        newPath.appendChild(doc.createTextNode("NB 405"));
        diversion.appendChild(newPath);
        
        // div path
        Element divPath = doc.createElement("DIV_PATH");
        divPath.appendChild(doc.createTextNode("N5_N5_N405"));
        diversion.appendChild(divPath);
        
        // max diversion
        Element maxDiv = doc.createElement("MAX_DIVERSION");
        maxDiv.appendChild(doc.createTextNode("50"));
        diversion.appendChild(maxDiv);
        
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(cmsDiversionsFileName));
        transformer.transform(source, result);

        return cmsDiversionsFileName;
    }

    /**
     * Creates an empty audio directory and returns the name of the directory.
     * @return the directory name
     */
    public String audioDir()
    {
        String dir = "audios";
        new File(dir).mkdir();
        return dir;
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        writer.close();
        System.setOut(System.out);
    }
    
    public void testAll() throws RemoteException, InterruptedException, ScriptException, ParserConfigurationException, TransformerException
    {
        // Wait for error messages to populate
        view.setWriter(writer);
        view.setVisible(true);
        Thread.sleep(30000);
        
        // Testing initial state
        assertTrue(writer.toString().endsWith(initialState));
        
        // Testing client connections
        String c1 = 
            "--- CAD Simulator ---\n" +
            "Elapsed Simulation Time     : 0:00:00\n" +
            "Status                      : No Script\n" +
            "Connected CAD Terminals     : 1\n" +
            "Simulation Manager Connected: No\n" +
            "Connected to Paramics       : No\n" +
            "Network Loaded              : None\n" +
            "-- Info Messages --\n" +
            "\n" +
            "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
        CADClientInterface clientMock1 = mock(CADClientInterface.class);
        try
        {
            CADSimulator.theCoordinator.registerForCallback(clientMock1);
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
            fail("Could connect client");
        }
        assertTrue(writer.toString().endsWith(c1));
        
        String c2 = 
            "--- CAD Simulator ---\n" +
            "Elapsed Simulation Time     : 0:00:00\n" +
            "Status                      : No Script\n" +
            "Connected CAD Terminals     : 2\n" +
            "Simulation Manager Connected: No\n" +
            "Connected to Paramics       : No\n" +
            "Network Loaded              : None\n" +
            "-- Info Messages --\n" +
            "\n" +
            "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
        CADClientInterface clientMock2 = mock(CADClientInterface.class);
        try
        {
            CADSimulator.theCoordinator.registerForCallback(clientMock2);
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
            fail("Could connect client");
        }
        assertTrue(writer.toString().endsWith(c2));

        try
        {
            CADSimulator.theCoordinator.unregisterForCallback(clientMock1);
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
            fail("Could connect client");
        }
        assertTrue(writer.toString().endsWith(c1));
        try
        {
            CADSimulator.theCoordinator.unregisterForCallback(clientMock2);
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
            fail("Could connect client");
        }
        assertTrue(writer.toString().endsWith(initialState));
        
        // Testing simulation manager connection
        String connectedSim = 
            "--- CAD Simulator ---\n" +
            "Elapsed Simulation Time     : 0:00:00\n" +
            "Status                      : No Script\n" +
            "Connected CAD Terminals     : 0\n" +
            "Simulation Manager Connected: Yes\n" +
            "Connected to Paramics       : No\n" +
            "Network Loaded              : None\n" +
            "-- Info Messages --\n" +
            "\n" +
            "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
        SimulationManagerInterface simMock = mock(SimulationManagerInterface.class);
        
        CADSimulator.theCoordinator.registerForCallback(simMock);
        assertTrue(writer.toString().endsWith(connectedSim));
        
        CADSimulator.theCoordinator.unregisterForCallback(simMock);
        assertTrue(writer.toString().endsWith(initialState));
        
        // Testing simulation status
        String ready = getReadyState();
        CADSimulator.theCoordinator.loadScriptFile(new File(practiceScriptXML()));
        Thread.sleep(500);
        assertTrue(writer.toString().endsWith(ready));
        
        String paused = getPausedState();
        CADSimulator.theCoordinator.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_PAUSED_STARTED);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(paused));
        
        String running = getRunningState();
        CADSimulator.theCoordinator.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_RUNNING);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(running));
        
        String synch = getSynchState();
        CADSimulator.theCoordinator.setScriptStatus(CADEnums.SCRIPT_STATUS.ATMS_SYNCHRONIZATION);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(synch));
        
        CADSimulator.theCoordinator.setScriptStatus(CADEnums.SCRIPT_STATUS.NO_SCRIPT);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(initialState));
        
        // Testing paramics status
        String paramics1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 0\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : Yes\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
        CADSimulator.theParamicsSimMgr = mock(ParamicsSimulationManager.class);
        when(CADSimulator.theParamicsSimMgr.getParamicsStatus())
                .thenReturn(CADEnums.PARAMICS_STATUS.CONNECTED);

        CADSimulator.theCoordinator.setParamicsStatus(
                CADSimulator.theParamicsSimMgr.getParamicsStatus());
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(paramics1));        

        when(CADSimulator.theParamicsSimMgr.getParamicsStatus())
                .thenReturn(CADEnums.PARAMICS_STATUS.DISCONNECTED);
        CADSimulator.theCoordinator.setParamicsStatus(
                CADSimulator.theParamicsSimMgr.getParamicsStatus());
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(initialState));
        
        // Testing network loaded
        String network1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 0\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : 1\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
        CADSimulator.theParamicsSimMgr = mock(ParamicsSimulationManager.class);
        when(CADSimulator.theParamicsSimMgr.getParamicsNetworkLoaded())
                    .thenReturn(1);
        CADSimulator.theCoordinator.setParamicsStatus(CADEnums.PARAMICS_STATUS.LOADED);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(network1));
        
        // Test elapsed simulation time
        String s1 = getElapsedTimeState1();
        String s2 = getElapsedTimeState2();
        String s3 = getElapsedTimeState3();
        String s4 = getElapsedTimeState4();
        String s5 = getElapsedTimeState5();

        CADSimulator.theCoordinator.gotoSimulationTime(1);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(s1));
        CADSimulator.theCoordinator.gotoSimulationTime(59);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(s2));
        CADSimulator.theCoordinator.gotoSimulationTime(60);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(s3));
        CADSimulator.theCoordinator.gotoSimulationTime(3599);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(s4));
        CADSimulator.theCoordinator.gotoSimulationTime(3600);
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(s5));
        CADSimulator.theCoordinator.gotoSimulationTime(0);
        Thread.sleep(200);
        
        // Test info messages
        Logger kLogger = Logger.getLogger("tmcsim.cadsimulator");
        view.setWriter(writer);
        
        String info1 = getInfoState1();
        kLogger.logp(Level.INFO, "CADConsoleViewerTest", "testInfoMessages", 
                        "Should show as the first info msg");
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(info1));
        
        String info2 = getInfoState2();
        kLogger.logp(Level.INFO, "CADConsoleViewerTest", "testInfoMessages", 
                        "Should show as the second info msg");
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(info2));
        
        // Test error messages
        String error1 = getErrorState1();
        kLogger.logp(Level.SEVERE, "CADConsoleViewerTest", "testErrorMessages", 
                        "Should show as the first error msg");
        Thread.sleep(200);
        assertTrue(writer.toString().endsWith(error1));
        
    }
    
    private String getReadyState()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : Ready\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getPausedState()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : Paused\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getRunningState()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : Running\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getSynchState()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : Synchronizing\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getElapsedTimeState1()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:01\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getElapsedTimeState2()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:59\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getElapsedTimeState3()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:01:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getElapsedTimeState4()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:59:59\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getElapsedTimeState5()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 1:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getInfoState1()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getInfoState2()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the second info msg" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }
    
    private String getInfoState3()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 1\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the second info msg" +
                "\n" +
                "-- Error Messages --\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
            "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n";
    }

    private String getErrorState1()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : 1\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the second info msg" +
                "\n" +
                "-- Error Messages --\n" +
                "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3004\n" +
                "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3001\n" +
                "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3003\n" +
                "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3002\n" +
                "DVDPlayerDB.loadFromXML:runnable = IOException in connecting DVD 192.168.251.9:3005\n" +
                "CADConsoleViewerTest.testErrorMessages = " +
                "Should show as the first error msg" +
                "\n";
    }
}
