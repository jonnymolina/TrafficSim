package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * IncidentInquiryWitnesses extends from IncidentInquiryLogEntry
 * to provide a model object containing information used to display a Witness 
 * entry on the IncidentInquiry CAD screen. Data for a witness includes the 
 * reporting party's name, phone number, and address.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT><br/>
 *    <REPORT_PARTY/><br/>
 *    <PHONE_NUM>/<br/>
 *    <ADDRESS/><br/>
 *    <POSITION_INFO/><br/>
 *    <TIMESTAMP/><br/>
 * </ROOT><br/>
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryWitnesses extends IncidentInquiryLogEntry 
    implements Serializable {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Witness' name. */
        REPORT_PARTY  ("REPORT_PARTY"),     
        /** Witness' phone number. */
        PHONE_NUM     ("PHONE_NUM"),    
        /** Witness' address. */
        ADDRESS       ("ADDRESS");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }       
    }
    
    /** Witness' name. */
    public String reportingParty;
    
    /** Witness' phone number. */
    public String telephoneNum;
    
    /** Witness' address. */
    public String address;      


    /** 
     * Constructor.  Initialize all Witness data to empty strings.
     * 
     *  @param newPosInfo String containing position info for this log entry
     */
    public IncidentInquiryWitnesses(String newPosInfo) {
        super(newPosInfo, "0000");

        reportingParty = "";
        telephoneNum   = "";
        address        = "";    
    }
    

    /**
     * Constructor.  Set local data and base data from parameters. 
     * 
     * @param newPositionInfo Position info for this log entry.
     * @param name            Witness' name
     * @param num             Witness' phone number
     * @param add             Witness' address
     * @param timestamp       Timestamp for this log entry
     */
    public IncidentInquiryWitnesses(String newPositionInfo, String name, 
            String num, String add, String timeStamp) {
        super(newPositionInfo, timeStamp);

        reportingParty = name;
        telephoneNum   = num;
        address        = add;   
    }       
    
    /**
     * Constructor.  Parse parameter node for Witness log entry data.
     * 
     * @param theNode Node containing data for this Witness log entry
     */
    public IncidentInquiryWitnesses(Node theNode) { 
        fromXML(theNode);   
    }
    

    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.REPORT_PARTY.tag);
        tempElem.appendChild(theDoc.createTextNode(reportingParty));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.PHONE_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(telephoneNum));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.ADDRESS.tag);
        tempElem.appendChild(theDoc.createTextNode(address));
        currElem.appendChild(tempElem);

        super.toXML(currElem);
    }    
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        reportingParty = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        telephoneNum = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        address = childNode.getTextContent();

        childNode = childNode.getNextSibling();
        super.fromXML(childNode);
        
    }
    
}