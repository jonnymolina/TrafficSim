package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentResponse implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_plan = "";
    private String init_area = "";
    
    private String plan;
    private String area;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentResponse() {
        plan = "";
        area = "";
    }

    public void resetCADDataSimulation(){
        plan = init_plan;
        area = init_area;
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_RESP.PLAN.tag)) {
            init_plan = value;
            setPlan(value);
        } else if (tag_name.equals(CADDataEnums.INC_RESP.AREA.tag)) {
            init_area = value;
            setArea(value);
        }

    }

}
