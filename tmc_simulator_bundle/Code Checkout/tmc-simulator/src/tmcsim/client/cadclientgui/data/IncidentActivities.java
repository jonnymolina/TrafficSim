package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentActivities implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_vehicle = "";
    private String init_activity = "";
    private String init_location = "";
    private String init_comment = "";
    private String init_disp = "";
    
    private String vehicle;
    private String activity;
    private String location;
    private String comment;
    private String disp;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentActivities() {
        vehicle = "";
        activity = "";
        location = "";
        comment = "";
        disp = "";
    }
    
    public void resetCADDataSimulation(){
        vehicle = init_vehicle;
        activity = init_activity;
        location = init_location;
        comment = init_comment;
        disp = init_disp;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDisp() {
        return disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_ACTIVITIES.VEHICLE.tag)) {
            init_vehicle = value;
            setVehicle(value);
        } else if (tag_name.equals(CADDataEnums.INC_ACTIVITIES.ACTIVITY.tag)) {
            init_activity = value;
            setActivity(value);
        } else if (tag_name.equals(CADDataEnums.INC_ACTIVITIES.LOCATION.tag)) {
            init_location = value;
            setLocation(value);
        } else if (tag_name.equals(CADDataEnums.INC_ACTIVITIES.COMMENT.tag)) {
            init_comment = value;
            setComment(value);
        } else if (tag_name.equals(CADDataEnums.INC_ACTIVITIES.DISP.tag)) {
            init_disp = value;
            setDisp(value);
        }
    }

}
