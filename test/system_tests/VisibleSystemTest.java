package system_tests;

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import tmcsim.cadsimulator.*;
import static junit.framework.Assert.fail;
import org.uispec4j.Button;
import org.uispec4j.TabGroup;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tmcsim.client.cadclientgui.enums.CADScriptTags;
import tmcsim.paramicscommunicator.ParamicsCommunicator;
import tmcsim.simulationmanager.SimulationManager;

/**
 * System test for CADSimulator, Simulation Manager, and Paramics Communicator.
 * @author Jonathan Molina
 */
public class VisibleSystemTest extends UISpecTestCase
{
    private ParamicsCommunicator paramics;
    private SimulationManager simManager;
    private AtomicBoolean paramicsLock = new AtomicBoolean(false);
    private AtomicBoolean simManagerLock = new AtomicBoolean(false);

    /**
     * Tests the interaction between CADSimulator, Simulation Manager, 
     * and Paramics Communicator.
     */
    public void testSystem() throws InterruptedException, ParserConfigurationException, TransformerConfigurationException, TransformerException
    {
        Runnable runParamics = new Runnable() {

            @Override
            public void run()
            {
                try
                {
                    Window paramicsWindow = WindowInterceptor.run(new Trigger()
                    {
                        @Override
                        public void run() throws Exception
                        {
                            try
                            {
                                paramics = new ParamicsCommunicator(
                                        "config/paramics_communicator_config.properties");
                            }
                            catch (Exception exp)
                            {
                                exp.printStackTrace();
                                fail("Couldn't launch Paramics Communicator");
                            }
                        }
                    });
                    Thread.sleep(7000);
//                    while (!paramicsLock.get());
                    // Check for reader 1 tab
//                    paramicsLock.compareAndSet(true, false);
//                    Thread.sleep(1000);
                    TabGroup tabs = paramicsWindow.getTabGroup("fileIOTabs");
                    tabs.selectTab("Reader 1");
                    assertTrue(tabs.getSelectedTab().isVisible());
//                    simManagerLock.compareAndSet(false, true);
                    
//                    while (!paramicsLock.get());
                    // Check for writer 1 tab
//                    Thread.sleep(1000);
                    tabs = paramicsWindow.getTabGroup("fileIOTabs");
                    tabs.selectTab("Writer 0");
                    assertTrue(tabs.getSelectedTab().isVisible());
                    //simManagerLock.compareAndSet(false, true);
                    
                    
                } 
                catch (Exception exp)
                {
                    exp.printStackTrace();
                }
            }
            
        };
        Thread thread = new Thread(runParamics);
        thread.start();
        
        Window simManagerWindow = WindowInterceptor.run(new Trigger()
        {
            @Override
            public void run() throws Exception
            {
                try
                {
                    simManager = new SimulationManager(
                            "config/sim_manager_config.properties");
                }
                catch (Exception exp)
                {
                    exp.printStackTrace();
                    fail("Couldn't launch Simulation Manager");
                }
            }
        });
        Thread.sleep(5000);
        // Test initial sim manager state
        TextBox textBox = simManagerWindow.getTextBox("simulationStatusText");
        assertTrue(textBox.textEquals("No Script"));
        textBox = simManagerWindow.getTextBox("paramicsStatusInfoLabel");
        assertTrue(textBox.textEquals("Unknown"));
        
        // Connect to paramics
        Button button = simManagerWindow.getButton("loadParamicsNetworkButton");
        assertFalse(button.isEnabled());
        button = simManagerWindow.getButton("connectToParamicsButton");
        assertTrue(button.textEquals("Connect to Paramics"));
        button.click();
        
        button = simManagerWindow.getButton("connectToParamicsButton");
        assertTrue(button.textEquals("Disconnect from Paramics"));
        textBox = simManagerWindow.getTextBox("paramicsStatusInfoLabel");
        assertTrue(textBox.textEquals("Connected"));
        button = simManagerWindow.getButton("loadParamicsNetworkButton");
        assertTrue(button.isEnabled());
        
        // Load Network
        Thread.sleep(1000);
        button = simManagerWindow.getButton("loadParamicsNetworkButton");
        button.click();
        textBox = simManagerWindow.getTextBox("paramicsStatusInfoLabel");
        assertTrue(textBox.textEquals("Sending Network ID"));
        button = simManagerWindow.getButton("loadParamicsNetworkButton");
        assertFalse(button.isEnabled());
        
        // update file warming up
        String paramicsStatusXML = "c:\\paramics_status.xml";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        // root element
        Element rootElement = doc.createElement("Paramics");
        doc.appendChild(rootElement);

        // Network_Status element
        Element networkStatusElement = doc.createElement("Network_Status");
        networkStatusElement.appendChild(doc.createTextNode("WARMING"));
        rootElement.appendChild(networkStatusElement);

        // time index
        Element networIDElement = doc.createElement("Network_ID");
        networIDElement.appendChild(doc.createTextNode("1"));
        rootElement.appendChild(networIDElement);
        
        // write to xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(paramicsStatusXML));
        transformer.transform(source, result);
        
        Thread.sleep(5500);
        textBox = simManagerWindow.getTextBox("paramicsStatusInfoLabel");
        assertTrue(textBox.textEquals("Warming Up"));
        
//        // update file network 1 loaded
//         docFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//        Document doc = docBuilder.newDocument();
//        
//        // root element
//        Element rootElement = doc.createElement("Paramics");
//        doc.appendChild(rootElement);
//
//        // Network_Status element
//        Element networkStatusElement = doc.createElement("Network_Status");
//        networkStatusElement.appendChild(doc.createTextNode("WARMING"));
//        rootElement.appendChild(networkStatusElement);
//
//        // time index
//        Element networIDElement = doc.createElement("Network_ID");
//        networIDElement.appendChild(doc.createTextNode("1"));
//        rootElement.appendChild(networIDElement);
//        
//        // write to xml file
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        DOMSource source = new DOMSource(doc);
//        StreamResult result = new StreamResult(new File(paramicsStatusXML));
//        transformer.transform(source, result);
//        
//        Thread.sleep(5500);
//        textBox = simManagerWindow.getTextBox("paramicsStatusInfoLabel");
//        assertTrue(textBox.textEquals("Warming Up"));
    }
}
