package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentAdditionalInfo implements Serializable
{
    private int incidentNumber;
    private String type;
    private String machine;
    private String callTaken;
    private String callStatus;
    private String callTakerExt;
    private String alarmLevel;
    private String rotationProviderArea;
    private String comment;
    
    private static enum ADDITIONAL_INFO_ENUMS
    {
        INCIDENT_NUM    ("INCIDENT_NUM"),
        TYPE            ("TYPE"),
        MACHINE   ("MACHINE"), 
        CALL_TAKEN   ("CALL_TAKEN"),
        CALL_STATUS   ("CALL_STATUS"),
        CALL_TAKER_EXT  ("CALL_TAKER_EXT"),
        ALARM_LEVEL ("ALARM_LEVEL"),
        ROTATION_PROVIDER_AREA  ("ROTATION_PROVIDER_AREA"),
        COMMENT ("COMMENT");
        
        public String tag;
        
        private ADDITIONAL_INFO_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentAdditionalInfo(){
        type = "";
        machine = "";
        callTaken = "";
        callStatus = "";
        callTakerExt = "";
        alarmLevel = "";
        rotationProviderArea = "";
        comment = "";
    }
    
    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getCallTaken() {
        return callTaken;
    }

    public void setCallTaken(String callTaken) {
        this.callTaken = callTaken;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallTakerExt() {
        return callTakerExt;
    }

    public void setCallTakerExt(String callTakerExt) {
        this.callTakerExt = callTakerExt;
    }

    
    
    public int getIncidentNumber() {
        return incidentNumber;
    }
    
    public void setIncidentNumber(int incidentNumber) {
        this.incidentNumber = incidentNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }
    
    public String getRotationProviderArea() {
        return rotationProviderArea;
    }

    public void setRotationProviderArea(String rotationProviderArea) {
        this.rotationProviderArea = rotationProviderArea;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(ADDITIONAL_INFO_ENUMS.INCIDENT_NUM.tag))
        {
            setIncidentNumber(Integer.parseInt(value));
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.TYPE.tag))
        {
            setType(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.MACHINE.tag))
        {
            setMachine(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.CALL_TAKEN.tag))
        {
            setCallTaken(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.CALL_STATUS.tag))
        {
            setCallStatus(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.CALL_TAKER_EXT.tag))
        {
            setCallTakerExt(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.ALARM_LEVEL.tag))
        {
            setAlarmLevel(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.ROTATION_PROVIDER_AREA.tag))
        {
            setRotationProviderArea(value);
        }
        else if(tag_name.equals(ADDITIONAL_INFO_ENUMS.COMMENT.tag))
        {
            setComment(value);
        }
    }
    
    
}
