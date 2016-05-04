package tmcsim.common;



/**
 * Exception class used to handle exceptions thrown within the CAD Simulation 
 * software relating to connection and initialization errors.  Each error is 
 * defined by this class' public static final Strings.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.7 
 */
@SuppressWarnings("serial")
public class SimulationException extends Exception {
    

    /** 
     * Exception error when a socket or RMI connection to the CAD Simulator fails.<br>
     * Error Message: "Unable to connect to the CAD Simulator."
     */
    public static final String CAD_SIM_CONNECT = "Unable to connect to the CAD Simulator.";
    
    /** 
     * Exception error when a socket or RMI communication to the CAD Simulator fails.<br>
     * Error Message: "Unable to communicate with the CAD Simulator."
     */
    public static final String CAD_SIM_COMM = "Unable to communicate with the CAD Simulator.";
    
    /** 
     * An error has occured in binding to a socket or rmi port.<br>
     * Error Message: "An error has occured in establishing communications."
     */
    public static final String BINDING     = "An error has occured in establishing communications.";

    /** 
     * Exception error when initialization of an object from a properties file fails.<br>
     * Error Message: "Unable to complete initialization, program exiting."
     */
    public static final String INITIALIZE_ERROR = "Unable to complete initialization, program exiting.";

    /** 
     * Exception error when registering the CAD Client with the CAD Simulator fails.<br>
     * Error Message: "Unable to complete CAD Client registration, program exiting."
     */
    public static final String REGISTER_ERROR = "Unable to complete CAD Client registration, program exiting.";

    /** 
     * Exception error if the SimulationManagerModel is initialized without 
     * the view componenta socket or RMI connection to the CAD Simulator fails.<br>
     * Error Message: "Unable to connect to the CAD Simulator."
     */
    public static final String NULL_VIEW = "Unable to connect to the CAD Simulator.";
        
    /**
     * Exception error when a connection to the ATMS server cannot be established.<br>
     * Error Message: "A connection to the ATMS server could not be established"
     */    
    public static final String ATMS_UNREACHABLE = "A connection to the ATMS server could not be established";
    
    /**
     * Exception error to notify the user that a connection to Paramics has 
     * not been established.<br>
     * Error Message: "A connection to Paramics has not been established."
     */
    public static final String PARAMICS_NOT_CONNECTED = "A connection to Paramics has not been established.";    
    
    /**
     * Exception error when there is an error parsing the script file.<br>
     * Error Message: "Unable to parse the script file."
     */
    public static final String INVALID_SCRIPT_FILE = "Unable to parse the script file.";    
        
    /**
     * Constructor accepting a String message.
     *
     * @param newMessage Exception message
     */ 
    public SimulationException(String newMessage) {
        super(newMessage);
    }    
    
    /**
     * Constructor accepting a String message and Throwable cause.
     *
     * @param newMessage Exception message 
     * @param e Cause of the exception.
     */ 
    public SimulationException(String newMessage, Throwable e) {
        super(newMessage, e);
    }       

    /**
     * Constructor accepting a String message, additional info object, and Throwable cause.
     *
     * @param newMessage Exception message
     * @param addlInfo Additional info
     * @param e Cause of the exception.
     */ 
    public SimulationException(String newMessage, Object addlInfo, Throwable e) {
        super(newMessage + " <" + addlInfo.toString() + ">", e);
    }       
    
        
}