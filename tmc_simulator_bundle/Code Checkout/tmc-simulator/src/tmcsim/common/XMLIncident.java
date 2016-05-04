package tmcsim.common;

import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XMLIncident contains incident information used to create the XML file that 
 * is transmitted to paramics.  It's methods construct an XML element with the
 * incident data.  
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.4 $
 */
@SuppressWarnings("serial")
public class XMLIncident implements Serializable {      

    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {      
        /** Current Incident info. */
        INCIDENT   ("Incident"),
        /** Incident's ID. */
        ID         ("Identifier"),
        /** Incident's status. */
        STATUS     ("Status"),
        /** Incident Location. */
        LOCATION   ("Location"),
        /** Incident's route location. */
        ROUTE      ("Route"),
        /** Incident's route direction. */
        DIRECTION  ("Direction"),
        /** Incident's route location type. */
        LOC_TYPE   ("Location_type"),
        /** Incident's route postmile. */
        POSTMILE   ("Postmile"),
        /** Incident's type. */
        INC_TYPE   ("Incident_type"),
        /** Incident lanes. */
        LANES      ("Lanes"),
        /** Lane number. */
        LANE_NUM   ("Lane_number");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /**
     * Enumeration containing possible incident status values.
     */
    public static enum INCIDENT_STATUS { 
        NEW      ("NEW"), 
        CHANGED  ("CHANGED"), 
        ON_GOING ("ON_GOING"), 
        CLEARED  ("CLEARED");  
    
        public String status;
        
        private INCIDENT_STATUS(String s) {
            status = s;
        }
        
        /**
         * Returns the INCIDENT_STATUS enumeration value which has a status
         * value that matches the parameter value.
         * @param val Incident status.
         * @throws ScriptException if the parameter value is invalid.
         * @return INCIDENT_STATUS for the parameter value.
         */
        public static INCIDENT_STATUS fromValue(String val) throws ScriptException {
            
            for(INCIDENT_STATUS incStatus : values()) {
                if(incStatus.status.equals(val))
                    return incStatus;
            }
            throw new ScriptException(ScriptException.INVALID_ENUM, val);
        }       
    
    };
    
    /**
     * Enumeration containing possible incident type values.
     */
    public static enum INCIDENT_TYPE { 
        LANE_BREAKDOWN ("LANE_BREAKDOWN"); 
    
        public String type;
        
        private INCIDENT_TYPE(String s) {
            type = s;
        }
            
        /**
         * Returns the INCIDENT_STATUS enumeration value which has a status
         * value that matches the parameter value.
         * @param val Incident status.
         * @throws ScriptException if the parameter value is invalid.
         * @return INCIDENT_STATUS for the parameter value.
         */
        public static INCIDENT_TYPE fromValue(String val) throws ScriptException {
            
            for(INCIDENT_TYPE incType : values()) {
                if(incType.type.equals(val))
                    return incType;
            }
            throw new ScriptException(ScriptException.INVALID_ENUM, val);
        }     
    };  

    /** Incident unique ID. */
    private String incidentID    = null;
    
    /** Incident status. */
    private INCIDENT_STATUS incidentStatus = null;
    
    /** Incident type. */ 
    private INCIDENT_TYPE incidentType  = null;
    
    /** Lane numbers affected incident. */
    private Vector<String> lanes = null;
    
    /** Incident location object. */
    private ParamicsLocation theLocation = null;
    
    
    /**
     * Constructor.  Initialize data members.
     * 
     * @param id Incident ID.
     * @param newLocation Incident location.
     */  
    public XMLIncident(String id, ParamicsLocation newLocation) {
        
        incidentID     = id;
        theLocation    = newLocation;
        incidentStatus = INCIDENT_STATUS.CLEARED; 
        incidentType   = INCIDENT_TYPE.LANE_BREAKDOWN;  
        lanes          = new Vector<String>();
    }
        
    /**
     * Get the incident ID.
     * @return Incident ID.
     */
    public String getIdentifier() {
        return incidentID;  
    }
    
    
    /**
     * Receive the tag name and data from XML parsing.
     * @param name XML Tag name.
     * @param value XML Tag value.
     * @throws ScriptException if there is an error in parsing the node data.
     */
    public void readXMLNode(String name, String value) throws ScriptException {
        
        if(name.equals(XML_TAGS.STATUS.tag))
            incidentStatus = INCIDENT_STATUS.fromValue(value);      
        else if(name.equals(XML_TAGS.INC_TYPE.tag))
            incidentType = INCIDENT_TYPE.fromValue(value);
        else if(name.equals(XML_TAGS.LANE_NUM.tag))
            lanes.add(value);       
                
    }   
    
    /**
     * Adds XML tags to the parameter Element for this Incident with the 
     * following schema: <br/>
     * <Incident><br/>
     *      <Identifier/><br/>
     *      <Status/><br/>
     *      <Location><br/>
     *           <Route/><br/>
     *           <Direction/><br/>
     *           <Location_type/><br/>
     *           <Postmile/><br/>
     *      </Location><br/>
     *      <Incident_type/><br/>
     *      <Lanes><br/>
     *          <Lane_number/><br/>
     *      </Lanes><br/>
     * </Incident><br/>
     *
     * @param currElem XML Element used as a root for XML tag appending.
     */
    public void toXML(Element currElem) {
                        
        Document theDoc = currElem.getOwnerDocument();
                
        Element incidentElem = theDoc.createElement(XML_TAGS.INCIDENT.tag);
        currElem.appendChild(incidentElem);
        
        Element idElement = theDoc.createElement(XML_TAGS.ID.tag);
        idElement.appendChild(theDoc.createTextNode(incidentID));
        incidentElem.appendChild(idElement);

        Element statusElement = theDoc.createElement(XML_TAGS.STATUS.tag);
        statusElement.appendChild(theDoc.createTextNode(incidentStatus.status));
        incidentElem.appendChild(statusElement);
        
        writeLocationXML(incidentElem);
            
        Element typeElement = theDoc.createElement(XML_TAGS.INC_TYPE.tag);
        typeElement.appendChild(theDoc.createTextNode(incidentType.type));
        incidentElem.appendChild(typeElement);
            
        writeLanesXML(incidentElem);
        
    }
    
    /**
     *
     * Write the location information for the incident with the following
     * XML Schema: <br>
     * <Location><br>
     *      <Route/><br>
     *      <Direction/><br>
     *      <Location_type/><br>
     *      <Postmile/><br>
     * </Location><br>
     */
    protected void writeLocationXML(Element currElem) {

        Document theDoc = currElem.getOwnerDocument();
        
        Element locationElement = theDoc.createElement(XML_TAGS.LOCATION.tag);
        currElem.appendChild(locationElement);

        Element routeElement = theDoc.createElement(XML_TAGS.ROUTE.tag);
        routeElement.appendChild(theDoc.createTextNode(theLocation.incidentRoute));
        locationElement.appendChild(routeElement);

        Element directionElement = theDoc.createElement(XML_TAGS.DIRECTION.tag);
        directionElement.appendChild(theDoc.createTextNode(theLocation.incidentDirection));
        locationElement.appendChild(directionElement);

        Element typeElement = theDoc.createElement(XML_TAGS.LOC_TYPE.tag);
        typeElement.appendChild(theDoc.createTextNode(theLocation.incidentLocType));
        locationElement.appendChild(typeElement);

        Element postmileElement = theDoc.createElement(XML_TAGS.POSTMILE.tag);
        postmileElement.appendChild(theDoc.createTextNode(theLocation.incidentPostmile));
        locationElement.appendChild(postmileElement);
    }
    
    /**
     * Write the lanes for the incident with the following XML Schema.<br/>
     * <Lanes><br>
     *     <Lane_number/><br>
     *     <Lane_number/><br>
     * </Lanes><br>
     */
    protected void writeLanesXML(Element currElem) {
        
        Document theDoc = currElem.getOwnerDocument();
        
        Element lanesElement = theDoc.createElement(XML_TAGS.LANES.tag);
        currElem.appendChild(lanesElement);

        Element laneElement = null;     

        for(String l : lanes) {
            laneElement = theDoc.createElement(XML_TAGS.LANE_NUM.tag);
            laneElement.appendChild(theDoc.createTextNode(l));
            lanesElement.appendChild(laneElement);      
        }
    }
    
    /** 
     * This method is used to set the status of this incident object to
     * "ON_GOING" if the incident has not been cleard.
     */
    public void update() {
        
        if(!isCleared())
            incidentStatus = INCIDENT_STATUS.ON_GOING;
        
    }
    
    /**
     * Method is called to determine if this incident update object is clearing 
     * an incident.  This is determined by checking if the status string is "CLEARED".
     *
     * @return true if object is clearing an incident, false if not.
     */
    public boolean isCleared() {
        return (incidentStatus == INCIDENT_STATUS.CLEARED); 
    }
    
}         