package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

public class IncidentInfo implements Serializable {

    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_callInit = "";
    private String init_callTaken = "";
    private String init_timeInQ = "";
    private String init_lastUpdated = "";
    
    
    private String callInit;
    private String callTaken;
    private String timeInQ;
    private String lastUpdated;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentInfo() {
        callInit = "";
        callTaken = "";
        timeInQ = "";
        lastUpdated = "";
    }
    
    public void resetCADDataSimulation(){
        callInit = init_callInit;
        callTaken = init_callTaken;
        timeInQ = init_timeInQ;
        lastUpdated = init_lastUpdated;
    }

    public String getCallInit() {
        return callInit;
    }

    public void setCallInit(String callInit) {
        this.callInit = callInit;
    }

    public String getCallTaken() {
        return callTaken;
    }

    public void setCallTaken(String callTaken) {
        this.callTaken = callTaken;
    }

    public String getTimeInQ() {
        return timeInQ;
    }

    public void setTimeInQ(String timeInQ) {
        this.timeInQ = timeInQ;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_INFO.CALL_INITIATED.tag)) {
            init_callInit = value;
            setCallInit(value);
        } else if (tag_name.equals(CADDataEnums.INC_INFO.CALL_TAKEN.tag)) {
            init_callTaken = value;
            setCallTaken(value);
        } else if (tag_name.equals(CADDataEnums.INC_INFO.TIME_IN_Q.tag)) {
            init_timeInQ = value;
            setTimeInQ(value);
        } else if (tag_name.equals(CADDataEnums.INC_INFO.LAST_UPDATED.tag)) {
            init_lastUpdated = value;
            setLastUpdated(value);
        }
    }
}
