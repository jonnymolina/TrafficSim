package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentCallBacks implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_initial = "";
    private String init_comment = "";
    
    private String initial;
    private String comment;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentCallBacks() {
        initial = "";
        comment = "";
    }
    
    public void resetCADDataSimulation(){
        initial = init_initial;
        comment = init_comment;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
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
        if (tag_name.equals(CADDataEnums.INC_CALLBACK.INITIAL.tag)) {
            init_initial = value;
            setInitial(value);
        } else if (tag_name.equals(CADDataEnums.INC_CALLBACK.COMMENT.tag)) {
            init_comment = value;
            setComment(value);
        }
    }

}
