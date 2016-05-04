package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentCaller implements Serializable
{
    private String callerType;
    private String callerName;
    private String phone;
    private String ext;
    
    private static enum CALLER_ENUMS
    {
        TYPE   ("TYPE"), 
        NAME   ("NAME"),
        PHONE  ("PHONE"),
        EXT    ("EXT");
        
        public String tag;
        
        private CALLER_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentCaller(String type, String name)
    {
        setCallerType(type);
        setCallerName(name);
    }
    
    public IncidentCaller()
    {
        callerType = "";
        callerName = "";
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(CALLER_ENUMS.TYPE.tag))
        {
            setCallerType(value);
        }
        else if(tag_name.equals(CALLER_ENUMS.NAME.tag))
        {
            setCallerName(value);
        }
        else if(tag_name.equals(CALLER_ENUMS.PHONE.tag))
        {
            setCallerName(value);
        }
        else if(tag_name.equals(CALLER_ENUMS.EXT.tag))
        {
            setCallerName(value);
        }
    }
}
