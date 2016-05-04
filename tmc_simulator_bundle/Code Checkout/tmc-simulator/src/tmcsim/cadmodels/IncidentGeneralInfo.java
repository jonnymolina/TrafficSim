package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentGeneralInfo implements Serializable
{
    private String id;
    private String masterIncNum;
    private String jurisdiction;
    private String alarm;
    private String agy;
    
    private static enum GENERAL_ENUMS
    {
        ID     ("ID"),
        JURISDICTION   ("JURISDICTION"), 
        ALARM   ("ALARM"),
        AGY     ("AGY");
        
        public String tag;
        
        private GENERAL_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentGeneralInfo(){
        jurisdiction = "";
        alarm = "";
        agy = "";
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

    public String getMasterIncNum() {
        return masterIncNum;
    }

    public void setMasterIncNum(String masterIncNum) {
        this.masterIncNum = masterIncNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(GENERAL_ENUMS.ID.tag))
        {
            setId(value);
        }
        else if(tag_name.equals(GENERAL_ENUMS.JURISDICTION.tag))
        {
            setJurisdiction(value);
        }
        else if(tag_name.equals(GENERAL_ENUMS.ALARM.tag))
        {
            setAlarm(value);
        }
        else if(tag_name.equals(GENERAL_ENUMS.AGY.tag))
        {
            setAgy(value);
        }
    }
    
}
