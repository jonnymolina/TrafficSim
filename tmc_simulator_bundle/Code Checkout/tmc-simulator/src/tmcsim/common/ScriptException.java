package tmcsim.common;

/**
 * Exception class used to handle exceptions thrown within 
 * the CAD Simulation software relating to errors caused by simulation 
 * script actions.  Each error is defined by this class' public static 
 * final Strings.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class ScriptException extends Exception {
            
    /**
     * Exception error when user attempts to perform an action but cannot
     * because a script file has not yet been loaded.<br>
     * Error Message: "Simulation must have at least one incident loaded."
     */
    public static final String NO_SCRIPT_LOADED = "Simulation must have at least one incident loaded.";
        
    /**
     * Exception error when user attempts to perform an action but cannot
     * because the simulation has not started.<br>
     * Error Message: "Simulation Not Started"
     */
    public static final String SIM_NOT_STARTED = "Simulation must be started.";
    
    /**
     * Exception error when user attempts to delete an incident that has 
     * already occured.<br>
     * Error Message: "Incident Already Occured"
     */
    public static final String INCIDENT_ALREADY_STARTED = "Incident has already occured.";
    
    /**
     * Exception error when user attempts to reschedule an incident to a time that has 
     * already passed.<br>
     * Error Message: "Time has already passed."
     */
    public static final String TIME_PASSED = "Simulation time has already passed.";

    /**
     * Exception error when user attempts to add an incident that is already in the simualtion. <br>
     * Error Message: "Duplicate incident number."
     */
    public static final String DUPLICATE_INCIDENT = "Duplicate incident number.";

    /**
     * Exception error during script parsing when an invalid enumeration value parsed.<br>
     * Error Message: "Invalid enumeration value."
     */
    public static final String INVALID_ENUM = "Invalid enumeration value.";

    
    /**
     * Constructor accepting a String message.
     *
     * @param newMessage Exception message
     */ 
    public ScriptException(String newMessage) {
        super(newMessage);
    }   
    
    /**
     * Constructor accepting a String message.
     *
     * @param newMessage Exception message
     */ 
    public ScriptException(String newMessage, Object addlInfo) {
        super(newMessage + " <" + addlInfo.toString() + ">");
    }       
        
    /**
     * Constructor accepting a String message and Throwable cause.
     *
     * @param newMessage Exception message 
     * @param e Cause of the exception.
     */ 
    public ScriptException(String newMessage, Throwable e) {
        super(newMessage, e);
    }       

    /**
     * Constructor accepting a String message, additional info object, and Throwable cause.
     *
     * @param newMessage Exception message
     * @param addlInfo Additional info
     * @param e Cause of the exception.
     */ 
    public ScriptException(String newMessage, Object addlInfo, Throwable e) {
        super(newMessage + " <" + addlInfo.toString() + ">", e);
    }       
    
        
}
    