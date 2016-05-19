package tmcsim.simulationmanager;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import tmcsim.common.SimulationException;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.ScriptException;

/**
 * Simulation Manager is the main class for this module.  The Simulation Manager
 * is used to control the view and control the simulation.  Simulation incidents
 * are loaded, removed, reschedule, and added from the Simulation Manager.  The
 * Simulation Manager provides functionality for connecting to the paramics
 * communicator and applying diversions.  A history of all events is shown as well.<br> 
 * The SimulationManager may be started at any point before, during, or after 
 * a simulation has begun. The SimulationManager connects to the CADSimulator 
 * and communicates through Java RMI methods.  If two SimulationManagers are started,
 * the second one started, chronologically, will receive communication from the 
 * CADSimulator. <br><br>  
 * The properties file for the SimulationManager class contains the following data.<br>
 * <code>
 * -----------------------------------------------------------------------------<br>
 * Host Name              The host name where the CADSimulator is located.<br>
 * Error File             The target file to use for error logging.<br>
 * -----------------------------------------------------------------------------<br>
 * Example File: <br>
 * CADSimulatorHost    = localhost <br>
 * ErrorFile           = sim_mgr_error.xml <br>
 * -----------------------------------------------------------------------------<br>
 * </code>
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2009/04/17 16:27:47 $ $Revision: 1.7 
 */
public class SimulationManager {
        
    /** Error logger. */
    private static Logger simManLogger = Logger.getLogger("tmcsim.simulationmanager");
    
    /**
     * Enumeration containing property names.
     * @author Matthew Cechini
     */
    private static enum PROPERTIES {
        CAD_SIM_HOST  ("CADSimulatorHost"),
        CAD_SIM_PORT  ("CADSimulatorRMIPort"),
        SCRIPT_DIR    ("ScriptDir"),
        FAKE_PARAMICS ("FakeParamicsConnection");
        
        public String name;
        
        private PROPERTIES(String n) {
            name = n;
        }
        
    }
        
    /**
     * Instance of the SimulationManagerModel which communicates with the 
     * CAD Simulator to display the current simulation information.  This
     * model class contains the data that is displayed by the SimulationManagerView
     * class.  The View purely provides a GUI interface for the data contained within
     * the model.
     */
    private SimulationManagerModel  theSimManagerModel;
    
    /**
     * Instance of the SimulationManagerView class which provides a GUI for the user
     * to view the current simulation information and to manage the simulation.  The 
     * view communicates to the SimulationManagerModel class to get and set data.
     */
    private SimulationManagerView   theSimManagerView;
    
    /** The Properties object for the Simulation Manager. */
    private Properties simManagerProperties;
    
    /**
     * Constructor.  Set communication data members from properties file.  Instantiate
     * the SimulationManager Model and View objects, and set visibility to true.
     * 
     * @param propertiesFile Properties file containing info for Simulation Manager.
     */
    public SimulationManager(String propertiesFile) throws SimulationException {    

        try {
            simManagerProperties = new Properties();
            simManagerProperties.load(new FileInputStream(new File(propertiesFile)));
            
            SimulationManagerView.SCRIPT_DIR = 
                simManagerProperties.getProperty(PROPERTIES.SCRIPT_DIR.name).trim();

            //make sure properties aren't null
            if(simManagerProperties.getProperty(PROPERTIES.CAD_SIM_HOST.name) == null)
                throw new Exception("CAD Simulator host property is null.");  
                
            if(simManagerProperties.getProperty(PROPERTIES.CAD_SIM_PORT.name) == null)
                throw new Exception("CAD Simulator port property is null.");
            
        }
        catch (Exception e)
        {     
            simManLogger.logp(Level.SEVERE, "SimulationManager", "Constructor", 
                    "Exception in reading properties file.", e);
            
            throw new SimulationException(SimulationException.INITIALIZE_ERROR, e);
        }

        //Construct the SimulationManagerModel
        try 
        {                
            theSimManagerModel = new SimulationManagerModel(
                    simManagerProperties.getProperty(PROPERTIES.CAD_SIM_HOST.name).trim(), 
                    simManagerProperties.getProperty(PROPERTIES.CAD_SIM_PORT.name).trim());

            //Construct the SimulationManagerView and set up the Model-View references.
            theSimManagerView = new SimulationManagerView(theSimManagerModel);
            theSimManagerModel.setView(theSimManagerView);                           
        }
        catch (RemoteException re) 
        {
            simManLogger.logp(Level.SEVERE, "SimulationManager", "Constructor", 
                    "Unable to establish RMI ", re);

            throw new SimulationException(SimulationException.CAD_SIM_CONNECT, re); 
        }

        theSimManagerView.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent e)  {}
            public void windowOpened(WindowEvent e)  {}
            public void windowIconified(WindowEvent e)  {}
            public void windowDeiconified(WindowEvent e)  {}
            public void windowActivated(WindowEvent e)  {}
            public void windowDeactivated(WindowEvent e)  {}
            public void windowClosing(WindowEvent e)  {
                theSimManagerModel.disconnect();
                System.exit(0);
            }
        });

        if(Boolean.parseBoolean(simManagerProperties.getProperty(
                PROPERTIES.FAKE_PARAMICS.name).trim()))
        {
            theSimManagerView.setParamicsStatus(PARAMICS_STATUS.CONNECTED);
        }

        //Show the SimulationManager
        theSimManagerView.setVisible(true);        
    }
    
    /** Load a simulation script from the specified file.
     * 
     * @param scriptFile the file containing the XML simulation control script to be run.
     * @throws ScriptException if the script throws an exception
     * @throws SimulationException if the simulation throws an exception
     */
    public void loadScript(File scriptFile) throws ScriptException, SimulationException
    {
        theSimManagerModel.loadScript(scriptFile);
    }
    
    /**
     * Main class.  
     * 
     * @param args Command line arguments.
     */
    static public void main(String[] args) {
        System.setProperty("SIM_MGR_PROPERTIES", "config/sim_manager_config.properties");

        try {
            if(System.getProperty("SIM_MGR_PROPERTIES") != null)
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                new SimulationManager(System.getProperty("SIM_MGR_PROPERTIES"));
            }
            else
            {
                throw new Exception ("SIM_MGR_PROPERTIES system property not defined.");
            }
        } 
        catch (Exception e) 
        {
            simManLogger.logp(Level.SEVERE, "SimulationManager", "Main", 
                    "Error occured initializing application", e);
            
            JOptionPane.showMessageDialog(null, e.getMessage(), 
                "Error - Program Exiting", JOptionPane.ERROR_MESSAGE);  
            
            System.exit(-1);
        }   
        
    }
}







