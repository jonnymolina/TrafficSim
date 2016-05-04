package tmcsim.common;


/**
 * The ObserverMessage acts as a message object to pass information between 
 * components within the CADClient and CAD Simulator.  The ObserverMessage
 * contains a enumerated message type and data object that may be used
 * by observers.
 * 
 * @author Matthew Cechini
 * @version 
 */
public class ObserverMessage {
    
    /**
     * Enumeration of all possible message types.
     * 
     * INCIDENT_INQUIRY      - Notify observers with a new IncidentInquiry model object.
     * INCIDENT_SUMMARY      - Notify observers with a new IncidentSummry model object.
     * INCIDENT_BOARD        - Notify observers with a new IncidentBoard model object.
     * ROUTED_MESSAGE        - Notify observers with a new RouteMessage model object.
     * BLANK_SCREEN          - Notify observers with a new BlankScreen model object.
     * SCREEN_UPDATE         - Notify observers with a new ScreenUpdate string.
     * TIME_UPDATE           - Notify observers with a new CAD Time value.
     * ROUTED_MESSAGE_COUNT_UPDATE  - Notify observers with a new count of routed messages.
     * ROUTED_MESSAGE_UNREAD_UPDATE - Notify observers with the unread message status boolean.
     * CAD_INFO_MESSAGE      - Notify observers with a new CAD info message.
     * REFRESH_VIEW          - Notify observers with to refresh the current view.
     * RESET_SIMULATION      - Notify observers that the simulation is being reset.
     */
    public static enum messageType { INCIDENT_INQUIRY, INCIDENT_SUMMARY, INCIDENT_BOARD, 
                                     ROUTED_MESSAGE, BLANK_SCREEN, SCREEN_UPDATE, 
                                     TIME_UPDATE, ROUTED_MESSAGE_COUNT_UPDATE,
                                     ROUTED_MESSAGE_UNREAD_UPDATE, CAD_INFO_MESSAGE, 
                                     REFRESH_VIEW, RESET_SIMULATION };
    
    /** Type of message. */
    public messageType type  = null;
    
    /** Data being sent to observers. */
    public Object      value = null;    
    
    /**
     * 
     * @param newType Type of message.
     * @param o Data object.
     */
    public ObserverMessage(messageType newType, Object o) {
        type  = newType;
        value = o;
    }   
    
}