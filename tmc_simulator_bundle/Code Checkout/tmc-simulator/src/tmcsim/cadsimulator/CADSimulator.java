package tmcsim.cadsimulator;

import java.io.File;
import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilderFactory;

import tmcsim.cadsimulator.db.CMSDiversionDB;
import tmcsim.cadsimulator.managers.ATMSManager;
import tmcsim.cadsimulator.managers.IncidentManager;
import tmcsim.cadsimulator.managers.MediaManager;
import tmcsim.cadsimulator.managers.ParamicsSimulationManager;
import tmcsim.cadsimulator.managers.SimulationControlManager;
import tmcsim.cadsimulator.viewer.MediaStatusPanel;
import tmcsim.cadsimulator.viewer.model.CADSimulatorModel;
import tmcsim.cadsimulator.viewer.model.SimulationStatusPanelModel;
import tmcsim.common.SimulationException;
import tmcsim.interfaces.CADViewer;


/**
 * CADSimulator is main class for the CAD Simulator application.  At 
 * construction the Coordinator, CoordinatorViewer, and all CAD Simulator 
 * Managers are initialized and data relationships are established.  
 * Simulation control is managed through the Coordinator and Managers.
 * The CADSimulator contains the instances of all Manager Objects that are used
 * to control the Simulation flow of data.<br>
 * <br>
 * 
 * The CADSimulator is initialized with a properties file containing the 
 * following data items:<br>
 * <code>
 * -----------------------------------------------------------------------------<br>
 * CADClientPort          The port number to use for remote CAD Client connections.<br>
 * CoordinatorRMIPort     The port number to use for binding the Coordinator.<br>
 * CMSDiversionXML        The filepath for the xml file containing initialization data for the Diversion "database."
 * AudioFileLocation      The root directory path where audio files are referenced from.<br>
 * ParamicsProperties     The filepath for the properties file to initialize the ParamicsControlManager.<br>
 * ATMSProperties         The filepath for the properties file to initialize the ATMSManager.<br>
 * MediaProperties        The filepath for the properties file to initialize the MediaManager.<br>
 * ErrorFile              The filename of the error file used for logging errors.<br>
 * ----------------------------------------------------------------------------<br>
 * Example File:<br>
 * CADClientPort          = 4444<br>
 * CoordinatorRMIPort     = 4445<br>
 * CMSDiversionXML        = ../data/cmsdiversions.xml<br>
 * AudioFileLocation      = ../audio/<br>
 * ParamicsProperties     = ../config/paramics.properties<br>
 * ATMSProperties         = ../config/atms.properties<br>
 * MediaProperties        = ../config/media.properties<br>
 * ErrorFile              = cad_sim_error.xml<br>
 * </code>
 *
 * @author Jonathan Molina
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2009/04/17 16:27:46 $ $Revision: 1.5 $
 */
public class CADSimulator {
    
    /** Error logger. */
    private static Logger cadSimLogger = Logger.getLogger("tmcsim.cadsimulator");

    /**
     * Enumeration containing properties name values.  See CADSimulator class 
     * description for more information.
     * @author Matthew
     * @see CADSimulator
     */
    private static enum CAD_PROPERTIES
    {
        /** RMI port to accept CAD Client connections. */
        CLIENT_PORT        ("CADClientPort"),       
        /** RMI port to bind the Coordinator to for RMI communication. */
        COOR_RMI_PORT      ("CoordinatorRMIPort"),  
        
        CAD_RMI_PORT       ("CADRmiPort"),
        /** Filepath for xml file containing diversion data. */     
        CMS_XML_FILE       ("CMSDiversionXML"), 
        /** Filepath for xml file containing dvd control data. */
        DVD_XML_FILE       ("DVDPlayerXML"),    
        /** Filepath for xml file containing still image control data. */
        IMAGE_XML_FILE     ("StillImagesXML"),
        /** Root directory path where audio files are referenced from. */
        AUDIO_LOCATION     ("AudioFileLocation"),
        /** Filepath for the properties file to initialize the media manager. */
        MEDIA_PROP_FILE    ("MediaProperties"),
        /** Filepath to the properties file to initialize paramics control manager. */
        PARAMICS_PROP_FILE ("ParamicsProperties"),
        /** Filepath for the properties file to initialize the atms manager. */
        ATMS_PROP_FILE     ("ATMSProperties"),
        /** The specific CADViewer used as the user interface for the CADsimulator */
        USER_INTERFACE     ("UserInterface");
        
        public String name;
        
        private CAD_PROPERTIES(String n)
        {
            name = n;
        }
    };
        
            
    /** CADSimulatorViewer instance. */
    protected static CADViewer theViewer;

    /** Coordinator instance. */
    public static  Coordinator theCoordinator;
    
    /** SoundPlayer instance. */
    protected static  SoundPlayer theSoundPlayer = null;
    
    /** SimulationControlManager instance. */
    protected static  SimulationControlManager theSimulationCntrlMgr = null;
    
    /**  ParamicsSimulationManager instance. */
    protected static  ParamicsSimulationManager theParamicsSimMgr = null;
    
    /** IncidentManager instance. */
    protected static  IncidentManager theIncidentMgr = null;
    
    /** MediaManager instance. */   
    protected static  MediaManager theMediaMgr = null;
    
    /** ATMSManager instance. */
    protected static  ATMSManager theATMSMgr = null; 

    /** Properties file for the CADSimulator. */
    private Properties cadSimulatorProperties;
    

    /**
     * Constructor.  Load the Properties file and initialize all CAD Simulator
     * Managers and establish Manager data relationships.  A 
     * CADSimulatorSocketHandler is instantiated and started to being 
     * listening for remote CAD connections.  The CMSDiversionDB is initialized
     * with the XML data(incomplete design).
     * 
     * @param propertiesFile Filename of CAD Simulator properties file.
     * @throws SimulationException if there's an error in initializing the CAD Simulator.
     */
    public CADSimulator(String propertiesFile) throws SimulationException
    {

        try
        {
            cadSimulatorProperties = new Properties();
            cadSimulatorProperties.load(new FileInputStream(propertiesFile));
        }
        catch (Exception e)
        {
            cadSimLogger.logp(Level.SEVERE, "CADSimulator", "Constructor",
                    "Exception in reading properties file.", e);

            throw new SimulationException(SimulationException.INITIALIZE_ERROR, e);
        }

        //Create the Coordinator and register it for RMI communicator.  Start the
        //CAD Simulator Socket Handler to begin to accept connections from CAD Clients.
        try
        {
            CADSimulatorModel cadSimModel = new CADSimulatorModel();

            theViewer = chooseUserInterface(cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.USER_INTERFACE.name.trim()));
            cadSimModel.addObserver(theViewer);

            theCoordinator = new Coordinator(cadSimModel);

            startRegistry(Integer.parseInt(
                    cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.COOR_RMI_PORT.name).trim()));
            startRegistry(Integer.parseInt(
                    cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.CAD_RMI_PORT.name).trim()));

            theSimulationCntrlMgr = new SimulationControlManager(theCoordinator);

            theATMSMgr = new ATMSManager(
                    cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.ATMS_PROP_FILE.name));

            theMediaMgr = new MediaManager(
                    cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.MEDIA_PROP_FILE.name),
                    theATMSMgr, cadSimModel);

            theParamicsSimMgr = new ParamicsSimulationManager(
                    cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.PARAMICS_PROP_FILE.name),
                    theCoordinator, theMediaMgr);

            theSoundPlayer = new SoundPlayer(
                    cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.AUDIO_LOCATION.name));
            theSoundPlayer.start();

            theIncidentMgr = new IncidentManager(theCoordinator, theSoundPlayer);


            //Begin accepting Client connections
            CADSimulatorSocketHandler tmsh = new CADSimulatorSocketHandler(
                    Integer.parseInt(cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.CLIENT_PORT.name).trim()));
            tmsh.start();
        }
        catch (RemoteException e)
        {
            cadSimLogger.logp(Level.SEVERE, "CADSimulator", "Constructor",
                    "Exception in starting Coordinator.", e);

            throw new SimulationException(SimulationException.BINDING, e);
        }

        //Load CMS Diversion Information from the XML file
        try
        {
            if (cadSimulatorProperties.getProperty(
                    CAD_PROPERTIES.CMS_XML_FILE.name) != null)
            {
                CMSDiversionDB.getInstance().loadFromXML(
                        DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new File(cadSimulatorProperties.getProperty(
                        CAD_PROPERTIES.CMS_XML_FILE.name))));
            }
        }
        catch (Exception e)
        {
            cadSimLogger.logp(Level.SEVERE, "CADSimulator", "Constructor",
                    "Exception in parsing CMSDiversion xml file.", e);

            JOptionPane.showMessageDialog(new JWindow(), "Unable to open "
                    + cadSimulatorProperties
                        .getProperty(CAD_PROPERTIES.CMS_XML_FILE.name),
                    "Initialization Error", JOptionPane.WARNING_MESSAGE);
        }

        theViewer.setVisible(true);

    }
    
    private CADViewer chooseUserInterface(String className)
    {
        Class viewClass = null;
        CADViewer view = null;
        try
        {
            viewClass = Class.forName(className);
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(CADSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            Object object = viewClass.newInstance();
            view = (CADViewer) object;
        }
        catch (InstantiationException ex)
        {
            Logger.getLogger(CADSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            Logger.getLogger(CADSimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return view;
    }
    
    /**
     * Binds the Coordinator to an RMI port so that the SimulationManager
     * can communicate with it, and so that the Coordinator can perform RMI
     * callback method calls.  The port numbers and RMI designators are parsed from
     * the properties file file.
     *
     * @param theCoor A reference to the Coordinator object.
     * @throws SimulationException if there are errors in binding the RMI to 
     * a port and name.
     */
    private void startRegistry(Integer regPort) throws SimulationException {
        
        try{
            LocateRegistry.createRegistry(regPort);                             
            
            String registryURL = "rmi://localhost:" + regPort + "/coordinator";
            Naming.rebind(registryURL, theCoordinator);
        }
        catch (Exception e) {           
            throw new SimulationException(SimulationException.BINDING, e);
        }
    }

    /**
     * Method returns a String represetnation of the current time.  String format
     * is HHMM 
     *
     * @return String representation of the current time.
     */
    public static String getCADTime() {
        String time = new String();     

        Calendar rightNow = Calendar.getInstance();
        
        if(rightNow.get(Calendar.HOUR_OF_DAY) < 10)
            time += "0";
        
        time += (String.valueOf(rightNow.get(Calendar.HOUR_OF_DAY)));
        
        if(rightNow.get(Calendar.MINUTE) < 10)
            time += "0";
        
        time += (String.valueOf(rightNow.get(Calendar.MINUTE)));        
                            
        return time;        
    }           
    
    /**
     * Returns a string representation of the current date.  String format is:
     * MMDDYY
     *
     * @return String format of the date.
     */
    public static String getCADDate() {
        String date = new String();
        
        Calendar rightNow = Calendar.getInstance();
        
        //Months are zero referenced
        if(rightNow.get(Calendar.MONTH) + 1 < 10)
            date += "0";
        
        date += (String.valueOf(rightNow.get(Calendar.MONTH)+ 1));
        
        if(rightNow.get(Calendar.DAY_OF_MONTH) < 10)
            date += "0";
        
        date += (String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH)));      
                            
        if(rightNow.get(Calendar.YEAR) % 1000 < 10)
            date += "0";
        
        date += (String.valueOf(rightNow.get(Calendar.YEAR) % 1000));                               
                            
        return date;    
        
    }
        
    /**
     * Main class.  Instantiate a CAD Simulator with the properties file 
     * specified on the command line or the default properties file
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {//-DCAD_SIM_PROPERTIES="config/cad_simulator_config.properties"
//        System.setProperty("CAD_SIM_PROPERTIES",  "config/cad_simulator_config.properties");
        
        try 
        {
            if(System.getProperty("CAD_SIM_PROPERTIES") != null)
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                new CADSimulator(System.getProperty("CAD_SIM_PROPERTIES"));
            }
            else
            {
                throw new Exception ("CAD_SIM_PROPERTIES system property not defined.");
            }
        }
        catch (Exception e) {
            cadSimLogger.logp(Level.SEVERE, "CADSimulator", "Main", 
                    "Error initializing application.", e);
            
            JOptionPane.showMessageDialog(new JWindow(), e.getMessage(), 
                    "Error - Program Exiting", JOptionPane.ERROR_MESSAGE);  
            
            System.exit(-1);
        }
        
    }
} 
