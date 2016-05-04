package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

/**
 * NOT IN USE. Originally meant for TransportInfo Tab in IncidentViewer.
 * This class is a
 * further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentTransportInfo implements Serializable {
    private String name;
    private String toCity;
    private String toLocation;
    private String toState;

    private static enum TRANSPORT_INFO_ENUMS {
        NAME("NAME"), TO_CITY("TO_CITY"), TO_LOCATION("TO_LOCATION"), TO_STATE(
                "TO_STATE");

        public String tag;

        private TRANSPORT_INFO_ENUMS(String t) {
            tag = t;
        }
    }

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentTransportInfo() {
        name = "";
        toCity = "";
        toLocation = "";
        toState = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(TRANSPORT_INFO_ENUMS.NAME.tag)) {
            setName(value);
        } else if (tag_name.equals(TRANSPORT_INFO_ENUMS.TO_CITY.tag)) {
            setToCity(value);
        } else if (tag_name.equals(TRANSPORT_INFO_ENUMS.TO_LOCATION.tag)) {
            setToLocation(value);
        } else if (tag_name.equals(TRANSPORT_INFO_ENUMS.TO_STATE.tag)) {
            setToState(value);
        }
    }
}
