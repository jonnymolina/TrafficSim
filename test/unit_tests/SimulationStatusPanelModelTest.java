package unit_tests;

import java.util.Observable;
import java.util.Observer;
import junit.framework.TestCase;
import tmcsim.cadsimulator.viewer.model.SimulationStatusPanelModel;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;

/**
 * Unit test for the SimulationStatusPanelModel class.
 * @author Jonathan Molina
 */
public class SimulationStatusPanelModelTest extends TestCase
{
    private SimulationStatusPanelModel statusModel;
    
    /**
     * Class constructor.
     * @param testName test name
     */
    public SimulationStatusPanelModelTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        statusModel = new SimulationStatusPanelModel();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Tests the default state upon construction.
     */
    public void testDefaultState()
    {
        assertEquals(0, statusModel.getNumClients());
        assertEquals(false, statusModel.isSimManagerConnected());
        assertEquals(0, statusModel.getTimeSegment());
        assertEquals(PARAMICS_STATUS.DISCONNECTED, statusModel.getParamicsStatus());
        assertEquals(SCRIPT_STATUS.NO_SCRIPT, statusModel.getScriptStatus());
        assertEquals(null, statusModel.getNetworkLoaded());
    }

    /**
     * Test of connectClient method, of class SimulationStatusPanelModel.
     */
    public void testConnectClient()
    {
        statusModel.connectClient();
        assertEquals(1, statusModel.getNumClients());
        statusModel.connectClient();
        statusModel.connectClient();
        assertEquals(3, statusModel.getNumClients());
        
        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals(4, model.getNumClients());
                }        
            }
        );
        statusModel.connectClient();
    }

    /**
     * Test of disconnectClient method, of class SimulationStatusPanelModel.
     */
    public void testDisconnectClient()
    {
        assertEquals(0, statusModel.getNumClients());
        statusModel.disconnectClient();
        assertEquals(0, statusModel.getNumClients());
        statusModel.connectClient();
        statusModel.connectClient();
        assertEquals(2, statusModel.getNumClients());
        statusModel.disconnectClient();
        assertEquals(1, statusModel.getNumClients());

        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals(0, statusModel.getNumClients());
                }        
            }
        );
        statusModel.disconnectClient();
    }

    /**
     * Test of setSimManagerStatus method, of class SimulationStatusPanelModel.
     */
    public void testSetSimManagerStatus()
    {
        assertEquals(false, statusModel.isSimManagerConnected());
        statusModel.setSimManagerStatus(true);
        assertEquals(true, statusModel.isSimManagerConnected());

        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals(false, statusModel.isSimManagerConnected());
                }        
            }
        );
        statusModel.setSimManagerStatus(false);
    }

    /**
     * Test of setTime method, of class SimulationStatusPanelModel.
     */
    public void testSetTime()
    {
        assertEquals(0, statusModel.getTimeSegment());
        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals(1000, statusModel.getTimeSegment());
                }        
            }
        );
        statusModel.setTime(1000);
    }

    /**
     * Test of setScriptStatus method, of class SimulationStatusPanelModel.
     */
    public void testSetScriptStatus()
    {
        assertEquals(SCRIPT_STATUS.NO_SCRIPT, statusModel.getScriptStatus());
        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals(SCRIPT_STATUS.SCRIPT_RUNNING,
                            statusModel.getScriptStatus());
                }        
            }
        );
        statusModel.setScriptStatus(SCRIPT_STATUS.SCRIPT_RUNNING);
    }

    /**
     * Test of setParamicsStatus method, of class SimulationStatusPanelModel.
     */
    public void testSetParamicsStatus()
    {
        assertEquals(PARAMICS_STATUS.DISCONNECTED, statusModel.getParamicsStatus());
        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals(PARAMICS_STATUS.CONNECTED,
                            statusModel.getParamicsStatus());
                }        
            }
        );
        statusModel.setParamicsStatus(PARAMICS_STATUS.CONNECTED);
        
    }

    /**
     * Test of setParamicsNetworkLoaded method, of class SimulationStatusPanelModel.
     */
    public void testSetParamicsNetworkLoaded()
    {
        assertEquals(null, statusModel.getNetworkLoaded());
        statusModel.addObserver(
            new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    SimulationStatusPanelModel model = (SimulationStatusPanelModel)o;
                    assertEquals("1", statusModel.getNetworkLoaded());
                }        
            }
        );
        statusModel.setParamicsNetworkLoaded("1");
    }
}
