package integration_tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static org.mockito.Mockito.*;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tmcsim.cadsimulator.CADSimulator;
import tmcsim.cadsimulator.Coordinator;
import tmcsim.cadsimulator.managers.ParamicsSimulationManager;
import tmcsim.client.cadclientgui.enums.CADScriptTags;
import tmcsim.common.CADEnums;
import tmcsim.interfaces.CADClientInterface;
import tmcsim.interfaces.SimulationManagerInterface;

/**
 * Integration test class for CADSimulatorViewer, running through the CADSimulator.
 *
 * @author Jonathan Molina
 */
public class CADSimulatorGUITest extends UISpecTestCase
{
    private CADSimulator cadSim;
    private String cadSimConfigFileName = "cad_simulator_config.properties";
    private String cmsDiversionsFileName = "cmsdiversions.xml";
    private String cadSimParamicsConfigFileName =
        "cad_simulator_paramics_config.properties";
    private String cadSimATMSConfigFileName = "cad_simulator_atms_config.properties";
    private String cadSimMediaConfigFileName = "cad_simulator_media_config.properties";


    /**
     * Set up the interceptor.
     */
    @Override
    public void setUp() throws FileNotFoundException
    {
        try
        {
            PrintWriter writer = new PrintWriter(cadSimConfigFileName);
            writer.print(
                "CADClientPort          = 4444 \n" +
                "CoordinatorRMIPort     = 4445 \n" +
                "CADRmiPort             = 4446\n" +
                "CMSDiversionXML        = " + cmsDivsersionsXML() + "\n" +
                "AudioFileLocation      = " + audioDir() + "\n" +
                "ParamicsProperties     = " + cadSimParamicsConfigFileName + "\n" +
                "ATMSProperties         = "+ cadSimATMSConfigFileName +"\n" +
                "MediaProperties        = " + cadSimMediaConfigFileName + "\n" +
                "UserInterface          = tmcsim.cadsimulator.viewer.CADSimulatorViewer");
            writer.close();
            writer = new PrintWriter(cadSimParamicsConfigFileName);
            writer.print(
                "ParamicsCommHost       = 192.168.251.45\n" +
                "ParamicsCommPort       = 4450\n" +
                "IncidentUpdateInterval = 30\n" +
                "IncidentUpdateFile     = exchange.xml\n" +
                "ParamicsStatusInterval = 15\n" +
                "ParamicsStatusFile     = paramics_status.xml\n" +
                "CameraStatusInterval   = 30\n" +
                "CameraStatusFile       = camera_status.xml");
            writer.close();
            writer = new PrintWriter(cadSimATMSConfigFileName);
            writer.print(
                "ATMSHost = 192.168.251.27\n" +
                "Username = atms_mgr\n" +
                "Password = atms_d12uci1\n" +
                "ImageDir = /opt/d12uci/user_config/cctv\n");
            writer.close();
            writer = new PrintWriter(cadSimMediaConfigFileName);
            writer.print(
                "DVDPlayerXML           = config/dvdplayers.xml\n" +
                "StillImagesXML         = config/stillimages.xml");
            writer.close();
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            fail("Error in setup");
        }
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

    /**
     * Tests the initial state of the view,.
     */
    public void testEntireClass()
    {
        Window window = WindowInterceptor.run(new Trigger()
        {
            @Override
            public void run() throws Exception
            {
                try
                {
                    cadSim = new CADSimulator(cadSimConfigFileName);
                }
                catch (Exception exp)
                {
                    exp.printStackTrace();
                    fail("Couldn't launch CADSimulator");
                }
            }
        });

        // Testing initial state
        window.titleEquals("CAD Simulator");

        TextBox textBox = window.getTextBox("simulationStatusText");
        assertTrue(textBox.textEquals("No Script"));
        textBox = window.getTextBox("termConnectedTF");
        assertTrue(textBox.textEquals("0"));
        textBox = window.getTextBox("managerConnectedTF");
        assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("infoMessagesTA");
        assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("paramicsConnectedTF");
        assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("networkLoadedTF");
        assertTrue(textBox.textEquals("None"));
        textBox = window.getTextBox("errorMessagesTA");
        assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("simulationClockLabel");
        assertTrue(textBox.textEquals("0:00:00"));
        
        Coordinator coord = CADSimulator.theCoordinator;
        textBox = window.getTextBox("simulationStatusText");
        assertTrue(textBox.textEquals("No Script"));

        try
        {
            // Testing client connection and disconnection
            CADClientInterface clientMock1 = mock(CADClientInterface.class);
            CADClientInterface clientMock2 = mock(CADClientInterface.class);

            textBox = window.getTextBox("termConnectedTF");
            assertTrue(textBox.textEquals("0"));

            coord.registerForCallback(clientMock1);
            textBox = window.getTextBox("termConnectedTF");
            assertTrue(textBox.textEquals("1"));

            coord.registerForCallback(clientMock2);
            textBox = window.getTextBox("termConnectedTF");
            assertTrue(textBox.textEquals("2"));

            coord.unregisterForCallback(clientMock1);
            textBox = window.getTextBox("termConnectedTF");
            assertTrue(textBox.textEquals("1"));

            coord.unregisterForCallback(clientMock2);
            textBox = window.getTextBox("termConnectedTF");
            assertTrue(textBox.textEquals("0"));
            
            // Testing sim manager connection
            SimulationManagerInterface simManagerMock = 
                    mock(SimulationManagerInterface.class);
            
            textBox = window.getTextBox("managerConnectedTF");
            assertTrue(textBox.textEquals("No"));

            coord.registerForCallback(simManagerMock);
            textBox = window.getTextBox("managerConnectedTF");
            assertTrue(textBox.textEquals("Yes"));

            coord.unregisterForCallback(simManagerMock);
            textBox = window.getTextBox("managerConnectedTF");
            assertTrue(textBox.textEquals("No"));
            
            // Testing script status
            coord.loadScriptFile(new File(practiceScriptXML()));
            textBox = window.getTextBox("simulationStatusText");
            assertTrue(textBox.textEquals("Ready"));
            
            // Testing connected paramics
            CADSimulator.theParamicsSimMgr = mock(ParamicsSimulationManager.class);
            when(CADSimulator.theParamicsSimMgr.getParamicsStatus())
                    .thenReturn(CADEnums.PARAMICS_STATUS.CONNECTED);
            CADSimulator.theCoordinator.setParamicsStatus(
                    CADSimulator.theParamicsSimMgr.getParamicsStatus());

            textBox = window.getTextBox("paramicsConnectedTF");
            assertTrue(textBox.textEquals("Yes"));
            
            // Testing network loaded
            when(CADSimulator.theParamicsSimMgr.getParamicsNetworkLoaded())
                    .thenReturn(123);

            coord.setParamicsStatus(CADEnums.PARAMICS_STATUS.LOADED);
            textBox = window.getTextBox("networkLoadedTF");
            assertTrue(textBox.textEquals("123"));
            
            // Testing elapsed time
            textBox = window.getTextBox("simulationClockLabel");
            assertTrue(textBox.textEquals("0:00:00"));

            coord.gotoSimulationTime(1);
            textBox = window.getTextBox("simulationClockLabel");
            assertTrue(textBox.textEquals("0:00:01"));

            coord.gotoSimulationTime(59);
            textBox = window.getTextBox("simulationClockLabel");
            assertTrue(textBox.textEquals("0:00:59"));

            coord.gotoSimulationTime(60);
            textBox = window.getTextBox("simulationClockLabel");
            assertTrue(textBox.textEquals("0:01:00"));

            coord.gotoSimulationTime(3599);
            textBox = window.getTextBox("simulationClockLabel");
            assertTrue(textBox.textEquals("0:59:59"));

            coord.gotoSimulationTime(3600);
            textBox = window.getTextBox("simulationClockLabel");
            assertTrue(textBox.textEquals("1:00:00"));
            
            // Testing info messages
            Logger kLogger = Logger.getLogger("tmcsim.cadsimulator");
            
            textBox = window.getTextBox("infoMessagesTA");
            assertTrue(textBox.textEquals(""));

            kLogger.logp(Level.INFO, "CADSimulatorGUITest", "testInfoMessages", 
                            "Should show as the first info msg");
            textBox = window.getTextBox("infoMessagesTA");
            assertTrue(textBox.textEquals(
                    "CADSimulatorGUITest.testInfoMessages = " +
                    "Should show as the first info msg\n"));
            
            // Testing error messages
            textBox = window.getTextBox("errorMessagesTA");
            assertTrue(textBox.textEquals(""));

            kLogger.logp(Level.SEVERE, "CADSimulatorViewerTest", "testErrorMessages", 
                            "Should show as the first error msg");
            textBox = window.getTextBox("errorMessagesTA");
            assertTrue(textBox.textEquals(
                    "CADSimulatorViewerTest.testErrorMessages = " +
                    "Should show as the first error msg\n"));
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            fail("Test failed: " + exp.getClass());
        }
    }
}
