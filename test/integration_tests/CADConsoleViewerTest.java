package integration_tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;
import tmcsim.cadsimulator.viewer.CADConsoleViewer;
import tmcsim.cadsimulator.viewer.model.CADSimulatorModel;
import tmcsim.common.CADEnums;

/**
 * Integration test class for the CADConsoleViewer.
 * @author Jonathan Molina
 */
public class CADConsoleViewerTest extends TestCase
{
    private CADConsoleViewer view;
    private CADSimulatorModel model;
    private StringWriter writer;
    private String initialState;
    private static final Logger kLogger = Logger.getLogger("tmcsim.cadsimulator");

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        model = new CADSimulatorModel();
        writer = new StringWriter();
        view = new CADConsoleViewer();
        initialState = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 0\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        writer.close();
        System.setOut(System.out);
    }

    /**
     * Tests the initial state of the view.
     */
    public void testInitialState()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);

        assertEquals(initialState, writer.toString());
    }
    
    /**
     * Tests the visibility of the state when initially false first and will catch up
     * on the current output.
     */
    public void testSetVisibleTrueWithWriterLater()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(false);
        
        assertEquals("", writer.toString());
        model.connectClient();
        assertEquals("", writer.toString());
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 1\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        view.setVisible(true);
        assertEquals(s1, writer.toString());
    }
    
    /**
     * Tests the state of the view without setting visible to true.
     */
    public void testSetVisibleWithoutSetWriter()
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        
        model.addObserver(view);
        view.setVisible(false);
        assertEquals("", os.toString());
        
        model.connectClient();
        assertEquals("", os.toString());
        
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 1\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        view.setVisible(true);
        assertEquals(s1, os.toString());
    }
    
    /**
     * Tests the output if setVisible() was never called but setWriter() is called.
     */
    public void testNoSetVisibleSetWriter()
    {
        model.addObserver(view);
        view.setWriter(writer);
        
        assertEquals("", writer.toString());
        model.connectClient();
        
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 1\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        assertEquals(s1, writer.toString());
        model.disconnectClient();
        assertEquals(s1 + initialState, writer.toString());
    }

    /**
     * Tests updating the simulation status.
     */
    public void testSimulationStatus()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        String ready = getReadyState();
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED);
        assertEquals(initialState + ready, writer.toString());
        
        String paused = getPausedState();
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_PAUSED_STARTED);
        assertEquals(initialState + ready + paused, writer.toString());
        
        String running = getRunningState();
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.SCRIPT_RUNNING);
        assertEquals(initialState + ready + paused + running, writer.toString());
        
        String synch = getSynchState();
        model.setScriptStatus(CADEnums.SCRIPT_STATUS.ATMS_SYNCHRONIZATION);
        assertEquals(initialState + ready + paused + running + synch, writer.toString());
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
                "\n";
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
                "\n";
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
                "\n";
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
                "\n";
    }
    
    /**
     * Tests updating the number of connected CAD terminals.
     */
    public void testConnectedTerminals()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 1\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        model.connectClient();
        assertEquals(initialState + s1, writer.toString());
        
        String s2 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 2\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        model.connectClient();
        assertEquals(initialState + s1 + s2, writer.toString());

        model.disconnectClient();
        assertEquals(initialState + s1 + s2+ s1, writer.toString());
        model.disconnectClient();
        assertEquals(initialState + s1 + s2 + s1 + initialState, writer.toString());
    }
    
    /**
     * Tests updating the status of the connection to the simulation manager.
     */
    public void testConnectedSimManager()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 0\n" +
                    "Simulation Manager Connected: Yes\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        model.setSimManagerStatus(true);
        assertEquals(initialState + s1, writer.toString());
        model.setSimManagerStatus(true);
        assertEquals(initialState + s1 + s1, writer.toString());
        model.setSimManagerStatus(false);
        assertEquals(initialState + s1 + s1 + initialState, writer.toString());
        model.setSimManagerStatus(false);
        assertEquals(
                initialState + s1 + s1 + initialState + initialState, writer.toString());
    }
    
    /**
     * Tests updating the connection to the paramics.
     */
    public void testConnectedParamics()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 0\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : Yes\n" +
                    "Network Loaded              : None\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        model.setParamicsStatus(CADEnums.PARAMICS_STATUS.CONNECTED);
        assertEquals(initialState + s1, writer.toString());
        model.setParamicsStatus(CADEnums.PARAMICS_STATUS.DISCONNECTED);
        assertEquals(initialState + s1 + initialState, writer.toString());
    }
    
    /**
     * Tests updating the network loaded information.
     */
    public void testNetworkLoaded()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        String s1 = "--- CAD Simulator ---\n" +
                    "Elapsed Simulation Time     : 0:00:00\n" +
                    "Status                      : No Script\n" +
                    "Connected CAD Terminals     : 0\n" +
                    "Simulation Manager Connected: No\n" +
                    "Connected to Paramics       : No\n" +
                    "Network Loaded              : 1\n" +
                    "-- Info Messages --\n" +
                    "\n" +
                    "-- Error Messages --\n" +
                    "\n";
        model.setParamicsNetworkLoaded("1");
        assertEquals(initialState + s1, writer.toString());
        model.setParamicsNetworkLoaded("None");
        assertEquals(initialState + s1 + initialState, writer.toString());
    }
    
    /**
     * Tests updating the elapsed simulation time.
     */
    public void testElapsedTime()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        String s1 = getElapsedTimeState1();
        String s2 = getElapsedTimeState2();
        String s3 = getElapsedTimeState3();
        String s4 = getElapsedTimeState4();
        String s5 = getElapsedTimeState5();

        model.setTime(1);
        assertEquals(initialState + s1, writer.toString());
        model.setTime(59);
        assertEquals(initialState + s1 + s2, writer.toString());
        model.setTime(60);
        assertEquals(initialState + s1 + s2 + s3, writer.toString());
        model.setTime(3599);
        assertEquals(initialState + s1 + s2 + s3 + s4, writer.toString());
        model.setTime(3600);
        assertEquals(initialState + s1 + s2 + s3 + s4 + s5, writer.toString());
    }
    
    private String getElapsedTimeState1()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:01\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    private String getElapsedTimeState2()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:59\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    private String getElapsedTimeState3()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:01:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    private String getElapsedTimeState4()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:59:59\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    private String getElapsedTimeState5()
    {
        return "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 1:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    /**
     * Test should not update the status tab.
     */
    public void testUpdateDVDStatus()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);

        assertEquals(initialState, writer.toString());
        model.updateDVDStatus(new DVDStatusUpdate("test", true));
        assertEquals(initialState, writer.toString());
    }
    
    /**
     * Test should not update the status tab.
     */
    public void testUpdateDVDTitle()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);

        assertEquals(initialState, writer.toString());
        model.updateDVDTitle(new DVDTitleUpdate("test", null, null, true, true));
        assertEquals(initialState, writer.toString());
    }
    
    /**
     * Test the population of info messages.
     */
    public void testInfoMessages()
    {
        Logger kLogger = Logger.getLogger("tmcsim.cadsimulator");
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        assertEquals(initialState, writer.toString());
        
        String s1 = getInfoState1();
        kLogger.logp(Level.INFO, "CADConsoleViewerTest", "testInfoMessages", 
                        "Should show as the first info msg");
        assertEquals(initialState + s1, writer.toString());
        
        String s2 = getInfoState2();
        kLogger.logp(Level.INFO, "CADConsoleViewerTest", "testInfoMessages", 
                        "Should show as the second info msg");
        assertEquals(initialState + s1 + s2, writer.toString());
        
        String s3 = getInfoState3();
        model.connectClient();
        assertEquals(initialState + s1 + s2 + s3, writer.toString());
    }
    
    /**
     * Test info messages printed if setVisible is never called.
     */
    public void testInfoPane()
    {
        model.addObserver(view);
        view.setWriter(writer);

        String expected5 =
                "--- CAD Simulator ---\n"
                + "Elapsed Simulation Time     : 0:00:00\n"
                + "Status                      : No Script\n"
                + "Connected CAD Terminals     : 0\n"
                + "Simulation Manager Connected: No\n"
                + "Connected to Paramics       : No\n"
                + "Network Loaded              : None\n"
                + "-- Info Messages --\n"
                + ". = Console Info Message.\n"
                + "-- Error Messages --\n\n";
        Logger cadSimLogger = Logger.getLogger("tmcsim.cadsimulator");
        cadSimLogger.logp(Level.INFO, "", "", "Console Info Message.");
        System.out.println("output: " + writer.toString());
        assertTrue(writer.toString().endsWith(expected5));
    }
    
    private String getInfoState1()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    private String getInfoState2()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the second info msg" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    private String getInfoState3()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 1\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "CADConsoleViewerTest.testInfoMessages = " +
                "Should show as the second info msg" +
                "\n" +
                "-- Error Messages --\n" +
                "\n";
    }
    
    /**
     * Test the population of error messages.
     */
    public void testErrorMessages()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        assertEquals(initialState, writer.toString());
        
        String s1 = getErrorState1();
        kLogger.logp(Level.SEVERE, "CADConsoleViewerTest", "testErrorMessages", 
                        "Should show as the first error msg");
        assertEquals(initialState + s1, writer.toString());
        
        String s2 = getErrorState2();
        kLogger.logp(Level.SEVERE, "CADConsoleViewerTest", "testErrorMessages", 
                        "Should show as the second error msg");
        assertEquals(initialState + s1 + s2, writer.toString());
        
        String s3 = getErrorState3();
        model.connectClient();
        assertEquals(initialState + s1 + s2 + s3, writer.toString());
    }
    
    private String getErrorState1()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "CADConsoleViewerTest.testErrorMessages = " +
                "Should show as the first error msg" +
                "\n";
    }
    
    private String getErrorState2()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "CADConsoleViewerTest.testErrorMessages = " +
                "Should show as the first error msg" +
                "\n" +
                "CADConsoleViewerTest.testErrorMessages = " +
                "Should show as the second error msg" +
                "\n";
    }
    
    private String getErrorState3()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 1\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "CADConsoleViewerTest.testErrorMessages = " +
                "Should show as the first error msg" +
                "\n" +
                "CADConsoleViewerTest.testErrorMessages = " +
                "Should show as the second error msg" +
                "\n";
    }
    
    /**
     * Test the population of all messages together.
     */
    public void testMessages()
    {
        model.addObserver(view);
        view.setWriter(writer);
        view.setVisible(true);
        
        assertEquals(initialState, writer.toString());
        
        String s1 = getMessagesState1();
        kLogger.logp(Level.SEVERE, "CADConsoleViewerTest", "testMessages", 
                        "Should show as the first error msg");
        assertEquals(initialState + s1, writer.toString());
        
        String s2 = getMessagesState2();
        model.connectClient();
        assertEquals(initialState + s1 + s2, writer.toString());
        
        String s3 = getMessagesState3();
        kLogger.logp(Level.INFO, "CADConsoleViewerTest", "testMessages", 
                        "Should show as the first info msg");
        assertEquals(initialState + s1 + s2 + s3, writer.toString());
    }
    
    private String getMessagesState1()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 0\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "CADConsoleViewerTest.testMessages = " +
                "Should show as the first error msg" +
                "\n";
    }
    
    private String getMessagesState2()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 1\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "\n" +
                "-- Error Messages --\n" +
                "CADConsoleViewerTest.testMessages = " +
                "Should show as the first error msg" +
                "\n";
    }
    
    private String getMessagesState3()
    {
        return  "--- CAD Simulator ---\n" +
                "Elapsed Simulation Time     : 0:00:00\n" +
                "Status                      : No Script\n" +
                "Connected CAD Terminals     : 1\n" +
                "Simulation Manager Connected: No\n" +
                "Connected to Paramics       : No\n" +
                "Network Loaded              : None\n" +
                "-- Info Messages --\n" +
                "CADConsoleViewerTest.testMessages = " +
                "Should show as the first info msg" +
                "\n" +
                "-- Error Messages --\n" +
                "CADConsoleViewerTest.testMessages = " +
                "Should show as the first error msg" +
                "\n";
    }
}
