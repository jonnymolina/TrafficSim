package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * Originally meant for Edit Log Tab in IncidentViewer, not really in use at the moment.
 * If this is used, this class 
 * @author Vincent
 * 
 */
public class IncidentEditLog implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    
    private String edit;
    private String reason;
    private String changeBy;
    private String terminal;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentEditLog() {
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_EDIT_LOG.EDIT.tag)) {
            setEdit(value);
        } else if (tag_name.equals(CADDataEnums.INC_EDIT_LOG.REASON.tag)) {
            setReason(value);
        } else if (tag_name.equals(CADDataEnums.INC_EDIT_LOG.CHANGE_BY.tag)) {
            setChangeBy(value);
        } else if (tag_name.equals(CADDataEnums.INC_EDIT_LOG.TERMINAL.tag)) {
            setTerminal(value);
        }
    }
}
