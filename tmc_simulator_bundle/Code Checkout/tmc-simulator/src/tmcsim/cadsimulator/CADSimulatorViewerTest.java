package tmcsim.cadsimulator;

import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;
import tmcsim.common.CADEnums;
import tmcsim.common.SimulationException;

/**
 * Integration test class for CADSimulatorViewer.
 * @author Jonathan Molina
 */
public class CADSimulatorViewerTest extends UISpecTestCase
{
    private CADSimulator cadSim;

    /**
     * Tests the simulation status field.
     * @pre need to add JUnit 4 to libraries
     * @throws SimulationException 
     */
    public void testSimulationStatusField() throws SimulationException
    {
        System.setProperty(
                "CAD_SIM_PROPERTIES", "config/cad_simulator_config.properties");
        WindowInterceptor.init(new Trigger()
            {
                @Override
                public void run() throws Exception
                {
                    cadSim = new CADSimulator(System.getProperty("CAD_SIM_PROPERTIES"));
                }
            
            }
        ).process(new WindowHandler()
            {
                @Override
                public Trigger process(Window window) throws Exception
                {
                    TextBox simStatusText = window.getTextBox("simulationStatusText");
                    assertTrue(simStatusText.textEquals("No Script"));
                    assertTrue(window.titleEquals("CAD Simulator"));
                    CADSimulator.theCoordinator
                            .setScriptStatus(
                            CADEnums.SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED);
                    assertTrue(simStatusText.textEquals("Ready"));                    
                    return Trigger.DO_NOTHING;
                }
            }
        ).run();        
    }
}
