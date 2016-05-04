package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentResponse implements Serializable
{
    private String plan;
    private String area;
    
    private static enum RESPONSE_ENUMS
    {
        PLAN     ("PLAN"),
        AREA     ("AREA");
        
        public String tag;
        
        private RESPONSE_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentResponse(){
        plan = "";
        area = "";
    }
    
    public String getPlan() {
        return plan;
    }
    
    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    
    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(RESPONSE_ENUMS.PLAN.tag))
        {
            setPlan(value);
        }
        else if(tag_name.equals(RESPONSE_ENUMS.AREA.tag))
        {
            setArea(value);
        }
        
    }
    
    
}
