package tmcsim.cadsimulator.managers;

import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import tmcsim.cadsimulator.stillimagecontrol.ATMSCommunicator;

/**
 * ATMSManager is a CAD Simulator Manager used to handle all
 * communication between the CAD Simulator and the ATMS server.
 * Upon construction, the ATMSCommunicator is initialized with 
 * information from the properties file.  This Object is used 
 * to perform the specific communication functions to send and 
 * receive data to/from the ATMS server.  Methods in this 
 * Manager allow for the current ATMS server time to be queried
 * and still images to be updated for display to ATMS clients.  
 * 
 * @author Matthew Cechini
 * @version 
 */
public class ATMSManager {

    /** Error Logger. */
    private static Logger atmsLogger = Logger.getLogger("tmcsim.cadsimulator.managers"); 
    
    /**
     * Enumeration containing property names for Properties parsing.
     * @author Matthew Cechini
     */
    private static enum ATMS_PROPERTIES {
        /**  */
        ATMS_HOST ("ATMSHost"), 
        /**  */
        USERNAME  ("Username"), 
        /**  */
        PASSWORD  ("Password"), 
        /**  */
        IMAGE_DIR ("ImageDir");

        public String name;
        
        private ATMS_PROPERTIES(String n) {
            name = n;
        }
    };  
    
    /** ATMSCommunicator Object used for communication to the ATMS server. */
    private ATMSCommunicator theATMSCommunicator;
    
    /** Properties Object. */
    private Properties atmsProperties = null;
    
    
    /**
     * Constructor.  Loads the Properties file and initializes the 
     * ATMSCommunicator with the parsed data.
     * 
     * @param propertiesFile Target file path of properties file.
     */
    public ATMSManager(String propertiesFile) {
        
        try {
            atmsProperties = new Properties();
            atmsProperties.load(new FileInputStream(propertiesFile));
            
            theATMSCommunicator = new ATMSCommunicator(
                    atmsProperties.getProperty(ATMS_PROPERTIES.ATMS_HOST.name),
                    atmsProperties.getProperty(ATMS_PROPERTIES.USERNAME.name),
                    atmsProperties.getProperty(ATMS_PROPERTIES.PASSWORD.name),
                    atmsProperties.getProperty(ATMS_PROPERTIES.IMAGE_DIR.name));
            
        }
        catch (Exception e) {
            atmsLogger.logp(Level.SEVERE, "ATMSManager", "Constructor",
                    "Exception in parsing properties file.", e);
        } 
    }
    

    /**
     * Returns the current ATMS server time as the number of seconds since Jan 1, 1970.
     * 
     * @return Current time in seconds.
     * @throws Exception if an exception occurs communicating to the ATMS server.
     */
    public long getCurrentTime() throws Exception {
        return theATMSCommunicator.getCurrentTime();
    }

    /**
     * Show a new image for an ATMS camera.  The ATMS camera files are named
     * <ATMS_Camera_ID>.xpm.  If a camera file exists, delete it.  Then copy
     * the parameter file name to <ATMS_Camera_ID>.xpm.  If the camera ID or 
     * new file does not exist, throw an exception.     
     * 
     * @param ATMS_cameraID ATMS indexed camera ID.
     * @param fileName Filename to show.
     * @throws RemoteException if an exception occurs communicating to the ATMS server.
     */
    public void showImage(Integer ATMS_cameraID, String fileName) throws RemoteException {
        theATMSCommunicator.showImage(ATMS_cameraID, fileName);
    }
    
}
