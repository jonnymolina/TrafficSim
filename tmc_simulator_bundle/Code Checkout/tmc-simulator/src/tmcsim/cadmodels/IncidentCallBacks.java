package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentCallBacks implements Serializable
{
    private String initial;
    private String comment;
    
    private static enum CALL_BACKS_ENUMS
    {
        INITIAL     ("INITIAL"),
        COMMENT   ("COMMENT");
        
        public String tag;
        
        private CALL_BACKS_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentCallBacks(){
        initial = "";
        comment = "";
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(CALL_BACKS_ENUMS.INITIAL.tag))
        {
            setInitial(value);
        }
        else if(tag_name.equals(CALL_BACKS_ENUMS.COMMENT.tag))
        {
            setComment(value);
        }
    }
    
}
