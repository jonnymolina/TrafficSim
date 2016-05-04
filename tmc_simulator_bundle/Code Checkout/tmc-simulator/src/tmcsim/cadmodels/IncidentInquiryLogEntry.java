package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * IncidentInquiryLogEntry is the base model object used for IncidentInquiry
 * log entries.  Every log entry has an associated position and timestamp for 
 * where the data originated. <br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT><br/>
 *    <POSITION_INFO/><br/>
 *    <TIMESTAMP/><br/>
 * </ROOT><br/>
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryLogEntry implements Serializable {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {      
        /** Log entry position info. */
        POSITION_INFO ("POSITION_INFO"),
        /** Log entry timestamp. */
        TIMESTAMP     ("TIMESTAMP");

        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /** Position information for this log entry. */
    protected String positionInfo;

    /** Time stamp value for this log entry. */
    protected String timeStamp; 
        
    /** Default Constructor.  Initialize local data. */
    public IncidentInquiryLogEntry() {
        positionInfo   = "";
        timeStamp      = "0000";
    }
    
    /** 
     * Constructor.  Set position info and time stamp. 
     * 
     * @param newPosInfo   Position info for this log entry
     * @param newTimeStamp Time stamp for this log entry
     */
    public IncidentInquiryLogEntry(String newPosInfo, String newTimeStamp) {
        positionInfo   = newPosInfo;
        timeStamp      = newTimeStamp;    
    }
    
    /**
     * Creates XML tags with the model data and adds them to the parameter 
     * Element.  See clss description for XML schema.
     *
     * @param currElem XML Element used as a root for XML tag appending.
     */
    public void toXML(Element currElem) {
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
                
        tempElem = theDoc.createElement(XML_TAGS.POSITION_INFO.tag);
        tempElem.appendChild(theDoc.createTextNode(positionInfo));
        currElem.appendChild(tempElem);
                
        tempElem = theDoc.createElement(XML_TAGS.TIMESTAMP.tag);
        tempElem.appendChild(theDoc.createTextNode(timeStamp));
        currElem.appendChild(tempElem);         
    }
    
    /**
     * Parses model data from the parameter Node.  See clss description for
     * XML schema.
     * 
     * @param modelNode XML Node containing model information.
     */
    public void fromXML(Node modelNode) {   
        Node childNode = null;
        
        //childNode = modelNode.getFirstChild();
        positionInfo = modelNode.getTextContent();
        
        childNode = modelNode.getNextSibling();
        timeStamp = childNode.getTextContent();     
    }
    
    
    /** 
     * Sets the time stamp for this log entry. 
     * 
     * @param newStamp The timestamp value.
     */
    public void timeStamp(String newStamp) {
        timeStamp = newStamp;       
    }       
    
    /** 
     * Returns the log information for this log entry.  The log information 
     * is the concatenation of the entering position info and timestamp.
     * 
     * @return Log entry's log information 
     */
    public String getLogInfo() {
        return positionInfo += timeStamp;
    }
}