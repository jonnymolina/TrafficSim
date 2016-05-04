package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentActivities implements Serializable
{
    private String vehicle;
    private String activity;
    private String location;
    private String comment;
    private String disp;
    
    private static enum ACTIVITIES_ENUMS
    {
        VEHICLE     ("VEHICLE"),
        ACTIVITY   ("ACTIVITY"),
        LOCATION   ("LOCATION"), 
        COMMENT   ("COMMENT"),
        DISP      ("DISP");
        
        public String tag;
        
        private ACTIVITIES_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentActivities(){
        vehicle = "";
        activity = "";
        location = "";
        comment = "";
        disp = "";
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(ACTIVITIES_ENUMS.VEHICLE.tag))
        {
            setVehicle(value);
        }
        else if(tag_name.equals(ACTIVITIES_ENUMS.ACTIVITY.tag))
        {
            setActivity(value);
        }
        else if(tag_name.equals(ACTIVITIES_ENUMS.LOCATION.tag))
        {
            setLocation(value);
        }
        else if(tag_name.equals(ACTIVITIES_ENUMS.COMMENT.tag))
        {
            setComment(value);
        }
        else if(tag_name.equals(ACTIVITIES_ENUMS.DISP.tag))
        {
            setDisp(value);
        }
    }
    
}
