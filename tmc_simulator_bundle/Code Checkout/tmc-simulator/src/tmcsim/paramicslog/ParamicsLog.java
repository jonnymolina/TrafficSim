package tmcsim.paramicslog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.util.Observable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;

import tmcsim.common.SimulationException;
import tmcsim.interfaces.CoordinatorInterface;
import tmcsim.paramicslog.gui.ParamicsLogGUI;

/**
 * Logs communication from ParamicsCommunicator to ParamicsSimulator.
 * 
 * The system property "PARAMICS_LOG_CONFIG" should be set to the path
 * where the properties file for this class is located. <br><br>
 * The data for the properties file follows. <br>
 * <code>
 * -----------------------------------------------------------------<br>
 * Log File                The file to write the communication to.
 * CAD Simulator Host      The host that runs the CAD Simulator.
 * CAD Simulator RMI Port  The port on the host that runs the CAD 
 *                         Simulator where the RMI Coordinator
 *                         object is registered to. 
 * -----------------------------------------------------------------<br>
 * Example File: <br>
 * LogFile=c:\\log.txt
 * CADSimulatorHost=localhost
 * CADSimulatorRMIPort=4445
 * -----------------------------------------------------------------<br>
 * </code>
 * 
 * @author Nathaniel Lehrer
 * @version 
 */
public class ParamicsLog extends Observable 
{   
    /**
     * Enmeration containing property names.
     * @author Nathaniel Lehrer
     */
    private enum PROPERTIES 
    {
        LOG_FILE      ("LogFile"),
        CAD_SIM_HOST  ("CADSimulatorHost"),
        CAD_SIM_PORT  ("CADSimulatorRMIPort");
        
        public String name;
        
        private PROPERTIES(String n)
        {
            name = n;
        }
    }
    
    /** Error logger. */
    private static Logger paramLogger = Logger.getLogger("tmcsim.paramicslog");
    
    /** Static instance. */
    private static ParamicsLog instance;
    
    /** Properties object. */
    private Properties paramicsLogProp;
    
    /** File log entries are written to */
    private File logFile;
    
    /** Stores the log entries. This contains the same information logFile. */
    private StringBuilder log;

    /** Remote reference to the simulation */
    private CoordinatorInterface theCoorInt;
    
    /** Object for synchronizing IO */
    Object lock;
    
    /**
     * Creates the singleton instance of this class.
     */
    static
    {
        try 
        {
            if (System.getProperty("PARAMICS_LOG_PROPERTIES") != null)
            {
                instance = new ParamicsLog(System.getProperty("PARAMICS_LOG_PROPERTIES"));
            }
            else
            {
                throw new Exception("PARAMICS_LOG_PROPERTIES system property not defined.");
            }
        } 
        catch (Exception e) 
        {
            instance = new ParamicsLog();
            
            paramLogger.logp(Level.WARNING, "ParamicsLog", "static initializer", 
                    "Error occured initializing application", e);

            JOptionPane.showMessageDialog(null, e.getMessage(), 
                    "Error - ParamicsLog will not save log to file.", 
                    JOptionPane.ERROR_MESSAGE); 
        }
        
        instance.addObserver(ParamicsLogGUI.getInstance());
    }
    
    /**
     * Creates an instance of ParamicsLog that does not write to a file when 
     * writeToLog is called.
     */
    private ParamicsLog() {
        
        lock = new Object();
        log = new StringBuilder("");
    }
    
    /**
     * Creates an instance of ParamicsLog that writes to a file when writeToLog is called.
     * @param propertiesFile
     */
    private ParamicsLog(String propertiesFile) {
        this();
        
        String logFile = null;
        String CADSIMHost = null;
        String CADSIMPort = null;
        
        try {
            paramicsLogProp = new Properties();
            paramicsLogProp.load(new FileInputStream(propertiesFile));
            
            if ((logFile = paramicsLogProp.getProperty(PROPERTIES.LOG_FILE.name)) == null)
            {
                throw new Exception("Properties file missing log file location.");
            }
            else if ((CADSIMHost = paramicsLogProp.getProperty(PROPERTIES.CAD_SIM_HOST.name)) == null)
            {   
                throw new Exception("Properties file missing CAD Simulator host.");
            }
            else if ((CADSIMPort = paramicsLogProp.getProperty(PROPERTIES.CAD_SIM_PORT.name)) == null)
            {
                throw new Exception("Properties file missing CAD Simulator RMI port.");
            }
            
            try
            {
                connect(CADSIMHost, CADSIMPort);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, 
                        "ParamicsLog: Could not connect to remote Coordinator object.", 
                        "Network Error", JOptionPane.ERROR_MESSAGE);                
            }
            
            try 
            {
                createLogFile(logFile);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, 
                        "ParamicsLog: Could not create new log file.", 
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            
            paramLogger.logp(Level.WARNING, "ParamicsLog", "ParamicsLog constructor", 
                    "Properties file incorrect or missing.", e);
            
            JOptionPane.showMessageDialog(null, 
                    "ParamicsLog: Properties file invalid.", 
                    "Invalid Configuration", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Creates the log file.
     * @param filePath The path to the file including the file name.
     * @throws IOException If the log file could not be created.
     */
    private void createLogFile(String filePath) throws IOException
    {
        try {
            logFile = new File(filePath);
            
            if (logFile.exists()) {
                logFile.delete();
            }
            
            logFile.createNewFile();
            
        } catch (Exception e) {
            
            logFile = null;
            
            paramLogger.logp(Level.WARNING, "ParamicsLog", "ParamicsLog constructor", 
                    "Could not create new log file.", e);
            
            throw new IOException("Could not create log file.");
        }               
    }
    
    /**
     * Connect to the Coordinator's RMI object.
     * @param hostname Host name of the CAD Simulator.    
     * @param portNumber Port number of the CAD Simulator RMI communication. 
     * @throws SimulationException if there is an error creating the RMI connection.
     */ 
    private void connect(String hostname, String portNumber) 
        throws SimulationException {
        
        String coorIntURL = "";
        
        try {  
            coorIntURL = "rmi://" + hostname + ":" + portNumber + "/coordinator"; 
            
            theCoorInt = (CoordinatorInterface)Naming.lookup(coorIntURL);           
        }
        catch (Exception e) 
        {
            paramLogger.logp(Level.WARNING, "ParamicsLog", 
                    "establishRMIConnection", "Unable to establish RMI " +
                    "communication with the CAD Simulator.  URL <" + coorIntURL + ">", e);
        }   
    }
    
    /**
     * Accessor to the entries in the log. No file IO is used.
     * @return The entries in the log.
     */
    public String getLog() {
        
        return log.toString();
    }
    
    /**
     * Writes an entry to the log. 
     * The simulator time when the message was sent is prepended to the entry.
     * Entries are padded by a blank line before and after them.
     * TODO: Ensure output is written in order of request.
     * @param entry
     */
    public void writeToLog(String entry) {
        
        String time = "?";
        String formattedEntry;
        
        if (theCoorInt != null)
        {
            try
            {
                time = formatTime(theCoorInt.getCurrentSimulationTime());
            }
            catch (Exception e)
            {
                paramLogger.logp(Level.WARNING, "ParamicsLog", 
                        "RMICommunication", "Unable to communicate with RMI object", e);
            }
        }
        
        formattedEntry = "\n" + "<!-- Time written to file: " + time + " -->\n" + entry + "\n";
        log.append(formattedEntry);
        
        if (logFile != null)
        {
            try 
            {
                synchronized(lock)
                {
                    FileWriter writer = new FileWriter(logFile, true);
                    writer.append(formattedEntry);
                    writer.flush();
                    writer.close();
                }
            } 
            catch (IOException e) 
            {
                paramLogger.logp(Level.WARNING, "ParamicsLog", "writeToLog", 
                        "Could not write to log file.", e);
            }
        }
        
        setChanged();
        notifyObservers(entry);
    }
    
    /**
     * Formats the time given in seconds to hh:mm:ss format.
     * @param time The time in seconds.
     */
    private String formatTime(long time)
    {
        long seconds = time % 60;
        long minutes = (time - seconds) / 60;
        long hours = (time - seconds - minutes * 60) / 60;

        return padr(hours) + ":" + padr(minutes) + ":" + padr(seconds);
    }
    
    private String padr(long n)
    {
        if (n < 10)
        {
            return "0" + n;
        }
        {
            return "" + n;
        }
    }
    
    /**
     * Accessor for static instance of this class.
     * @return The instance of this class.
     */
    public static ParamicsLog getInstance() {
        
        return instance;
    }
}
