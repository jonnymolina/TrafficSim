package tmcsim.common;

import java.io.Serializable;

/**
 * IncidentLocation contains information regarding incident 
 * locations.  These locations are referenced within the 
 * Paramics control portion of the CAD Simulator.  The data 
 * within these objects is read in from a simulation script, 
 * and must correspond to valid locations within the loaded 
 * paramics network.
 * 
 * @author
 * @version
 */
@SuppressWarnings("serial")
public class ParamicsLocation implements Serializable {

    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    public static enum XML_TAGS {
        /** Route. */
        ROUTE      ("Route"),
        /** Route direction. */
        DIRECTION  ("Direction"),
        /** Location type. */
        LOC_TYPE   ("Location_type"),
        /** Location postmile. */
        POSTMILE   ("Postmile");

        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /** Unique ID for this IncidentLocation. */
    public String locationID        = null;
    
    /** Route name of incident location. */
    public String incidentRoute     = null;
    
    /** Route direction of incident location. */
    public String incidentDirection = null;
    
    /** Route postmile of incident location. */
    public String incidentPostmile  = null;
    
    /** Type of incident location. */
    public String incidentLocType   = null;

    
    /**
     * Constructor.
     * 
     * @param id Unique ID.
     */
    public ParamicsLocation(String id) {
        locationID = id;
    }

    /**
     * Receive the tag name and value data from a parsed XML node.  
     * Set the corresponding data member with the new value.
     * 
     * @param tag_value XML tag name.
     * @param value XML tag value.
     */
    public void readXMLNode(String tag_name, String value) {
        
        if(tag_name.equals(XML_TAGS.ROUTE.tag))
            incidentRoute = value;      
        else if(tag_name.equals(XML_TAGS.DIRECTION.tag))
            incidentDirection = value;  
        else if(tag_name.equals(XML_TAGS.POSTMILE.tag))
            incidentPostmile = value;       
        else if(tag_name.equals(XML_TAGS.LOC_TYPE.tag))
            incidentLocType = value;
                
    }   
}
