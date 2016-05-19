package system_tests;

import java.util.concurrent.atomic.AtomicBoolean;
import tmcsim.cadsimulator.*;
import static junit.framework.Assert.fail;
import org.uispec4j.Button;
import org.uispec4j.TabGroup;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;
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
    public void testSystem() throws InterruptedException
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
                    
                    while (!paramicsLock.get());
                    // Check for reader 1 tab
                    paramicsLock.compareAndSet(true, false);
                    Thread.sleep(1000);
                    TabGroup tabs = paramicsWindow.getTabGroup("fileIOTabs");
                    tabs.selectTab("Reader 1");
                    assertTrue(tabs.getSelectedTab().isVisible());
                    simManagerLock.compareAndSet(false, true);
                    
                    while (!paramicsLock.get());
                    // Check for writer 1 tab
                    Thread.sleep(1000);
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
        paramicsLock.compareAndSet(false, true);
        
        // Load Network
        while (!simManagerLock.get());
        Thread.sleep(1000);
        button = simManagerWindow.getButton("loadParamicsNetworkButton");
        button.click();
        textBox = simManagerWindow.getTextBox("paramicsStatusInfoLabel");
        assertTrue(textBox.textEquals("Sending Network ID"));
        button = simManagerWindow.getButton("loadParamicsNetworkButton");
        assertFalse(button.isEnabled());
        paramicsLock.compareAndSet(false, true);
    }
}
