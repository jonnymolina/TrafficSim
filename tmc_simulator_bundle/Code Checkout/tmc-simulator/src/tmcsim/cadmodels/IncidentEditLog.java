package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentEditLog implements Serializable
{
    private String edit;
    private String reason;
    private String changeBy;
    private String terminal;
    
    private static enum EDIT_LOG_ENUMS
    {
        EDIT     ("EDIT"),
        REASON  ("REASON"),
        CHANGE_BY   ("CHANGE_BY"), 
        TERMINAL   ("TERMINAL");
        
        public String tag;
        
        private EDIT_LOG_ENUMS(String t)
        {
            tag = t;
        }
    }
    
    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentEditLog(){
        edit = "";
        reason = "";
        changeBy = "";
        terminal = "";
    }
    
    public String getEdit() {
        return edit;
    }
    
    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
    
    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(EDIT_LOG_ENUMS.EDIT.tag))
        {
            setEdit(value);
        }
        else if(tag_name.equals(EDIT_LOG_ENUMS.REASON.tag))
        {
            setReason(value);
        }
        else if(tag_name.equals(EDIT_LOG_ENUMS.CHANGE_BY.tag))
        {
            setChangeBy(value);
        }
        else if(tag_name.equals(EDIT_LOG_ENUMS.TERMINAL.tag))
        {
            setTerminal(value);
        }
    }
}
