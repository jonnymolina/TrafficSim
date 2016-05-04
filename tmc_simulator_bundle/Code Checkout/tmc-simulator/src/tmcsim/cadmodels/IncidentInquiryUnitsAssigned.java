package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * IncidentInquiryUnitsAssigned extends from IncidentInquiryLogEntry
 * to provide a model object containing information used to display an assigned
 * unit.  Data for an assigned unit includes a flag designating whether
 * the unit is primary, the beat number, the unit's status, and a flag 
 * designating whether the unit is active or not.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT><br/>
 *    <IS_PRIMARY/><br/>
 *    <BEAT>/<br/>
 *    <STATUS_TYPE/><br/>
 *    <IS_ACTIVE/><br/>
 *    <POSITION_INFO/><br/>
 *    <TIMESTAMP/><br/>
 * </ROOT><br/>
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryUnitsAssigned extends IncidentInquiryLogEntry 
    implements Serializable {
    
    private static enum XML_TAGS {
        /** Unit's primary flag. */
        IS_PRIMARY  ("IS_PRIMARY"),     
        /** Unit's beat. */
        BEAT        ("BEAT"),
        /** Unit's status type. */
        STATUS_TYPE ("STATUS_TYPE"),
        /** Unit's active flag. */
        IS_ACTIVE   ("IS_ACTIVE");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
        
    }

    /** Unit is primary flag. */
    public boolean isPrimary;   

    /** Unit's beat. */
    public String  beat;    
 
    /** Unit's current status. */
    public String  statusType;  

    /** Unit's current active flag. */
    public boolean isActive;
    
    
    /** 
     * Constructor.  Initialize all unit data to empty strings or false.
     * 
     *  @param newPosInfo String containing position info for this log entry
     */
    public IncidentInquiryUnitsAssigned(String newPosInfo) {
        super(newPosInfo, "0000");
        
        isPrimary  = false;
        beat       = "";
        statusType = "";
        isActive   = false;     
    }
  
    /**
     * Constructor.  Parse parameter node for Unit log entry data.
     * 
     * @param theNode Node containing data for this Unit log entry
     */
    public IncidentInquiryUnitsAssigned(Node theNode) { 
        fromXML(theNode);
    }   

    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.IS_PRIMARY.tag);
        tempElem.appendChild(theDoc.createTextNode(String.valueOf(isPrimary)));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.BEAT.tag);
        tempElem.appendChild(theDoc.createTextNode(beat));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.STATUS_TYPE.tag);
        tempElem.appendChild(theDoc.createTextNode(statusType));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.IS_ACTIVE.tag);
        tempElem.appendChild(theDoc.createTextNode(String.valueOf(isActive)));
        currElem.appendChild(tempElem);

        super.toXML(currElem);
    }    
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        isPrimary = Boolean.parseBoolean(childNode.getTextContent());
        
        childNode = childNode.getNextSibling();
        beat = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        statusType = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        isActive = Boolean.parseBoolean(childNode.getTextContent());

        childNode = childNode.getNextSibling();
        super.fromXML(childNode);
        
    }     
    
    
    /** Determines equality based on beat */
    public boolean equals(Object o) {
        return beat.equals(((IncidentInquiryUnitsAssigned)o).beat);
    }
}