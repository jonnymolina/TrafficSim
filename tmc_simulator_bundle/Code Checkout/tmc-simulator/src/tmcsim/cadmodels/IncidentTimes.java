package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentTimes implements Serializable
{
    private String unit;
    private String alarm;
    private String assigned;
    private String enroute;
    private String staged;
    private String arrival;
    private String access;
    private String depart;
    private String atDest;
    private String status5;
    private String available;
    private String respNum;
    
    private String ring;
    private String inQueue;
    private String allAvailable;
    private String callClosed;
    
    private static enum TIMES_ENUMS
    {
        UNIT     ("UNIT"),
        ALARM  ("ALARM"),
        ASSIGNED   ("ASSIGNED"), 
        ENROUTE   ("ENROUTE"),
        STAGED     ("STAGED"),
        ARRIVAL     ("ARRIVAL"),
        ACCESS  ("ACCESS"),
        DEPART   ("DEPART"), 
        AT_DEST   ("AT_DEST"),
        STATUS_5     ("STATUS_5"),
        AVAILABLE     ("AVAILABLE"),
        RESP_NUM    ("RESP_NUM"),
        
        RING    ("RING"),
        IN_QUEUE    ("IN_QUEUE"),
        ALL_AVAILABLE   ("ALL_AVAILABLE"),
        CALL_CLOSED ("CALL_CLOSED");
        
        public String tag;
        
        private TIMES_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentTimes(){
        unit = "";
        alarm = "";
        assigned = "";
        enroute = "";
        staged = "";
        arrival = "";
        access = "";
        depart = "";
        atDest = "";
        status5 = "";
        available = "";
        respNum = "";
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAssigned() {
        return assigned;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public String getEnroute() {
        return enroute;
    }

    public void setEnroute(String enroute) {
        this.enroute = enroute;
    }

    public String getStaged() {
        return staged;
    }

    public void setStaged(String staged) {
        this.staged = staged;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getAtDest() {
        return atDest;
    }

    public void setAtDest(String atDest) {
        this.atDest = atDest;
    }

    public String getStatus5() {
        return status5;
    }

    public void setStatus5(String status5) {
        this.status5 = status5;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getRespNum() {
        return respNum;
    }

    public void setRespNum(String respNum) {
        this.respNum = respNum;
    }
    
    public String getRing() {
        return ring;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }
    
    public String getInQueue() {
        return inQueue;
    }

    public void setInQueue(String inQueue) {
        this.inQueue = inQueue;
    }
    
    public String getAllAvailable() {
        return allAvailable;
    }

    public void setAllAvailable(String allAvailable) {
        this.allAvailable = allAvailable;
    }
    
    public String getCallClosed() {
        return callClosed;
    }

    public void setCallClosed(String callClosed) {
        this.callClosed = callClosed;
    }
    
    
    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(TIMES_ENUMS.UNIT.tag))
        {
            setUnit(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.ALARM.tag))
        {
            setAlarm(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.ASSIGNED.tag))
        {
            setAssigned(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.ENROUTE.tag))
        {
            setEnroute(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.STAGED.tag))
        {
            setStaged(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.ARRIVAL.tag))
        {
            setArrival(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.ACCESS.tag))
        {
            setAccess(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.DEPART.tag))
        {
            setDepart(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.AT_DEST.tag))
        {
            setAtDest(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.STATUS_5.tag))
        {
            setStatus5(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.AVAILABLE.tag))
        {
            setAvailable(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.RESP_NUM.tag))
        {
            setRespNum(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.RING.tag))
        {
            setRing(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.IN_QUEUE.tag))
        {
            setInQueue(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.ALL_AVAILABLE.tag))
        {
            setAllAvailable(value);
        }
        else if(tag_name.equals(TIMES_ENUMS.CALL_CLOSED.tag))
        {
            setCallClosed(value);
        }
    }
}
