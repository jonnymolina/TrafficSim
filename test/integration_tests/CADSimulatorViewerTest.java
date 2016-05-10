package integration_tests;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.uispec4j.TextBox; 
import org.uispec4j.assertion.UISpecAssert;
import org.uispec4j.Window;
import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;
import tmcsim.cadsimulator.viewer.CADSimulatorViewer;
import tmcsim.cadsimulator.viewer.CADSimulatorViewer;
import tmcsim.cadsimulator.viewer.model.CADSimulatorModel;
import tmcsim.common.CADEnums;

/**
 * Integration test class for CADSimulatorViewer.
 * @author Jonathan Molina
 */
public class CADSimulatorViewerTest extends TestCase
{
    private CADSimulatorViewer view; 
    private CADSimulatorModel model;
    private Window window;
    private static final Logger kLogger = Logger.getLogger("tmcsim.cadsimulator");
    
    /**
     * Set up the interceptor.
     */
    @Override
    public void setUp()
    {
        view = new CADSimulatorViewer();
        model = new CADSimulatorModel();
        model.addObserver(view);
        view.setVisible(false);
        window = new Window(view);
    }
    
    /**
     * Tests the initial state of the view.
     */
    public void testInitialState()
    {
        UISpecAssert.assertTrue(window.titleEquals("CAD Simulator"));
        
        TextBox textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("No Script"));
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("None"));
        textBox = window.getTextBox("errorMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:00"));
    }
    
    /**
     * Tests updating the simulation status.
     */
    public void testSimulationStatus()
    {
        TextBox textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("No Script"));
        
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED);
        textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("Ready"));
        
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_PAUSED_STARTED);
        textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("Paused"));
        
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_RUNNING);
        textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("Running"));
        
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.ATMS_SYNCHRONIZATION);
        textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("Synchronizing"));
    }
    
    /**
     * Tests updating the number of connected CAD terminals.
     */
    public void testConnectedTerminals()
    {
        TextBox textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        assertEquals(0, model.getNumClients());
        
        model.connectClient();
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("1"));
        assertEquals(1, model.getNumClients());
        
        model.connectClient();
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("2"));
        assertEquals(2, model.getNumClients());
        
        model.disconnectClient();
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("1"));
        assertEquals(1, model.getNumClients());
        
        model.disconnectClient();
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        assertEquals(0, model.getNumClients());
        
        model.disconnectClient();
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        assertEquals(0, model.getNumClients());
    }
    
    /**
     * Tests updating the status of the connection to the simulation manager.
     */
    public void testConnectedSimManager()
    {
        TextBox textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        
        model.setSimManagerStatus(true);
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("Yes"));
        assertTrue(model.isSimManagerConnected());
        
        model.setSimManagerStatus(true);
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("Yes"));
        assertTrue(model.isSimManagerConnected());
        
        model.setSimManagerStatus(false);
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        assertFalse(model.isSimManagerConnected());
        
        model.setSimManagerStatus(false);
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        assertFalse(model.isSimManagerConnected());
    }
    
    /**
     * Tests updating the connection to the paramics.
     */
    public void testConnectedParamics()
    {
        TextBox textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        
        model.setParamicsStatus(CADEnums.PARAMICS_STATUS.CONNECTED);
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("Yes"));
        
        model.setParamicsStatus(CADEnums.PARAMICS_STATUS.CONNECTED);
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("Yes"));
        
        model.setParamicsStatus(CADEnums.PARAMICS_STATUS.DISCONNECTED);
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        
        model.setParamicsStatus(CADEnums.PARAMICS_STATUS.CONNECTING);
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
    }
    
    /**
     * Tests updating the network loaded information.
     */
    public void testNetworkLoaded()
    {
        TextBox textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("None"));
        
        model.setParamicsNetworkLoaded("1");
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("1"));
        
        model.setParamicsNetworkLoaded("2");
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("2"));
        
        model.setParamicsNetworkLoaded("Something");
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("Something"));
    }
    
    /**
     * Tests updating the elapsed simulation time.
     */
    public void testElapsedTime()
    {
        TextBox textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:00"));
        
        model.setTime(1);
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:01"));
        
        model.setTime(59);
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:59"));
        
        model.setTime(60);
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:01:00"));
        
        model.setTime(3599);
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:59:59"));
        
        model.setTime(3600);
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("1:00:00"));
    }
    
    /**
     * Test should not update the status tab.
     */
    public void testUpdateDVDStatus()
    {
        UISpecAssert.assertTrue(window.titleEquals("CAD Simulator"));
        
        TextBox textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("No Script"));
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("None"));
        textBox = window.getTextBox("errorMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:00"));
        
        model.updateDVDStatus(new DVDStatusUpdate("test", true));
        
        textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("No Script"));
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("None"));
        textBox = window.getTextBox("errorMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:00"));
    }
    
    /**
     * Test should not update the status tab.
     */
    public void testUpdateDVDTitle()
    {
        UISpecAssert.assertTrue(window.titleEquals("CAD Simulator"));
        
        TextBox textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("No Script"));
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("None"));
        textBox = window.getTextBox("errorMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:00"));
        
        model.updateDVDTitle(new DVDTitleUpdate(null, null, null, true, true));
        
        textBox = window.getTextBox("simulationStatusText");
        UISpecAssert.assertTrue(textBox.textEquals("No Script"));
        textBox = window.getTextBox("termConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("0"));
        textBox = window.getTextBox("managerConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("paramicsConnectedTF");
        UISpecAssert.assertTrue(textBox.textEquals("No"));
        textBox = window.getTextBox("networkLoadedTF");
        UISpecAssert.assertTrue(textBox.textEquals("None"));
        textBox = window.getTextBox("errorMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        textBox = window.getTextBox("simulationClockLabel");
        UISpecAssert.assertTrue(textBox.textEquals("0:00:00"));
    }
    
    /**
     * Test the population of info messages.
     */
    public void testInfoMessages()
    {
        TextBox textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        
        kLogger.logp(Level.INFO, "CADSimulatorViewerTest", "testInfoMessages", 
                        "Should show as the first info msg");
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(
                textBox.textEquals(
                "CADSimulatorViewerTest.testInfoMessages = " +
                "Should show as the first info msg\n"));
        
        kLogger.logp(Level.INFO, "CADSimulatorViewerTest", "testInfoMessages", 
                        "Should show as the second info msg");
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(
                textBox.textEquals(
                "CADSimulatorViewerTest.testInfoMessages = " +
                "Should show as the first info msg\n" +
                "CADSimulatorViewerTest.testInfoMessages = " +
                "Should show as the second info msg\n"));
    }
    
    /**
     * Test the population of info messages.
     */
    public void testErrorMessages()
    {
        TextBox textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(textBox.textEquals(""));
        
        kLogger.logp(Level.INFO, "CADSimulatorViewerTest", "testErrorMessages", 
                        "Should show as the first error msg");
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(
                textBox.textEquals(
                "CADSimulatorViewerTest.testErrorMessages = " +
                "Should show as the first error msg\n"));
        
        kLogger.logp(Level.INFO, "CADSimulatorViewerTest", "testErrorMessages", 
                        "Should show as the second error msg");
        textBox = window.getTextBox("infoMessagesTA");
        UISpecAssert.assertTrue(
                textBox.textEquals(
                "CADSimulatorViewerTest.testErrorMessages = " +
                "Should show as the first error msg\n" +
                "CADSimulatorViewerTest.testErrorMessages = " +
                "Should show as the second error msg\n"));
    }
}
