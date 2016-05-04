package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentGeneralInfo implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_jurisdiction = "";
    private String init_alarm = "";
    private String init_agy = "";
    
    private String jurisdiction;
    private String alarm;
    private String agy;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentGeneralInfo() {
        jurisdiction = "";
        alarm = "";
        agy = "";
    }

    public void resetCADDataSimulation(){
        jurisdiction = init_jurisdiction;
        alarm = init_alarm;
        agy = init_agy;
    }
    
    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAgy() {
        return agy;
    }

    public void setAgy(String agy) {
        this.agy = agy;
    }

    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_GEN_INFO.JURISDICTION.tag)) {
            init_jurisdiction = value;
            setJurisdiction(value);
        } else if (tag_name.equals(CADDataEnums.INC_GEN_INFO.ALARM.tag)) {
            init_alarm = value;
            setAlarm(value);
        } else if (tag_name.equals(CADDataEnums.INC_GEN_INFO.AGY.tag)) {
            init_agy = value;
            setAgy(value);
        }
    }

}
