package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentAdditionalInfo implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_type = "";
    private String init_typeCode = "";
    private String init_machine = "";
    private String init_callStatus = "";
    private String init_callTakerExt = "";
    private String init_alarmLevel = "";
    private String init_rotationProviderArea = "";
    private String init_comment = "";
    
    private String type;
    private String typeCode;
    private String machine;
    private String callStatus;
    private String callTakerExt;
    private String alarmLevel;
    private String rotationProviderArea;
    private String comment;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentAdditionalInfo() {
        type = "";
        typeCode = "";
        machine = "";
        callStatus = "";
        callTakerExt = "";
        alarmLevel = "";
        rotationProviderArea = "";
        comment = "";
    }

    public void resetCADDataSimulation(){
        type = init_type;
        typeCode = init_typeCode;
        machine = init_machine;
        callStatus = init_callStatus;
        callTakerExt = init_callTakerExt;
        alarmLevel = init_alarmLevel;
        rotationProviderArea = init_rotationProviderArea;
        comment = init_comment;
    }
    
    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String type) {
        this.typeCode = type;
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_ADD_INFO.TYPE.tag)) {
            init_type = value;
            setType(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.TYPE_CODE.tag)) {
            init_typeCode = value;
            setTypeCode(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.MACHINE.tag)) {
            init_machine = value;
            setMachine(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.CALL_STATUS.tag)) {
            init_callStatus = value;
            setCallStatus(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.CALL_TAKER_EXT.tag)) {
            init_callTakerExt = value;
            setCallTakerExt(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.ALARM_LEVEL.tag)) {
            init_alarmLevel = value;
            setAlarmLevel(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.ROTATION_PROVIDER_AREA.tag)) {
            init_rotationProviderArea = value;
            setRotationProviderArea(value);
        } else if (tag_name.equals(CADDataEnums.INC_ADD_INFO.COMMENT.tag)) {
            init_comment = value;
            setComment(value);
        }
    }

}
