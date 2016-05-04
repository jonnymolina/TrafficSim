package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentCaller implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_callerType = "";
    private String init_callerName = "";
    private String init_phone = "";
    private String init_ext = "";
    
    private String callerType;
    private String callerName;
    private String phone;
    private String ext;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentCaller(String type, String name) {
        setCallerType(type);
        setCallerName(name);
    }

    public IncidentCaller() {
        callerType = "";
        callerName = "";
    }
    
    public void resetCADDataSimulation(){
        callerType = init_callerType;
        callerName = init_callerName;
        phone = init_phone;
        ext = init_ext;
    }

    public String getCallerType() {
        return callerType;
    }

    public void setCallerType(String callerType) {
        this.callerType = callerType;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_CALLER.TYPE.tag)) {
            init_callerType = value;
            setCallerType(value);
        } else if (tag_name.equals(CADDataEnums.INC_CALLER.NAME.tag)) {
            init_callerName = value;
            setCallerName(value);
        } else if (tag_name.equals(CADDataEnums.INC_CALLER.PHONE.tag)) {
            init_phone = value;
            setPhone(value);
        } else if (tag_name.equals(CADDataEnums.INC_CALLER.EXT.tag)) {
            init_ext = value;
            setExt(value);
        }
    }
}
